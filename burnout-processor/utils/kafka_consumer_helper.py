from logging_config import logger
from logging_fields import logging_agent_id, logging_interaction_id, logging_org_id
from constants.app_constants import AppConstants


def log_assignment(consumer, partitions):
    logger.info('Consumer %s assigned to partitions: %s', consumer, partitions, extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id, AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id, AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
