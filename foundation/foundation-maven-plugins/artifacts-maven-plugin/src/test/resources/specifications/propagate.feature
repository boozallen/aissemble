@Propagate
Feature: Propagate Artifacts
    Propagate artifacts at the given coordinates to an alternative repository

    Background:
        Given the following artifacts:
            |   name    | groupId       | artifactId    | version   | extension | classifier |
            |   A       | com.example   | test1         | 1.0.0     | jar       | sources    |
            |   B       | com.example   | test1         | 1.0.0     | jar       | tests      |
            |   C       | com.example   | test1         | 1.0.0     | jar       | javadoc    |
            |   D       | com.example   | test1         | 1.0.0     | jar       |            |
            |   E       | com.example   | test1         | 1.0.0     | pom       |            |
            |   F       | com.example   | test2         | 1.0.0     | pom       |            |
            |   G       | com.example   | test2         | 1.0.0     | war       |            |
            |   H       | com.example   | test2         | 1.0.0     | jar       |            |
            |   I       | com.example   | test3         | 1.0.0     | jar       | sources    |
            |   J       | com.example   | test3         | 1.0.0     | jar       |            |
            |   K       | com.example   | test3         | 1.0.0     | pom       |            |

    Scenario: This is when artifacts identified by Maven coordinates com.example:test1:1.0.0 are deployed to a given target Maven repository 
        Given a Maven groupId coordinate com.example
        And a Maven artifactId coordinate test1
        And a Maven version coordinate 1.0.0
        And a target Maven repository URL
        When the artifact propagation is triggered
        Then only artifacts A,B,C,D,E are deployed to an alternate repository

    Scenario: Deploy and entire project's artifacts to an alternate repository
        Given a project test2 with pom dependency on com.example:test2:1.0.0
        And a module test3 with pom dependency on com.example:test3:1.0.0
        And a target Maven repository URL
        When the project dependency propagation is triggered
        Then only artifacts F,G,H,I,J,K are deployed to an alternate repository

