# aiSSEMBLE&trade; Jenkins Helm Chart
Baseline Helm chart for packaging and deploying Jenkins. Built on the [official Helm chart](https://github.com/jenkinsci/helm-charts)
and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See the 
[Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install jenkins ghcr.io/boozallen/aissemble-jenkins-chart --version <AISSEMBLE-VERSION>
```
_**NOTE:**_ *the version should match the aiSSEMBLE project version.*

# Properties
See the official Helm chart [documentation](https://github.com/jenkinsci/helm-charts/tree/main/charts/jenkins). The following
default values have been modified from the original helm chart.

| Property                 | Default                                                                              |
|--------------------------|--------------------------------------------------------------------------------------|
| `controller.image`       | NB: OSS: <default aissemble docker repo>/boozallen/aissemble-jenkins-controller      |
| `controller.tag`         | _Chart.Version_                                                                      |
| `controller.testEnabled` | false                                                                                |
| `agent.image`            | NB: OSS: <default aissemble docker repo>//boozallen/aissemble-jenkins-agent |
| `agent.tag`              | _Chart.Version_                                                                      |

All properties must be prefixed with the key `aissemble-jenkins-chart.jenkins` to override any values in the chart. See the 
[helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) for more info.

# Default Login

The base Jenkins chart generates a password for the `admin` user. This password is stored in a Kubernetes secret. To
retrieve the password, run the following command:

``bash
kubectl exec -it svc/jenkins -c jenkins -- /bin/cat /run/secrets/additional/chart-admin-password && echo
``

For more information on running Jenkins on Kubernetes, visit:
https://cloud.google.com/solutions/jenkins-on-container-engine

For more information about Jenkins Configuration as Code, visit:
https://jenkins.io/projects/jcasc/
