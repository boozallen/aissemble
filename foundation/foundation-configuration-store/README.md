# Configuration Store
This module serves as a tool that enables the various configurations for a project to be centrally defined and managed, while also standardizing access to the configurations.

This README is intended to provide technical insight into the
implementation of this package.  For consumption guidance,
please refer to the [aiSSEMBLE&trade; Github Pages](https://boozallen.github.io/aissemble/aissemble/current/guides/guides-configuration-store.html).

## Developer Guidance

### Deserializing Properties Content into Java Objects with Krausening
The `ConfigLoader` leverages [Krausening](https://github.com/TechnologyBrewery/krausening/tree/dev/krausening) to parse properties files define configurations and associated metadata, which enables the configuration value encryption for security reasons.

A simple example below to show how to create the property `AWS_ACCESS_KEY_ID` in the group `aws-credentials` with a value of `base-access-key-id` and the property `AWS_SECRET_ACCESS_KEY` with a value of `base-secret-access-key`:

aws-credentials.properties
```aws-credentials.properties
AWS_ACCESS_KEY_ID=base-access-key-id
AWS_SECRET_ACCESS_KEY=base-secret-access-key
```

### Handling Load Status
The `ConfigLoader` leverages `load-status` to detect whether properties were already fully loaded or only partially loaded due to an interrupted process. The code logic ensures that a `fully-loaded` status is set when properties are loaded successfully. This status is then used to skip loading if the properties were successfully loaded previously, thus avoiding unnecessary reloading during deployment refreshes. For details on this behavior, refer to the implementation code and comments in the `ConfigLoader` class.