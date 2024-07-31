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

# Based on gencerts.sh script from ghcr.io/kubeflow/spark-operator (Copyright 2018 Google LLC, Apache License, Version 2.0 [http://www.apache.org/licenses/LICENSE-2.0])

set -e
SCRIPT=`basename ${BASH_SOURCE[0]}`

function usage {
  cat<< EOF
  Usage: $SCRIPT
  Options:
  -h | --help                    Display help information.
  -n | --namespace <namespace>   The namespace where the the config store is installed.			
  -s | --service <service>       The name of the config store webhook service.
  -c | --secret-name <name>      The name of the secret that will hold the certs
EOF
}

function parse_arguments {
  while [[ $# -gt 0 ]]
  do
    case "$1" in
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
      -c|--secret-name)
      if [[ -n "$2" ]]; then
        SECRET_NAME="$2"
      else
        echo "-c or --secret-name requires a value."
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

TMP_DIR="/tmp/config-store-webhook-certs"

echo "Generating certs for the config store mutating webhook in ${TMP_DIR}."
mkdir -p ${TMP_DIR}
cat > ${TMP_DIR}/server.conf << EOF
[req]
req_extensions = v3_req
distinguished_name = req_distinguished_name
[req_distinguished_name]
[ v3_req ]
basicConstraints = CA:FALSE
keyUsage = nonRepudiation, digitalSignature, keyEncipherment
extendedKeyUsage = clientAuth, serverAuth
subjectAltName = DNS:${SERVICE}.${NAMESPACE}.svc
EOF

# Create a certificate authority.
touch ${TMP_DIR}/.rnd
export RANDFILE=${TMP_DIR}/.rnd
openssl genrsa -out ${TMP_DIR}/ca-key.pem 2048
openssl req -x509 -new -nodes -key ${TMP_DIR}/ca-key.pem -days 100000 -out ${TMP_DIR}/ca-cert.pem -subj "/CN=${SERVICE}-CA"

# Create a server certificate.
openssl genrsa -out ${TMP_DIR}/server-key.pem 2048
# Note the CN is the DNS name of the service of the webhook.
openssl req -new -key ${TMP_DIR}/server-key.pem -out ${TMP_DIR}/server.csr -subj "/CN=${SERVICE}.${NAMESPACE}.svc" -config ${TMP_DIR}/server.conf
openssl x509 -req -in ${TMP_DIR}/server.csr -CA ${TMP_DIR}/ca-cert.pem -CAkey ${TMP_DIR}/ca-key.pem -CAcreateserial -out ${TMP_DIR}/server-cert.pem -days 100000 -extensions v3_req -extfile ${TMP_DIR}/server.conf

TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)

# Base64 encode secrets and then remove the trailing newline to avoid issues in the curl command
ca_cert=$(cat ${TMP_DIR}/ca-cert.pem | base64 | tr -d '\n')
server_cert=$(cat ${TMP_DIR}/server-cert.pem | base64 | tr -d '\n')
server_key=$(cat ${TMP_DIR}/server-key.pem | base64 | tr -d '\n')

# Create the secret resource
echo "Creating a secret for the certificate and keys"
STATUS=$(curl -ik \
  -o ${TMP_DIR}/output \
  -w "%{http_code}" \
  -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "kind": "Secret",
  "apiVersion": "v1",
  "metadata": {
    "name": "'"$SECRET_NAME"'",
    "namespace": "'"$NAMESPACE"'"
  },
  "data": {
    "ca.crt": "'"$ca_cert"'",
    "tls.crt": "'"$server_cert"'",
    "tls.key": "'"$server_key"'"
  }
}' \
https://kubernetes.default.svc/api/v1/namespaces/${NAMESPACE}/secrets)

cat ${TMP_DIR}/output

case "$STATUS" in
  201)
    printf "\nSuccess - secret created.\n"
  ;;
  409)
    printf "\nSuccess - secret already exists.\n"
    ;;
    *)
    printf "\nFailed creating secret.\n"
    printf "$STATUS"
    exit 1
    ;;
esac

# Clean up after we're done.
printf "\nDeleting ${TMP_DIR}.\n"
rm -rf ${TMP_DIR}
