import logging
from logstash_formatter import LogstashFormatterV1

logger = logging.getLogger('burnout-processor')
logger.setLevel(logging.INFO)

handler = logging.StreamHandler()
formatter = LogstashFormatterV1()

handler.setFormatter(formatter)
logger.addHandler(handler)
