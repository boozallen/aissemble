# aiSSEMBLE&trade; Policy Decision Point Helm Chart
Baseline Helm chart for packaging and deploying policy decision point service. Chart is built and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See https://github.com/kokuwaio/helm-maven-plugin for more details. 

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install policy-decision-point ghcr.io/boozallen/aissemble-policy-decision-point-chart --version <AISSEMBLE-VERSION>
```
**Note**: *the version should match the aiSSEMBLE project version.*

# Properties
| Property                      | Description                                 | Required Override | Default                                                                            |
|-------------------------------|---------------------------------------------|-------------------|------------------------------------------------------------------------------------|
| app.name                      | Sets label for app.kubernetes.io/name       | No                | Chart.Name (aissemble-policy-decision-point-chart)                                 |
| app.version                   | Sets label for app.kubernetes.io/version    | No                | Chart.AppVersion (aiSSEMBLE project version)                                       |
| hostname                      | The hostname for the application            | No                | policy-decision-point                                                              |
| image.name                    | The image name                              | Yes               | boozallen/aissemble-policy-decision-point                                          |
| image.imagePullPolicy         | The image pull policy                       | No                | Always (ensures local docker image is pulled, rather than from Nexus repo)         |
| image.dockerRepo              | The image docker repository                 | No                | NB: OSS: update with aissemble docker repository                                   |
| image.tag                     | The image tag                               | No                | Chart.AppVersion                                                                   |
| service.spec.ports            | The service spec ports                      | No                | - name: rest-api <br/>&emsp;&emsp;port: 8080 <br/>&emsp;&emsp;targetPort: 8080     |
| deployment.ports              | The deployment ports                        | No                | - name: http-1 <br/>&emsp;&emsp;containerPort: 8080 <br/>&emsp;&emsp;protocol: TCP |
| deployment.restartPolicy      | The deployment restart policy               | No                | Always                                                                             |

# Migration from aiSSEMBLE v1 Helm Charts
If you are migrating from the v1 version of the policy decision point chart, use the tables below to apply any existing customizations from the old chart to the new v2 chart.

## Property Location
All properties listed below have been moved to the parent chart. If any properties are set to the default value, we recommend removing them from your values file entirely.

| Old Property Location                      | New Property Location                                                   |                                                                                                                                                                       
|--------------------------------------------|-------------------------------------------------------------------------|
| app.name                                   | aissemble-policy-decision-point-chart.app.name                                |                                                                                                                                 
| app.version                                | aissemble-policy-decision-point-chart.app.version                             |                                                                                                                                    
| hostname                                   | aissemble-policy-decision-point-chart.hostname                                |                                                                                                                                                           
| image.name                                 | aissemble-policy-decision-point-chart.image.name                              |                                                                                                                                       
| image.imagePullPolicy                      | aissemble-policy-decision-point-chart.image.imagePullPolicy                   |                                                                                                      
| image.dockerRepo                           | aissemble-policy-decision-point-chart.image.dockerRepo                        |                                                                                                                             
| image.tag                                  | aissemble-policy-decision-point-chart.image.tag                               |                                                                                                                                                             
| service.spec.ports                         | aissemble-policy-decision-point-chart.service.spec.ports                      | 
| deployment.ports                           | aissemble-policy-decision-point-chart.deployment.ports                        | 
| deployment.restartPolicy                   | aissemble-policy-decision-point-chart.deployment.restartPolicy                | 

## Property Removed
The following properties no longer exist.

| Property                                   | Reason                                                          |                                                                                                                                                                       
|--------------------------------------------|-----------------------------------------------------------------|
| replicaCount                               | This property was ignored in the original chart by default      | 

