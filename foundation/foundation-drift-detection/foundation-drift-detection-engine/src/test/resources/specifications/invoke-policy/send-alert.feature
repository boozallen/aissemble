@alerting @cdi
Feature: Drift Detection -> Send an alert about the status of drift detection
  As the system, I can send alerts about the results of drift detection

  Scenario: Alerts are sent to the alerts topic
    When an alert is sent
    Then the alert is published to an "alerts" topic

  Scenario Outline: By default, always send an alert whether drift has been detected or not
    Given a policy has been defined with no alert configuration
    When "<driftStatus>" using the policy specified
    Then an alert is sent that "<driftStatus>"

    Examples: 
      | driftStatus          |
      | no drift is detected |
      | drift is detected    |

  Scenario Outline: Send alerts only when drift is detected
    Given a policy has been configured to only send alerts when drift is detected
    When "<driftStatus>" using the policy specified
    Then an alert "<alertSent>" sent

    Examples: 
      | driftStatus          | alertSent |
      | no drift is detected | is not    |
      | drift is detected    | is        |

  Scenario Outline: Never send alerts
    Given a policy has been configured to never send alerts
    When "<driftStatus>" using the policy specified
    Then an alert is not sent

    Examples: 
      | driftStatus          | 
      | no drift is detected | 
      | drift is detected    | 
