# aiSSEMBLE&trade; Infrastructure Helm Chart

Baseline Helm chart with the necessary infrastructure for deploying your aiSSEMBLE project. This umbrella chart includes support for:
- [Argo CD](https://argo-cd.readthedocs.io/en/stable/)
- [Jenkins](https://www.jenkins.io/)
- [Nginx Ingress](https://docs.nginx.com/nginx-ingress-controller/)


It is managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory.
See [Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI

To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart
version with the desired aiSSEMBLE version. For example:

```bash
helm install aissemble-infrastructure ghcr.io/boozallen/aissemble-infrastructure-chart --version <AISSEMBLE-VERSION>
```

**Note**: *the version should match the aiSSEMBLE project version.*

# Custom Properties
The following properties are specific to the aiSSEMBLE Infrastructure chart and can be used to configure which subcharts to include in your deployment:

| Property | Description | Default |
|----------|-------------|---------|
| argo-cd.enabled | Whether to include the Argo CD deployment | true |
| jenkins.enabled | Whether to include the Jenkins deployment | true |
| ingress-nginx.enabled | Whether to include the Nginx Ingress deployment | true |

# Overridden Inherited Properties

## Argo CD

The following properties are inherited from the base [Argo CD chart](https://github.com/argoproj/argo-helm/tree/argo-cd-7.4.1/charts/argo-cd), 
but with updated default values. For more information on Argo CD see the [official docs](https://argo-cd.readthedocs.io/en/stable/getting_started/).

| Property | Description |  Default | Additional Notes |
|----------|-------------|----------|------------------|
| fullnameOverride | String to fully override the deployment name | argocd | |
| namespaceOverride | Override the release namespace | argocd | Used for deploying to a different namespace than the umbrella chart |
| crds.keep | Keep CRDs on chart uninstall | false | |
| global.domain | Default domain used by all components | argocd.localdev.me | `*.localdev.me` host names are only valid for local deployments |
| configs.params | Additional configuration parameters | server.insecure: true | Disables TLS. See [official docs](https://argo-cd.readthedocs.io/en/stable/operator-manual/tls/) for details on TLS configuration | 
| server.ingress.enabled | Enable an ingress resource for the Argo CD HTTP server | true |  |
| server.ingress.ingressClassName | Defines which ingress controller will implement the resource | nginx | |
| server.ingressGrpc.enabled | Enable a dedicated gRPC ingress for use with the Argo CD CLI | true | |
| server.ingressGrpc.ingressClassName | Defines which ingress controller will implement the resource | nginx | |
| server.ingressGrpc.annotations | Additional ingress annotations for dedicated gRPC ingress | nginx.ingress.kubernetes.io/backend-protocol: GRPC | |

All properties must be prefixed with the key `argo-cd` to override any values in the chart. See 
[helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) 
for more info.

#### Default Login

The base Argo CD chart generates a password for the `admin` user. To retrieve the password, run the following command using the Argo CD CLI:

```bash
argocd admin initial-password -n argocd
```

## Jenkins

The following properties are inherited from the base [Jenkins chart](https://github.com/jenkinsci/helm-charts/tree/jenkins-5.5.4/charts/jenkins), 
but with updated default values. For more information on running Jenkins see the [official docs](https://www.jenkins.io/doc/book/installing/kubernetes/#install-jenkins-with-helm-v3).

| Property | Description |  Default | Additional Notes |
|----------|-------------|----------|------------------|
| fullnameOverride | String to fully override the deployment name | jenkins | |
| namespaceOverride | Override the release namespace | jenkins | Used for deploying to a different namespace than the umbrella chart |
| controller.image.registry | Controller image registry | ghcr.io | |
| controller.image.repository | Controller image repository | boozallen/aissemble-jenkins-controller | |
| controller.image.tag | Controller image tag override |  _Chart.Version_ | |
| controller.ingress.enabled | Enable an ingress resource for the Jenkins HTTP server | true |  |
| controller.ingress.hostname | Configures the hostname for the server | jenkins.localdev.me | `*.localdev.me` host names are only valid for local deployments |
| controller.ingress.ingressClassName | Defines which ingress controller will implement the resource | nginx | |
| controller.ingress.tls | TLS configuration for the ingress | [] | Disables TLS. See [official docs](https://github.com/jenkinsci/helm-charts/blob/jenkins-5.5.4/charts/jenkins/README.md#external-url-configuration) for details on TLS configuration | 
| controller.testEnabled | Used to disable rendering controller test resources when using helm template| false | Disables the test as the official chart does not use the controller image tag value |
| agent.image.registry | Agent image registry | ghcr.io | |
| agent.image.repository | Agent image repository | boozallen/aissemble-jenkins-agent | |
| agent.image.tag | Agent image tag override |  _Chart.Version_ | |

All properties must be prefixed with the key `jenkins` to override any values in the chart. See 
[helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) 
for more info.

#### Default Login

The base Jenkins chart generates a password for the `admin` user. This password is stored in a Kubernetes secret. To
retrieve the password, run the following command:

```bash
kubectl exec --namespace jenkins -it svc/jenkins -c jenkins -- /bin/cat /run/secrets/additional/chart-admin-password && echo
```

## Ingress Nginx

The following properties are inherited from the base [Ingress Nginx chart](https://github.com/kubernetes/ingress-nginx/tree/helm-chart-4.11.1/charts/ingress-nginx), 
but with updated default values. For more information on running Ingress Nginx see the [official docs](https://kubernetes.github.io/ingress-nginx/deploy/).

| Property | Description |  Default | Additional Notes |
|----------|-------------|----------|------------------|
| fullnameOverride | String to fully override the deployment name | ingress-nginx | |
| namespaceOverride | Override the release namespace | ingress-nginx | Used for deploying to a different namespace than the umbrella chart |

All properties must be prefixed with the key `ingress-nginx` to override any values in the chart. See 
[helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) 
for more info.