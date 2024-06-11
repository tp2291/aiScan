import json

import psycopg2.extras
from confluent_kafka import Consumer
from confluent_kafka import Producer

from constants.app_constants import AppConstants
from logging_config import logger
from utils.config_helper import ConfigHelper
from utils.kafka_config_builder import KafkaConfigBuilder
from utils.kafka_consumer_helper import log_assignment
from utils.kafka_producer_helper import delivery_callback
from prometheus_client import generate_latest, CollectorRegistry, Gauge, Counter,start_http_server
import psutil,time
if __name__ == '__main__':

    kafka_config_builder = KafkaConfigBuilder()

    kafka_consumer_config = kafka_config_builder.build_from_catalogue(AppConstants.CATALOG_URL_AGENTBURNOUT_INDEX)
    kafka_config_builder.load_consumer_config(kafka_consumer_config)
    notifs_kafka_producer_config = {AppConstants.BOOTSTRAP_SERVERS: AppConstants.NOTIFS_BOOTSTRAP_SERVERS}
    db_config = ConfigHelper.get_db_config_from_vault()
    input_topic = AppConstants.INPUT_KAFKA_TOPIC
    output_topic = AppConstants.NOTIFS_TOPIC

    c = Consumer(kafka_consumer_config)
    c.subscribe([input_topic], on_assign=log_assignment)
    p = Producer(notifs_kafka_producer_config)
    registry = CollectorRegistry()

    SYSTEM_CPU_USAGE = Gauge('saa_burnout_processor_system_cpu_usage',
                             'Hold current CPU resource usage',
                             ['app_name'])
    SYSTEM_MEMORY_USAGE = Gauge('saa_burnout_processor_system_memory_usage',
                                'Hold current Meomory resource usage',
                                ['app_name'])
    KAFKA_ERROR_COUNT= Counter('saa_burnout_processor_kafka_error_count',
                                          'Number of kafka message errors',
                                          ['app_name', 'topic_name'])
    EVENT_STATUS_COUNTER = Counter('saa_burnout_processsor_event_status',
                                   'Number of events that were produced to the topic successfully',
                                   ['app_name', 'status' ,'orgId'])
    TOTAL_PROCESSING_TIME=Gauge('saa_burnout_processor_total_processing_time_seconds',
                                'Total processing time after the event is consumed'
                                , ['app_name','orgId'])
    start_http_server(AppConstants.PROMETHEUS_PORT)
    while True:

        msg = c.poll(timeout=1.0)
        org_id=""
        logging_org_id = ""
        logging_agent_id = ""
        logging_interaction_id = ""
        if msg is None or msg.error():
            if msg is not None:
                KAFKA_ERROR_COUNT.labels('burnout-processor', input_topic).inc()
                EVENT_STATUS_COUNTER.labels('burnout-processor',"KAFKA_ERROR","N/A").inc()
                SYSTEM_CPU_USAGE.labels('burnout-processor').set(psutil.cpu_percent())
                SYSTEM_MEMORY_USAGE.labels('burnout-processor').set(psutil.virtual_memory()[2])
            continue
        else:
            try:
                logger.info(f'Started consuming: {"interaction_id"})')
                value = json.loads(msg.value())
                interaction_id = value['interactionId']
                agent_id = value['agentId']
                org_id = value['orgId']
                EVENT_STATUS_COUNTER.labels("burnout-processor","CONSUMED",org_id).inc()
                SYSTEM_CPU_USAGE.labels('burnout-processor').set(psutil.cpu_percent())
                SYSTEM_MEMORY_USAGE.labels('burnout-processor').set(psutil.virtual_memory()[2])

                logging_org_id = org_id
                logging_agent_id = agent_id
                logging_interaction_id = interaction_id
                logger.info(f'{msg.topic()} [{msg.partition()}] at offset {msg.offset()} \n', extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id, AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id, AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
                start_timer_total_processing_tome=time.perf_counter()
                with psycopg2.connect(
                        host=db_config['host'],
                        dbname=db_config['dbname'],
                        user=db_config['username'],
                        password=db_config['password'],
                        port=db_config['port']) as conn:
                    with conn.cursor(cursor_factory=psycopg2.extras.DictCursor) as cur:
                        logger.info(
                            f'Publishing burnout event  for interaction id: {interaction_id}', extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id, AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id, AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
                        cur.execute(
                            "SELECT subscription_url_id FROM ab.subscriptionsnew_info WHERE organization_id=  %s;",
                            (org_id,))
                        subscriptionid_list = cur.fetchall()

                        if subscriptionid_list is None:
                            logger.info(
                                f'Subscription Id not set for agent {agent_id}, cannot publish burnout event.', extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id, AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id, AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
                            continue
                        subscriptionIds = [row[0] for row in subscriptionid_list]
                        logger.info(f'orgId {org_id})')
                        logger.info(f'SubscriptionIds  {subscriptionIds})')
                        notifs_dict = dict()
                        notifs_dict["type"] = "saa-agentburnout"
                        notifs_dict["orgId"] = org_id
                        notifs_dict["subscriptionIds"] = subscriptionIds
                        notifs_dict["data"] = value
                        notifs_payload = json.dumps(notifs_dict)
                        p.produce(topic=AppConstants.NOTIFS_TOPIC, value=notifs_payload,
                                  callback=delivery_callback)
                        p.poll(10000)
                        p.flush()
                        EVENT_STATUS_COUNTER.labels("burnout-processor","PRODUCED",org_id).inc()
                        end_timer_total_processing_time=time.perf_counter()
                        TOTAL_PROCESSING_TIME.labels("burnout-processor",org_id).set(end_timer_total_processing_time-start_timer_total_processing_tome)

            except Exception as e:
                EVENT_STATUS_COUNTER.labels("burnout-processor","RUNTIME_EXCEPTION",org_id).inc()
                logger.exception("An exception occurred" + str(e), exc_info=True, extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id, AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id, AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
            finally:
                logging_interaction_id = ""
                logging_agent_id = ""
                logging_org_id = ""
        generate_latest(registry)
    c.close()