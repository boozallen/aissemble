# Extensions Helm
Parent module for aiSSEMBLE Helm charts. 

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
* Delete the directory holding the module's existing Helm charts if they have already been generated:
  * delete `<project-name>-deploy/src/main/resources/apps/<module-name>`
  * Note: if any custom overrides/changes were made to any existing Helm artifacts, it is crucial to preserve these 
    changes, prior to deleting this directory, so that they may be applied in the new Helm pattern
* Update the deploy profile in the project's deploy pom file:
  * in `<project-name>-deploy/pom.xml` replace `<profile><module-name>-deploy</profile>` with 
    `<profile><module-name>-deploy-v2</profile>`, assuming the profile name corresponding to the new Helm pattern is 
    noted with a "-v2" suffix
* Rebuild the project
* In `<project-name>-deploy/src/main/resources/apps/<module-name>/Chart.yaml`, update the `version` field to the 
  corresponding aiSSEMBLE version
* In the project's Tiltfile, make the following modifications:
  * add the following lines above the module's `docker_build()` call:
    * `local('helm dep update <project-name>-deploy/src/main/resources')`
    * `local('helm dependency build <project-name>-deploy/src/main/resources/apps/<module-name>') `
  * Below the module's corresponding `k8s_resource()` call, add the following line to manually build the docker image 
    for the migrated module:
    * `local('docker build -f <project-name>-docker/<project-name>-<module-name>-docker/target/Dockerfile -t boozallen/<project-name>-<module-name>:latest <project-name>-docker/<project-name>-<module-name>-docker/ --build-arg VERSION_AISSEMBLE=<current aiSSEMBLE version>') `
      * Note: this step is not always necessary, and only needed when the chart is dependent on an image being built in 
        a downstream project

After making these changes, the migrated module in the aiSSEMBLE project can properly leverage the new Helm pattern, 
defined by the Extensions Helm module.

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