import json
from time import sleep

import requests
from robot.api.deco import keyword, library
import AppValues
from robot.libraries.BuiltIn import BuiltIn


@library
class APIOperations:

    def __init__(self):
        self.selLib = BuiltIn().get_library_instance("SeleniumLibrary")
        self.builtIn = BuiltIn().get_library_instance("BuiltIn")

    @keyword
    def get_burnout_metrics(self, access_token):
        headers = {'Authorization': 'Bearer ' + access_token}
        response = requests.get(f"{AppValues.BASE_URL}/{AppValues.AGENT_ID}/metrics?page=0&size=100", headers=headers)
        if response.status_code == 200:
            return response.content, response.status_code

    @keyword
    def verify_metrics_response(self, response):
        response = json.loads(response)
        if isinstance(response, list):
            if len(response) >= 1:
                for record in response:
                    if isinstance(record, dict):
                        if all(key in record for key in
                               ['interactionId', 'agentId', 'interactionDateTime', 'burnoutIndex', 'actionDateTime',
                                'actionType']):
                            return True
                        else:
                            self.builtIn.log("Invalid, Records not in appropriate format")
                            return False
            else:
                self.builtIn.log("No Records Found")
                return True

    @keyword
    def check_break_eligibility(self, access_token):
        headers = {'Authorization': 'Bearer ' + access_token}
        response = requests.get(f"{AppValues.BASE_URL}/breaks?agentId={AppValues.AGENT_ID}", headers=headers)
        if response.status_code == 200:
            return response.content

    @keyword
    def verify_break_eligibility_response(self, response):
        response = json.loads(response)
        if isinstance(response, dict):
            if all(key in response for key in ['breakEligibility', 'breakIneligibilityReason']):
                return True
        else:
            self.builtIn.log("Invalid, Record not in appropriate format")
            return False

    @keyword
    def give_break_to_agent(self, access_token):
        headers = {'Authorization': 'Bearer ' + access_token}
        response = requests.post(f"{AppValues.BASE_URL}/breaks?interactionId={AppValues.INTERACTION_ID}",
                                 headers=headers)
        if response.status_code == 200:
            return response.content

    @keyword
    def agent_subscription(self, access_token):
        headers = {'Authorization': 'Bearer ' + access_token}
        body = {'agentId': AppValues.AGENT_ID, 'orgId': AppValues.ORG_ID_VALUE, 'subscriptionId': 's1'}
        url = f"{AppValues.BASE_URL}/subscriptions"
        self.builtIn.log_to_console("\n"+url)
        response = requests.post(url, json=body, headers=headers)
        self.builtIn.log_to_console(response.status_code)
        if response.status_code == 200:
            return response.content, body

    @keyword
    def get_agent_config(self, access_token):
        headers = {'Authorization': 'Bearer ' + access_token}
        response = requests.get(f"{AppValues.BASE_URL}/configs?orgId={AppValues.ORG_ID_VALUE}", headers=headers)
        self.builtIn.log(response.content)
        if response.status_code == 200:
            return response.content

    @keyword
    def post_agent_config(self, access_token):
        headers = {'Authorization': 'Bearer ' + access_token}
        body = {"agentId": AppValues.AGENT_ID, "orgId": AppValues.ORG_ID_VALUE, "automatedBreaksStatus": True}
        response = requests.post(f"{AppValues.BASE_URL}/configs", json=body, headers=headers)
        self.builtIn.log(response.content)
        if response.status_code == 200:
            return response.content, body

    @keyword
    def verify_give_break_response(self, response):
        response = json.loads(response)
        if isinstance(response, dict):
            if all(key in response for key in
                   ['interactionId', 'interactionDateTime', 'agentId', 'orgId', 'agentSessionId', 'burnoutIndex',
                    'actionTaken', 'actionDateTime', 'actionType']):
                return True
            else:
                return False
        else:
            self.builtIn.log("Invalid, Record not in appropriate format")
            return False

    @keyword
    def verify_subscription_response(self, response, body):
        response = json.loads(response)
        self.builtIn.log_to_console(response)
        if isinstance(response, dict):
            if response == body:
                return True
            else:
                return False
        else:
            return False

    @keyword
    def verify_agent_config(self, response, *body):
        response = json.loads(response)
        if isinstance(response, dict):
            if all(key in response for key in ['agentId', 'orgId', 'automatedBreaksStatus']):
                if str(body[0]["automatedBreaksStatus"]).lower() == str(response["automatedBreaksStatus"]).lower():
                    return True
            else:
                return False
        elif isinstance(response, list):
            for item in response:
                if all(key in item for key in ['agentId', 'automatedBreaksStatus']):
                    if isinstance(item["automatedBreaksStatus"], bool):
                        return True
                else:
                    return False
        else:
            return False

    @keyword
    def get_burnout_metrics_and_verify_current_interaction(self, access_token):
        headers = {'Authorization': 'Bearer ' + access_token}
        response = requests.get(f"{AppValues.BASE_URL}/{AppValues.AGENT_ID}/metrics?page=0&size=100", headers=headers)
        if response.status_code != 200:
            self.builtIn.log("Error occurred during the request!")
            return None
        response = response.content
        response = json.loads(response)
        if isinstance(response, list):
            if len(response) >= 1:
                for record in response:
                    if isinstance(record, dict):
                        if all(key in record for key in
                               ['interactionId', 'agentId', 'interactionDateTime', 'burnoutIndex', 'actionDateTime',
                                'actionType']):
                            self.builtIn.log(record)
                            if record['interactionId'] != AppValues.INTERACTION_ID:
                                continue
                            else:
                                burnoutIndex = record["burnoutIndex"]
                                return burnoutIndex
                        else:
                            self.builtIn.log("Invalid, Records not in appropriate format")
                            return None
            else:
                self.builtIn.log("No Records Found")
                return None