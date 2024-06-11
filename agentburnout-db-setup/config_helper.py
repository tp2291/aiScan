import configparser

from app_constants import AppConstants


class ConfigHelper:

    @staticmethod
    def read_config_file(file, key='default'):
        config_parser = configparser.ConfigParser()
        config_parser.read(file)
        config = dict(config_parser[key])
        return config

    @staticmethod
    def get_db_config_from_vault():
        db_config = ConfigHelper.read_config_file(AppConstants.VAULT_DB_CRED_FILE_PATH)
        db_config.update({
            'host': AppConstants.WXCC_DB_HOST,
            'dbname': AppConstants.DB_NAME,
            'port': AppConstants.DB_PORT
        })
        return db_config
