###
# #%L
# AIOps Foundation::AIOps Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from krausening.properties import PropertyManager


class SparkPostgresConfig:
    """
    Configurations for PySpark Postgres support.
    """

    DEFAULT_JDBC_URL = "jdbc:postgresql://postgres:5432/db"
    DEFAULT_JDBC_DRIVER = "org.postgresql.Driver"
    DEFAULT_POSTGRES_USER = "postgres"
    DEFAULT_POSTGRES_PASSWORD = "password"

    def __init__(self):
        self.properties = PropertyManager.get_instance().get_properties(
            "spark-postgres.properties"
        )

    def jdbc_url(self):
        """
        The JDBC URL for the database connection.
        """
        return self.properties.getProperty(
            "jdbc.url", SparkPostgresConfig.DEFAULT_JDBC_URL
        )

    def jdbc_driver(self):
        """
        The JDBC driver class name.
        """
        return self.properties.getProperty(
            "jdbc.driver", SparkPostgresConfig.DEFAULT_JDBC_DRIVER
        )

    def postgres_user(self):
        """
        The Postgres user.
        """
        return self.properties.getProperty(
            "postgres.user", SparkPostgresConfig.DEFAULT_POSTGRES_USER
        )

    def postgres_password(self):
        """
        The password for the Postgres user.
        """
        return self.properties.getProperty(
            "postgres.password", SparkPostgresConfig.DEFAULT_POSTGRES_PASSWORD
        )
