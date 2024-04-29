# aiSSEMBLE Vault Helm Chart
Baseline Helm chart for packaging and deploying Vault. Built on the [official Helm chart](https://github.com/hashicorp/vault-helm)
and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See the
[Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install vault ghcr.io/boozallen/aissemble-vault-chart --version <AISSEMBLE-VERSION>
```
_**NOTE:**_ *the version should match the aiSSEMBLE project version.*

# Properties
See the official Helm chart configurations [documentation](https://developer.hashicorp.com/vault/docs/v1.15.x/platform/k8s/helm/configuration). The following
default values have been modified from the original helm chart.

| Property                | Default                                                                        |
|-------------------------|--------------------------------------------------------------------------------|
| server.dataStorage      | true                                                                           |
| server.auditStorage     | true                                                                           |
| server.image.repository | "ghcr.io/boozallen/aissemble-vault" |

All properties must be prefixed with the key `aissemble-vault-chart.vault` to override any values in the chart. See the
[helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) for more info.