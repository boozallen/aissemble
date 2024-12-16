@deployment-generation
Feature: Generating deployments
  @manual
  Scenario: Customize sub-app values files
    Given An ArgoCD app-of-apps deployment
    And The sub-apps have a values file named "values-ci.yaml"
    And the top level apps file is configured to use "values-ci.yaml"
    When I deploy the app-of-apps deployment to ArgoCD
    Then the sub-apps are deployed using their "values-ci.yaml" file

  @manual
  Scenario: Hive metastore service deployment relies on hive metastore db deployment
    Given a pipeline that requires spark hive metastore
    When the MDA generation is run
    Then the user is notified to add the hive metastore service deployment to tilt
    And the user is notified to add the hive metastore db deployment to tilt
    And the tilt deployment for hive metastore service depends on hive metastore db
