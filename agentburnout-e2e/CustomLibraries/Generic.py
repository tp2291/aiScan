import configparser
import datetime
import json
import urllib
from confluent_kafka import Consumer, Producer
from robot.api.deco import keyword
from robot.libraries.BuiltIn import BuiltIn
from agentburnout_exception import AgentburnoutException
import logs


class Generic:

    def __init__(self):
        self.builtIn = BuiltIn().get_library_instance("BuiltIn")
        self.SENTIMENT_AVAILABLE_EVENT = 'sentiment-available'
        self.UPDATE_EVENT = "update-event"

        # WXCC_PRODUCER_KAFKA_TOPIC = 'ace.intgus1.adx.incoming.active'
        self.WXCC_CONSUMER_KAFKA_TOPIC = 'agentburnout.index'
        self.WXCC_PRODUCER_KAFKA_TOPIC = 'wxcc.update.event'

        self.CATALOG_URL_UPDATE_EVENT = f"https://catalog.intgus1.ciscoccservice.com/catalog/registration/wxcc/update.event"
        self.CATALOG_URL_AGENTBURNOUT_INDEX = f"https://catalog.intgus1.ciscoccservice.com/catalog/registration/agentburnout/index"

        self.GROUP_ID_CONFIG = f"consumer.saa.agentburnout-e2e"
        self.AUTO_OFFSET_RESET_CONFIG = 'earliest'
        self.MAX_POLL_RECORDS_CONFIG = '1'
        self.SESSION_TIMEOUT_CONFIG = 45000
        self.ACKS_CONFIG = 'all'
        self.PRODUCER_BROKER = "kafkaanalyzer.service.consul:9092"

        self.ORG_ID_VALUE = "4f07a18c-cfef-429c-981f-c1c367979a0c"
        self.AGENT_ID = "ea9a7ed0-9097-4fda-bc51-0e0e59498405"
        self.INTERACTION_ID = "bacd0f14-bb40-4dfa-950a-81229e8f94aa"

        self.AGENT_SENTIMENT = "0.0, 3"
        self.CALLER_SENTIMENT = "0.0, 3"

    @keyword
    def load_from_confluent(self, kafka):
        kafka_config = dict()
        if kafka == "consumer":
            URL = self.CATALOG_URL_AGENTBURNOUT_INDEX
        elif kafka == "producer":
            URL = self.CATALOG_URL_UPDATE_EVENT
        try:
            with urllib.request.urlopen(URL) as url:
                data = json.loads(url.read().decode())
                kafka_config.update({'bootstrap.servers': data['dataPlaneConfig']['kafkaUrl']})
        except Exception as e:
            raise AgentburnoutException(
                "An unexpected error occurred while loading config from catalogue:") from e
        return kafka_config

    @keyword
    def read_config_file(self, file, key='default'):
        try:
            config_parser = configparser.ConfigParser()
            with open(file) as f:
                config_parser.read_file(f)
            config = dict(config_parser[key])
            return config
        except Exception as e:
            raise AgentburnoutException(
                "An unexpected error occurred while loading config from catalogue:") from e

    @keyword
    def consumer_update(self, config):
        config.update({
            'group.id': self.GROUP_ID_CONFIG,
            'auto.offset.reset': self.AUTO_OFFSET_RESET_CONFIG,
            'session.timeout.ms': self.SESSION_TIMEOUT_CONFIG
        })
        con = Consumer(config)
        con.subscribe([self.WXCC_CONSUMER_KAFKA_TOPIC])
        return con

    @keyword
    def producer_update(self, config):
        config.update({
            'acks': 'all'
        })
        return config

    @keyword
    def create_producer_object(self, config):
        p = Producer(config)
        return p

    @keyword
    def create_message(self):
        curr_timestamp = int(datetime.datetime.timestamp(datetime.datetime.now()) * 1000)
        # message = {
        #     "path": "4f07a18c-cfef-429c-981f-c1c367979a0c/INCOMING/ELAPSED/ACD/CSR",
        #     "entityId": self.INTERACTION_ID,
        #     "eventName": self.SENTIMENT_AVAILABLE_EVENT,
        #     "timestamp": "1683275069261",
        #     "attrMap": {
        #         "sentiment": {
        #             "agent": "-0.0015, 3",
        #             "caller": "-0.189, 2"
        #         },
        #         "contactId": self.INTERACTION_ID,
        #         "eventName": "sentiment-available",
        #         "orgId": self.ORG_ID_VALUE,
        #         "agentId": "",
        #         "recordingStartTime": "1683275036426"
        #     }
        # }
        message = {
            "sourceType": self.SENTIMENT_AVAILABLE_EVENT,
            "tenantId": self.ORG_ID_VALUE,
            "eventTime": "1689331322502",
            "eventName": self.UPDATE_EVENT,
            "entityId": self.INTERACTION_ID,
            "eventType": "CSR",
            "attrMap": {
                "recordingStartTime": "1689331280616",
                "sentiment": {"agent": self.AGENT_SENTIMENT, "caller": self.CALLER_SENTIMENT},
                "agentId": self.AGENT_ID,
                "agentSessionId": "7281e7d7-6939-4762-ab6c-8e105fe98518",
                "startTime": "1689331211251",
                "endTime": "1689331279988"
            },
            "orgId": self.ORG_ID_VALUE
        }
        data_string = json.dumps(message)
        self.builtIn.log_to_console(data_string)
        return data_string, message

    @keyword
    def send_message(self, p, msg):
        p.produce(self.WXCC_PRODUCER_KAFKA_TOPIC, msg.encode('utf-8'))
        p.poll(10000)
        p.flush()

    @keyword
    def consume_message(self, con):
        for i in range(1, 100):
            msg = con.poll(timeout=1.0)
            if msg is None or msg.error():
                if msg is not None:
                    self.builtIn.log(msg.error)
                self.builtIn.log("None")
                continue
            else:
                try:
                    value = json.loads(msg.value())
                    if 'agentId' in value and 'burnoutIndex' in value:
                        if value['interactionId'] != self.INTERACTION_ID:
                            continue
                        else:
                            burnout_index = value['burnoutIndex']
                            return value, burnout_index
                    else:
                        continue
                except Exception as e:
                    self.builtIn.log_to_console("An unexpected error occurred: " + str(e))
        con.close()
