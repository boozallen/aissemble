Feature: Migration ArgoCD value files syncPolicy

  Scenario Outline: An ArgoCD value file is migrated with different syncPolicy configuration
    Given an ArgoCD value file has "<configured syncPolicy>" configuration defined
    When the 1.9.0 ArgoCD value file syncPolicy migration executes
    Then the value file is updated with expected syncPolicy value function

    Examples:
    | configured syncPolicy     |
    | no-automated              |
    | no-sync-policy            |
    | no-sync-options           |
    | partial-sync-options      |
    | with-automated-prune      |
    | with-sync-options-replace |
    | with-sync-policy          |

  Scenario: Migrate ArgoCD value file indent with 4 spaces
      Given an ArgoCD value file has indent with 4 spaces
      When the 1.9.0 ArgoCD value file syncPolicy migration executes
      Then the value file is updated with expected syncPolicy value function
       And the file still has indent with 4 spaces
