# aiSSEMBLE&trade; Versioning Helm Chart
Baseline Helm chart for packaging and deploying aiSSEMBLE Versioning module. Built on the extension-helm-fastapi module and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See [Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install versioning ghcr.io/boozallen/aissemble-versioning-chart --version <AISSEMBLE-VERSION>
```
**Note**: *the version should match the aiSSEMBLE project version.*

# Properties
| Property                      | Description                                 | Required Override | Default                                                                     |
|-------------------------------|---------------------------------------------|-------------------|-----------------------------------------------------------------------------|
| app.name                      | Sets label for app.kubernetes.io/name       | No                | Chart.Name                                                                  |
| app.version                   | Sets label for app.kubernetes.io/version    | No                | Chart.AppVersion (aiSSEMBLE project version)                                |
| deployment.ports              | The deployment ports                        | No                | - name: http-1 <br/>&emsp;&emsp;containerPort: 80 <br/>&emsp; protocol: TCP |
| deployment.restartPolicy      | The deployment restart policy               | No                | Always                                                                      |
| deployment.volumes            | The volumes for the pod                     | No                | None                                                                        |
| deployment.volumeMounts       | The volume mounts for the pod               | No                | None                                                                        |
| deployment.securityContext    | The security context for the pod            | No                | None                                                                        |
| deployment.serviceAccountName | The service account for the pod             | No                | Default user in the cluster namespace                                       |
| deployment.env                | The environment variables for the pod       | No                | None                                                                        |
| deployment.args               | The args for the pod                        | No                | None                                                                        |
| image.name                    | The image name                              | Yes               | boozallen/aissemble-versioning                                              |
| image.imagePullPolicy         | The image pull policy                       | No                | Always (ensures local docker image is pulled, rather than from Nexus repo)  |
| image.dockerRepo              | The image docker repository                 | No                | ghcr.io/                                                                    |
| image.tag                     | The image tag                               | No                | Chart.AppVersion                                                            |
| replicaCount                  | Sets desired number of replicas (instances) | No                | 1                                                                           |
| service.spec.ports            | The service spec ports                      | No                | - name: http <br/>&emsp;&emsp;port: 8080 <br/>&emsp; targetPort: 80         |
| service.type                  | The service type                            | No                | ClusterIP                                                                   |

All properties must be prefixed with the key `aissemble-versioning-chart` to override any values in the chart. See [helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) for more info.