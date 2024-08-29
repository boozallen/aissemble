/*-
 * #%L
 * AIOps Foundation::Archetype::Project
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
/**
 * Common steps for pipelines
 */
def argocdAuthenticate(argocdUrl, argocdPassword) {
    sh '''
        # Check if Argocd is installed
        if ! type "/usr/local/bin/argocd" > /dev/null; then
            echo "Installing Argocd..."
            sudo curl --silent --location -o /usr/local/bin/argocd https://github.com/argoproj/argo-cd/releases/latest/download/argocd-linux-amd64
            sudo chmod +x /usr/local/bin/argocd
            /usr/local/bin/argocd version --client
        fi
    '''
    sh "/usr/local/bin/argocd login ${argocdUrl} --username admin --password ${argocdPassword} --skip-test-tls --grpc-web"

}

def argocdTerminate(argocdAppName) {
    try {
       sh "/usr/local/bin/argocd app delete ${argocdAppName} --grpc-web"
    } catch (err) {
        slackSend color: "warning",
                    message: "${projectName} app not found on the server, deploying..."
        echo "App name not found on the server, deploying..."
    }
}

def argocdSync(argocdAppName, argocdBranch) {
    sh "sleep 10"
    sh "/usr/local/bin/argocd app sync ${argocdAppName} --grpc-web --revision ${argocdBranch}"
}

def argocdDeploy(argocdAppName, argocdUrl, argocdDestinationServer, gitRepo, argocdBranch, argocdNamespace, values) {
    String valuesParam = "--values " + values.join(" --values ")
    sh "sleep 60" //wait for app to shutdown before creation
    sh "/usr/local/bin/argocd app create ${argocdAppName} --grpc-web \
            --server ${argocdUrl} \
            --dest-namespace ${argocdNamespace} \
            --dest-server ${argocdDestinationServer} \
            --repo ${gitRepo} \
            --path ${artifactId}-deploy/src/main/resources --revision ${argocdBranch} \
            ${valuesParam}"
    try {
        argocdSync(argocdAppName, argocdBranch)
    } catch (err) {
        echo "Sync failed, retrying..."
        slackSend color: "warning",
                    message: "${projectName} Argocd sync failed, retrying..."
        retry(3) {
            argocdSync(argocdAppName, argocdBranch)
        }
    }
}

return this
