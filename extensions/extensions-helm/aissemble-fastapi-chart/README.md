# aiSSEMBLE&trade; FastAPI Helm Chart
Baseline Helm chart for packaging and deploying FastAPI web applications. Chart is built and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See https://github.com/kokuwaio/helm-maven-plugin for more details. 

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the image and tag with the desired image built from the aiSSEMBLE FastAPI baseline image. For example:
```bash
helm install fastapi ghcr.io/boozallen/aissemble-fastapi-chart --version <AISSEMBLE-VERSION>
```
**Note**: *the version should match the aiSSEMBLE project version.*

# Properties
| Property                      | Description                                                     | Required Override | Default                                                                     |
|-------------------------------|-----------------------------------------------------------------|-------------------|-----------------------------------------------------------------------------|
| app.name                      | Sets label for app.kubernetes.io/name                           | No                | Chart.Name                                                                  |
| app.version                   | Sets label for app.kubernetes.io/version                        | No                | Chart.AppVersion (aiSSEMBLE project version)                                |
| deployment.ports              | The deployment ports                                            | No                | - name: http <br/>&emsp;&emsp;containerPort: 8080 <br/>&emsp; protocol: TCP |
| deployment.restartPolicy      | The deployment restart policy                                   | No                | Always                                                                      |
| deployment.volumes            | The volumes for the pod                                         | No                | None                                                                        |
| deployment.volumeMounts       | The volume mounts for the pod                                   | No                | None                                                                        |
| deployment.securityContext    | The security context for the pod                                | No                | None                                                                        |
| deployment.serviceAccountName | The service account for the pod                                 | No                | Default user in the cluster namespace                                       |
| deployment.env                | The environment variables for the pod                           | No                | None                                                                        |
| deployment.args               | The args for the pod                                            | No                | None                                                                        |
| hostname                      | The hostname for the application                                | No                | fastapi                                                                     |
| image.name                    | The image name                                                  | Yes               | boozallen/aissemble-fastapi                                                 |
| image.imagePullPolicy         | The image pull policy                                           | No                | Always (ensures local docker image is pulled, rather than from Nexus repo)  |
| image.dockerRepo              | The image docker repository                                     | No                | NB: OSS: UPDATE_WITH_DEFAULT_DOCKER_REPOSITORY_HERE                         |
| image.tag                     | The image tag                                                   | No                | Chart.AppVersion                                                            |
| replicaCount                  | Sets desired number of replicas (instances)                     | No                | 1                                                                           |
| service.spec.ports            | The service spec ports                                          | No                | - name: http <br/>&emsp;&emsp;port: 8080 <br/>&emsp; targetPort: 80         |
| service.type                  | The service type                                                | No                | ClusterIP                                                                   |
| namespaceOverride             | Namespace to deploy to instead of the Helm release namespace    | No                | .Release.Namespace                                                          |

# Developing with aissemble-fastapi
When leveraging deployments that utilize the aissemble-fastapi Helm charts as a dependency, it may be necessary to manually modify the deployment's Dockerfile of the downstream project so that the python file defining the FastAPI business logic is copied into the Docker container. For example:

```Docker
COPY ./target/scripts/main.py /app/main.py
```

`main.py` defines the FastAPI application's business logic. In the above modification, this file is added to the Docker container. By default, it is expected that the python file defining FastAPI application resides in the `/app/` directory. If the file is saved in a different location, then the `MODULE` environment variable must be overridden to point to wherever the file is saved.