#!/bin/sh

###
# #%L
# aiSSEMBLE::Extensions::Docker::Spark Infrastructure
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

#Create events dir that's usually mounted at runtime
mkdir /tmp/spark-events
$SPARK_HOME/sbin/start-history-server.sh &

#Switch to Embedded Derby DB for Thrift Server test
sed -i 's/jdbc:mysql:\/\/hive-metastore-db:3306\/metastore?createDatabaseIfNotExist=true&amp;allowPublicKeyRetrieval=true&amp;useSSL=false/jdbc:derby:\/tmp\/metastore;create=true/' $SPARK_HOME/conf/hive-site.xml
sed -i 's/com.mysql.cj.jdbc.Driver/org.apache.derby.jdbc.EmbeddedDriver/' $SPARK_HOME/conf/hive-site.xml
$SPARK_HOME/sbin/start-thriftserver.sh
