@enforce-helm-version
Feature: Require a certain helm version to be installed when building aiSSEMBLE

    Scenario Outline: Validate lower helm versions are flagged
        Given a helm version "<currentVersion>"
        When the required helm version is "<requiredVersion>"
        Then the version is flagged

    Examples:
        | currentVersion | requiredVersion |
        | 3.11.3         | 3.12.0          |
        | 3.12.0         | 3.12.1          |
        | 2.12.1         | 3.10.0          |

    Scenario Outline: Validate higher or equal helm versions are NOT flagged
        Given a helm version "<currentVersion>"
        When the required helm version is "<requiredVersion>"
        Then the version is NOT flagged

    Examples:
        | currentVersion | requiredVersion |
        | 3.12.0         | 3.12.0          |
        | 3.12.1         | 3.10.0          |
        | 3.10.0         | 2.12.2          |


