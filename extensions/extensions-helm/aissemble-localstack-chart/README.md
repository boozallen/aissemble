# aiSSEMBLE&trade; Localstack Helm Chart

Baseline Helm chart for packaging and deploying localstack. The chart is built and managed during the normal Maven build
lifecycle and placed in the **target/helm/repo** directory.

For aiSSEMBLE-based projects, note that this chart is intended only for local development work, such as when deploying
via Tilt.

# Basic usage with Helm CLI

After building the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and install the chart. For example:

```bash
helm install localstack ghcr.io/boozallen/aissemble-localstack-chart --version <AISSEMBLE-VERSION>
```

**Note**: *the version should match the aiSSEMBLE project version.*

# Overridden Defaults

The following properties have been overridden from the upstream localstack subchart, documented here:
https://github.com/localstack/helm-charts/tree/main/charts/localstack

| Property             | Description                                                                                         | Required Override | Default                                                                  |
|----------------------|-----------------------------------------------------------------------------------------------------|-------------------|--------------------------------------------------------------------------|
| fullnameOverride     | String to fully override common.names.fullname                                                      | No                | s3-local                                                                 |
| startServices        | Comma-separated list of AWS CLI service names which should be loaded right when starting LocalStack | No                | s3                                                                       |
| enableStartupScripts | Enables execution of startup behaviors                                                              | No                | true                                                                     |
| startupScriptContent | Base script for triggering creation of localstack resources                                         | No                | Triggers creation of s3 buckets/keys                                     |
| volumes              | Creates required volumes                                                                            | No                | configMap `localstack-resources` -> `create-s3-resources.sh`             |
| volumeMounts         | Mounts volumes to the filesystem                                                                    | No                | Mounts `create-s3-resources.sh` to `/opt/scripts/create_s3_resources.sh` |

# Custom Properties

The following properties are provided by the aissemble-localstack chart

| Property | Description                                                  | Required Override | Default             |
|----------|--------------------------------------------------------------|--------------|---------------------|
| buckets  | Collection of buckets and keys to create in s3               | No           | []                  |
| namespaceOverride | Namespace to deploy to instead of the Helm release namespace | No           | .Release.Namespace  |

# Migration From v1 Structure

## Moved Properties

| Property                      | New Location                                       | Notes                                                                                                                                                         |
|-------------------------------|----------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| buckets                       | aissemble-localstack.buckets                       |                                                                                                                                                               |
| deployment.env                | aissemble-localstack.localstack.extraEnvVars       |                                                                                                                                                               |
| deployment.ports              | See Note below                                     |                                                                                                                                                               |
| deployment.securityContext    | aissemble-localstack.localstack.securityContext    |                                                                                                                                                               |
| deployment.serviceAccountName | aissemble-localstack.localstack.serviceAccountName |                                                                                                                                                               |
| hostname                      | aissemble-localstack.localstack.fullnameOverride   |                                                                                                                                                               |
| image.dockerRepo              | aissemble-localstack.localstack.image.repository   | Merged with `image.name` into a shared property                                                                                                               |
| image.imagePullPolicy         | aissemble-localstack.localstack.image.pullPolicy   |                                                                                                                                                               |
| image.name                    | aissemble-localstack.localstack.image.repository   |                                                                                                                                                               |
| image.tag                     | aissemble-localstack.localstack.image.tag          |                                                                                                                                                               |
| replicaCount                  | aissemble-localstack.localstack.replicaCount       | This property was generated into v1 values, however it was not being used. It is now available in the v2 chart, and is used to set the replica count properly |
| service.spec.ports            | See Note below                                     |                                                                                                                                                               |
| service.spec.type             | aissemble-localstack.localstack.service.type       |                                                                                                                                                               |

*Note*
The localstack chart does not use a range of port definitions, it instead creates only one by default, the properties of
which are defined by aissemble-localstack.localstack.service.edgeService.[name | targetPort].
There is a new pair of properties named service.externalServicePorts.start and service.externalServicePorts.end that can
allow you to define a range of ports instead.

## Removed Properties

| Property                                | Reason for removal                                                               |
|-----------------------------------------|----------------------------------------------------------------------------------|
| deployment.automountServiceAccountToken | This property uses the default Kubernetes value of `true` and cannot be changed  |
| deployment.restartPolicy                | his property uses the default Kubernetes value of `Always` and cannot be changed |


