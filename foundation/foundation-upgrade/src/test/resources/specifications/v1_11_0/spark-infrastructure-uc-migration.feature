Feature: Migrate Spark Infrastructure Universal Configuration Migration
  The Hive Metastore Service starts to implement Universal Configuration Store to inject hive credentials. We make configuration store call
  by the default. If consumer keep default value of hive credentials, it will be migrated to use configuration store, if not it would override to use custom value without migration.
  hive.username refers to  javax.jdo.option.ConnectionUserName and  aissemble-hive-metastore-service-chart.mysql.auth.username

Scenario: Default Spark infrastructure file is migrated
  Given default values.yaml of spark infrastructure
  When the spark infrastructure configuration migration executes
  Then values.yaml is updated to remove hive username properties



Scenario: Spark infrastructure file with modified hive credentials is not migrated
  Given values.yaml of spark infrastructure with hive username and password changed from "hive"
  When the spark infrastructure configuration migration executes
  Then spark infrastructure configuration migration is skipped