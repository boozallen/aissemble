## aiSSMEBLE&trade; Foundation Python Messaging Client (UNDER CONSTRUCTION)

[![PyPI](https://img.shields.io/pypi/v/aissemble-foundation-messaging-python?logo=python&logoColor=gold)](https://pypi.org/project/aissemble-foundation-messaging-python/)
![PyPI - Python Version](https://img.shields.io/pypi/pyversions/aissemble-foundation-messaging-python?logo=python&logoColor=gold)
![PyPI - Wheel](https://img.shields.io/pypi/wheel/aissemble-foundation-messaging-python?logo=python&logoColor=gold)

# Overview
aiSSEMBLE&trade;'s python messaging module provides an interface through which python pipelines can leverage messaging's
features. This is done through the MessagingClient class, the details of which are discussed below, along with
examples of its use.

# Subscribing
The messaging client can pass along a subscription request to the service to subscribe to a given topic. The message client can start receiving messages after subscription. However, any messages before subscription will be ignored. To do this, you will need to tell the service which topic you want to subscribe to, provide a callback function if you would like to execute one, and an acknowledgement strategy for handling messages on the topic.

```python
def my_pipeline_function()
client = MessagingClient()
ack_strategy = AckStrategy.<STRATEGY>
client.subscribe("topic_name", my_callback_func(), ack_strategy)
```

# Sending
The messaging client can send a message to a given topic name through the service and will receive back a future object to handle that messages receipt. To do this, you can simply provide a topic name and a message object containing the text string for your message, like the following example.

```python
c = MessagingClient()
message = Message()
message.set_payload("test message")
result = c.queueForSend("TopicA", message)
```

# Receiving
The message client can receive a message to a given topic name through the service after subscribe to the topic. However, any messages to the given topic before subscription is made will be ignored.

To do this, you can define a Callback function and subscribe to the topic with the Callback function created, like the following example:

Note: the client can must ack or nack the message received. If there are any exception thrown during the process time, the exception will be handled by the service and the message will be nacked.
```python
from aissemble_messaging import ProcessMessageError, MessagingClient, AckStrategy, Message, Callback
import threading

def receive_message(msg: Message, topic):
    message_received = msg.get_payload()
    print("Received message from topic: {}, message: {}".format(topic, message_received))
    
    # to nack a message, return msg.nack(reason)
    # ex:
    if "nacked" in message_received :
        return msg.nack("receiver nacks the message: " + message_received)
    
    # when raise an Exception for any issue, message will be nacked
    # ex:
    if "problem" in message_received:
        raise Exception("error while processing message: " + message_received)

    # to ack a message, return msg.ack()
    return msg.ack()

def demo_topic_callback(msg: Message):
    receive_message(msg, "demoTopic")
    
def subscribe_to_topic(consumer, callback_func):
    consumer.subscribe("demoTopic", callback_func, AckStrategy.MANUAL)
    is_subscribed = consumer.isTopicSubscribed("demoTopic")
    while is_subscribed:
        pass


# Create MessageClient with default and the messaging configration
client = MessagingClient(properties_location = os.path.join("microprofile-config.properties"))

sb_thread = threading.Thread(target=subscribe_to_topic,args=[client, Callback(demo_topic_callback)])
sb_thread.start()
```

# Misc.
The testing strategy for the client's integration tests is to run the messaging service's jar through py4j's launch_gateway() function, and then interact with it through the MessagingClient class. In addition to running the jar, the launch_gateway() function has some quality of life features for testing. In order to include logging from the jvm in the python test output, add the following parameters to the function, and run the behave tests with the --no-capture flag.

```python
redirect_stdout=sys.stdout,
redirect_stderr=sys.stdout
```

Please see the [aiSSEMBLE Messaging documentation](https://boozallen.github.io/aissemble/aissemble/current/messaging-details.html)
for additional general information.