# aiSSEMBLE SealedSecrets Helm Chart

Baseline Helm chart for packaging and deploying SealedSecrets. Built on the [official Helm chart](https://github.com/bitnami-labs/sealed-secrets/tree/main/helm/sealed-secrets) and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See [Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install sealed-secrets ghcr.io/boozallen/aissemble-sealed-secrets-chart --version <AISSEMBLE-VERSION>
```

**Note**: *the version should match the aiSSEMBLE project version.*

# Overridden Defaults

The following properties are inherited from the [base SealedSecrets chart](https://github.com/bitnami-labs/sealed-secrets/tree/main/helm/sealed-secrets), but with updated default values.
| Property             | Description                                      | Required Override | Default                             |
|----------------------|--------------------------------------------------|-------------------|-------------------------------------|
| fullnameOverride     | String to fully override sealed-secrets.fullname | No                | aissemble-sealed-secrets-controller |

All properties must be prefixed with the key `aissemble-sealed-secrets-chart.sealed-secrets` to override any values in the chart. See [helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) for more info.