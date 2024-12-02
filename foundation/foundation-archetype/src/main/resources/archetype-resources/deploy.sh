#!/bin/sh

## ArgoCD local deployment script
deploy() {
  argocd app get ${artifactId} --server localhost:30080 --plaintext &> /dev/null

  if [ $? -eq 0 ]; then
    echo "${artifactId} is deployed"
  else
    argocd app create ${artifactId} \
      --server localhost:30080 --plaintext \
      --dest-namespace ${artifactId} \
      --dest-server https://kubernetes.default.svc \
      --repo ${projectGitUrl} \
      --path ${artifactId}-deploy/src/main/resources \
      --revision main \
      --values values.yaml \
      --values values-dev.yaml \
      --sync-policy automated
  fi
}

down() {
  argocd app get ${artifactId} --server localhost:30080 --plaintext &> /dev/null

  if [ $? -eq 0 ]; then
    argocd app delete ${artifactId} --server localhost:30080 --plaintext
  else
    echo "${artifactId} is not deployed"
}

print_usage() {
  echo "Usage: $0 [up|down]"
}

if [ "$1" = "up" ]; then
  deploy
else
  if [ "$1" = "down" ]; then
    down
  else
    print_usage
  fi
fi

