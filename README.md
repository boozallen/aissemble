# aiSSEMBLE&trade;

## aiSSEMBLE Overview

### Purpose of the aiSSEMBLE

aiSSEMBLE is Booz Allen's lean manufacturing approach for holistically designing, developing and fielding
AI solutions across the engineering lifecycle from data processing to model building, tuning, and training to secure
operational deployment. This repository consists of standardized components which make it easy for dev teams to quickly
reuse and apply to their project to drive consistency, reliability and low delivery risk. aiSSEMBLE offers projects the
rapid generation of necessary scaffolding, boilerplate libraries, and container images with the flexibility of custom
configuration. It consists of pre-fabricated components that can be used as is within your projects and generated
capabilities that can be extended.

### Languages and frameworks used to implement aiSSEMBLE

Many languages can be useful across the full breadth of AI solutions. Currently, the following languages are leveraged:
* Data Delivery / Machine Learning Inference
    * Java
    * Python
* Machine Learning Training
    * Python

In addition, the following build tools and container frameworks are an important part of aiSSEMBLE:
* Fermenter MDA
* Maven
    * Habushu Maven Plugin (builds Python modules)
    * Orphedomos Maven Plugin (build Docker modules)
    * Helm Maven Plugin
* Kubernetes
* Helm

### Detailed Documentation

aiSSEMBLE documentation will be posted on GitHub pages along with this repository.  We are [actively porting our existing
documentation](https://github.com/boozallen/aissemble/issues/5) and will update this section with a link as soon as it is up and available.

### aiSSEMBLE Releases

aiSSEMBLE is currently released about once a month, but we intend to increase to around twice a month as we get our
processes adjusted and honed into the public GitHub

## Configurations

For details on using the configuration tool, please consult our [Configuring Your Environment guidance (LINK COMING SOON)](https://boozallen.github.io/aissemble/current-dev/configurations.html).

## Build

The following steps will build aiSSEMBLE. *You must follow the configuration guidance above first*.
1. To get started, pull the latest code for the aiSSEMBLE repo from git.
2. Ensure Rancher Desktop is running.
3. Build the project locally using the `mvn clean install` command.
    * A successful build will have an output similar to the below.
    ```
            [INFO] ------------------------------------------------------------------------
            [INFO] BUILD SUCCESS
            [INFO] ------------------------------------------------------------------------
            [INFO] Total time:  10:16 min
            [INFO] Finished at: 2021-09-09T10:01:10-04:00
            [INFO] ------------------------------------------------------------------------
    ```

### Helpful Profiles
The aiSSEMBLE baseline project provides several build profiles that may be helpful for different development environments.
To activate each one, use the standard Maven syntax: `mvn clean install -P[profile_name]`, for
instance, `mvn clean install -PnoRdAdmin`.  There are many profiles you can find in the root `pom.xml` file. The
following profiles are often useful when first starting with aiSSEMBLE:

* *noRdAdmin*: For configurations that disallow granting administrator privileges to Rancher Desktop. Testing frameworks
  leveraged by aiSSEMBLE may, at times, assume that the docker unix socket is located at `/var/run/docker.sock`, which is
  not the case when presented with a non-elevated Rancher installation.  Activating this profile will override the
  `DOCKER_HOST` seen by these dependencies, pointing it instead at `unix://$HOME/.rd/docker.sock`.
* *integration-test*: Some integration tests require Docker and automatically start/stop Docker Compose services while
  executing tests (i.e. see the test/test-mda-models/test-data-delivery-pyspark-patterns module). **Note that the Maven
  build does not build the Docker images directly. The images are built within the Kubernetes cluster to speed up
  development builds and save disk space.**

## Use a Maven Archetype to Create a New aiSSEMBLE-Based  Project

The first step in creating a new project is to leverage Maven’s archetype functionality to incept a new Maven project
that will contain all of your aiSSEMBLE component implementations - Data Delivery and Machine Learning pipelines as
well as Path to Production modules.

Open a terminal to the location in which you want your project to live and execute the following command:
```
mvn archetype:generate \
    -DarchetypeGroupId=com.boozallen.aissemble \
    -DarchetypeArtifactId=foundation-archetype \
    -DarchetypeVersion=<version number>
 ```
This command will trigger an interactive questionnaire giving you the opportunity to enter the following information (in order):
1. groupId
2. artifactId
3. version
4. package
5. projectGitUrl
6. projectName

* For details on these fields refer to (LINK COMING SOON) https://boozallen.github.io/aissemble/current-dev/archetype.html#_use_a_maven_archetype_to_create_a_new_project

* For detailed instructions on adding a pipeline refer to (LINK COMING SOON) https://boozallen.github.io/aissemble/current-dev/add-pipelines-to-build.html

## Troubleshooting

When executing the `aissemble` build for the first time, you may encounter the following transient error when building
the `test-data-delivery-pyspark-patterns` module:
```
:: problems summary
:::::: WARNINGS
                [NOT FOUND  ] org.apache.commons#commons-math3;3.2!commons-math3.jar (0ms)
         ==== local-m2-cache: tried           file:/Users/ekonieczny/.m2/repository/org/apache/commons/commons-math3/3.2/commons-math3-3.2.jar
                 ::::::::::::::::::::::::::::::::::::::::::::::
                 ::              FAILED DOWNLOADS            ::
                 :: ^ see resolution messages for details ^. ::
                 ::::::::::::::::::::::::::::::::::::::::::::::
                 :: org.apache.commons#commons-math3;3.2!commons-math3.jar
                 ::::::::::::::::::::::::::::::::::::::::::::::
:::: ERRORS
        SERVER ERROR: Bad Gateway url=https://dl.bintray.com/spark-packages/maven/org/sonatype/oss/oss-parent/9/oss-parent-9.jar
         SERVER ERROR: Bad Gateway url=https://dl.bintray.com/spark-packages/maven/org/antlr/antlr4-master/4.7/antlr4-master-4.7.jar
         SERVER ERROR: Bad Gateway url=https://dl.bintray.com/spark-packages/maven/org/antlr/antlr-master/3.5.2/antlr-master-3.5.2.jar
```
If this occurs, remove your local Ivy cache (`rm -rf ~/.ivy2`) and then manually download the dependency that failed to
download. Taking the above error message as an example, the following Maven command would download the needed commons-math3 jar:

`mvn org.apache.maven.plugins:maven-dependency-plugin:get -Dartifact=org.apache.commons:commons-math3:3.2`