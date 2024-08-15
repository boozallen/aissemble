@config-webhook
Feature: Modify kubernetes resources with config values using a mutating webhook

  Scenario: The configuration service can process Kubernetes resources
    Given the configuration service has started
    When a kubernetes resource request is made
    Then the processed kubernetes resource is returned
