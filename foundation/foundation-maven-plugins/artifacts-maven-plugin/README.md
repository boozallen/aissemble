# Artifacts Maven Plugin

The Artifacts Maven Plugin is used to deploy artifacts to an alternate repository (local or remote). The plugin includes two goals.
* `propagate` - Goal used to deploy artifacts from a given set of Maven coordinates(groupId:artifactId:version) to an alternate repository. 

* `propagate-all` - Goal used to deploy a project's direct and transitive dependencies of a Maven project to an alternate repository. This includes all downstream modules. 

Below shows the different types of artifacts that can be deployed.

* pom.xml
* jar
* sources-jar
* tests-jar
* javadoc-jar
* war

# Installation
To install the plugin, execute `mvn clean install`. The plugin will be installed on a successful build.

## Propagate
Below describes how to use the `propagate` goal. 

* Execute `mvn com.boozallen.aissemble:artifacts-maven-plugin:propagate -DgroupId=[group ID] -DartifactId=[artifact ID] -Dversion=[version] -Durl=[url] -DrepositoryId=[repository ID]`. If successfull, the artifacts at the given coordinates will be deployed to the alternate repository given by the URL (Note: The property `repositoryId` is an optional parameter and it is only needed if the alternate repository requires credentials)

## Propagate All
Below describes how to use the `propagate-all` goal. 

* Inside a Maven project, execute `mvn com.boozallen.aissemble:propagate-all -Durl=[url] -DrepositoryId=[repository ID]`. If successfull, all of the project's direct and transitive dependencies will be deployed to an alternate repository given by the URL. (Note: The property `repositoryId` is an optional parameter and it is only needed if the alternate repository requires credentials)

## Remote Repository

If deploying to a remote repository, it is likely you will need to add in credentials for deployment. The `repositoryId` will need to be added in order to locate the credentials for a repository. Credentials are normally added in the `settings.xml` file in you .m2 directory

## Local Repository

If deploying to a local repository, you do not need to include the property `repositoryId` to add in credentials. You will however need to prefix your `url` with `file://`

