#!/bin/bash

###
# #%L
# aiSSEMBLE::Extensions::Docker::Configuration Store
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###

set -e
SCRIPT=`basename ${BASH_SOURCE[0]}`

function usage {
  cat<< EOF
  Usage: $SCRIPT
  Options:
  -h | --help                    Display help information.
  -c | --ca-bundle <ca-bundle>   PEM encoded CA bundle which will be used to validate the webhook's server certificate.
  -n | --namespace <namespace>   The namespace where the the config store is installed.			
  -s | --service <service>       The name of the config store webhook service.
  -w | --webhook-name <name>     The name of the webhook for the configuration store.
EOF
}

function parse_arguments {
  while [[ $# -gt 0 ]]
  do
    case "$1" in
      -c|--ca-bundle)
      if [[ -n "$2" ]]; then
        CA_BUNDLE="$2"
      else
        echo "-c or --ca-bundle requires a value."
        exit 1
      fi
      shift 2
      continue
      ;;
      -n|--namespace)
      if [[ -n "$2" ]]; then
        NAMESPACE="$2"
      else
        echo "-n or --namespace requires a value."
        exit 1
      fi
      shift 2
      continue
      ;;
      -s|--service)
      if [[ -n "$2" ]]; then
        SERVICE="$2"
      else
        echo "-s or --service requires a value."
        exit 1
      fi
      shift 2
      continue
      ;;
      -w|--webhook-name)
      if [[ -n "$2" ]]; then
        WEBHOOK_NAME="$2"
      else
        echo "-w or --webhook-name requires a value."
        exit 1
      fi
      shift 2
      continue
      ;;
      -h|--help)
      usage
      exit 0
      ;;
      --)              # End of all options.
        shift
        break
      ;;
      '')              # End of all options.
        break
      ;;
      *)
        echo "Unrecognized option: $1"
        exit 1
      ;;
    esac
  done
}

parse_arguments "$@"

echo "Creating a MutatingWebhookConfiguration"

TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)

TMP_DIR="/tmp/config-store-webhook/"
mkdir -p ${TMP_DIR}

# Base64 encode and then remove the any line breaks to avoid issues in the curl command
ca_bundle=$(echo "$CA_BUNDLE"| base64 -w 0| tr -d '\n')

# Create the MutatingWebhookConfiguration on the API server
# Webhook does not run on any resources in kube-* and the release namespaces.
# This is a best practice to avoid avoiding deadlocks in self-hosted webhooks.
# https://kubernetes.io/docs/reference/access-authn-authz/extensible-admission-controllers/#avoiding-deadlocks-in-self-hosted-webhooks
STATUS=$(curl -ik \
  -o ${TMP_DIR}/output \
  -w "%{http_code}" \
  -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "apiVersion": "admissionregistration.k8s.io/v1",
  "kind": "MutatingWebhookConfiguration",
  "metadata": {
    "name": "'"$WEBHOOK_NAME"'",
    "namespace": "'"$NAMESPACE"'"
  },
  "webhooks": [
    {
      "name": "'"$WEBHOOK_NAME"'",
      "sideEffects": "None",
      "admissionReviewVersions": ["v1", "v1beta1"],
      "matchPolicy": "Equivalent",
      "failurePolicy": "Fail",
      "clientConfig": {
        "caBundle": "'"$ca_bundle"'",
        "service": {
          "name": "'"$SERVICE"'",
          "namespace": "'"$NAMESPACE"'",
          "path": "/webhook/process",
          "port": 443
        }
      },
      "rules": [
        {
          "operations": ["CREATE", "UPDATE"],
          "apiGroups": ["", "apps"],
          "apiVersions": ["*"],
          "resources": ["*"],
          "scope": "Namespaced"
        }
      ],
      "namespaceSelector": {
        "matchExpressions": [
          {
            "key": "kubernetes.io/metadata.name",
            "operator": "NotIn",
            "values": ["kube-system", "kube-public", "kube-node-lease", "'"$NAMESPACE"'"]
          }
        ]
      }
    }
  ]
}' \
https://kubernetes.default.svc/apis/admissionregistration.k8s.io/v1/mutatingwebhookconfigurations)

cat ${TMP_DIR}/output

case "$STATUS" in
  201)
    printf "\nSuccess - mutating webhook created.\n"
  ;;
  409)
    printf "\nSuccess - mutating webhook already exists.\n"
    ;;
    *)
    printf "\nFailed creating mutating webhook.\n"
    printf "$STATUS"
    exit 1
    ;;
esac

# Clean up after we're done.
printf "\nDeleting ${TMP_DIR}.\n"
rm -rf ${TMP_DIR}
