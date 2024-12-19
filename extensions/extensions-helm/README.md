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

When testing modifications to a Helm chart in a downstream project, special steps have to be taken as Helm charts are not published to the remote Helm Repository until a build
via GitHub Actions is completed. Firstly, all modifications to the aiSSEMBLE chart need to be committed and pushed to GitHub. Then, the chart downstream dependency needs to be
updated to point to the modified chart on your branch in GitHub.  Unfortunately, this is [still not natively supported in
Helm](https://github.com/boozallen/aissemble/issues/488#issuecomment-2518466847), so we need to do some setup work to enable a plugin that provides this functionality.

### Add the plugin to ArgoCD

The repo server is responsible for running Helm commands to push changes into the cluster.  This is
[documented](https://argo-cd.readthedocs.io/en/stable/user-guide/helm/#using-initcontainers) in ArgoCD, however these instructions didn't work, at least with our current chart
version of 7.4.1. (Note there were some discussions on the `argocd-helm` GitHub about a specific update causing a breaking change in this functionality, and the latest docs were
the "fix" for the breaking change. So could be that the old instructions would have worked fine.) To do this, we'll add an init container to the repo server that installs the
plugin to a shared volume mount.

```yaml
aissemble-infrastructure-chart:
  argo-cd:
    repoServer:
      env:
        - name: HELM_CACHE_HOME
          value: /helm-working-dir/.cache #Work around for install issue where plugins and cache locations being the same conflicts
      initContainers:
        - name: helm-plugin-install
          image: alpine/helm
          env:
            - name: HELM_PLUGINS
              value: /helm-working-dir/plugins #Configure Helm to write to the volume mount that the repo server uses
          volumeMounts:
            - mountPath: /helm-working-dir
              name: helm-working-dir
          command: [ "/bin/sh", "-c" ]
          args: # install plugin
            - apk --no-cache add curl;
              helm plugin install https://github.com/aslafy-z/helm-git --version 1.3.0;
              chmod -R 777 $HELM_PLUGINS;
```

### Updating the chart dependency

To use your modified chart in the downstream project, the following changes should be made to the `Chart.yaml` file that pulls in the modified chart as a dependency:

 * Point `repository` to the modified chart on your branch in GitHub
   * e.g.: `git+https://github.com/boozallen/aissemble/@extensions/extensions-helm/<modified-chart>?ref=<your-branch>`
   * _**NB:** if the chart being tested is in a nested project under extensions-helm, update the repo path accordingly_
 * Set `version` to `1.0.0`

### Potential pitfalls

 * There is an issue with committing Chart.lock files when using an explicit repository vs a repository alias, so Chart.lock files must not be committed.
