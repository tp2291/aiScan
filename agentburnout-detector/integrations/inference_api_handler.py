import json
import requests
from constants.app_constants import AppConstants
from logging_config import logger
from prometheus_client import Gauge, Counter


GET_INFERENCE_CODES = Counter("saa_ab_detector_get_inference_codes",
                              "Counter for various response codes received when getting inferences from the model",
                              ["app_name", "orgId", "STATUS_CODE"])


def get_inference(org_id, agent_id, interaction_id, recording_time, agent_sentiment, caller_sentiment):
    request_body = json.dumps({"orgId": org_id,
                               "agentId": agent_id,
                               "agentSentiment": agent_sentiment,
                               "callerSentiment": caller_sentiment,
                               "interactionId": interaction_id,
                               "recordingTime": recording_time})
    headers = {'Content-type': 'application/json'}
    response = requests.post(url=f"{AppConstants.INFERENCE_APP_URL}/agentburnout/inference", headers=headers,
                             json=json.loads(request_body))
    response_json = json.loads(response.content)
    if response.status_code == 200:
        GET_INFERENCE_CODES.labels("agentburnout-detector", org_id, "200").inc()
        logger.info("Agentburnout inference success for interaction_id: "+interaction_id,
                    extra={AppConstants.KIBANA_ORG_ID_FIELD_NAME: org_id,
                           AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME: interaction_id,
                           AppConstants.KIBANA_AGENT_ID_FIELD_NAME: agent_id})
        return response_json["burnout_index"], "Success"
    else:
        if response.status_code >= 400 and response.status_code < 500:
            GET_INFERENCE_CODES.labels("agentburnout-detector", org_id, "4xx").inc()
        else:
            GET_INFERENCE_CODES.labels("agentburnout-detector", org_id, "5xx").inc()
        return -1, response_json["message"]
