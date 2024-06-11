from redis import Redis

from utils.config_helper import ConfigHelper
from constants.app_constants import AppConstants
from logging_config import logger
from rediscluster import RedisCluster


def redis_connect():
    props = ConfigHelper.read_configs_from_file(AppConstants.VAULT_REDIS_FILE_PATH)

    client = RedisCluster(host=AppConstants.REDIS_HOST,
                          port=AppConstants.REDIS_PORT,
                          password=props["password"].data,
                          ssl=True,
                          decode_responses=True, skip_full_coverage_check=True)


    if client.ping():
        logger.info('Connected to Redis!')
    return client