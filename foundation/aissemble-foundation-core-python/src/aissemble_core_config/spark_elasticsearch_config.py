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


class SparkElasticsearchConfig:
    """
    Configurations for PySpark Elasticsearch support.
    See the following documentation for more details:
    https://www.elastic.co/guide/en/elasticsearch/hadoop/master/configuration.html
    """

    """
    TODO: Need to unify the elasticsearch python implementation with the java implementation-- currently a difference in form
    and an abundance of code duplication in generated files results in not being able to kill off this element of spark
    configuration.
    Ticket: [PLACEHOLDER]
    """
    SPARK_ES_NODES = "spark.es.nodes"
    SPARK_ES_PORT = "spark.es.port"
    ES_NODES_PATH_PREFIX = "es.nodes.path.prefix"
    ES_NODES_DISCOVERY = "es.nodes.discovery"
    ES_NODES_CLIENT_ONLY = "es.nodes.client.only"
    ES_NODES_DATA_ONLY = "es.nodes.data.only"
    ES_NODES_INGEST_ONLY = "es.nodes.ingest.only"
    ES_NODES_WAN_ONLY = "es.nodes.wan.only"
    ES_HTTP_TIMEOUT = "es.http.timeout"
    ES_HTTP_RETRIES = "es.http.retries"
    ES_NET_HTTP_AUTH_USER = "es.net.http.auth.user"
    ES_NET_HTTP_AUTH_PASS = "es.net.http.auth.pass"

    def __init__(self) -> None:
        self.properties = PropertyManager.get_instance().get_properties(
            "spark-elasticsearch.properties"
        )

    def spark_es_nodes(self) -> str:
        """
        List of Elasticsearch nodes to connect to.
        """
        value = self.properties[SparkElasticsearchConfig.SPARK_ES_NODES]
        return value if value is not None else "localhost"

    def spark_es_port(self) -> str:
        """
        HTTP/REST port used for connecting to Elasticsearch.
        """
        value = self.properties[SparkElasticsearchConfig.SPARK_ES_PORT]
        return value if value is not None else "9200"

    def es_nodes_path_prefix(self) -> str:
        """
        Prefix to add to all requests made to Elasticsearch.
        """
        return self.properties[SparkElasticsearchConfig.ES_NODES_PATH_PREFIX]

    def es_nodes_discovery(self) -> str:
        """
        Whether to discover the nodes within the Elasticsearch cluster or only to
        use the ones given in es.nodes for metadata queries.
        """
        return self.properties[SparkElasticsearchConfig.ES_NODES_DISCOVERY]

    def es_nodes_client_only(self) -> str:
        """
        Whether to use Elasticsearch client nodes (or load-balancers).
        """
        return self.properties[SparkElasticsearchConfig.ES_NODES_CLIENT_ONLY]

    def es_nodes_data_only(self) -> str:
        """
        Whether to use Elasticsearch data nodes only.
        """
        return self.properties[SparkElasticsearchConfig.ES_NODES_DATA_ONLY]

    def es_nodes_ingest_only(self) -> str:
        """
        Whether to use Elasticsearch ingest nodes only.
        """
        return self.properties[SparkElasticsearchConfig.ES_NODES_INGEST_ONLY]

    def es_nodes_wan_only(self) -> str:
        """
        Whether the connector is used against an Elasticsearch instance in a
        cloud/restricted environment over the WAN, such as Amazon Web Services.
        """
        return self.properties[SparkElasticsearchConfig.ES_NODES_WAN_ONLY]

    def es_http_timeout(self) -> str:
        """
        Timeout for HTTP/REST connections to Elasticsearch.
        """
        return self.properties[SparkElasticsearchConfig.ES_HTTP_TIMEOUT]

    def es_http_retries(self) -> str:
        """
        Number of retries for establishing a (broken) http connection.
        """
        return self.properties[SparkElasticsearchConfig.ES_HTTP_RETRIES]

    def es_net_http_auth_user(self) -> str:
        """
        Basic Authentication user name.
        """
        return self.properties[SparkElasticsearchConfig.ES_NET_HTTP_AUTH_USER]

    def es_net_http_auth_pass(self) -> str:
        """
        Basic Authentication password.
        """
        return self.properties[SparkElasticsearchConfig.ES_NET_HTTP_AUTH_PASS]

    def get_es_configs(self) -> dict:
        """
        Convenience method to return all Elasticsearch-related configurations.
        """
        configs = dict()
        # For whatever reason these two configs are named differently in spark vs pyspark.
        # Using the spark config names for the properties file to keep it consistent,
        # but will set the correct pyspark config name here.
        configs["es.nodes"] = self.spark_es_nodes()
        configs["es.port"] = self.spark_es_port()

        self.add_optional_config(
            configs,
            SparkElasticsearchConfig.ES_NODES_PATH_PREFIX,
            self.es_nodes_path_prefix(),
        )
        self.add_optional_config(
            configs,
            SparkElasticsearchConfig.ES_NODES_DISCOVERY,
            self.es_nodes_discovery(),
        )
        self.add_optional_config(
            configs,
            SparkElasticsearchConfig.ES_NODES_CLIENT_ONLY,
            self.es_nodes_client_only(),
        )
        self.add_optional_config(
            configs,
            SparkElasticsearchConfig.ES_NODES_DATA_ONLY,
            self.es_nodes_data_only(),
        )
        self.add_optional_config(
            configs,
            SparkElasticsearchConfig.ES_NODES_INGEST_ONLY,
            self.es_nodes_ingest_only(),
        )
        self.add_optional_config(
            configs,
            SparkElasticsearchConfig.ES_NODES_WAN_ONLY,
            self.es_nodes_wan_only(),
        )
        self.add_optional_config(
            configs, SparkElasticsearchConfig.ES_HTTP_TIMEOUT, self.es_http_timeout()
        )
        self.add_optional_config(
            configs, SparkElasticsearchConfig.ES_HTTP_RETRIES, self.es_http_retries()
        )
        self.add_optional_config(
            configs,
            SparkElasticsearchConfig.ES_NET_HTTP_AUTH_USER,
            self.es_net_http_auth_user(),
        )
        self.add_optional_config(
            configs,
            SparkElasticsearchConfig.ES_NET_HTTP_AUTH_PASS,
            self.es_net_http_auth_pass(),
        )

        return configs

    def add_optional_config(
        self, configs: dict, config_key: str, config_value: str
    ) -> None:
        if config_value is not None:
            configs[config_key] = config_value
