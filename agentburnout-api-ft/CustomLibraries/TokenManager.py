import json
import datetime
import configparser
import requests
from robot.api.deco import keyword, library
from agentburnout_exception import AgentburnoutException
import AppValues
from config_helper import ConfigHelper
from robot.libraries.BuiltIn import BuiltIn


@library
class TokenManager(object):

    def __init__(self):
        self.selLib = BuiltIn().get_library_instance("SeleniumLibrary")
        self.builtIn = BuiltIn().get_library_instance("BuiltIn")
        self.token = ""
        self.props = {}
        self.generated_time = datetime.datetime.now()

    @keyword
    def token_manager(self):
        self.get_properties_from_vault()
        self.generate_token()

    def get_properties_from_vault(self):
        self.props = ConfigHelper.read_config_file(self, AppValues.VAULT_CRED_FILE_PATH)
        self.props[AppValues.SCOPE] = AppValues.SCOPE_VALUE
        self.props[AppValues.CLIENT_ID] = AppValues.CLIENT_ID_VALUE
        self.props[AppValues.CLIENT_SECRET] = AppValues.CLIENT_SECRET_VALUE
        self.props[AppValues.GRANT_TYPE] = AppValues.GRANT_TYPE_VALUE

    @keyword
    def get_token(self):
        if (datetime.datetime.now() - self.generated_time).seconds > AppValues.SECONDS_IN_HOUR:
            self.generate_token()
            self.generated_time = datetime.datetime.now()
        return self.token

    def generate_token(self):
        auth_code = self.__get_auth_code(self.props)
        self.token = self.__get_access_code(self.props, auth_code)

    @staticmethod
    def __get_auth_code(props):
        body = {AppValues.NAME: props[AppValues.NAME],
                AppValues.PASSWORD: props[AppValues.PASSWORD]}
        response = requests.post(
            f"{AppValues.BROKER_URL}/token/{props[AppValues.ORG_ID]}/v2/actions/GetBearerToken/invoke",
            json=body)
        content = json.loads(response.content.decode('utf-8'))
        if response.status_code == 200:
            return content['BearerToken']

        raise AgentburnoutException(f"Unable to get Authorization code due to - {content['error']}")

    @staticmethod
    def __get_access_code(props, auth_code):
        auth_info = {AppValues.SCOPE: props[AppValues.SCOPE],
                     AppValues.ASSERTION: auth_code,
                     AppValues.CLIENT_ID: props[AppValues.CLIENT_ID],
                     AppValues.CLIENT_SECRET: props[AppValues.CLIENT_SECRET],
                     AppValues.GRANT_TYPE: props[AppValues.GRANT_TYPE]}
        response = requests.post(f"{AppValues.BROKER_URL}/oauth2/v1/access_token", auth_info)
        content = json.loads(response.content.decode('utf-8'))
        if response.status_code == 200:
            return content['access_token']
        raise AgentburnoutException(
            f"Unable to get Authorization code due to - Error: {content['error']}, Description: {content['error_description']}")
