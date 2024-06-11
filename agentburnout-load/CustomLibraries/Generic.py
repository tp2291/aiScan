import json
import urllib
import datetime
from dateutil.relativedelta import relativedelta
from confluent_kafka import Consumer
from robot.libraries.BuiltIn import BuiltIn
import AppValues
from robot.api.deco import keyword
from agentburnout_exception import AgentburnoutException
from prometheus_client import generate_latest, CollectorRegistry, Counter, Gauge, Histogram, start_http_server


class Generic:

    def __init__(self):
        self.builtIn = BuiltIn().get_library_instance("BuiltIn")

    @keyword
    def load_from_confluent(self, event):
        config = dict()
        self.builtIn.log(config)
        self.builtIn.log(AppValues.CATALOG_URL_UPDATE_EVENT)
        if event == "sentiment":
            URL = AppValues.CATALOG_URL_UPDATE_EVENT
        else:
            URL = AppValues.CATALOG_URL_AGENTBURNOUT_INDEX
        try:
            self.builtIn.log(URL)
            with urllib.request.urlopen(URL) as url:
                data = json.loads(url.read().decode())
                config.update({'bootstrap.servers': data['dataPlaneConfig']['kafkaUrl']})
        except Exception as e:
            raise AgentburnoutException(
                "An unexpected error occurred while loading config from catalogue:") from e
        self.builtIn.log(config)
        return config

    @keyword
    def load_consumer_common_config(self, config, event):
        if event == "sentiment":
            groupID = AppValues.SENTIMENT_GROUP_ID_CONFIG
        else:
            groupID = AppValues.AGENTBURNOUT_GROUP_ID_CONFIG
        config.update({
            'group.id': groupID,
            'auto.offset.reset': AppValues.AUTO_OFFSET_RESET_CONFIG,
            'session.timeout.ms': 45000
        })
        return config

    @keyword
    def consume_message(self, sentiment_config, agentburnout_config):
        con_sentiment = Consumer(sentiment_config)
        con_agentburnout = Consumer(agentburnout_config)
        event_count = 0
        total_time_diff_from_event = 0
        total_time_diff_from_interaction = 0

        try:
            con_sentiment.subscribe([AppValues.WXCC_UPDATE_KAFKA_TOPIC])
            con_agentburnout.subscribe([AppValues.WXCC_BURNOUT_KAFKA_TOPIC])
            while True:
                msg = con_sentiment.poll(timeout=1.0)
                if msg is None or msg.error():
                    if msg is not None:
                        self.builtIn.log(msg.error)
                    self.builtIn.log("None for sentiment")
                    continue

                value = json.loads(msg.value())
                self.builtIn.log("Printing Value:")
                self.builtIn.log(value)
                if 'sourceType' in value:
                    if value['sourceType'] != AppValues.SENTIMENT_AVAILABLE_EVENT:
                        continue
                    interaction_id = value["entityId"]
                    self.builtIn.log("Got interaction id from sentiment available:")
                    while True:
                        self.builtIn.log("Polling for burnout Index")
                        msg2 = con_agentburnout.poll(timeout=1.0)
                        if msg2 is None or msg2.error():
                            if msg2 is not None:
                                self.builtIn.log(msg2.error)
                            self.builtIn.log("None for burnout")
                            continue

                        value2 = json.loads(msg2.value())
                        self.builtIn.log("Printing Value2:")
                        self.builtIn.log(value2)
                        if 'burnoutIndex' in value2:
                            if value2['interactionId'] != interaction_id:
                                continue
                            interaction_end_time = value["attrMap"]["endTime"]
                            sentiment_event_time = value["eventTime"]
                            agentburnout_event_time = value2["dateTime"]
                            self.builtIn.log("agentburnout_event_time exact")
                            self.builtIn.log(agentburnout_event_time)
                            self.builtIn.log(type(agentburnout_event_time))

                            agentburnout_event_time = datetime.datetime.strptime(agentburnout_event_time, "%Y-%m-%d %H:%M:%S.%f%z")
                            self.builtIn.log("agentburnout_event_time after first conversion")
                            self.builtIn.log(agentburnout_event_time)
                            self.builtIn.log(type(agentburnout_event_time))

                            agentburnout_event_time = agentburnout_event_time.timestamp() * 1000
                            event_count += 1
                            self.builtIn.log("agentburnout_event_time after second conversion")
                            self.builtIn.log(agentburnout_event_time)
                            self.builtIn.log(type(agentburnout_event_time))

                            self.builtIn.log(sentiment_event_time)
                            self.builtIn.log(type(sentiment_event_time))

                            self.builtIn.log(interaction_end_time)
                            self.builtIn.log(type(interaction_end_time))

                            time_diff_from_event = self.relative_del(agentburnout_event_time, int(sentiment_event_time))
                            time_diff_from_interaction = self.relative_del(agentburnout_event_time,
                                                                           int(interaction_end_time))
                            total_time_diff_from_event += time_diff_from_event
                            total_time_diff_from_interaction += time_diff_from_interaction
                            break
                if event_count == 1:
                    self.builtIn.log(event_count)
                    break
            return total_time_diff_from_event / event_count, total_time_diff_from_interaction / event_count, event_count

        except Exception as e:
            print("An unexpected error occurred: " + str(e))
        finally:
            con_sentiment.close()
            con_agentburnout.close()

    @staticmethod
    def relative_del(end_time, start_time):
        rd = relativedelta(datetime.datetime.fromtimestamp(end_time / 1000),
                           datetime.datetime.fromtimestamp(start_time / 1000))
        return (rd.days * 86400) + (rd.hours * 3600) + (rd.minutes * 60) + rd.seconds

    @keyword
    def push_the_calculated_metrics_to_prometheus(self, delta_time_event, delta_time_interaction, event_count):
        registry = CollectorRegistry()
        date = datetime.datetime.now()
        current_month = date.strftime("%B")
        EVENT_COUNT = Gauge('saa_load_test_event_count',
                            'Number of events ',
                            ['month'])
        DELTA_TIME_EVENT = Gauge('saa_load_test_delta_time_event',
                                 'Number of events ',
                                 ['month'])

        DELTA_TIME_INTERACTION = Gauge('saa_load_test_delta_time_interaction',
                                       'Number of events ',
                                       ['month'])
        start_http_server(9090)
        EVENT_COUNT.labels(current_month).set(event_count)
        DELTA_TIME_EVENT.labels(current_month).set(delta_time_event)
        DELTA_TIME_INTERACTION.labels(current_month).set(delta_time_interaction)
        generate_latest(registry)