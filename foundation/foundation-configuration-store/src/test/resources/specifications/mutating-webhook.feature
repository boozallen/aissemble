@config-webhook
Feature: Modify kubernetes resources with config values using a mutating webhook

  Scenario: The configuration service can inject values to newly created configMap
    Given the configuration service has started
      And a ConfigMap definition that contains the substitution key exists
      And the ConfigMap definition has the injection metadata label
     When a kubernetes resource request is made to create a ConfigMap
     Then the ConfigMap patch is returned
      And the ConfigMap patch contains the injected value