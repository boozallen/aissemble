# aiSSEMBLE Elasticsearch Operator Helm Chart
Baseline Helm chart for packaging and deploying Elasticsearch Operator. Built on the [EKC Operator Helm chart](https://github.com/elastic/cloud-on-k8s/tree/main/deploy/eck-operator) and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See [Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install elasticsearch-operator ghcr.io/boozallen/aissemble-elasticsearch-operator-chart --version <AISSEMBLE-VERSION>
```
**Note**: *the version should match the aiSSEMBLE project version.*

# Properties
| Property                        | Description                                 | Required Override | Default                        |
|-------------------------------  |---------------------------------------------|-------------------|--------------------------------|
| eck-operator.nameOverride       | The short name for the deployment           | No                | elasticsearch-operator         |
| eck-operator.fullnameOverride   | The full name for the deployment            | No                | elasticsearch-operator         |

See the official Elastic Helm chart [documentation](https://www.elastic.co/guide/en/cloud-on-k8s/2.8/index.html) for installation, usage, and default parameters. For use in a production environment, it is recommended to create a new namespace called `elastic-system` for the pod to run in.

All properties must be prefixed with the key `eck-operator` to override any values in the chart. See [helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) for more info.
