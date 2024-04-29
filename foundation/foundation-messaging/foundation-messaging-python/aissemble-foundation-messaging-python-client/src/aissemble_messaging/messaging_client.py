###
# #%L
# aiSSEMBLE::Foundation::Messaging Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from .message import Message
from .future import Future
from .ack_strategy import AckStrategy
from .exception.topic_not_supported_error import TopicNotSupportedError
from py4j.java_gateway import JavaGateway, GatewayParameters, CallbackServerParameters
from krausening.logging import LogManager
from collections.abc import Callable
from threading import RLock
import os
import sys
from py4j.java_gateway import launch_gateway
from . import RESDIR


class MessagingClient:
    """
    The entry-point to the API that initiates enqueuing and subscribing to messaging topics. Creates Futures to
     track message promises from the service.
    """

    logger = LogManager.get_instance().get_logger("MessagingClient")

    def __init__(self, port=None, properties_location=None):
        self.properties_location = properties_location
        self.load_configs(properties_location)
        if not port:
            self.service_port = self.start_service_jvm()
        else:
            self.service_port = port
        self.callback_server_started = False
        self.lock = RLock()
        # start a Gateway without python callback server
        self.gateway = JavaGateway(
            gateway_parameters=GatewayParameters(port=self.service_port)
        )
        self.service_handle = (
            self.gateway.jvm.com.boozallen.aissemble.messaging.python.MessagingService.getInstance()
        )

    def queueForSend(self, topic: str, msg: Message) -> Future:
        """
        Tells the service to send a given message.
        """
        try:
            result = self.service_handle.publish(topic, msg.get_payload())
            future = Future(result)
            return future
        except Exception as e:
            if self.is_java_exception(e, "TopicNotSupportedError"):
                raise TopicNotSupportedError(topic)
            else:
                raise e

    def subscribe(self, topic: str, callback: Callable, ack_strategy: AckStrategy):
        """
        Attempts to subscribe to a given topic. Returns nothing on success, or throws a NoTopicFoundError if the
        topic cannot be found in the service.
        """
        try:
            # set callback server for subscription
            with self.lock:
                self.start_callback_server()
            self.service_handle.subscribe(topic, callback, ack_strategy.value)
        except Exception as e:
            if self.is_java_exception(e, "TopicNotSupportedError"):
                raise TopicNotSupportedError(topic)
            else:
                raise e

    def isTopicSubscribed(self, topic: str) -> bool:
        """
        Returns true if the client is subscribed to the topic, and false if the client is not subscribed
        to the topic. Throws a TopicNotSupportedError if the topic cannot be found in the service.
        """
        try:
            return self.service_handle.isTopicSubscribed(topic)
        except Exception:
            raise TopicNotSupportedError(topic)

    def getSubscription(self, topic: str) -> object:
        """
        Returns Java Subscription object for the given topic
        Throws a TopicNotSupportedError if there is no subscription found for the given topic in the service.
        """
        try:
            return self.service_handle.getSubscription(topic)
        except Exception as e:
            if self.is_java_exception(e, "TopicNotSupportedError"):
                raise TopicNotSupportedError(topic)
            else:
                raise e

    def shutdown(self):
        """
        Shutdown gateway to releases all objects that were referenced by this Gateway and
        also shuts down the CallbackClient.
        """
        self.gateway.shutdown()

    def is_java_exception(self, exception: object, exception_name: str) -> bool:
        """
        Returns true if the given exception is a java exception with given exception name
        :param exception the exception to be examined
        :param exception_name the expected exception name
        """
        return (
            hasattr(exception, "java_exception")
            and exception_name == exception.java_exception.getClass().getSimpleName()
        )

    def start_callback_server(self):
        """
        Start a python callback server
        """
        if self.callback_server_started == False:
            # start the callback server with the dynamic callback_port
            self.gateway.start_callback_server(
                CallbackServerParameters(port=0, daemonize=True)
            )

            # retrieve the port on which the python callback server was bound to.
            callback_port = self.gateway.get_callback_server().get_listening_port()

            # tell the Java side to connect to the python callback server with the new
            # callback port. Note that we use the java_gateway_server attribute that
            # retrieves the GatewayServer instance.
            self.gateway.java_gateway_server.resetCallbackClient(
                self.gateway.java_gateway_server.getCallbackClient().getAddress(),
                callback_port,
            )

            # the port on which the python callback server was bound to.
            self.logger.info(
                "python callback server started on port: {}".format(callback_port)
            )
            self.callback_server_started = True

    def load_configs(self, file_location):
        """
        Read in the microprofile properties from the resources folder and
        write them as environment variables for the jvm. This function is
        necessary to work around a missing feature in microprofile,
        detailed here: https://github.com/eclipse/microprofile-config/issues/422
        """
        if file_location and file_location.strip():
            with open(file_location, "r") as file:
                for line in file:
                    if not line.strip() == "" and not line.startswith("#"):
                        keypair = line.split("=")
                        os.environ[keypair[0].strip()] = keypair[1].strip()

    def start_service_jvm(self):
        """
        Create a java gateway for the messaging service, including the
        dependency jars on its classpath
        :return: The service port number
        """
        path_to_jar = str(RESDIR.joinpath("classpath/*"))
        classpath = path_to_jar

        return launch_gateway(
            classpath=classpath,
            die_on_exit=True,
            redirect_stdout=sys.__stdout__,
            redirect_stderr=sys.__stderr__,
        )
