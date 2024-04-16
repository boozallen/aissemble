# aiSSEMBLE&trade; Elasticsearch Helm Chart
Baseline Helm chart for packaging and deploying Elasticsearch. Built on the [ECK Stack Helm chart](https://github.com/elastic/cloud-on-k8s/tree/main/deploy/eck-stack) and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See [Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install elasticsearch ghcr.io/boozallen/aissemble-elasticsearch-chart --version <AISSEMBLE-VERSION>
```
**Note**: *the version should match the aiSSEMBLE project version.*

# aiSSEMBLE Custom Properties
The following properties are unique to the aissemble-elasticsearch chart and extend the functionality of the base ECK chart.
| Property                   | Description                           | Required Override | Default    |
|----------------------------|---------------------------------------|-------------------|------------|
| basicAuth.enabled          | Enable basic http auth                | No                | true       |
| basicAuth.user.username    | Default username for basic auth       | No                | elastic    |
| basicAuth.user.password    | Default password for basic auth       | No                | elastic    |
| basicAuth.user.roles       | Default role for the basic auth user  | No                | superuser  |
| namespaceOverride          | Namespace to deploy to instead of the Helm release namespace | No                | .Release.Namespace |

Enabling basicAuth packages a kubernetes basic-auth secret into the chart named `elastic-user-secret` with the default credentials described above. This secret is then mounted into the elasticsearch pod with the property: `eck-stack.eck-elasticsearch.auth.fileRealm.secretName`. These credentials can be used for accessing elasticsearch via the less secure HTTP basic authentication. To exclude this secret from being created, simply set the `basicAuth.enabled` to `false`.

All properties must be prefixed with the key `aissemble-elasticsearch-chart` to override any values in the chart. See [helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) for more info.

# Elasticsearch Default Properties
The following properties are inherited from the base [ECK chart](https://github.com/elastic/cloud-on-k8s/tree/main/deploy/eck-stack/charts/eck-elasticsearch), but with updated default values.
| Property                                                  | Description                          | Required Override | Default                        |
|-----------------------------------------------------------|--------------------------------------|-------------------|--------------------------------|
| eck-elasticsearch.enabled                                 | Create an Elasticsearch resource     | No                | true                           |
| eck-elasticsearch.version                                 | Version of Elasticsearch to use      | No                | 8.1.2                          |
| eck-elasticsearch.nameOverride                            | The short name for the deployment    | No                | elasticsearch                  |
| eck-elasticsearch.fullnameOverride                        | The full name for the deployment     | No                | elasticsearch                  |
| eck-elasticsearch.http.tls.selfSignedCertificate.disabled | Disable TLS for the HTTP layer       | No                | true                           |
| eck-elasticsearch.auth.fileRealm.secretName               | Basic auth secret name               | No                | elastic-user-secret            |
| eck-kibana.enabled                                        | Create a Kibana resource             | No                | false                          |
| eck-agent.enabled                                         | Create an Elastic Agent resource     | No                | false                          |
| eck-fleet-server.enabled                                  | Create a Fleet Server resource       | No                | false                          |

See the official Elasticsearch Helm chart [documentation](https://www.elastic.co/guide/en/cloud-on-k8s/2.8/k8s-elasticsearch-specification.html) and Elasticsearch for Apache Hadoop [documentation](https://www.elastic.co/guide/en/elasticsearch/hadoop/8.1/reference.html) for installation, usage, and parameters.

All properties must be prefixed with the key `aissemble-elasticsearch-chart.eck-stack` to override any values in the chart. See [helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) for more info.