# Extensions Helm

Parent module for aiSSEMBLE&trade; Helm charts. 


## Requirements

Each aiSSEMBLE Helm chart should contain the following
1. A chart directly under the module folder 
2. A README file with basic information on the chart including all values documented
3. A tests folder with unit tests validating behavior

**Note**: The unit tests are built with https://github.com/helm-unittest/helm-unittest#install and it is required to 
install both Helm and the plugin locally to run this module.


## Leveraging Extensions Helm

After an aiSSEMBLE module's Helm migration has been completed, the following steps can be taken in a downstream project 
to leverage the new Helm pattern for the aiSSEMBLE module at hand:
* Add the aiSSEMBLE helm repo to your local repos: `helm repo add ghcr.io https://UPDATE_WITH_HELM_REPO`


## For legacy aiSSEMBLE Projects

Follow the instructions in the [_Kubernetes Artifacts Upgrade_](https://boozallen.github.io/aissemble/aissemble/current/containers.html#_kubernetes_artifacts_upgrade)
section of the _Path to Production > Container Support_ documentation page to update older projects to the new Extensions Helm baseline approach.

## Developing with Extension Helm
* When completing and locally testing a migration of a module to Extensions Helm, the above steps can be taken.
  * As a precursor to the above steps, it would be helpful to create a simple aiSSEMBLE project to use as a test-bed.
* It is important to note that because the module's helm charts will not be published to the Helm Repository remote 
  until a build via GitHub Actions is completed, the `repository` field in the module's `Chart.yaml` file must be updated 
  to point to the local aiSSEMBLE baseline code. More specifically, in the test-bed project, the `repository` field in
  `<project-name>-deploy/src/main/resources/apps/<module-name>/Chart.yaml` should have hold a value in the following form:
`"file://../../../../../../../aissemble/extensions/extensions-helm/aissemble-<module-name>-chart"`
  * the file path given is relative to the location of the `Chart.yaml` file, so 7 or more `../` prefixes will be 
    required to reach wherever the local aiSSEMBLE baseline is stored on your local machine
  * in this example 7 `../` prefixes are added to the relative path, as the test-bed project sits in the same directory 
    as the local `aissemble` baseline code.
* Additionally, for local development only, the application's `Chart.yaml` in its corresponding `aissemble-<app-name>-chart` 
  should set the `version` and `appVersion` field to whatever the current aiSSEMBLE version is; this will allow for 
  testing of the local deployment when leveraging tilt
  * If making use of additional aiSSEMBLE charts within your application's dependencies, the dependent subcharts should 
  have their `version` and `appVersion` updated to the current aiSSEMBLE version as well