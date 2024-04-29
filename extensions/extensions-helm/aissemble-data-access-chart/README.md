# aiSSEMBLE Data Access Helm Chart
Baseline Helm chart for packaging and deploying aiSSEMBLE Data Access module. Built on the extension-helm-quarkus module and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See [Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart 
version with the desired aiSSEMBLE version. For example:
```bash
helm install data-access ghcr.io/boozallen/aissemble-data-access-chart --version <AISSEMBLE-VERSION>
```
**Note**: *the version should match the aiSSEMBLE project version.*

The following properties are override from the base (Quarkus)
# Properties
| Property                                | Description                                                                                                                      | Required Override | Default                                                                                                                                                                                                                                                                                                                     |
|-----------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|-------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| deployment.env                          | The environment variables for the pod                                                                                            | No                | - name: DATA_ACCESS_GRAPHQL_PORT_EXTERNAL<br/>&emsp;&emsp;value: "http-1"<br/>- name: DATA_ACCESS_GRAPHQL_HOST_EXTERNAL<br/>&emsp;&emsp;value: "data-access"                                                                                                                                                                |
| deployment.ports                        | The deployment ports                                                                                                             | No                | - name: http-1 <br/>&emsp;&emsp;containerPort: 8080 <br/>&emsp;&emsp;protocol: TCP                                                                                                                                                                                                                                          |
| service.ports                           | The service ports                                                                                                                | No                | - name: http <br/>&emsp;&emsp;port: 8081<br/>&emsp;&emsp;protocol: TCP<br/>&emsp;&emsp;targetPort: 8080                                                                                                                                                                                                                     |

All properties must be prefixed with the key `aissemble-quarkus-chart` to override any values in the chart. See [helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) for more info.



# Migration from aiSSEMBLE v1 Helm Charts
If you are migrating from the v1 version of the data access chart, use the tables below to apply any existing customizations from the old chart to the new v2 chart.

## Property Location
Old properties would have to be prepended with aissemble-data-access.aissemble-quarkus, e.g.: app.name => aissemble-data-access.aissemble-quarkus.app.name

If any properties are set to the default value, we recommend removing them from your values file entirely.

Below property name has been changed

| Old Property Location    | New Property Location |                                                                                                                                                                       
|--------------------------|-----------------------|
| image.dockerRepo         | image.repo            |  

## Property Removed
The following properties no longer exist.

| Property                                   | Reason                                                          |                                                                                                                                                                       
|--------------------------------------------|-----------------------------------------------------------------|
| replicaCount                               | This property was ignored in the original chart by default      | 

