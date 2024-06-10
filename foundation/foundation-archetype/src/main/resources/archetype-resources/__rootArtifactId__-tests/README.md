# Integration Tests

This module contains resources used for integration testing the solution baseline. The tests are [Helm chart tests](https://helm.sh/docs/topics/chart_tests/) that deploy and run the test containers within a Kubernetes cluster. This allows for maximum portability and ease of testing between environments by separating the test from the build process.

Please ensure the tools and configurations at the link below are completed before proceeding.
https://pages.github.com/boozallen/solution-baseline-docs/aissemble/current/archetype.html#_build_prerequisites

<details>
  <summary>Example steps for running tests manually</summary>
Below are example steps for deploying the environment and running tests. The steps are specific to the data pipeline test but the process is easy to modify for any other test framework or language.

1. Build the baseline
   1. `./mvnw clean install`
2. Initiate integration tests
   1. Ensure Rancher Desktop is running
   2. In the terminal window run `./mvnw clean install -P integration-test`
   3. Wait for the maven command to finish
3. You should be able to find the test results in the logs

          NAME: pipeline-test
          LAST DEPLOYED: Fri Dec 17 13:00:51 2021
          NAMESPACE: default
          STATUS: deployed
          REVISION: 1
          TEST SUITE:     test
          Last Started:   Fri Dec 17 13:00:59 2021
          Last Completed: Fri Dec 17 13:05:34 2021
          Phase:          Succeeded
4. To get more information (for Cucumber) run
   `kubectl logs test` and follow the link
</details>
