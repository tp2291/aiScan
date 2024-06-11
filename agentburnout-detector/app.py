from datetime import datetime
import json
import sys
import psycopg2.extras
from confluent_kafka import Consumer, Producer

from integrations import inference_api_handler
from constants.app_constants import AppConstants
from constants.business_constants import BusinessConstants
from exceptions.agentburnout_exception import AgentburnoutException
from logging_config import logger
from utils.config_helper import ConfigHelper
from utils.kafka_config_builder import KafkaConfigBuilder
from utils.kafka_consumer_helper import log_assignment
from utils.kafka_producer_helper import delivery_callback
from prometheus_client import generate_latest, CollectorRegistry, Counter, Gauge, start_http_server
import psutil
import time
from logging_fields import logging_agent_id, logging_interaction_id, logging_org_id
from utils.redis_helper import redis_connect

if __name__ == '__main__':

    run_in_wxcc = (len(sys.argv) == 1)

    if not run_in_wxcc and len(sys.argv) < 4:
        raise AgentburnoutException("Config files missing to run application locally")

    kafka_config_builder = KafkaConfigBuilder()
    redis_client = redis_connect()

    if run_in_wxcc:
        logger.info("Running in wxcc env", extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id,
                                                  AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id,
                                                  AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
        kafka_consumer_config = kafka_config_builder.build_from_catalogue(AppConstants.CATALOG_URL_SENTIMENT_AVAILABLE)
        kafka_producer_config = kafka_config_builder.build_from_catalogue_producer(
            AppConstants.CATALOG_URL_AGENTBURNOUT_INDEX)
        db_config = ConfigHelper.get_db_config_from_vault()
        input_topic = AppConstants.WXCC_INPUT_KAFKA_TOPIC
        output_topic = AppConstants.WXCC_OUTPUT_KAFKA_TOPIC
    else:
        kafka_consumer_config = kafka_config_builder.build_from_confluent_config(sys.argv[2])
        kafka_producer_config = kafka_consumer_config.copy()
        db_config = ConfigHelper.read_config_file(sys.argv[3])
        input_topic = AppConstants.CONFLUENT_INPUT_KAFKA_TOPIC
        output_topic = AppConstants.CONFLUENT_OUTPUT_KAFKA_TOPIC

    kafka_config_builder.load_consumer_config(kafka_consumer_config)
    kafka_config_builder.load_producer_config(kafka_producer_config)
    c = Consumer(kafka_consumer_config)
    c.subscribe([input_topic], on_assign=log_assignment)

    p1 = Producer(kafka_producer_config)
    p2 = Producer({'bootstrap.servers': AppConstants.ADX_TOPIC_BROKER, 'acks': AppConstants.ACKS_CONFIG})

    registry = CollectorRegistry()

    SYSTEM_CPU_USAGE = Gauge('saa_ab_detector_system_cpu_usage', 'Hold current CPU resource usage', ['app_name'])
    SYSTEM_MEMORY_USAGE = Gauge('saa_ab_detector_system_memory_usage', 'Hold current Meomory resource usage',
                                ['app_name'])
    EVENT_CONSUMED_SUCCESSFULLY = Counter('saa_ab_detector_event_consumed',
                                          'Number of events that were consumed from the topic successfully',
                                          ['app_name', 'topic_name', 'orgId'])
    EVENT_PRODUCED_SUCCESSFULLY = Counter('saa_ab_detector_event_produced',
                                          'Number of events that were produced to the topic successfully',
                                          ['app_name', 'topic_name', 'orgId'])
    EVENT_STATUS_COUNTER = Counter('saa_ab_detector_event_status',
                                   'Number of events that were produced to the topic successfully',
                                   ['app_name', 'status', 'orgId'])

    EVENT_SKIPPED_COUNT = Counter('saa_ab_detector_event_skipped_count',
                                  'Number of times event was skipped ',
                                  ['app_name', 'reason', 'orgId'])
    TOTAL_PROCESSING_TIME = Gauge('saa_ab_detector_total_processing_time_seconds',
                                  'Total time spent for the event execution',
                                  ['app_name', 'orgId'])

    start_http_server(AppConstants.PROMETHEUS_PORT)

    while True:

        msgs = c.consume(1, timeout=1)
        for msg in msgs:
            org_id = ""

            if msg is None or msg.error():
                if msg is not None:
                    SYSTEM_CPU_USAGE.labels('agentburnout-detector').set(psutil.cpu_percent())
                    SYSTEM_MEMORY_USAGE.labels('agentburnout-detector').set(psutil.virtual_memory()[2])
            else:
                try:
                    value = json.loads(msg.value())

                    eventName = value.get('eventName').casefold()

                    if eventName not in [AppConstants.EVENT_SENTIMENT_AVAILABLE]:
                        continue

                    org_id = value.get("attrMap").get("orgId")
                    agent_id = value.get("attrMap").get("agentId")
                    interaction_id = value.get("attrMap").get("contactId")

                    logging_org_id = org_id
                    logging_agent_id = agent_id
                    logging_interaction_id = interaction_id

                    if redis_client.sismember(AppConstants.AGENTBURNOUT_ENABLED_AGENT_LIST_REDIS_SET, agent_id):
                        logger.info("Present in redis agent_id: " + agent_id,
                                    extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: org_id,
                                           AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id,
                                           AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
                    else:
                        continue

                    if not redis_client.exists(f"{AppConstants.REDIS_ABAC_CONNECTED_PREFIX}{interaction_id}"):
                        logger.info(
                            f"Skipping sentiment-available event for interaction id: {interaction_id} as connected event not present in redis",
                            extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: org_id,
                                   AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id,
                                   AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
                        continue

                    connected_event = json.loads(
                        redis_client.get(f"{AppConstants.REDIS_ABAC_CONNECTED_PREFIX}{interaction_id}"))

                    tenant_id = value['path'].split('/')[0]
                    event_time = value['timestamp']
                    agent_session_id = value['attrMap']['agentSessionId']
                    call_end_time = value['attrMap']['endTime']
                    recording_start_time = value['attrMap']['recordingStartTime']
                    agent_sentiment = value['attrMap']['sentiment']['agent'].split(',')[1]
                    caller_sentiment = value['attrMap']['sentiment']['caller'].split(',')[1]

                    logger.info(f'{msg.topic()} [{msg.partition()}] at offset {msg.offset()} \n',
                                extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id,
                                       AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id,
                                       AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})

                    EVENT_CONSUMED_SUCCESSFULLY.labels('agentburnout-detector', input_topic, org_id).inc()
                    EVENT_STATUS_COUNTER.labels("agentburnout-detector", "CONSUMED", org_id).inc()
                    SYSTEM_CPU_USAGE.labels('agentburnout-detector').set(psutil.cpu_percent())
                    SYSTEM_MEMORY_USAGE.labels('agentburnout-detector').set(psutil.virtual_memory()[2])

                    start_timer_total_processing_time = time.perf_counter()

                    with psycopg2.connect(
                            host=db_config['host'],
                            dbname=db_config['dbname'],
                            user=db_config['username'],
                            password=db_config['password'],
                            port=db_config['port']) as conn:
                        with conn.cursor(cursor_factory=psycopg2.extras.DictCursor) as cur:

                            logger.info(f"Trying to fetch model for agent: {agent_id} from database...",
                                        extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id,
                                               AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id,
                                               AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
                            cur.execute(
                                "SELECT model, org_id, agent_id FROM ab.model WHERE agent_id=%s AND org_id=%s;",
                                (agent_id, org_id))
                            response = cur.fetchone()

                            if response is None:
                                logger.info(
                                    "Record does not exist in database for orgId: " + org_id + " agentId: " + agent_id,
                                    extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id,
                                           AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id,
                                           AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
                            elif response[0] is not None:
                                burnout_index, message = inference_api_handler.get_inference(org_id, agent_id,
                                                                                             interaction_id,
                                                                                             recording_start_time,
                                                                                             agent_sentiment,
                                                                                             caller_sentiment)
                                curr_timestamp_utc = datetime.utcnow()

                                if burnout_index == -1:
                                    logger.info(
                                        f"Burnout Index not calculated due to {message} for interaction_id: " + interaction_id,
                                        extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id,
                                               AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id,
                                               AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
                                else:
                                    burnout_event_agentburnout_index = {'agentId': agent_id,
                                                                        'orgId': org_id,
                                                                        'interactionId': interaction_id,
                                                                        'burnoutDetectedTimeStamp': str(int(round((
                                                                                                                  curr_timestamp_utc - datetime(
                                                                                                              1970, 1,
                                                                                                              1)).total_seconds() * 1000))),
                                                                        'interactionEndTime': call_end_time,
                                                                        'burnoutIndex': str(burnout_index),
                                                                        'burnoutThreshold': str(
                                                                            BusinessConstants.burnout_threshold)
                                                                        }

                                    burnout_event_adx_incoming_active = {
                                        "attrMap": {
                                            "agentDn": connected_event["attrMap"]["agentDn"],
                                            "agentId": connected_event["attrMap"]["agentId"],
                                            "agentLogin": connected_event["attrMap"]["agentLogin"],
                                            "agentName": connected_event["attrMap"]["agentName"],
                                            "agentSessionId": connected_event["attrMap"]["agentSessionId"],
                                            "agentSystemId": connected_event["attrMap"]["agentSystemId"],
                                            "burnoutDetectedTimeStamp": str(int(round(time.time() * 1000))),
                                            "burnoutIndex": str(burnout_index),
                                            "burnoutThreshold": str(BusinessConstants.burnout_threshold),
                                            "channelId": connected_event["attrMap"]["channelId"],
                                            "channelType": connected_event["attrMap"]["channelType"],
                                            "contactId": connected_event["attrMap"]["contactId"],
                                            "eventName": "burnout-index",
                                            "productVersion": connected_event["attrMap"]["productVersion"],
                                            "siteId": connected_event["attrMap"]["siteId"],
                                            "siteName": connected_event["attrMap"]["siteName"],
                                            "siteSystemId": connected_event["attrMap"]["siteSystemId"],
                                            "teamId": connected_event["attrMap"]["teamId"],
                                            "teamName": connected_event["attrMap"]["teamName"],
                                            "teamSystemId": connected_event["attrMap"]["teamSystemId"]
                                        },
                                        "entityId": connected_event["attrMap"]["channelId"],
                                        "eventName": "burnout-index",
                                        "path": f"{tenant_id}/INCOMING/ACTIVE/ACD/AAR",
                                        "publishTimestamp": curr_timestamp_utc.isoformat() + "Z",
                                        "timestamp": str(
                                            int(round((curr_timestamp_utc - datetime(1970, 1, 1)).total_seconds() * 1000)))
                                    }

                                    p1.produce(output_topic, json.dumps(burnout_event_agentburnout_index).encode('utf-8'),
                                           callback=delivery_callback)
                                    p1.poll(10000)
                                    p1.flush()
                                    p2.produce(AppConstants.ADX_INCOMING_EVENT_TOPIC,
                                           json.dumps(burnout_event_adx_incoming_active).encode('utf-8'),
                                           callback=delivery_callback)
                                    p2.poll(10000)
                                    p2.flush()
                                    EVENT_PRODUCED_SUCCESSFULLY.labels("agentburnout-detector", output_topic, org_id).inc()
                                    EVENT_PRODUCED_SUCCESSFULLY.labels("agentburnout-detector", burnout_event_adx_incoming_active, org_id).inc()
                                    EVENT_STATUS_COUNTER.labels("agentburnout-detector", "PRODUCED", org_id).inc()

                            else:
                                logger.info(
                                    "Agent Burnout model not available for orgId: " + org_id + " agentId: " + agent_id,
                                    extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id,
                                           AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id,
                                           AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
                                EVENT_SKIPPED_COUNT.labels("agentburnout-detector", "model_not_available", org_id).inc()

                    end_timer_total_processing_time = time.perf_counter()
                    TOTAL_PROCESSING_TIME.labels('agentburnout-detector', org_id).set(
                        end_timer_total_processing_time - start_timer_total_processing_time)

                except Exception as e:
                    EVENT_SKIPPED_COUNT.labels("agentburnout-detector", "runtime_exception", org_id).inc()
                    EVENT_STATUS_COUNTER.labels("agentburnout-detector", "runtime_exception", org_id).inc()
                    logger.exception("An exception occurred" + str(e), exc_info=True,
                                     extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id,
                                            AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id,
                                            AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
                finally:
                    logging_interaction_id = ""
                    logging_agent_id = ""
                    logging_org_id = ""

        generate_latest(registry)

    c.close()
