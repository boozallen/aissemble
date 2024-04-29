###
# #%L
# AIOps Docker Baseline::Vault
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import hvac
import requests
import json
import subprocess
import time

VAULT_ADDR='http://0.0.0.0:8200'

subprocess.Popen(["vault","server","-config=/vault/setup/config.hcl"])

# Give the server a moment to start up
time.sleep(5)
client = hvac.Client(url=VAULT_ADDR)

# If the server has not been initialized we will initialize and store the unseal keys and root token
if(not client.sys.is_initialized()):
    shares = 5
    threshold = 3
    result = client.sys.initialize(shares, threshold)
    root_token = result['root_token']

    keys = result['keys']

    client.token = root_token

    # Unseal the vault
    if(client.sys.is_sealed()):
        client.sys.submit_unseal_key(keys[0])
        client.sys.submit_unseal_key(keys[1])
        client.sys.submit_unseal_key(keys[2])
        print('UNSEALED')

    # Enable the transit encryption service
    headers = {'X-Vault-Token': client.token}
    r = requests.post(VAULT_ADDR+ '/v1/sys/mounts/transit', data={'type':'transit'}, headers=headers)
    client.secrets.transit.create_key('aiopskey', exportable=True, mount_point='transit')

    # Create a policy for the transit service
    encrypt_policy = {
      'policy': 'path "transit/encrypt/aiopskey" {   capabilities = [ "update" ]} path "transit/decrypt/aiopskey" {   capabilities = [ "update" ]}'
    }
    r = requests.post(VAULT_ADDR+ '/v1/sys/policies/acl/app-aiops', data=encrypt_policy, headers=headers)

    # Create a token for an encrypt client
    transit_token = client.auth.token.create(policies=['app-aiops'])

    # Save the KEYS (keys can be copied to a secure location and cleared from the server)
    with open('unseal_keys.txt', 'w+') as f:
        f.write(json.dumps(keys))

    with open('root_key.txt', 'w+') as f:
        f.write(root_token)

    with open('transit_client_token.txt', 'w+') as f:
        f.write(json.dumps(transit_token))
