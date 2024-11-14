# Actions Runner Controller

The [Actions Runner Controller](https://github.com/actions/actions-runner-controller) (ARC) is a project to ease the
creation of self-hosted GitHub action runners.  The following content is largely in line with the information provided
in the GitHub documentation but is preserved here as an audit trail in case the online information changes, since ARC is
still pre-1.0.

## Standing up ARC from scratch

### Creating the Controller

The controller only needs to be created once for the whole cluster.  The controller MUST be created before the runner
sets and the versions must match between the two.  The controller is responsible for detecting new ARC CRD manifests and
provisioning the appropriate resources for the runner-set, including the listener which recieves action run requests
from GitHub.

```sh
helm install arc-controller oci://ghcr.io/actions/actions-runner-controller-charts/gha-runner-scale-set-controller \
             --namespace gh-actions-controller \
             --create-namespace \
             -f controller-values.yaml
```

### Creating the Runner Scale Set

Previously, we needed special permissions on the runner pod to execute `helm install --dry-run` as an integration test
for our baseline charts. However, this requires pretty expansive permissions (cluster level + secrets retrieval). The
dry-run IT wasn't really adding much over the simple strict linting approach so it's been dropped. The custom service
account setup has been left in place simply to demonstrate how this _would_ be done if we need permissions for some
other use case in the future.

>[!NOTE]
>The namespace is currently hard-coded in the YAML file, so if you intend to deploy to a different namespace when
>installing the runner-set, you must update the YAML file as well.  This is required because a RoleBinding object in
>Kubernetes must specify the ServiceAccount namespace explicitly.

```sh
kubectl apply -f arc-runner-service-account.yaml
```

Finally, the runner-set can be created via Helm.  The Github token is any Personal Access Token (Classic) that has Repo
permissions.  In the future, this could be changed to use a Github App so that it isn't tied to a specific user.
Additionally, we could consider using SealedSecrets or a pre-defined secret for the App settings.

>[!NOTE]
>The installation name (`arc-runner-set-aissemble`) will be the label used to select the runner set in a workflow file.

```sh
helm install arc-runner-set-aissemble oci://ghcr.io/actions/actions-runner-controller-charts/gha-runner-scale-set \
             --namespace gh-actions-aissemble \
             --create-namespace \
             --set githubConfigSecret.github_token="{TOKEN}" \
             -f runnerset-values.yaml
```

## Upgrading ARC or Updating configuration

### Controller

The controller cannot be upgraded in place according to the Github documentation, so all runnersets in the cluster must
be uninstalled, then the controller uninstalled, and then the new controller version installed.  A `helm upgrade` to
simply update the values in the values file has not been tested, so it is unclear if a full uninstall is needed for that
case.

### Runner Set

If the values file has been updated and simply needs to be re-applied, the `--reuse-values` flag can be used to preserve
the existing GH PAT.  It is unclear whether `--reuse-values` would allow removal of values from the values file, and so
the token may be required to achieve this.

```sh
helm upgrade arc-runner-set-aissemble oci://ghcr.io/actions/actions-runner-controller-charts/gha-runner-scale-set \
             --namespace gh-actions-aissemble \
             --reuse-values \
             -f runnerset-values.yaml
```
