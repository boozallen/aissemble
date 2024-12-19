/*-
 * #%L
 * AIOps Foundation::Archetype::Project
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.lang.model.SourceVersion

final Logger logger = LoggerFactory.getLogger("com.boozallen.aissemble.foundation.archetype-post-generate")

def dir = new File(new File(request.outputDirectory), request.artifactId)
file = new File(dir,"deploy.sh")
file.setExecutable(true, false)

def groupIdRegex = ~'^[a-z][a-z0-9]*(?:\\.[a-z][a-z0-9]*)*$' // lowercase letters, numbers, and periods
def artifactIdRegex = ~'^[a-z][a-z0-9]*(?:-?[\\da-z]+)*$' // lowercase letters, numbers, and hyphens
def versionRegex = ~'^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$' // Semantic Versioning
def packageRegex = ~'^[a-z][a-z0-9]*(?:\\.[a-z][a-z0-9]*)*$' // lowercase letters, numbers, and periods
def projectGitUrlRegex = ~'^[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)$' // valid URL, TLD between 2 and 6 characters
def yesRegex = ~'^[yY].*$' // start with lowercase or uppercase 'y'

def criticalMessages = []
def nonCriticalMessages = []

if (!(request.groupId =~ groupIdRegex)) {
    criticalMessages.add("The provided groupId does not conform to Maven naming conventions (https://maven.apache.org/guides/mini/guide-naming-conventions.html). It is strongly recommended to follow the naming conventions (e.g. avoid hyphens in groupId) in order to avoid build errors down the line.")
}
if (!(request.artifactId =~ artifactIdRegex)) {
    criticalMessages.add("The provided artifactId does not conform to Maven naming conventions (https://maven.apache.org/guides/mini/guide-naming-conventions.html). It is strongly recommended to follow the naming conventions (e.g. avoid periods in artifactId) in order to avoid build errors down the line.")
}
if (!(request.version =~ versionRegex)) {
    nonCriticalMessages.add("The provided version does not conform to Semantic Versioning conventions (https://semver.org/#semantic-versioning-200). It is recommended to use Semantic Versioning.")
}
if (!(request.package =~ packageRegex)) {
    criticalMessages.add("The provided package does not conform to Java package naming rules (https://docs.oracle.com/javase/specs/jls/se6/html/packages.html#7.7). It is strongly recommended to follow the package naming rules (e.g. avoid hyphens in package) in order to avoid build errors down the line.")
}
for (identifier in request.package.split("\\.")) {
    if (SourceVersion.isKeyword(identifier)) {
        criticalMessages.add("The provided package contains a Java keyword: " + identifier)
    }
}
if (!(request.getProperties().getProperty('projectGitUrl') =~ projectGitUrlRegex)) {
    nonCriticalMessages.add("The provided projectGitUrl is not a valid URL.")
}

if (nonCriticalMessages.size() > 0) {
    logger.warn("The following items require attention:")

    nonCriticalMessages.each {
        logger.warn(it)
    }
}

if (criticalMessages.size() > 0) {
    def concatenatedCriticalMessages = ""
    logger.error("The following issues may make the generated project unusable:")

    criticalMessages.each {
        logger.error(it)
        concatenatedCriticalMessages = concatenatedCriticalMessages + " " + it
    }

    if (System.console() != null) { // null during IDE project creation or when running unit tests
        def result = System.console().readLine "It is recommended to regenerate the project with valid parameters. Delete current project? (yes/no): "
        if (result =~ yesRegex) {
            dir.deleteDir()
            concatenatedCriticalMessages = concatenatedCriticalMessages + " " + "Project deleted."
        }
    }

    throw new Exception(concatenatedCriticalMessages)
}

// Set maven wrapper files to be executable
def mvnwFile = new File( request.getOutputDirectory(), request.getArtifactId() + "/mvnw" )
mvnwFile.setExecutable(true, false)
def mvnwFileCmd = new File( request.getOutputDirectory(), request.getArtifactId() + "/mvnw.cmd" )
mvnwFileCmd.setExecutable(true, false)
