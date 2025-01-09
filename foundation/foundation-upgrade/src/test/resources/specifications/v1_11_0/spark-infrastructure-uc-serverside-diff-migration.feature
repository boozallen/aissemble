Feature: Migrate Spark Infrastructure Universal Configuration ServerSide Diff Migration
  The Hive Metastore Service starts to implement Universal Configuration Store to inject hive credentials. We make configuration store call
  by the default. If consumer keep default value of hive credentials, it will be migrated to use configuration store, if not it would override to use custom value without migration.
  hive.username refers to  javax.jdo.option.ConnectionUserName and  aissemble-hive-metastore-service-chart.mysql.auth.username

Scenario: Spark infrastructure application file is migrated
  Given spark infrastructure yaml file
  When the spark infrastructure configuration serverside diff migration executes
  Then spark-infrastructure.yaml is updated to add server side diff annotation.