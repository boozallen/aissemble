# aiSSEMBLE&trade; Ready Jenkins Deployment
The containers in this project provide an instance of Jenkins that 
requires minimal setup for aiSSEMBLE projects.  It comes preconfigured 
the plugins and configuration files you need to quickly setup up Jenkins
for your aiSSEMBLE projects.  This project extends default Jenkins Long
Term Support (LTS) Docker images.

> NOTE: this capability is in flight - it currently uses just a single controller
> instance, but will shortly include a kubernetes-ready deployment that
> will auto-scale your agents to maximize your team's productivity.

## Configuration Steps
While we have automated as much as possible for you so this instance is 
ready to run, the following steps MUST be performed.

### Before Launching the Container
Some configurations must occur prior to launching your container.

#### Jenkins Path Configuration
Because the `PATH` environment variable is set in the Jenkins provided 
Dockerfile this project extends,it must be overridden at startup.  Update
your docker-compose.yaml file with the following to ensure pyenv support
in addition to other tools you may need on the `PATH`.
```yaml
  jenkins-controller:
    ...
    environment:
      # THE FOLLOWING LINE **MUST** BE ADDED:
      PATH: /var/pyenv/bin:/bin:/opt/java/openjdk/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
```
Alternatively, you can set an environment variable in the Jenkins 
configuration at runtime.

### After Launching Jenkins
Launch Jenkins through a `docker-compose up` or equivalent, then 
perform the following configuration steps.

#### Use One-Time Password to Log In
During startup, Jenkins will provide an initial password to access
the system in the log, which will look something like this:
```bash
*************************************************************
*************************************************************
*************************************************************

Jenkins initial setup is required. An admin user has been created and a password generated.
Please use the following password to proceed to installation:

<YOUR PASSWORD WILL BE HERE>

This may also be found at: /var/jenkins_home/secrets/initialAdminPassword

*************************************************************
*************************************************************
*************************************************************
```

Open your browser to `http://localhost:8080`.  You will be prompted to enter
the code found in YOUR log file.

#### Customize Jenkins
You will then be prompted to add plugins.  All required plugins have already
been included in this image, so choose:
* `Select plugins to install`
* Then select `None` in the top left
* Finally, select `Install` in the bottom left

#### Add Admin User
Enter the details for your admin user and select `Save and Continue`.

#### Update the URL
If appropriate, update the base URL to use for your instance to your
valid domain name and select `Save and Finish`.

#### Add Jenkins Credentials
A minimum of two credentials need to be configured to get started.  Select
`Manage Jenkins` --> `Manage Credentials` --> `(global) Domain` to add
both credentials.

##### Github Credential
Select `Add Credentials`, then enter the following information credential
that will allow Jenkins to access GitHub resources:
* `Username`: This should be your GitHub username (typically Lastname-Firstname)
* Check the `Treat usename as a secret` tick box
* `Password`: Use the following steps to create an access code:
  * On your GitHub profile, select `Settings`
  * Select `Developer Settings` (at the very bottom)
  * Choose `Personal access tokens`
  * Finally, use `Generate new token`
  * Select the `repo` options and `Generate Token` at the bottom as 
  your token options
* `ID`: Use `github`
* Finally, select `OK`

##### Artifact Repository Credential(s)
Repeat the general process above to create a credential that will enable push
access to your artifact repository instance(s) (e.g., Maven, PyPI, Docker, Helm).

#### Maven settings.xml
This step is needed to provide your project-specific artifact repository
credential(s) as well as customize the global `settings.xml` that will be
used for your Maven builds.

Select `Manage Jenkins` --> `Managed files` --> edit button next to `maven-global-settings`.

Next, select `Add` under Server Credentials.  Then enter the following:
* `ServerId`: `artifactRepository`
* `Credentials`: Select the `artifactRepository` from the drop down list

Next, select `Add` under Server Credentials. Then enter the following:
* `ServerId`: `<URL TO ARTIFACT REPOSITORY>`
* `Credentials`: Select the `appropriate credential` from the drop down list

If you want to update the `settings.xml` file, you can also do that before 
select `Submit`

#### Ensure Your Jenkinsfiles Use the Global settings.xml
To have your Jenkinsfiles leverage the global `settings.xml` file that was just 
configured, ensure the following configuration exists in the file:
```groovy
configFileProvider(
    [configFile(fileId: 'maven-global-settings', variable: 'MAVEN_GLOBAL_SETTINGS')]) {
    maven.run([profiles: ['ci']], ["YOUR MAVEN COMMAND(S)", "-s $MAVEN_GLOBAL_SETTINGS"])
}
```

You are now ready to add your build jobs!

## Kubernetes-CLI Plugin

### Prerequisites
Install the [Kubernetes-CLI plugin](https://plugins.jenkins.io/kubernetes-cli/) through the Jenkins dashboard 
(Manage Jenkins > Manage Plugins > Available > Search for "Kubernetes CLI" > Install). After installing the 
plugin, add the kube config file was added to Jenkins' credential manager:
* Manage Jenkins > Manage Credentials > select relevant store system > select relevant domain > click "+ Add Credentials" button in top right
* select "Secret file" for kind field
* enter an ID (this will be the **credentialsId** argument the plugin will reference in a groovy script)
* enter a description (optional but recommended)
* upload kube config file, see below for an example:

```
apiVersion: v1
kind: Config
clusters:
- name: "local"
  cluster:
    insecure-skip-tls-verify: true
    server: "<https:your-server.sh/>"

users:
- name: "local"
  user:
    token: "<kubeconfig-user-token-string>"


contexts:
- name: "local"
  context:
    user: "local"
    cluster: "local"

current-context: "local"
```
Note: on a machine, a kube config file is normally stored at: `~/.kube/config`

### Purpose and Usage
Prior to the addition of the [Kubernetes-cli plugin](https://plugins.jenkins.io/kubernetes-cli/) to aiSSEMBLE's groovy 
scripts, a new Jenkins agent would have to be configured with a kube config file manually. With the usage of the 
Kubernetes-CLI plugin in aiSSEMBLE, each build can now reference the kube config file, which was saved to Jenkins' 
managed credentials as a secret file, bypassing the need to perform any manual setup with a newly spun up Jenkins 
agent. For example, in `devops/JenkinsfileBuild.groovy`:

```groovy
withKubeConfig([credentialsId: 'kubeconfig']) {
    withMaven(maven: 'maven') {
        configFileProvider([configFile(fileId: 'maven-global-settings', variable: 'MAVEN_GLOBAL_SETTINGS')]) {
            sh "./mvnw -s $MAVEN_GLOBAL_SETTINGS clean deploy -Pci,arm64 -pl :aissemble-quarkus,:aissemble-fastapi,:aissemble-spark"
        }
    }
}
```

The `withKubeConfig()` call accesses the secret file (with ID kubeconfig) saved to Jenkins' managed credentials, and 
temporarily applies those kubernetes configurations to the instance's environment. Note, the kube config file houses 
the access token, allowing the pipeline to authenticate and interface with the kubernetes cluster. This security 
protocol could be upgraded to OIDC Authenticator in later iterations.

