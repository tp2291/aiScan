import json
import requests
from robot.api.deco import keyword, library
from robot.libraries.BuiltIn import BuiltIn

import AppValues
import CredValues
from TokenManager import TokenManager


@library
class AuthHandling:

    @keyword
    def get_bearer_token(self):
        body = {AppValues.NAME: CredValues.name, AppValues.PASSWORD: CredValues.password}
        # body = {AppValues.NAME: uname, AppValues.PASSWORD: password}
        response = requests.post(AppValues.GET_BEARER_TOKEN, json=body)
        content = json.loads(response.content.decode('utf-8'))
        if response.status_code == 200:
            return content['BearerToken']

    @keyword
    def get_access_token(self, auth_code):
        auth_info = {
            AppValues.SCOPE: AppValues.SCOPE_VALUE,
            AppValues.ASSERTION: auth_code,
            AppValues.CLIENT_ID: AppValues.CLIENT_ID_VALUE,
            AppValues.CLIENT_SECRET: AppValues.CLIENT_SECRET_VALUE,
            AppValues.GRANT_TYPE: AppValues.GRANT_TYPE_VALUE
        }
        response = requests.post(AppValues.GET_ACCESS_TOKEN, auth_info)
        content = json.loads(response.content.decode('utf-8'))
        if response.status_code == 200:
            return content['access_token']

    # @keyword
    # def wxccAuthTokenManager(self):
    #     token_manager = TokenManager()
    #     token = token_manager.get_token()
    #     return token

