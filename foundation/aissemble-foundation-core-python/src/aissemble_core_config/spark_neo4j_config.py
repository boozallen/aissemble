###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from krausening.properties import PropertyManager
from typing import Dict


class SparkNeo4jConfig:
    """
    Configurations for Spark Neo4j support.

    See Neo4j Spark Connector documentation for more information:
    https://neo4j.com/docs/spark/current/configuration/
    """

    URL = "url"
    AUTHENTICATION_TYPE = "authentication.type"
    AUTHENTICATION_BASIC_USERNAME = "authentication.basic.username"
    AUTHENTICATION_BASIC_PASSWORD = "authentication.basic.password"
    AUTHENTICATION_KERBEROS_TICKET = "authentication.kerberos.ticket"
    AUTHENTICATION_CUSTOM_PRINCIPAL = "authentication.custom.principal"
    AUTHENTICATION_CUSTOM_CREDENTIALS = "authentication.custom.credentials"
    AUTHENTICATION_CUSTOM_REALM = "authentication.custom.realm"
    ENCRYPTION_ENABLED = "encryption.enabled"
    ENCRYPTION_TRUST_STRATEGY = "encryption.trust.strategy"
    ENCRYPTION_CA_CERTIFICATE_PATH = "encryption.ca.certificate.path"
    CONNECTION_MAX_LIFETIME_MSECS = "connection.max.lifetime.msecs"
    CONNECTION_LIVENESS_TIMEOUT_MSECS = "connection.liveness.timeout.msecs"
    CONNECTION_ACQUISITION_TIMEOUT_MSECS = "connection.acquisition.timeout.msecs"
    CONNECTION_TIMEOUT_MSECS = "connection.timeout.msecs"

    NEO4J_FORMAT = "org.neo4j.spark.DataSource"
    LABELS_OPTION = "labels"

    def __init__(self) -> None:
        self.properties = PropertyManager.get_instance().get_properties(
            "spark-neo4j.properties"
        )

    def _get_property_value(self, property: str, default_value: str = None) -> str:
        value = self.properties[property]
        return value if value is not None else default_value

    def url(self) -> str:
        """
        The url of the Neo4j instance to connect to.
        """
        return self._get_property_value(SparkNeo4jConfig.URL, "bolt://neo4j:7687")

    def authentication_type(self) -> str:
        """
        The authentication method to be used: none, basic, kerberos, custom.
        """
        return self._get_property_value(SparkNeo4jConfig.AUTHENTICATION_TYPE, "basic")

    def authentication_basic_username(self) -> str:
        """
        Username to use for basic authentication type.
        """
        return self._get_property_value(
            SparkNeo4jConfig.AUTHENTICATION_BASIC_USERNAME, "neo4j"
        )

    def authentication_basic_password(self) -> str:
        """
        Password to use for basic authentication type.
        """
        return self._get_property_value(
            SparkNeo4jConfig.AUTHENTICATION_BASIC_PASSWORD, "p455w0rd"
        )

    def authentication_kerberos_ticket(self) -> str:
        """
        The kerberos authentication ticket for kerberos authentication type.
        """
        return self._get_property_value(SparkNeo4jConfig.AUTHENTICATION_KERBEROS_TICKET)

    def authentication_custom_principal(self) -> str:
        """
        Principal for custom authentication type.
        """
        return self._get_property_value(
            SparkNeo4jConfig.AUTHENTICATION_CUSTOM_PRINCIPAL
        )

    def authentication_custom_credentials(self) -> str:
        """
        Credentials for custom authentication type.
        """
        return self._get_property_value(
            SparkNeo4jConfig.AUTHENTICATION_CUSTOM_CREDENTIALS
        )

    def authentication_custom_realm(self) -> str:
        """
        Realm for custom authentication type.
        """
        return self._get_property_value(SparkNeo4jConfig.AUTHENTICATION_CUSTOM_REALM)

    def encryption_enabled(self) -> str:
        """
        Whether encryption should be enabled.
        """
        return self._get_property_value(SparkNeo4jConfig.ENCRYPTION_ENABLED)

    def encryption_trust_strategy(self) -> str:
        """
        Certificate trust strategy when encryption is enabled.
        """
        return self._get_property_value(SparkNeo4jConfig.ENCRYPTION_TRUST_STRATEGY)

    def encryption_ca_certificate_path(self) -> str:
        """
        Certificate path when encryption is enabled.
        """
        return self._get_property_value(SparkNeo4jConfig.ENCRYPTION_CA_CERTIFICATE_PATH)

    def connection_max_lifetime_msecs(self) -> str:
        """
        Connection lifetime in milliseconds.
        """
        return self._get_property_value(SparkNeo4jConfig.CONNECTION_MAX_LIFETIME_MSECS)

    def connection_liveness_timeout_msecs(self) -> str:
        """
        Liveness check timeout in milliseconds.
        """
        return self._get_property_value(
            SparkNeo4jConfig.CONNECTION_LIVENESS_TIMEOUT_MSECS
        )

    def connection_acquisition_timeout_msecs(self) -> str:
        """
        Connection acquisition timeout in milliseconds.
        """
        return self._get_property_value(
            SparkNeo4jConfig.CONNECTION_ACQUISITION_TIMEOUT_MSECS
        )

    def connection_timeout_msecs(self) -> str:
        """
        Connection timeout in milliseconds.
        """
        return self._get_property_value(SparkNeo4jConfig.CONNECTION_TIMEOUT_MSECS)

    def get_spark_options(self) -> Dict[str, str]:
        """
        Returns the options to pass to spark for writing to and reading from neo4j.
        """
        options = {
            SparkNeo4jConfig.URL: self.url(),
            SparkNeo4jConfig.AUTHENTICATION_TYPE: self.authentication_type(),
            SparkNeo4jConfig.AUTHENTICATION_BASIC_USERNAME: self.authentication_basic_username(),
            SparkNeo4jConfig.AUTHENTICATION_BASIC_PASSWORD: self.authentication_basic_password(),
            SparkNeo4jConfig.AUTHENTICATION_KERBEROS_TICKET: self.authentication_kerberos_ticket(),
            SparkNeo4jConfig.AUTHENTICATION_CUSTOM_PRINCIPAL: self.authentication_custom_principal(),
            SparkNeo4jConfig.AUTHENTICATION_CUSTOM_CREDENTIALS: self.authentication_custom_credentials(),
            SparkNeo4jConfig.AUTHENTICATION_CUSTOM_REALM: self.authentication_custom_realm(),
            SparkNeo4jConfig.ENCRYPTION_ENABLED: self.encryption_enabled(),
            SparkNeo4jConfig.ENCRYPTION_TRUST_STRATEGY: self.encryption_trust_strategy(),
            SparkNeo4jConfig.ENCRYPTION_CA_CERTIFICATE_PATH: self.encryption_ca_certificate_path(),
            SparkNeo4jConfig.CONNECTION_MAX_LIFETIME_MSECS: self.connection_max_lifetime_msecs(),
            SparkNeo4jConfig.CONNECTION_LIVENESS_TIMEOUT_MSECS: self.connection_liveness_timeout_msecs(),
            SparkNeo4jConfig.CONNECTION_ACQUISITION_TIMEOUT_MSECS: self.connection_acquisition_timeout_msecs(),
            SparkNeo4jConfig.CONNECTION_TIMEOUT_MSECS: self.connection_timeout_msecs(),
        }

        return {key: value for key, value in options.items() if value is not None}
