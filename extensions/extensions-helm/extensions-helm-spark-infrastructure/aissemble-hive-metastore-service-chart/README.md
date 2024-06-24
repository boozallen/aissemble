# aiSSEMBLE&trade; Hive Metastore Service Helm Chart

Baseline Helm chart for packaging and deploying Hive Metastore Services. Chart is built and managed during the normal
Maven build lifecycle and placed in the **target/helm/repo** directory.
See https://github.com/kokuwaio/helm-maven-plugin for more details.

# Basic usage with Helm CLI

To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart
version with the desired aiSSEMBLE version. For example:

```bash
helm install hive-metastore-service oci://ghcr.io/boozallen/aissemble-hive-metastore-service-chart --version <AISSEMBLE-VERSION>
```

**Note**: *the version should match the aiSSEMBLE project version.*

# Properties

| Property                                        | Description                                                                                                                                             | Required Override | Default                                                                                                                                                                                                                                                                     |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| app.name                                        | Sets label for app.kubernetes.io/name                                                                                                                   | No                | hive-metastore-service                                                                                                                                                                                                                                                      |
| app.version                                     | Sets label for app.kubernetes.io/version                                                                                                                | No                | Chart.AppVersion (aiSSEMBLE project version)                                                                                                                                                                                                                                |
| replicaCount                                    | Sets desired number of replicas (instances)                                                                                                             | No                | 1                                                                                                                                                                                                                                                                           |
| hostname                                        | The hostname for the application                                                                                                                        | No                | hive-metastore-service                                                                                                                                                                                                                                                      |
| image.name                                      | The image name                                                                                                                                          | Yes               | boozallen/aissemble-hive-service                                                                                                                                                                                                                                            |
| image.imagePullPolicy                           | The image pull policy                                                                                                                                   | No                | IfNotPresent                                                                                                                                                                                                                                                                |
| image.dockerRepo                                | The image docker repository                                                                                                                             | No                | ghcr.io/                                                                                                                                                                                                                                                                    |
| image.tag                                       | The image tag                                                                                                                                           | No                | Chart.AppVersion                                                                                                                                                                                                                                                            |
| deployment.ports                                | The deployment ports                                                                                                                                    | No                | &emsp; - name: "thrift" <br/>&emsp;&emsp;containerPort: 9083                                                                                                                                                                                                                |
| deployment.env                                  | The environment variables for the pod                                                                                                                   | No                | See [values.yaml](./values.yaml)                                                                                                                                                                                                                                            |
| deployment.restartPolicy                        | The deployment restart policy                                                                                                                           | No                | Always                                                                                                                                                                                                                                                                      |
| deployment.volumeMounts                         | The deployment volume mounts                                                                                                                            | No                | &emsp; - name: metastore-service-config <br/>&emsp;&emsp;mountPath: /opt/hive/conf/metastore-site.xml <br/>&emsp;&emsp;subPath: metastore-site.xml                                                                                                                          |
| deployment.volumes                              | The deployment volumes                                                                                                                                  | No                | &emsp; - name: metastore-service-config <br/>&emsp;&emsp;configMap: <br/>&emsp;&emsp;&emsp;name: metastore-service-config <br/>&emsp;&emsp;&emsp;items: <br/>&emsp;&emsp;&emsp;&emsp; - key: metastore-site.xml <br/>&emsp;&emsp;&emsp;&emsp;&emsp;path: metastore-site.xml |
| service.spec.ports                              | The service spec ports                                                                                                                                  | No                | &emsp; - name: "thrift" <br/>&emsp;&emsp;port: 9083 <br/>&emsp;&emsp;targetPort: 9083                                                                                                                                                                                       |
| mysql.enabled                                   | Whether to use mysql as the backing database                                                                                                            | No                | true                                                                                                                                                                                                                                                                        |
| configMap.metastoreServiceConfig.baseProperties | Default configuration for the metastore service                                                                                                         | No                | See [values.yaml](./values.yaml)                                                                                                                                                                                                                                            |
| configMap.metastoreServiceConfig.properties     | Optional configuration for the metastore service. Properties added here will be included in the configuration without overriding the default properties | No                |                                                                                                                                                                                                                                                                             |

# Subchart Overrides

## mysql

See [the official bitnami documentation](https://github.com/bitnami/charts/tree/main/bitnami/mysql) for full
configuration options.

| Property         | Default             |
|------------------|---------------------|
| fullnameOverride | "hive-metastore-db" |
| auth.database    | "metastore"         |

# Migration from aiSSEMBLE v1 Helm Charts

If you are migrating from the v1 version of the hive-metastore-service chart, use the tables below to apply any
existing customizations from the old chart to the new v2 chart.
Note that because this new chart includes a hive-metastore-db deployment, the version 1 chart for hive-metastore-db is
no longer needed.

## Property Location

All properties listed below have been moved to the parent chart. If any properties are set to the default value, we
recommend removing them from your values file entirely. One major change is how the `metastoreServiceConfig` values
are now being set in standard yaml format. Some of these values have now been set in the parent chart.
In the table below, the notation `env[KEY]` refers the `env` list item whose `name` value was `key`. The notation
`properties[KEY]` refers the `properties` list item whose `name` value was `key`.

**Note**: *all new property locations include the prefix `aissemble-hive-metastore-service-chart`*

| Old Property Location                                                                          | New Property Location                                                                  | Additional Notes |
|------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|------------------|
| hive-metastore-db.service.spec.ports[].port                                                    | mysql.primary.service.ports.mysql                                                      | Default to 3306  |
| deployment.env[HADOOP_CLASSPATH]                                                               | deployment.baseEnv[HADOOP_CLASSPATH]                                                   |                  |
| deployment.env[JAVA_HOME]                                                                      | deployment.baseEnv[JAVA_HOME]                                                          |                  |
| configMap.metastoreServiceConfig.configuration.property[metastore.thrift.uris]                 | configMap.metastoreServiceConfig.baseProperties[metastore.thrift.uris]                 |                  |
| configMap.metastoreServiceConfig.configuration.property[metastore.task.threads.always]         | configMap.metastoreServiceConfig.baseProperties[metastore.task.threads.always]         |                  |
| configMap.metastoreServiceConfig.configuration.property[metastore.expression.proxy]            | configMap.metastoreServiceConfig.baseProperties[metastore.expression.proxy]            |                  |
| configMap.metastoreServiceConfig.configuration.property[javax.jdo.option.ConnectionDriverName] | configMap.metastoreServiceConfig.baseProperties[javax.jdo.option.ConnectionDriverName] |                  |
| configMap.metastoreServiceConfig.configuration.property[javax.jdo.option.ConnectionURL]        | configMap.metastoreServiceConfig.baseProperties[javax.jdo.option.ConnectionURL]        |                  |

## Property Removed

The following properties no longer exist.
In the table below, the notation `env[KEY]` refers the `env` list item whose `name` value was `key`.

| Property                                              | Reason                                                                     |
|-------------------------------------------------------|----------------------------------------------------------------------------|
| hive-metastore-db.replicaCount                        | This property was ignored in the original chart by default                 |
| hive-metastore-db.service.spec.ports[].name           | Value is inherited by base mysql chart and can not be modified             |
| hive-metastore-db.service.spec.ports[].targetPort     | Value is inherited by base mysql chart and can not be modified             |
| hive-metastore-db.hostname                            | Removed the need for a specific deployment chart for the Hive metastore db |
| hive-metastore-db.deployment                          | Removed the need for a specific deployment chart for the Hive metastore db |
| hive-metastore-db.deployment.env[MYSQL_DATABASE]      | Value can be set in mysql.auth.database                                    |
| hive-metastore-db.deployment.env[MYSQL_PASSWORD]      | Value can be set in mysql.auth.password                                    |
| hive-metastore-db.deployment.env[MYSQL_ROOT_PASSWORD] | Value can be set in mysql.auth.rootPassword                                |
| hive-metastore-db.deployment.env[MYSQL_USER]          | Value can be set in mysql.auth.username                                    |
