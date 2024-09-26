# aiSSEMBLE&trade; Apache Keycloak Helm Chart
Baseline Helm chart for packaging and deploying Keycloak. Built on the [Bitnami Helm chart](https://bitnami.com/stack/keycloak/helm) and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See [Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install keycloak oci://ghcr.io/boozallen/aissemble-keycloak-chart --version <AISSEMBLE-VERSION>
```
**Note**: *the version should match the aiSSEMBLE project version.*

# Properties
See the Keycloak packaged by Bitnami [GitHub page](https://github.com/bitnami/charts/tree/main/bitnami/keycloak) for installation, usage, and parameters.

## Persistent Volume Claim for PostgreSQL Deployment
The Helm deployment of Keycloak through the Bitnami community charts also result in a PostgreSQL deployment, which expects a connection from the Keycloak deployment. A persistent volume claim is maintained for the PostgreSQL database. However, when conducting local testing, any subsequent deployments after a teardown will cause the new deployment pods to be out of sync with the persistent volume claim in terms of expected credentials, thus resulting in an inability to authenticate with the database. 

Although this would rarely pose an issue in production, this behavior can pose as an unintuitive issue when conducting local testing. In order to prevent this issue from arising for local testing, the template values-dev.yaml file `foundation/foundation-mda/src/main/resources/templates/deployment/keycloak/v2/keycloak.values-dev.yaml.vm` overrides postgresql parameters, resulting in the disabling of the persistent volume claim.