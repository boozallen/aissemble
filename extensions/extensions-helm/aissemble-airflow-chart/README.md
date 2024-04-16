# aiSSEMBLE&trade; Airflow Helm Chart
Baseline Helm chart for packaging and deploying Airflow. Built on the [official Helm chart](https://airflow.apache.org/docs/helm-chart/stable) and managed during the 
normal Maven build lifecycle and placed in the **target/helm/repo** directory. See 
[Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart 
version with the desired aiSSEMBLE version. For example:
```bash
helm install airflow ghcr.io/boozallen/aissemble-airflow-chart --version <AISSEMBLE-VERSION>
```
**Note**: *the version should match the aiSSEMBLE project version.*

# Properties

The following properties are inherited from the base [Airflow chart](https://github.com/apache/airflow/tree/helm-chart/1.10.0/chart), 
but with updated default values. 

*NOTE* there are several values that should be modified before use in a production environment; see 
[production documentation](https://airflow.apache.org/docs/helm-chart/1.10.0/production-guide.html) for details. 

| Property | Description | Default                                                                                                                                                                                                                                                                                            |
|----------|-------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| airflowVersion | The version of airflow to use | 2.6.2                                                                                                                                                                                                                                                                                              | 
| defaultAirflowRepository | The airflow repo and image to use | ghcr.io/boozallen/aissemble-airflow                                                                                                                                                                                                                                                                | 
| defaultAirflowTag | The airflow image tag | Chart.Version                                                                                                                                                                                                                                                                                      | 
| images.airflow.pullPolicy | The airflow image pull policy | Always                                                                                                                                                                                                                                                                                             |
| executor | The mechanism that handles the running of tasks | KubernetesExecutor                                                                                                                                                                                                                                                                                 |
| statsd.enabled | Custom metrics service | false                                                                                                                                                                                                                                                                                              |
| triggerer.enabled | Monitors all deferred tasks in your environment | false                                                                                                                                                                                                                                                                                              |
| redis.enabled | Messaging service | false                                                                                                                                                                                                                                                                                              |
| logs.persistence.enabled | Store logs output from DAGs | true                                                                                                                                                                                                                                                                                               |
| logs.persistence.size | Size of PVC for storing logs | 500Mi                                                                                                                                                                                                                                                                                              |
| webserver.args | Arguments used for starting the webserver | args: ["bash", "-c", "exec airflow webserver --port 10080"]                                                                                                                                                                                                                                        |
| webserver.service.type | Type of service for the webserver | ClusterIP                                                                                                                                                                                                                                                                                          | 
| webserver.service.ports | Ports forwarding to the airflow ui | &emsp;- name: "http-1" <br/>&emsp;&emsp;port: 9085 <br/>&emsp;&emsp;targetPort: airflow-ui <br/>&emsp;- name: "http-2" <br/>&emsp;&emsp;port: 5000 <br/>&emsp;&emsp;targetPort: airflow-ui                                                                                                         |
| webserver.defaultUser.enabled | Create the default user | true                                                                                                                                                                                                                                                                                               |
| webserver.defaultUser.role | Default user role | Admin                                                                                                                                                                                                                                                                                              |
| webserver.defaultUser.username | Default username | admin                                                                                                                                                                                                                                                                                              |
| webserver.defaultUser.password | Default password | aiops                                                                                                                                                                                                                                                                                              |
| webserver.defaultUser.email | Default email | adminbah@someurl.com                                                                                                                                                                                                                                                                               |
| webserver.defaultUser.firstName | Default first name | Booz                                                                                                                                                                                                                                                                                               |
| webserver.defaultUser.lastName | Default last name | Allen                                                                                                                                                                                                                                                                                              |
| securityContext.runAsUser| Numeric user id for airflow's main process | 50000                                                                                                                                                                                                                                                                                              |
| securityContext.runAsGroup  | Numeric group id for airflow's main process | 0                                                                                                                                                                                                                                                                                                  |
| securityContext.fsGroup  | Numeric group id for airflow's file system | 0                                                                                                                                                                                                                                                                                                  |
| ports.airflowUI | Port the airflow ui is listening on | 10080                                                                                                                                                                                                                                                                                              |
| env  | Airflow environment variables | &emsp;- name: KRAUSENING_BASE <br/>&emsp;&emsp;value: /opt/airflow/config/ <br/>&emsp;- name: KAFKA_BOOTSTRAP_SERVER <br/>&emsp;&emsp;value: kafka-cluster:9093 <br/>&emsp;- name: PYTHONUNBUFFERED <br/>&emsp;&emsp;value: "1" <br/>&emsp;- name: GIT_PYTHON_REFRESH <br/>&emsp;&emsp;value: quiet |
| volumeMounts | Volume mounts for airflow | &emsp;- mountPath: /tmp/model <br/>&emsp;&emsp;name: model <br/>&emsp;- mountPath: /notebooks/boms <br/>&emsp;&emsp;name: boms-notebook                                                                                                                                                            |
| volumes | Volumes for airflow | &emsp;- name: model <br/>&emsp;&emsp;persistentVolumeClaim: <br/>&emsp;&emsp;&emsp;claimName: model <br/>&emsp;- name: boms-notebook <br/>&emsp;&emsp;persistentVolumeClaim: <br/>&emsp;&emsp;&emsp;claimName: boms-notebook                                                                       |
| namespaceOverride         | Namespace to deploy to instead of the Helm release namespace                                                                           | .Release.Namespace                                                                                                                                                                                                                                                                                 |

All properties must be prefixed with the key `aissemble-airflow-chart.airflow` to override any values in the chart. See 
[helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) 
for more info.

# Migration from aiSSEMBLE v1 Helm Charts
If you are migrating from the v1 version of the airflow chart, use the tables below to apply any existing customizations 
from the old chart to the new v2 chart.

## Property Location
All properties listed below have been moved to the parent chart. If any properties are set to the default value, we 
recommend removing them from your values file entirely.

**Note**: *all new property locations include the prefix `aissemble-airflow-chart.airflow`*

| Old Property Location | New Property Location | Same Default Value | Additional Notes                                                                                                                                                                     |
|-----------------------|-----------------------|--------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| image.name | defaultAirflowRepository | No                 | This attribute should be combined with `image.dockerRepo`                                                                                                                            |
| image.dockerRepo| defaultAirflowRepository | No                 | This attribute should be combined with `image.name`                                                                                                                                  |
| image.tag | defaultAirflowTag | No                 |                                                                                                                                                                                      |
| image.imagePullPolicy | images.airflow.pullPolicy | Yes                |                                                                                                                                                                                      |
| service.spec.type | webserver.service.type | Yes                |                                                                                                                                                                                      |
| service.spec.ports | webserver.service.ports | No                 | Port `name` must be a string and it's recommended to set the `targetPort` to `airflow-ui`                                                                                            |
| deployment.securityContext.runAsUser | securityContext.runAsUser | Yes                |                                                                                                                                                                                      |
| deployment.securityContext.runAsGroup | securityContext.runAsGroup | Yes                |                                                                                                                                                                                      |
| deployment.securityContext.fsGroup | securityContext.fsGroup | Yes                |                                                                                                                                                                                      |
| deployment.ports | ports.airflowUI | No                 | Can only set one port number, name cannot be overridden and defaults to `airflow-ui`. Port number must match the value in `aissemble-airflow-chart.airflow.webserver.args`           |
| deployment.env | env | Yes                | Original environment variables are in the parent chart `env`. Use `extraEnv` to append new environment variables without clearing the defaults. Use `env` to overwrite the defaults. |
| deployment.volumeMounts | volumeMounts | Yes                |                                                                                                                                                                                      |
| deployment.volumes | volumes | Yes                |                                                                                                                                                                                      |
| ingress.enabled | ingress.web.enabled | No                 |                                                                                                                                                                                      |
| ingress.metadata.annotations | ingress.web.annotations | No                |                                                                                                                                                                                      |
| ingress.hosts[\*].host | ingress.web.hosts[\*].name | No                | 
| ingress.hosts[\*].paths[\*].path | ingress.web.path | No                | To provide additional paths with custom service names, make use of `ingress.web.precedingPaths[*].serviceName` or `ingress.web.succeedingPaths[*].serviceName`                       |
| ingress.hosts[\*].paths[\*].pathType | ingress.web.pathType | No                |                                                                                                                                                                                      | 

## Property Removed
The following properties no longer exist.

| Property | Reason |
|----------|--------|
| replicaCount | This property was ignored in the original chart by default |
| hostname | This property is not used in the new chart |
| serviceaccount.namespace | Defaults to `Release.Namespace` and cannot be overridden |
| deployment.serviceAccountName | Each of the following service's now have their own accounts:<br/>&emsp;`workers.serviceAccount.name`<br/>&emsp;`scheduler.serviceAccount.name`<br/>&emsp;`webserver.serviceAccount.name`<br/>&emsp;`migrateDatabaseJob.serviceAccount.name`<br/>&emsp;`createUserJob.serviceAccount.name` |
| deployment.automountServiceAccountToken | This property is not used in the new chart |
| deployment.restartPolicy | Defaults to `Always` and cannot be overridden |
| ingress.hosts[\*].paths[\*].path.backend.service.name | Defaults to `.Release.Name -webserver` and cannot be overridden. To provide additional paths with custom service names, make use of `ingress.web.precedingPaths[*].serviceName` or `ingress.web.succeedingPaths[*].serviceName` |
| ingress.hosts[\*].paths[\*].path.backend.service.port.number | Defaults to `airflow-ui` and cannot be overridden. To provide additional paths with custom service ports, make use of `ingress.web.precedingPaths[*].servicePort` or `ingress.web.succeedingPaths[*].servicePort`. Port must be a name instead of a number |
| ingress.status | This property is not used in the new chart |
| ingressmlflow[\*] | This property has been moved to the `mlflow-ui` chart |

## Additional Changes

### Dockerfile
The following script `<YOUR-PROJECT>-docker/<YOUR-PROJECT>-airflow-docker/src/main/resources/start.sh` and it's 
associated usage in the `<YOUR-PROJECT>-docker/<YOUR-PROJECT>-airflow-docker/src/main/resources/docker/Dockerfile` 
should be removed:
```
COPY ./src/main/resources/start.sh $AIRFLOW_HOME/
RUN chmod +x $AIRFLOW_HOME/start.sh

CMD ["/opt/airflow/start.sh"]
```

This functionality is handled in the new chart by passing your configurations through the following `args` locations:
```
aissemble-airflow-chart.airflow.workers.args
aissemble-airflow-chart.airflow.scheduler.args
aissemble-airflow-chart.airflow.createUserJob.args
aissemble-airflow-chart.airflow.migrateDatabaseJob.args
aissemble-airflow-chart.airflow.webserver.args
```

### Tiltfile
Within your Tiltfile, update the airflow docker_build to look like the following:

**Note**: *the addition of the `extra_tag` argument with `latest` on the end*

```
# airflow
docker_build(
    ref='boozallen/<YOUR-PROJECT>-airflow-docker',
    context='<YOUR-PROJECT>-docker/<YOUR-PROJECT>-airflow-docker',
    build_args=build_args,
    extra_tag='boozallen/<YOUR-PROJECT>-airflow-docker:latest',
    dockerfile='<YOUR-PROJECT>docker/<YOUR-PROJECT>-airflow-docker/src/main/resources/docker/Dockerfile'
)
```
