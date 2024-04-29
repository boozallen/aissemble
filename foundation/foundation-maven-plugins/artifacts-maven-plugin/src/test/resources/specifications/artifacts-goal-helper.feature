Feature: Artifact Goal Helper Functionality

    Scenario: Determine that an artifact is installed in the local repository
        Given Maven coordinates to a pom artifact that is locally installed
        When the local repository is queried for the artifact
        Then The artifact is found to exist

    Scenario: Determine that an artifact is not installed in the local repository
        Given Maven coordinates to a pom artifact that is not locally installed
        When the local repository is queried for the artifact
        Then The artifact is found to not exist

    Scenario: Execute the deployment of valid artifacts to an alternate repository
        Given locally installed artifacts
        When the artifacts deployment process is triggered
        Then the artifacts are deployed to an alternate repository
