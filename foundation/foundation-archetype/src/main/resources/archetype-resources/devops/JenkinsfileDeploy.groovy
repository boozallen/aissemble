def gitId = 'github'
def gitRepo = '${projectGitUrl}'
// Set branch in the Jenkins Job or set here if not variable
def gitBranch = 'refs/heads/${branch}'
def projectDirectory = '${rootArtifactId}'
def jenkinsSteps

node('master') {
    dir(projectDirectory) {
        checkout([$class: 'GitSCM', branches: [[name: gitBranch]], userRemoteConfigs: [[credentialsId: gitId, url: gitRepo]]])
        jenkinsSteps = load 'devops/jenkinsPipelineSteps.groovy'
    }
}

stage("Authenticate") {
    node('master') {
        // TODO: replace the helmfilePassword with correct value
        withCredentials([string(credentialsId: 'helmfilePassword', variable: 'helmfilePassword')]) {
            try {
                dir(projectDirectory) {
                    slackSend color: "warning",
                            message: "${projectName} authenticating"
                    // TODO: add helmfile authentication command
                }
            } catch (err) {
                slackSend color: "danger",
                        message: "${projectName} failed to authenticate properly"
                throw err
            }
        }
    }
}

stage("Deploy") {
    node('master') {
        try {
            dir(projectDirectory) {
                slackSend color: "warning",
                        message: "${projectName} deploying"
                // TODO: add the helmfile deploy comment
                slackSend color: "good",
                        message: "${projectName} deployed successfully"
            }
        } catch (err) {
            slackSend color: "danger",
                    message: "${projectName} failed to deploy properly"
            throw err
        }
    }
}

stage("Running") {
    node('master') {
        try {
            timeout(environmentUptime) {
                input message: 'Ready to kill the environment?', ok: 'Kill Test Server'
            }
        } catch (err) {}
    }
}

stage("Teardown") {
    node('master') {
        try {
            dir(projectDirectory) {
                slackSend color: "warning",
                        message: "${projectName} shutting down"
                //TODO: add the helmfile terminate command
            }
        } catch (err) {
            slackSend color: "danger",
                    message: "${projectName} failed to shut down properly"
            throw err
        }
    }
}

