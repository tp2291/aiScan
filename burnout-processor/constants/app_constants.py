import os


class AppConstants:
    INPUT_KAFKA_TOPIC = 'agentburnout.index'
    CATALOG_URL_AGENTBURNOUT_INDEX = f"https://catalog.{os.environ['DC']}.ciscoccservice.com/catalog/registration/agentburnout/index"
    GROUP_ID_CONFIG = f"consumer.saa.{os.environ['appName']}{os.getenv('appPrefix', '')}"
    AUTO_OFFSET_RESET_CONFIG = 'earliest'
    NOTIFS_TOPIC = "ccc_datanotifs"
    VAULT_DB_CRED_FILE_PATH = "/vault/secrets/aurora-creds.ini"
    BOOTSTRAP_SERVERS = 'bootstrap.servers'
    NOTIFS_BOOTSTRAP_SERVERS = 'kafka.service.consul'
    SESSION_TIMEOUT_CONFIG = 45000
    ACKS_CONFIG = 'all'

    WXCC_DB_HOST = f"agentburnout-aurora.{os.environ['DC']}.ciscoccservice.com"
    DB_NAME = "saaagentburnout"
    DB_PORT = "5432"
    PROMETHEUS_PORT=9090

    KIBANA_INTERACTION_ID_FIELD_NAME = "interaction_id"
    KIBANA_AGENT_ID_FIELD_NAME = "agent_id"
    KIBANA_ORG_ID_FIELD_NAME = "org_id"
