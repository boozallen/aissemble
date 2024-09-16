# aiSSEMBLE&trade; Configuration Store Chart

Baseline Helm chart for packaging and deploying aiSSEMBLE Configuration Store. It is managed during the normal Maven
build lifecycle and placed in the **target/helm/repo** directory.
See [Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI

To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart
version with the desired aiSSEMBLE version. For example:

```bash
helm install configuration-store oci://ghcr.io/boozallen/aissemble-configuration-store-chart --version <AISSEMBLE-VERSION>
```

**Note**: *the version should match the aiSSEMBLE project version.*

# Properties

## Overridden Inherited Properties
The following properties are override from the base (Quarkus)

| Property                                                    | Description                                                                                                                                                                                                                                                                    | Required Override | Default                                                                                                                                                                                                                         |
|-------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| app.name                                                    | Sets label for app.kubernetes.io/name                                                                                                                                                                                                                                          | No                | aissemble-configuration-store-chart                                                                                                                                                                                             |
| deployment.image.repo                                       | The image pull repository                                                                                                                                                                                                                                                      | No                | ghcr.io/                                                                                                                                                                                                                        |
| deployment.image.name                                       | The image name                                                                                                                                                                                                                                                                 | No                | boozallen/aissemble-configuration-store                                                                                                                                                                                         |
| deployment.image.imagePullPolicy                            | The image pull policy                                                                                                                                                                                                                                                          | No                | Always                                                                                                                                                                                                                          |
| deployment.hostname                                         | The hostname for the application                                                                                                                                                                                                                                               | No                | configuration-store                                                                                                                                                                                                             | 
| deployment.restartPolicy                                    | The restart policy                                                                                                                                                                                                                                                             | No                | Always                                                                                                                                                                                                                          |
| service.ports                                               | The service ports                                                                                                                                                                                                                                                              | No                | &emsp;- name: http <br/>&emsp;&emsp;port: 8083<br/>&emsp;&emsp;protocol: TCP<br/>&emsp;&emsp;targetPort: 8080<br/>&emsp;- name: https <br/>&emsp;&emsp;port: 443<br/>&emsp;&emsp;protocol: TCP<br/>&emsp;&emsp;targetPort: 8443 |
| configMap.name                                              | The name of the config map for quarkus config properties                                                                                                                                                                                                                       | No                | config-store-quarkus-config                                                                                                                                                                                                     |
| toggleableConfiguration.toggle                              | The flag to include the content for the pod or not;</br> the value should be the same with `webhook.enable`. If webhook is enabled, the required volumes should be in place for webhook or when the webhook is disabled, there shouldn't be any volumes hanging for webhook.   | No                | true                                                                                                                                                                                                                            |
| toggleableConfiguration.volumeMounts                        | The toggleable volume mounts for the pod                                                                                                                                                                                                                                       | No                | `/etc/webhook/cert=certs`                                                                                                                                                                                                       |
| toggleableConfiguration.volumes                             | The toggleable volumes for the pod                                                                                                                                                                                                                                             | No                | `certs=aissemble-configuration-store-webhook-certs`                                                                                                                                                                             |
| toggleableConfiguration.configMap.supplementalQuarkusConfig | The toggleable list of additional properties to provide to the Quarkus app                                                                                                                                                                                                     | No                | `- quarkus.http.ssl.certificate.file=/etc/webhook/cert/tls.crt`<br/>`- quarkus.http.ssl.certificate.key-file=/etc/webhook/cert/tls.key`                                                                                         |

## Persistent Volume Properties
The following properties are used to create the Persistent Volume and Persistent Volume Claim used to mount the configs
onto the configuration service. Additionally you must define [environment variables](#environment-variables) specifying the location
of the configs to the service.

| Property                                     | Description                                                                                   | Required Override | Default             |
|----------------------------------------------|-----------------------------------------------------------------------------------------------|-------------------|---------------------|
| configurationVolume.enabled                  | Enables or disables the volume generation                                                     | No                | true                |
| configurationVolume.name                     | The name used to create the pv and pvc                                                        | No                | configuration-store |
| configurationVolume.storageType              | Determines mounting a local mount for tests or a custom mount <br/>Options: local, custom     | No                | custom              |
| configurationVolume.storageClass             | The PVCs class of storage. If storageType is custom then the storageClass needs to be defined | No*               | ""                  |
| configurationVolume.volumePathOnNode         | Defines the host machine filesystem path to mount                                             | Yes**             | ""                  |
| configurationVolume.accessModes.ReadOnlyMany | The modes this PVC will support when mounting                                                 | No                | ReadOnlyMany        |
| configurationVolume.size                     | The size of the PVC                                                                           | No                | 1Gi                 |     

\* If the storage class is left as default empty string then Kubernetes will try and find the Clusters default
StorageClass

\*\* Should only be overwritten when using a local mount as the storage type


### Environment Variables
The following environment variables are used by the configuration service for locating the mounted configs. These environment
variables should specify their filepath location within the Persistent Volume. 

```yaml
aissemble-configuration-store-chart:
  aissemble-quarkus-chart:
    deployment:
      env:
        - name: BASE_PROPERTY
          value: <URI housing base property configurations>
        - name: ENVIRONMENT_PROPERTY
          value: <Optional URI housing property overrides>
```

## Custom Properties
The following properties are specific to the aiSSEMBLE Configuration Store chart and it's creation of a [Kubernetes mutating webhook](https://kubernetes.io/docs/reference/access-authn-authz/extensible-admission-controllers/) for injecting config store values into kubernetes resources. When the webhook is enabled, this helm chart should be deployed in a separate namespace than your project resources to [avoid deadlocks with self hosted webhooks](https://kubernetes.io/docs/reference/access-authn-authz/extensible-admission-controllers/#avoiding-deadlocks-in-self-hosted-webhooks). It is recommended to keep the webhook enabled to ensure full functionality of the configuration store.

When disabling the webhook, ensure the `configMap.supplementalQuarkusConfig`, `deployment.volumes`, and `deployment.volumeMounts` are overridden to remove webhook specific configurations.

| Property                      | Description                                                                       | Required Override | Default                                         |
|-------------------------------|-----------------------------------------------------------------------------------|-------------------|-------------------------------------------------|
| webhook.enable                | Enable custom webhook                                                             | No                | true                                            |
| webhook.name                  | Name of the custom webhook (must be at least three segments separated by dots)    | No                | boozallen.aissemble-configuration-store.webhook |
| webhook.serviceAccount.create | Enable the creation of a service account for managing the webhook                 | No                | true                                            |
| webhook.serviceAccount.name   | Name of the service account used to managing the webhook                          | Yes*              | aissemble-configuration-store-webhook-sa        |
| webhook.certSecret.create     | Enable the creation of a secret with necessary certificates for using the webhook | No                | true                                            |
| webhook.certSecret.name       | Name of the secret the certificates are stored in                                 | Yes*              | aissemble-configuration-store-webhook-certs     |

\* When creation of the service account/certificate secret is disabled, the name must be overridden with an existing resource on the cluster to be there replacement. The secret should include the following keys with their respective base64 encoded values: `ca.crt`, `tls.crt`, `tls.key`. Note the `ca.crt` is the Certificate Authority used to validating the webhook's server certificate represented by `tls.crt` and `tls.key`. Additionally, the service account must have at the minimum permissions to create and destroy both `jobs` and `mutatingwebhookconfigurations` kubernetes resources.
