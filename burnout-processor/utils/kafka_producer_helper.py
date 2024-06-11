from logging_config import logger
from logging_fields import logging_agent_id, logging_interaction_id, logging_org_id
from constants.app_constants import AppConstants


def delivery_callback(err, msg):
    if err:
        logger.info('ERROR: Message failed delivery: {}'.format(err), extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id, AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id, AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
    else:
        logger.info("Produced event to topic {topic}: value = {value:12}".format(
            topic=msg.topic(), value=msg.value().decode('utf-8')), extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: logging_org_id, AppConstants.KIBANA_AGENT_ID_FIELD_NAME: logging_agent_id, AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: logging_interaction_id})
