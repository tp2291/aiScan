import json
import urllib.request

from constants.app_constants import AppConstants
from exceptions.agentburnout_exception import AgentburnoutException
from utils.config_helper import ConfigHelper


class KafkaConfigBuilder:

    @staticmethod
    def build_from_catalogue(catalogue_url):
        config = dict()
        try:
            with urllib.request.urlopen(catalogue_url) as url:
                data = json.loads(url.read().decode())
                config.update({'bootstrap.servers': data['dataPlaneConfig']['kafkaUrl']})
                return config
        except Exception as e:
            raise AgentburnoutException(
                "An unexpected error occurred while loading config from catalogue:") from e

    @staticmethod
    def build_from_catalogue_producer(catalogue_url):
        config = dict()
        try:
            with urllib.request.urlopen(catalogue_url) as url:
                data = json.loads(url.read().decode())
                broker = data['dataPlaneConfig']['kafkaUrl']
                config.update({'bootstrap.servers': broker})
                return config
        except Exception as e:
            raise AgentburnoutException(
                "An unexpected error occurred while loading config from catalogue:") from e
    @staticmethod
    def build_from_confluent_config(file_path):
        return ConfigHelper.read_config_file(file_path)

    @staticmethod
    def load_consumer_config(config):
        config.update({
            'group.id': AppConstants.GROUP_ID_CONFIG,
            'auto.offset.reset': AppConstants.AUTO_OFFSET_RESET_CONFIG,
            'session.timeout.ms': AppConstants.SESSION_TIMEOUT_CONFIG
        })

    @staticmethod
    def load_producer_config(config):
        config.update({
            'acks': AppConstants.ACKS_CONFIG
        })
