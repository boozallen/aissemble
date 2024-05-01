This module enables the foundation-configuration-store module ConfigLoader class to read/write configurations to Vault.

1. Include this module to the dependency
```pom.xml
        <dependency>
            <groupId>com.boozallen.aissemble</groupId>
            <artifactId>extensions-configuration-store-vault</artifactId>
            <version>${version.aissemble}</version>
        </dependency>
```
2. Instantiate the ConfigLoader with PropertyDao class
```
    ConfigLoader configLoader = new ConfigLoader(new PropertyDao());
```

3. ConfigLoader can read/write configuration to Vault. e.g.:
```
    // write property to vault
    Property property = new Property("myGroupName", "myTestKey", "myTestValue");
    configLoader.write(property);
            
    // read property from server
    Property property = configLoader.read("myGroupName", "myTestKey");
```


