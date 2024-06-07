###
# #%L
# aiSSEMBLE::Foundation::Core Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import os
from sqlalchemy import create_engine
from sqlalchemy.pool import QueuePool
from sqlalchemy.orm import sessionmaker


class RDBMSConnectionPool:
    """
    Connection pool for RDBMS (Relational Database Management Systems) using SQLAlchemy.
    """

    def __init__(self, max_connections=5):
        """
        Initializes a connection pool object for connecting to an RDBMS server.

        :param max_connections: Maximum number of connections in the pool (default: 5)
        The `max_connections` parameter specifies the maximum number of connections
        that can be established with the RDBMS server.
        """
        self.max_connections = max_connections

    def get_connection(self, db_url):
        """
        The get_connection function retrieves a connection pool object for connecting to an RDBMS server.
        :param db_url: Database URL specifying the connection details for the RDBMS server.
        The `db_url` parameter should be a string that provides the necessary
        connection details to establish a connection with the RDBMS server.
        For example, depending on the databse you choose:
            PostgreSQL: 'postgresql://username:password@host:port/database'
            MySQL: 'mysql://username:password@host:port/database'
            SQLite: 'sqlite:///db.sqlite'
        """
        engine = create_engine(
            db_url, poolclass=QueuePool, pool_size=self.max_connections
        )
        Session = sessionmaker(bind=engine)
        session = Session()
        return session
