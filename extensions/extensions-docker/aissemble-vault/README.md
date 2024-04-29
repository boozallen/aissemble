## Important steps if you make changes to the vault image

Whenever the Vault server initializes, new unseal and root keys are created, so the configuration will need to be updated.
The [technical documentation](https://boozallen.github.io/aissemble/current-dev/data-encryption.html#_vault_encryption)
has more information on how to configure Vault encryption. Note: the aiSSEMBLE Vault image installs HashiCorp tools in
order to provide encryption service. Setup of the Vault server is based off of the official Hashicorp vault image.