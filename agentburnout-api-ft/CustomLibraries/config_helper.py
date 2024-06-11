import configparser
from agentburnout_exception import AgentburnoutException
from robot.libraries.BuiltIn import BuiltIn


class ConfigHelper:

    def __init__(self):
        self.selLib = BuiltIn().get_library_instance("SeleniumLibrary")
        self.builtIn = BuiltIn().get_library_instance("BuiltIn")

    @staticmethod
    def read_config_file(self, file, key='default'):
        try:
            config_parser = configparser.ConfigParser()
            config_parser.read(file)
            config = dict(config_parser[key])
            return config
        except Exception as e:
            raise AgentburnoutException(
                "An unexpected error occurred while loading config from catalogue:") from e
