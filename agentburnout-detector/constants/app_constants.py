import os


class AppConstants:

    PROD_DC = "produs1"
    DC = os.getenv('DC', 'devus1')

    TIME_FORMAT = "%Y-%m-%d %H:%M:%S.%f%z"
    MODEL_VALIDITY_IN_DAYS = 1
    SECONDS_IN_1_DAY = 24 * 60 * 60

    # Model Status: Needs_training = 0, Insufficient_data = 1, Trained = 2
    MODEL_STATUS_NEEDS_TRAINING = 0
    MODEL_STATUS_INSUFFICIENT_DATA = 1
    MODEL_STATUS_TRAINED = 2

    EVENT_SENTIMENT_AVAILABLE = 'sentiment-available'
    EVENT_CONNECTED = 'connected'
    PATH_TYPE_AAR = 'AAR'
    WXCC_INPUT_KAFKA_TOPIC = 'saa.sentiment.available'
    CONFLUENT_INPUT_KAFKA_TOPIC = 'update.event'
    WXCC_OUTPUT_KAFKA_TOPIC = 'agentburnout.index'
    CONFLUENT_OUTPUT_KAFKA_TOPIC = 'agent.burnout'

    ADX_INCOMING_EVENT_TOPIC = f'ace.{DC}.adx.incoming.active'
    ADX_TOPIC_BROKER = 'kafkaanalyzer.service.consul:9092'
    CATALOG_URL_SENTIMENT_AVAILABLE = f"https://catalog.{DC}.ciscoccservice.com/catalog/registration/saa/sentiment.available"
    CATALOG_URL_AGENTBURNOUT_INDEX = f"https://catalog.{DC}.ciscoccservice.com/catalog/registration/agentburnout/index"
    INFERENCE_APP_URL = "http://inference:8080/api"
    AUTO_OFFSET_RESET_CONFIG = 'earliest'
    GROUP_ID_CONFIG = f"consumer.saa.v2.{os.environ['appName']}{os.getenv('appPrefix', '')}"
    if DC != PROD_DC:
        GROUP_ID_CONFIG += os.getenv('gitCommit')
    MAX_POLL_RECORDS_CONFIG = '1'
    SESSION_TIMEOUT_CONFIG = 45000
    ACKS_CONFIG = 'all'

    WXCC_DB_HOST = f"agentburnout-aurora.{DC}.ciscoccservice.com"
    DB_NAME = "saaagentburnout"
    DB_PORT = "5432"

    VAULT_DB_CRED_FILE_PATH = "/vault/secrets/aurora-creds.ini"
    VAULT_REDIS_CRED_FILE_PATH = "/vault/secrets/redis.txt"

    VAULT_REDIS_FILE_PATH = "/vault/secrets/redis.txt"
    REDIS_HOST = os.getenv('REDIS_HOST')
    REDIS_PORT = 6379
    AGENTBURNOUT_ENABLED_AGENT_LIST_REDIS_SET = "agentburnout_enabled_agent_list"
    REDIS_ABAC_CONNECTED_PREFIX = "abac_connected_"

    PROMETHEUS_PORT = 9090
    SPLIT_NAME_BURNOUT = 'agent_burnout_org'
    SPLIT_KEY_NAME = 'value'
    SPLIT_KEY_FILE_NAME = '/vault/secrets/split-value.ini'
    SPLIT_API_KEY = ""

    KIBANA_INTERACTION_ID_FIELD_NAME = "interaction_id"
    KIBANA_AGENT_ID_FIELD_NAME = "agent_id"
    KIBANA_ORG_ID_FIELD_NAME = "org_id"
