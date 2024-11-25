# aiSSEMBLE&trade; Trino Helm Chart
Baseline Helm chart for packaging and deploying Trino. Built on the [official Helm chart](https://github.com/trinodb/charts/blob/trino-0.33.0/charts/trino/README.md)
and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See the
[Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install trino oci://ghcr.io/boozallen/aissemble-trino-chart --version <AISSEMBLE-VERSION>
```
_**NOTE:**_ *the version should match the aiSSEMBLE project version.*

# Properties
See the official Helm chart configurations [documentation](https://trino.io/docs/current/installation/kubernetes.html#configuration). The following
default values have been modified from the original helm chart.

| Property                                                   | Description                                          | Default |
|------------------------------------------------------------|------------------------------------------------------|---------|
| aissemble-trino-chart.trino.service.port                   | The port the service listens on                      | 8084    |
| aissemble-trino-chart.trino.server.workers                 | The number of initial workers for executing queries  | 1       |
| aissemble-trino-chart.trino.server.autoscaling.enabled     | Enable the number of workers to scale                | true    |
| aissemble-trino-chart.trino.server.autoscaling.maxReplicas | The max amount of workers for executing queries      | 5       |

To override any values in the chart, see the [helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) for more info.