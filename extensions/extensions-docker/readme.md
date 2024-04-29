## extensions-docker module naming convention

In general, we strive to have a consistent file structure with fully qualified module names.
However, we also want to balance this internal naming consistency with easier to understand and use
naming for end users.  

To achieve this balance, the `extensions-docker` folder will leverage the following naming convention:
* **WILL NOT** use the standard aissemble convention (which would result in names like `aissemble-extensions-docker-foobar`)
* **WILL** use simplified naming within this folder (resulting in names like `aissemble-foobar`)

Additionally, when publishing these modules to `ghcr.io`, we will be leveraging this same denormalized name _directly_
withing the `ghcr.io` namespace.  As such, we'll end up with easier to configure names where users don't have to add
subdomains into their docker configuration.  Instead, a single `ghcr.io` configuration can be leveraged.  The resulting
Docker container name will result in a simpler `ghcr.io/boozallen/aissemble-foobar`.