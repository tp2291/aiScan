import os


class AppConstants:
    DC = os.getenv('datacenter', 'devus1')
    PROD_DC = 'produs1'
    VAULT_DB_CRED_FILE_PATH = "/vault/secrets/aurora-creds.ini"
    WXCC_DB_HOST = f"agentburnout-aurora.{DC}.ciscoccservice.com"
    DB_NAME = "saaagentburnout"
    DB_PORT = "5432"
