# Configuration Store
This module serves as a tool that enables the various configurations for a project to be centrally defined and managed, while also standardizing access to the configurations.

This README is intended to provide technical insight into the
implementation of this package.  For consumption guidance,
please refer to the [aiSSEMBLE&trade; Github Pages](https://boozallen.github.io/aissemble/aissemble/current/guides/guides-configuration-store.html).

## Developer Guidance

### Deserializing YAML Content into Java Objects with snakeyaml
The `ConfigLoader` leverages [snakeyaml](https://bitbucket.org/snakeyaml/snakeyaml/src/master/) to parse `YAML` files define configurations and associated metadata. This module contains two classes, `YamlProperty` and `YamlConfig`, which are the objects that house the deserialized information from the configuration `YAML` files. An example of properly formatted configurations files:

```yaml
groupName: model-training-api
properties:
  - name: AWS_ACCESS_KEY_ID
    value: base-access-key-id
  - name: AWS_SECRET_ACCESS_KEY
    value: base-secret-access-key
```

Because the `YamlProperty` and `YamlConfig` classes are required to have attributes that mirror the format of the configuration `YAML` files, there is a separate `Property` class that simply adds the `groupName` metadata to the existing information housed in `YamlProperty`. Furthermore, because an object that will house the deserialized `YAML` contents must have an empty constructor, it was not possible to have any extending relationship between a supposed `AbstractProperty` and `YamlProperty` / `Property`.

See the [snakeyaml documentation](https://bitbucket.org/snakeyaml/snakeyaml/wiki/Documentation) for further details on deserializing yaml files.

### Handling Load Status
The `ConfigLoader` leverages `load-status` to detect whether properties were already fully loaded or only partially loaded due to an interrupted process. The code logic ensures that a `fully-loaded` status is set when properties are loaded successfully. This status is then used to skip loading if the properties were successfully loaded previously, thus avoiding unnecessary reloading during deployment refreshes. For details on this behavior, refer to the implementation code and comments in the `ConfigLoader` class.
