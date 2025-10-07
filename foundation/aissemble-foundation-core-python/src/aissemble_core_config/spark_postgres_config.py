###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###
from krausening.properties import PropertyManager


class SparkRDBMSConfig:
    """
    Configurations for PySpark Relational Database Management System support.
    """

    DEFAULT_JDBC_URL = "jdbc:postgresql://postgres:5432/db"
    DEFAULT_JDBC_DRIVER = "org.postgresql.Driver"
    DEFAULT_USER = "postgres"
    DEFAULT_PASSWORD = "password"

    def __init__(self):
        self.properties = PropertyManager.get_instance().get_properties(
            "spark-rdbms.properties"
        )

    def jdbc_url(self):
        """
        The JDBC URL for the database connection.
        """
        return self.properties.getProperty(
            "jdbc.url", SparkRDBMSConfig.DEFAULT_JDBC_URL
        )

    def jdbc_driver(self):
        """
        The JDBC driver class name.
        """
        return self.properties.getProperty(
            "jdbc.driver", SparkRDBMSConfig.DEFAULT_JDBC_DRIVER
        )

    def user(self):
        """
        The RDBMS user.
        """
        return self.properties.getProperty("jdbc.user", SparkRDBMSConfig.DEFAULT_USER)

    def password(self):
        """
        The password for the RDBMS user.
        """
        return self.properties.getProperty(
            "jdbc.password", SparkRDBMSConfig.DEFAULT_PASSWORD
        )
