import configparser

from constants.app_constants import AppConstants
from exceptions.burnout_processor_exception import BurnoutProcessorException


class ConfigHelper:

    @staticmethod
    def read_config_file(file, key='default'):
        try:
            config_parser = configparser.ConfigParser()
            config_parser.read(file)
            config = dict(config_parser[key])
            return config
        except Exception as e:
            raise BurnoutProcessorException(
                "An unexpected error occurred while loading config from catalogue:") from e

    @staticmethod
    def get_db_config_from_vault():
        db_config = ConfigHelper.read_config_file(AppConstants.VAULT_DB_CRED_FILE_PATH)
        db_config.update({
            'host': AppConstants.WXCC_DB_HOST,
            'dbname': AppConstants.DB_NAME,
            'port': AppConstants.DB_PORT
        })
        return db_config
