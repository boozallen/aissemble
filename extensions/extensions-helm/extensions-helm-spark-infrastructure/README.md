# Spark Infrastructure Helm Charts

This directory houses the set of aiSSEMBLE baseline helm charts which, collectively, make up the aiSSEMBLE Spark
Infrastructure.
At this time, the following helm charts are maintained here:

- [aissemble-hive-metastore-service-chart](./aissemble-hive-metastore-service-chart/README.md)
- [aissemble-spark-history-chart](./aissemble-spark-history-chart/README.md)
- [aissemble-thrift-server-chart](./aissemble-thrift-server-chart)

# Migration from aiSSEMBLE v1 Helm Charts

This singular chart, following aiSSEMBLE's v2 structure, replaces the v1 charts for spark-infrastructure,
hive-metastore-db, and hive-metastore-service.

In order to perform this migration, the following steps should be taken:

- Identify and remove the `fermenter-mda` executions from `<YOUR_PROJECT>-deploy/pom.xml` for
  the following profiles:
    - `hive-metastore-service-deploy`
    - `hive-metastore-db-deploy`
- Within the associated `fermenter-mda` execution in `<YOUR_PROJECT>-deploy/pom.xml`, replace references to the
  `aissemble-spark-infrastructure-deploy` profile with `aissemble-spark-infrastructure-deploy-v2`
- Remove references to `hive-metastore-db` and `hive-metastore-service` from your Tiltfile
- Remove or rename the directory `<YOUR_PROJECT>-deploy/src/main/resources/apps/spark-infrastructure`
- Remove or rename the directory `<YOUR_PROJECT>-deploy/src/main/resources/apps/hive-metastore-db`
- Remove or rename the directory `<YOUR_PROJECT>-deploy/src/main/resources/apps/hive-metastore-service`
- Remove the following files from `<YOUR_PROJECT>-deploy/src/main/resources/templates/`:
    - `hive-metastore-db.yaml`
    - `hive-metastore-service.yaml`
    - `spark-infrastructure.yaml`
- Execute `./mvnw clean install -pl :<YOUR_PROJECT>-deploy`
- Apply any customizations as needed to the generated `spark-infrastructure` chart. See the following charts for
  more information
    - [aissemble-hive-metastore-service-chart](./aissemble-hive-metastore-service-chart/README.md)
    - [aissemble-spark-history-chart](./aissemble-spark-history-chart/README.md)
    - [aissemble-thrift-server-chart](./aissemble-thrift-server-chart)

Certain settings must also be applied to each of your SparkApplications. The following entries should be added to each
data-delivery pipeline's `resources/apps/<pipeline>-dev.yaml` file:

```yaml
sparkApp:
  spec:
    sparkConf:
      spark.hadoop.fs.s3a.endpoint: "http://s3-local:4566"
      spark.eventLog.dir: "/opt/spark/spark-events"
      spark.hive.metastore.warehouse.dir: "s3a://spark-infrastructure/warehouse"
```
