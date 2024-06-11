def delivery_callback(err, msg):
    if err:
        print('ERROR: Message failed delivery: {}'.format(err))
    else:
        print("Produced event to topic {topic}: value = {value:12}".format(
            topic=msg.topic(), value=msg.value().decode('utf-8')))


def log_assignment(consumer, partitions):
    print('Consumer %s assigned to partitions: %s', consumer, partitions)
