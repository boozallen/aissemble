#!/bin/sh
set -euo pipefail 

# constants

SPARK_JARS="$1/jars"
SPARK_VERSION="$2"
HADOOP_VERSION="$3"

echo "Starting Spark JAR updates for version: $SPARK_VERSION (Hadoop: $HADOOP_VERSION)"

#---
## updates Spark JARs from Maven Central
## @param: list of Maven coordinates (group:artifact:version:classifier)
#---
update_maven_jars() {
  echo "Updating JARs in: $SPARK_JARS"
  
  mvnjars="$1"
  temp_dir=$(mktemp -d)

  for gav in $mvnjars; do
    group=$(echo "$gav" | cut -d : -f 1)
    artifact=$(echo "$gav" | cut -d : -f 2)
    version=$(echo "$gav" | cut -d : -f 3)
    classifier=$(echo "$gav" | cut -d : -f 4 || echo "")

    if [ -n "$classifier" ]; then
      classifier="-$classifier"
    fi

    jar="$artifact-$version$classifier.jar"
    path=$(echo "$group" | sed 's|\.|/|g')
    url="https://repo1.maven.org/maven2/$path/$artifact/$version/$jar"

    echo "Fetching: $url"
    wget -q "$url" -P "$temp_dir" || { echo "Failed to download $jar"; exit 1; }

    replaceable=$(find "$SPARK_JARS" -maxdepth 1 -type f -name "$artifact-*" 2>/dev/null || echo "")

    if [ -n "$replaceable" ]; then
      echo "Replacing: $replaceable"
      rm "$replaceable" || exit 1
    fi

    mv "$temp_dir/$jar" "$SPARK_JARS" || { echo "Failed to move $jar"; exit 1; }
    echo "Updated: $artifact to version $version"
  done

  rm -rf "$temp_dir"
}

#---
## updates Jackson-mapper JAR to a RedHat patched version
## @param: RedHat version name
#---
update_jackson() {
  JACKSON_VER="$1"
  echo "Updating Jackson to version: $JACKSON_VER"

  temp_dir=$(mktemp -d)
  jackson_url="https://maven.repository.redhat.com/ga/org/codehaus/jackson/jackson-mapper-asl/$JACKSON_VER/jackson-mapper-asl-$JACKSON_VER.jar"

  wget -q "$jackson_url" -P "$temp_dir" || { echo "Failed to download Jackson JAR"; exit 1; }
  
  rm -f "$SPARK_JARS/jackson-mapper-asl-*.jar" || exit 1
  mv "$temp_dir/jackson-mapper-asl-$JACKSON_VER.jar" "$SPARK_JARS" || exit 1

  rm -rf "$temp_dir"
  echo "Jackson updated successfully!"
}

#---
## Removes Mesos-related JARs from Spark
#---
remove_mesos() {
  echo "Removing Mesos JARs..."
  find "$SPARK_JARS" -name '*mesos*.jar' -exec echo "Deleting: {}" \; -exec rm {} \; || exit 1
  echo "Mesos JARs removed successfully!"
}

#---
## Registers PySpark with Python Package Index
## @param: Spark home directory
## @param: Spark version
#---
register_pyspark() {
  SPARK_HOME="$1"
  VERSION="$2"
  echo "Registering PySpark installation for version: $VERSION"

  cat <<EOF > "$SPARK_HOME/python/setup.py"
from setuptools import setup
setup(
    name='pyspark',
    version='$VERSION',
    description='A dummy package representing the provided PySpark installation',
)
EOF

  python3 -m pip install "$SPARK_HOME/python" || { echo "Failed to register PySpark"; exit 1; }
  echo "PySpark registered successfully!"
}

# Run updates
update_maven_jars "com.google.code.gson:gson:2.8.9 \
                   com.google.guava:guava:33.3.1-jre \
                   com.squareup.okhttp3:okhttp:3.14.9 \
                   io.netty:netty-codec-http2:4.1.116.Final \
                   io.netty:netty-codec-http:4.1.116.Final \
                   io.netty:netty-common:4.1.116.Final \
                   org.apache.avro:avro-ipc:1.11.4 \
                   org.apache.avro:avro-mapred:1.11.4 \
                   org.apache.avro:avro:1.11.4 \
                   org.apache.commons:commons-compress:1.27.1 \
                   commons-io:commons-io:2.16.1 \
                   commons-codec:commons-codec:1.17.2 \
                   org.apache.derby:derby:10.16.1.1 \
                   org.apache.derby:derbytools:10.16.1.1 \
                   org.apache.derby:derbyshared:10.16.1.1 \
                   org.apache.hadoop.thirdparty:hadoop-shaded-guava:1.3.0 \
                   org.apache.hadoop:hadoop-client-api:$HADOOP_VERSION \
                   org.apache.hadoop:hadoop-client-runtime:$HADOOP_VERSION \
                   org.apache.hadoop:hadoop-yarn-server-web-proxy:$HADOOP_VERSION \
                   org.apache.hive:hive-beeline:2.3.10 \
                   org.apache.hive:hive-cli:2.3.10 \
                   org.apache.hive:hive-common:2.3.10 \
                   org.apache.hive:hive-exec:2.3.10:core \
                   org.apache.hive:hive-jdbc:2.3.10 \
                   org.apache.hive:hive-llap-common:2.3.10 \
                   org.apache.hive:hive-metastore:2.3.10 \
                   org.apache.hive:hive-serde:2.3.10 \
                   org.apache.hive:hive-shims:2.3.10 \
                   org.apache.hive.shims:hive-shims-0.23:2.3.10 \
                   org.apache.hive.shims:hive-shims-common:2.3.10 \
                   org.apache.hive.shims:hive-shims-scheduler:2.3.10 \
                   org.apache.thrift:libthrift:0.16.0 \
                   org.apache.ivy:ivy:2.5.3 \
                   org.apache.parquet:parquet-column:1.15.0 \
                   org.apache.parquet:parquet-format-structures:1.15.0 \
                   org.apache.parquet:parquet-encoding:1.15.0 \
                   org.apache.parquet:parquet-jackson:1.15.0 \
                   org.apache.parquet:parquet-hadoop:1.15.0 \
                   org.apache.parquet:parquet-common:1.15.0 \
                   org.apache.zookeeper:zookeeper-jute:3.9.3 \
                   org.apache.zookeeper:zookeeper:3.9.3"

update_jackson "1.9.14.jdk17-redhat-00001"
remove_mesos
register_pyspark "$SPARK_HOME" "$SPARK_VERSION"

echo "Spark JAR updates completed successfully!"
