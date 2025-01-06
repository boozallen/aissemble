@config-webhook
Feature: Modify kubernetes resources with config values using a mutating webhook

  Scenario: The configuration service can inject values to newly created configMap
    Given the configuration service has started
      And a ConfigMap definition that contains the substitution key exists
      And the ConfigMap definition has the injection metadata label
     When a kubernetes resource request is made to create a ConfigMap
     Then the patch is returned
      And the ConfigMap patch contains the injected value

  Scenario: The configuration service can inject encoded values to newly created Secret
    Given the configuration service has started
    And a Secret definition that contains the encoded substitution key exists
    And the Secret definition has the injection metadata label
    When a kubernetes resource request is made to create a Secret
    Then the patch is returned
    And the Secret patch contains the encoded injected value


  Scenario: The configuration service can inject both plain text and encoded values to newly created Secret
    Given the configuration service has started
    And a Secret definition that contains the encoded and plain text substitution key exists
    And the Secret definition has the injection metadata label
    When a kubernetes resource request is made to create a Secret
    Then the patch is returned
    And the Secret patch contains the both plain text and encoded injected value