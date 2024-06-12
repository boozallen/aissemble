package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KubernetesGenerator extends AbstractKubernetesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                                        | Template                                                                                  | Generated File                                                             |
     * |-----------------------------------------------|-------------------------------------------------------------------------------------------|----------------------------------------------------------------------------|
     * | airflowRoleFile                               | deployment/airflow/airflow-role.yaml.vm                                                   | apps/${appName}/templates/airflow-role.yaml                                |
     * | airflowRoleBindingFile                        | deployment/airflow/airflow-rolebinding.yaml.vm                                            | apps/${appName}/templates/airflow-rolebinding.yaml                         |
     * | airflowServiceAccountTokenFile                | deployment/airflow/airflow-serviceaccount-token.yaml.vm                                   | apps/${appName}/templates/airflow-serviceaccount-token.yaml                |
     * | airflowServiceAccountFile                     | deployment/airflow/airflow-serviceaccount.yaml.vm                                         | apps/${appName}/templates/airflow-serviceaccount.yaml                      |
     * | airflowValuesDevFile                          | deployment/airflow/airflow.values-dev.yaml.vm                                             | apps/${appName}/values-dev.yaml                                            |
     * | airflowValuesFile                             | deployment/airflow/airflow.values.yaml.vm                                                 | apps/${appName}/values.yaml                                                |
     * | mlflowIngressFile                             | deployment/airflow/mlflow.ingress.yaml.vm                                                 | apps/${appName}/templates/mlflow-ingress.yaml                              |
     * | airflowHelmChartFileV2                        | deployment/airflow/v2/airflow.chart.yaml.vm                                               | apps/${appName}/Chart.yaml                                                 |
     * | airflowValuesDevFileV2                        | deployment/airflow/v2/airflow.values-dev.yaml.vm                                          | apps/${appName}/values-dev.yaml                                            |
     * | airflowValuesFileV2                           | deployment/airflow/v2/airflow.values.yaml.vm                                              | apps/${appName}/values.yaml                                                |
     * | airflowArgoCD                                 | deployment/argocd/airflow.yaml.vm                                                         | templates/airflow.yaml                                                     |
     * | bomArgoCD                                     | deployment/argocd/bom.yaml.vm                                                             | templates/bom.yaml                                                         |
     * | dataAccessArgoCD                              | deployment/argocd/data-access.yaml.vm                                                     | templates/data-access.yaml                                                 |
     * | elasticsearchArgoCD                           | deployment/argocd/elasticsearch.yaml.vm                                                   | templates/elasticsearch.yaml                                               |
     * | hiveMetastoreDbArgoCD                         | deployment/argocd/hive-metastore-db.yaml.vm                                               | templates/hive-metastore-db.yaml                                           |
     * | hiveMetastoreServiceArgoCD                    | deployment/argocd/hive-metastore-service.yaml.vm                                          | templates/hive-metastore-service.yaml                                      |
     * | inferenceArgoCD                               | deployment/argocd/inference.yaml.vm                                                       | templates/${appName}.yaml                                                  |
     * | kafkaConnectArgoCD                            | deployment/argocd/kafka-connect.yaml.vm                                                   | templates/kafka-connect.yaml                                               |
     * | kafkaArgoCD                                   | deployment/argocd/kafka.yaml.vm                                                           | templates/kafka.yaml                                                       |
     * | keycloakArgoCD                                | deployment/argocd/keycloak.yaml.vm                                                        | templates/keycloak.yaml                                                    |
     * | metadataArgoCD                                | deployment/argocd/metadata.yaml.vm                                                        | templates/metadata.yaml                                                    |
     * | modelTrainingApiArgoCD                        | deployment/argocd/model-training-api.yaml.vm                                              | templates/model-training-api.yaml                                          |
     * | neo4jArgoCD                                   | deployment/argocd/neo4j.yaml.vm                                                           | templates/neo4j.yaml                                                       |
     * | nexusArgoCD                                   | deployment/argocd/nexus.yaml.vm                                                           | templates/nexus.yaml                                                       |
     * | policyDecisionPointArgoCD                     | deployment/argocd/policy-decision-point.yaml.vm                                           | templates/policy-decision-point.yaml                                       |
     * | postgresArgoCD                                | deployment/argocd/postgres.yaml.vm                                                        | templates/postgres.yaml                                                    |
     * | aissembleQuarkusArgoCD                        | deployment/argocd/quarkus.yaml.vm                                                         | templates/${appName}.yaml                                                  |
     * | sharedInfrastructureArgoCD                    | deployment/argocd/shared-infrastructure.yaml.vm                                           | templates/shared-infrastructure.yaml                                       |
     * | sparkInfrastructureArgoCD                     | deployment/argocd/spark-infrastructure.yaml.vm                                            | templates/spark-infrastructure.yaml                                        |
     * | sparkOperatorArgoCD                           | deployment/argocd/spark-operator.yaml.vm                                                  | templates/spark-operator.yaml                                              |
     * | airflowArgoCDV2                               | deployment/argocd/v2/airflow.yaml.vm                                                      | templates/airflow.yaml                                                     |
     * | dataAccessArgoCDV2                            | deployment/argocd/v2/data-access.yaml.vm                                                  | templates/data-access.yaml                                                 |
     * | elasticsearchOperatorArgoCDV2                 | deployment/argocd/v2/elasticsearch.operator.yaml.vm                                       | templates/elasticsearch-operator.yaml                                      |
     * | elasticsearchArgoCDV2                         | deployment/argocd/v2/elasticsearch.yaml.vm                                                | templates/elasticsearch.yaml                                               |
     * | hiveMetastoreDbArgoCDV2                       | deployment/argocd/v2/hive-metastore-db.yaml.vm                                            | templates/hive-metastore-db.yaml                                           |
     * | hiveMetastoreServiceArgoCDV2                  | deployment/argocd/v2/hive-metastore-service.yaml.vm                                       | templates/hive-metastore-service.yaml                                      |
     * | inferenceArgoCDV2                             | deployment/argocd/v2/inference.yaml.vm                                                    | templates/airflow.yaml                                                     |
     * | jenkinsArgoCDV2                               | deployment/argocd/v2/jenkins.yaml.vm                                                      | templates/jenkins.yaml                                                     |
     * | kafkaArgoCDV2                                 | deployment/argocd/v2/kafka.yaml.vm                                                        | templates/kafka.yaml                                                       |
     * | keycloakArgoCDV2                              | deployment/argocd/v2/keycloak.yaml.vm                                                     | templates/keycloak.yaml                                                    |
     * | lineageCustomConsumerArgoCD                   | deployment/argocd/v2/lineage.custom.consumer.yaml.vm                                      | templates/lineage-custom-consumer.yaml                                     |
     * | lineageHttpConsumerArgoCD                     | deployment/argocd/v2/lineage.http.consumer.yaml.vm                                        | templates/lineage-http-consumer.yaml                                       |
     * | metadataArgoCDV2                              | deployment/argocd/v2/metadata.yaml.vm                                                     | templates/metadata.yaml                                                    |
     * | mlflowArgoCDV2                                | deployment/argocd/v2/mlflow-ui.yaml.vm                                                    | templates/mlflow-ui.yaml                                                   |
     * | pipelineInvocationServiceArgoCD-v2            | deployment/argocd/v2/pipeline.invocation.service.yaml.vm                                  | templates/pipeline-invocation-service.yaml                                 |
     * | policyDecisionPointArgoCDV2                   | deployment/argocd/v2/policy-decision-point.yaml.vm                                        | templates/policy-decision-point.yaml                                       |
     * | sealedSecretArgoCDFile                        | deployment/argocd/v2/sealed.secret.yaml.vm                                                | templates/sealed-secret.yaml                                               |
     * | sparkOperatorArgoCDV2                         | deployment/argocd/v2/spark.operator.yaml.vm                                               | templates/spark-operator.yaml                                              |
     * | versioningArgoCDV2                            | deployment/argocd/v2/versioning.yaml.vm                                                   | templates/versioning.yaml                                                  |
     * | vaultArgoCD                                   | deployment/argocd/vault.yaml.vm                                                           | templates/vault.yaml                                                       |
     * | versioningArgoCD                              | deployment/argocd/versioning.yaml.vm                                                      | templates/versioning.yaml                                                  |
     * | zookeeperAlertArgoCD                          | deployment/argocd/zookeeper-alert.yaml.vm                                                 | templates/zookeeper-alert.yaml                                             |
     * | bomValuesDevFile                              | deployment/bom/bom.values-dev.yaml.vm                                                     | apps/${appName}/values-dev.yaml                                            |
     * | bomValuesFile                                 | deployment/bom/bom.values.yaml.vm                                                         | apps/${appName}/values.yaml                                                |
     * | dataAccessValuesDevFile                       | deployment/data-access/data.access.values-dev.yaml.vm                                     | apps/${appName}/values-dev.yaml                                            |
     * | dataAccessValuesFile                          | deployment/data-access/data.access.values.yaml.vm                                         | apps/${appName}/values.yaml                                                |
     * | dataAccessHelmChartFileV2                     | deployment/data-access/v2/data.access.chart.yaml.vm                                       | apps/${appName}/Chart.yaml                                                 |
     * | dataAccessValuesDevFileV2                     | deployment/data-access/v2/data.access.values-dev.yaml.vm                                  | apps/${appName}/values-dev.yaml                                            |
     * | dataAccessValuesFileV2                        | deployment/data-access/v2/data.access.values.yaml.vm                                      | apps/${appName}/values.yaml                                                |
     * | elasticsearchOperatorHelmChartFileV2          | deployment/elasticsearch-operator/v2/elasticsearch.operator.chart.yaml.vm                 | apps/${appName}/Chart.yaml                                                 |
     * | elasticsearchOperatorValuesDevFileV2          | deployment/elasticsearch-operator/v2/elasticsearch.operator.values-dev.yaml.vm            | apps/${appName}/values-dev.yaml                                            |
     * | elasticsearchOperatorValuesFileV2             | deployment/elasticsearch-operator/v2/elasticsearch.operator.values.yaml.vm                | apps/${appName}/values.yaml                                                |
     * | elasticsearchValuesDevFile                    | deployment/elasticsearch/elasticsearch.values-dev.yaml.vm                                 | apps/${appName}/values-dev.yaml                                            |
     * | elasticsearchValuesFile                       | deployment/elasticsearch/elasticsearch.values.yaml.vm                                     | apps/${appName}/values.yaml                                                |
     * | elasticsearchHelmChartFileV2                  | deployment/elasticsearch/v2/elasticsearch.chart.yaml.vm                                   | apps/${appName}/Chart.yaml                                                 |
     * | elasticsearchValuesDevFileV2                  | deployment/elasticsearch/v2/elasticsearch.values-dev.yaml.vm                              | apps/${appName}/values-dev.yaml                                            |
     * | elasticsearchValuesFileV2                     | deployment/elasticsearch/v2/elasticsearch.values.yaml.vm                                  | apps/${appName}/values.yaml                                                |
     * | helmChartFile                                 | deployment/helm/chart.yaml.vm                                                             | apps/${appName}/Chart.yaml                                                 |
     * | helmDeploymentFile                            | deployment/helm/deployment.yaml.vm                                                        | apps/${appName}/templates/deployment.yaml                                  |
     * | helmIgnoreFile                                | deployment/helm/helmignore.vm                                                             | apps/${appName}/.helmignore                                                |
     * | helmIngressFile                               | deployment/helm/ingress.yaml.vm                                                           | apps/${appName}/templates/ingress.yaml                                     |
     * | helmServiceFile                               | deployment/helm/service.yaml.vm                                                           | apps/${appName}/templates/service.yaml                                     |
     * | hiveMetastoreDbValuesDevFile                  | deployment/hive-metastore-db/hive.metastore.db.values-dev.yaml.vm                         | apps/${appName}/values-dev.yaml                                            |
     * | hiveMetastoreDbValuesFile                     | deployment/hive-metastore-db/hive.metastore.db.values.yaml.vm                             | apps/${appName}/values.yaml                                                |
     * | hiveMetastoreDbHelmChartFileV2                | deployment/hive-metastore-db/v2/hive.metastore.db.chart.yaml.vm                           | apps/${appName}/Chart.yaml                                                 |
     * | hiveMetastoreDbValuesDevFileV2                | deployment/hive-metastore-db/v2/hive.metastore.db.values-dev.yaml.vm                      | apps/${appName}/values-dev.yaml                                            |
     * | hiveMetastoreDbValuesFileV2                   | deployment/hive-metastore-db/v2/hive.metastore.db.values.yaml.vm                          | apps/${appName}/values.yaml                                                |
     * | hiveMetastoreServiceConfigMapFile             | deployment/hive-metastore-service/templates/configmap.yaml.vm                             | apps/${appName}/templates/configmap.yaml                                   |
     * | hiveMetastoreServiceHelmChartFileV2           | deployment/hive-metastore-service/v2/hive-metastore-service-chart.yaml.vm                 | apps/${appName}/Chart.yaml                                                 |
     * | inferenceValuesDevFile                        | deployment/inference/inference.values-dev.yaml.vm                                         | apps/${appName}/values-dev.yaml                                            |
     * | inferenceHelmChartFileV2                      | deployment/inference/v2/inference.chart.yaml.vm                                           | apps/${appName}/Chart.yaml                                                 |
     * | jenkinsControllerDeploymentFile               | deployment/jenkins/jenkins.controller.deployment.yaml.vm                                  | apps/${appName}/templates/deployment.yaml                                  |
     * | jenkinsValuesDevFile                          | deployment/jenkins/jenkins.values-dev.yaml.vm                                             | apps/${appName}/values-dev.yaml                                            |
     * | jenkinsValuesFile                             | deployment/jenkins/jenkins.values.yaml.vm                                                 | apps/${appName}/values.yaml                                                |
     * | jenkinsHelmChartFileV2                        | deployment/jenkins/v2/jenkins.chart.yaml.vm                                               | apps/${appName}/Chart.yaml                                                 |
     * | jenkinsValuesDevFileV2                        | deployment/jenkins/v2/jenkins.values-dev.yaml.vm                                          | apps/${appName}/values-dev.yaml                                            |
     * | jenkinsValuesFileV2                           | deployment/jenkins/v2/jenkins.values.yaml.vm                                              | apps/${appName}/values.yaml                                                |
     * | kafkaConnectValuesDevFile                     | deployment/kafka-connect/kafka.connect.values-dev.yaml.vm                                 | apps/${appName}/values-dev.yaml                                            |
     * | kafkaConnectValuesFile                        | deployment/kafka-connect/kafka.connect.values.yaml.vm                                     | apps/${appName}/values.yaml                                                |
     * | kafkaValuesDevFile                            | deployment/kafka/kafka.values-dev.yaml.vm                                                 | apps/${appName}/values-dev.yaml                                            |
     * | kafkaValuesFile                               | deployment/kafka/kafka.values.yaml.vm                                                     | apps/${appName}/values.yaml                                                |
     * | kafkaHelmChartFileV2                          | deployment/kafka/v2/kafka.chart.yaml.vm                                                   | apps/${appName}/Chart.yaml                                                 |
     * | kafkaValuesDevFileV2                          | deployment/kafka/v2/kafka.values-dev.yaml.vm                                              | apps/${appName}/values-dev.yaml                                            |
     * | kafkaValuesFileV2                             | deployment/kafka/v2/kafka.values.yaml.vm                                                  | apps/${appName}/values.yaml                                                |
     * | keycloakValuesDevFile                         | deployment/keycloak/keycloak.values-dev.yaml.vm                                           | apps/${appName}/values-dev.yaml                                            |
     * | keycloakValuesFile                            | deployment/keycloak/keycloak.values.yaml.vm                                               | apps/${appName}/values.yaml                                                |
     * | keycloakChartFileV2                           | deployment/keycloak/v2/keycloak.chart.yaml.vm                                             | apps/${appName}/Chart.yaml                                                 |
     * | keycloakValuesDevFileV2                       | deployment/keycloak/v2/keycloak.values-dev.yaml.vm                                        | apps/${appName}/values-dev.yaml                                            |
     * | keycloakValuesFileV2                          | deployment/keycloak/v2/keycloak.values.yaml.vm                                            | apps/${appName}/values.yaml                                                |
     * | lineageCustomConsumerHelmChartFile            | deployment/lineage-custom-consumer/v2/lineage.custom.consumer.chart.yaml.vm               | apps/${appName}/Chart.yaml                                                 |
     * | lineageCustomConsumerValuesDevFile            | deployment/lineage-custom-consumer/v2/lineage.custom.consumer.values-dev.yaml.vm          | apps/${appName}/values-dev.yaml                                            |
     * | lineageCustomConsumerValuesFile               | deployment/lineage-custom-consumer/v2/lineage.custom.consumer.values.yaml.vm              | apps/${appName}/values.yaml                                                |
     * | lineageHttpConsumerHelmChartFile              | deployment/lineage-http-consumer/v2/lineage.http.consumer.chart.yaml.vm                   | apps/${appName}/Chart.yaml                                                 |
     * | lineageHttpConsumerValuesDevFile              | deployment/lineage-http-consumer/v2/lineage.http.consumer.values-dev.yaml.vm              | apps/${appName}/values-dev.yaml                                            |
     * | lineageHttpConsumerValuesFile                 | deployment/lineage-http-consumer/v2/lineage.http.consumer.values.yaml.vm                  | apps/${appName}/values.yaml                                                |
     * | s3LocalChartFileV2                            | deployment/localstack/localstack.chart.yaml.vm                                            | apps/${appName}/Chart.yaml                                                 |
     * | s3LocalValuesDevFileV2                        | deployment/localstack/localstack.values.dev.yaml.vm                                       | apps/${appName}/values-dev.yaml                                            |
     * | s3LocalValuesFileV2                           | deployment/localstack/localstack.values.yaml.vm                                           | apps/${appName}/values.yaml                                                |
     * | metadataValuesDevFile                         | deployment/metadata/metadata.values-dev.yaml.vm                                           | apps/${appName}/values-dev.yaml                                            |
     * | metadataValuesFile                            | deployment/metadata/metadata.values.yaml.vm                                               | apps/${appName}/values.yaml                                                |
     * | metadataHelmChartFileV2                       | deployment/metadata/v2/metadata.chart.yaml.vm                                             | apps/${appName}/Chart.yaml                                                 |
     * | metadataValuesDevFileV2                       | deployment/metadata/v2/metadata.values-dev.yaml.vm                                        | apps/${appName}/values-dev.yaml                                            |
     * | metadataValuesFileV2                          | deployment/metadata/v2/metadata.values.yaml.vm                                            | apps/${appName}/values.yaml                                                |
     * | mlflowHelmChartFileV2                         | deployment/mlflow/v2/mlflow.chart.yaml.vm                                                 | apps/${appName}/Chart.yaml                                                 |
     * | mlflowValuesDevFileV2                         | deployment/mlflow/v2/mlflow.values-dev.yaml.vm                                            | apps/${appName}/values-dev.yaml                                            |
     * | mlflowValuesFileV2                            | deployment/mlflow/v2/mlflow.values.yaml.vm                                                | apps/${appName}/values.yaml                                                |
     * | modelTrainingApiConfigMapFile                 | deployment/model-training-api/model-training-api.configmap.yaml.vm                        | apps/${appName}/templates/configmap.yaml                                   |
     * | modelTrainingApiDeploymentFile                | deployment/model-training-api/model-training-api.deployment.yaml.vm                       | apps/${appName}/templates/deployment.yaml                                  |
     * | modelTrainingApiRbacFile                      | deployment/model-training-api/model-training-api.rbac.yaml.vm                             | apps/${appName}/templates/rbac.yaml                                        |
     * | modelTrainingApiServiceAccountFile            | deployment/model-training-api/model-training-api.serviceaccount.yaml.vm                   | apps/${appName}/templates/serviceaccount.yaml                              |
     * | modelTrainingApiValuesDevFile                 | deployment/model-training-api/model-training-api.values-dev.yaml.vm                       | apps/${appName}/values-dev.yaml                                            |
     * | modelTrainingApiValuesFile                    | deployment/model-training-api/model-training-api.values.yaml.vm                           | apps/${appName}/values.yaml                                                |
     * | neo4jValuesDevFile                            | deployment/neo4j/neo4j.values-dev.yaml.vm                                                 | apps/${appName}/values-dev.yaml                                            |
     * | neo4jValuesFile                               | deployment/neo4j/neo4j.values.yaml.vm                                                     | apps/${appName}/values.yaml                                                |
     * | nexusValuesDevFile                            | deployment/nexus/nexus.values-dev.yaml.vm                                                 | apps/${appName}/values-dev.yaml                                            |
     * | nexusValuesFile                               | deployment/nexus/nexus.values.yaml.vm                                                     | apps/${appName}/values.yaml                                                |
     * | jenkinsPersistentVolumeClaim                  | deployment/persistentvolumeclaim/jenkins.persistentvolumeclaim.yaml.vm                    | apps/${appName}/templates/jenkins-persistentvolumeclaim.yaml               |
     * | sparkInfrastructurePersistentVolumeClaimFile  | deployment/persistentvolumeclaim/spark.infrastructure.persistentvolumeclaim.yaml.vm       | apps/${appName}/templates/spark-infrastructure-persistentvolumeclaim.yaml  |
     * | pipelineInvocationServiceHelmChartFile-v2     | deployment/pipeline-invocation-service/v2/pipeline.invocation.service.chart.yaml.vm       | apps/${appName}/Chart.yaml                                                 |
     * | pipelineInvocationServiceConfigMapFile-v2     | deployment/pipeline-invocation-service/v2/pipeline.invocation.service.config.map.yaml.vm  | apps/${appName}/templates/configmap.yaml                                   |
     * | pipelineInvocationServiceValuesDevFile-v2     | deployment/pipeline-invocation-service/v2/pipeline.invocation.service.values-dev.yaml.vm  | apps/${appName}/values-dev.yaml                                            |
     * | pipelineInvocationServiceValuesFile-v2        | deployment/pipeline-invocation-service/v2/pipeline.invocation.service.values.yaml.vm      | apps/${appName}/values.yaml                                                |
     * | policyDecisionPointValuesDevFile              | deployment/policy-decision-point/policy.decision.point.values-dev.yaml.vm                 | apps/${appName}/values-dev.yaml                                            |
     * | policyDecisionPointValuesFile                 | deployment/policy-decision-point/policy.decision.point.values.yaml.vm                     | apps/${appName}/values.yaml                                                |
     * | policyDecisionPointHelmChartFileV2            | deployment/policy-decision-point/v2/policy.decision.point.chart.yaml.vm                   | apps/${appName}/Chart.yaml                                                 |
     * | policyDecisionPointValuesDevFileV2            | deployment/policy-decision-point/v2/policy.decision.point.values-dev.yaml.vm              | apps/${appName}/values-dev.yaml                                            |
     * | policyDecisionPointValuesFileV2               | deployment/policy-decision-point/v2/policy.decision.point.values.yaml.vm                  | apps/${appName}/values.yaml                                                |
     * | postgresValuesDevFile                         | deployment/postgres/postgres.values-dev.yaml.vm                                           | apps/${appName}/values-dev.yaml                                            |
     * | postgresValuesFile                            | deployment/postgres/postgres.values.yaml.vm                                               | apps/${appName}/values.yaml                                                |
     * | aissembleQuarkusHelmChartFile                 | deployment/quarkus/quarkus.chart.yaml.vm                                                  | apps/${appName}/Chart.yaml                                                 |
     * | aissembleQuarkusValuesDevFile                 | deployment/quarkus/quarkus.values-dev.yaml.vm                                             | apps/${appName}/values-dev.yaml                                            |
     * | aissembleQuarkusValuesFile                    | deployment/quarkus/quarkus.values.yaml.vm                                                 | apps/${appName}/values.yaml                                                |
     * | s3LocalValuesDevFile                          | deployment/s3-local/s3-local.values-dev.yaml.vm                                           | apps/${appName}/values-dev.yaml                                            |
     * | s3LocalValuesFile                             | deployment/s3-local/s3-local.values.yaml.vm                                               | apps/${appName}/values.yaml                                                |
     * | s3LocalDeploymentFile                         | deployment/s3-local/templates/deployment.yaml.vm                                          | apps/${appName}/templates/deployment.yaml                                  |
     * | sharedInfrastructureValuesDevFile             | deployment/shared-infrastructure/shared-infrastructure.values-dev.yaml.vm                 | apps/${appName}/values-dev.yaml                                            |
     * | sharedInfrastructureValuesFile                | deployment/shared-infrastructure/shared-infrastructure.values.yaml.vm                     | apps/${appName}/values.yaml                                                |
     * | sparkInfrastructureConfigMapFile              | deployment/spark-infrastructure/templates/configmap.yaml.vm                               | apps/${appName}/templates/configmap.yaml                                   |
     * | sparkInfrastructureHelmChartFileV2            | deployment/spark-infrastructure/v2/spark.infrastructure.chart.yaml.vm                     | apps/${appName}/Chart.yaml                                                 |
     * | sparkInfrastructureHelmValuesDevFileV2        | deployment/spark-infrastructure/v2/spark.infrastructure.values.dev.yaml.vm                | apps/${appName}/values-dev.yaml                                            |
     * | sparkInfrastructureHelmValuesFileV2           | deployment/spark-infrastructure/v2/spark.infrastructure.values.yaml.vm                    | apps/${appName}/values.yaml                                                |
     * | sparkOperatorScheduledApplicationsCRD         | deployment/spark-operator/crds/sparkoperator.k8s.io_scheduledsparkapplications.yaml.vm    | apps/${appName}/crds/sparkoperator.k8s.io_scheduledsparkapplications.yaml  |
     * | sparkOperatorApplicationsCRD                  | deployment/spark-operator/crds/sparkoperator.k8s.io_sparkapplications.yaml.vm             | apps/${appName}/crds/sparkoperator.k8s.io_sparkapplications.yaml           |
     * | sparkOperatorValuesDevFile                    | deployment/spark-operator/spark-operator.values-dev.yaml.vm                               | apps/${appName}/values-dev.yaml                                            |
     * | sparkOperatorValuesFile                       | deployment/spark-operator/spark-operator.values.yaml.vm                                   | apps/${appName}/values.yaml                                                |
     * | sparkOperatorTemplateHelper                   | deployment/spark-operator/templates/_helpers.tpl.vm                                       | apps/${appName}/templates/_helpers.tpl                                     |
     * | sparkOperatorHelmDeploymentFile               | deployment/spark-operator/templates/deployment.yaml.vm                                    | apps/${appName}/templates/deployment.yaml                                  |
     * | sparkOperatorPrometheusPodMonitorFile         | deployment/spark-operator/templates/prometheus-podmonitor.yaml.vm                         | apps/${appName}/templates/prometheus-podmonitor.yaml                       |
     * | sparkOperatorRbacFile                         | deployment/spark-operator/templates/rbac.yaml.vm                                          | apps/${appName}/templates/rbac.yaml                                        |
     * | sparkOperatorServiceAccountFile               | deployment/spark-operator/templates/serviceaccount.yaml.vm                                | apps/${appName}/templates/serviceaccount.yaml                              |
     * | sparkOperatorSparkRbacFile                    | deployment/spark-operator/templates/spark-rbac.yaml.vm                                    | apps/${appName}/templates/spark-rbac.yaml                                  |
     * | sparkOperatorSparkServiceAccountFile          | deployment/spark-operator/templates/spark-serviceaccount.yaml.vm                          | apps/${appName}/templates/spark-serviceaccount.yaml                        |
     * | sparkOperatorWebhookCleanupJobFile            | deployment/spark-operator/templates/webhook-cleanup-job.yaml.vm                           | apps/${appName}/templates/webhook-cleanup-job.yaml                         |
     * | sparkOperatorWebhookInitJobFile               | deployment/spark-operator/templates/webhook-init-job.yaml.vm                              | apps/${appName}/templates/webhook-init-job.yaml                            |
     * | sparkOperatorWebhookServiceFile               | deployment/spark-operator/templates/webhook-service.yaml.vm                               | apps/${appName}/templates/webhook-service.yaml                             |
     * | sparkOperatorChartFileV2                      | deployment/spark-operator/v2/spark.operator.chart.yaml.vm                                 | apps/${appName}/Chart.yaml                                                 |
     * | sparkOperatorValuesDevFileV2                  | deployment/spark-operator/v2/spark.operator.values.dev.yaml.vm                            | apps/${appName}/values-dev.yaml                                            |
     * | sparkOperatorValuesFileV2                     | deployment/spark-operator/v2/spark.operator.values.yaml.vm                                | apps/${appName}/values.yaml                                                |
     * | valuesCIFile                                  | deployment/values-ci.yaml.vm                                                              | apps/${appName}/values-ci.yaml                                             |
     * | vaultValuesDevFile                            | deployment/vault/vault.values-dev.yaml.vm                                                 | apps/${appName}/values-dev.yaml                                            |
     * | vaultValuesFile                               | deployment/vault/vault.values.yaml.vm                                                     | apps/${appName}/values.yaml                                                |
     * | versioningDeployment                          | deployment/versioning.deployment.yaml.vm                                                  | kubernetes/versioning/versioning-deployment.yaml                           |
     * | versioningService                             | deployment/versioning.service.yaml.vm                                                     | kubernetes/versioning/versioning-service.yaml                              |
     * | versioningHelmChartFileV2                     | deployment/versioning/v2/versioning.chart.yaml.vm                                         | apps/${appName}/Chart.yaml                                                 |
     * | versioningValuesDevFileV2                     | deployment/versioning/v2/versioning.values-dev.yaml.vm                                    | apps/${appName}/values-dev.yaml                                            |
     * | versioningValuesFileV2                        | deployment/versioning/v2/versioning.values.yaml.vm                                        | apps/${appName}/values.yaml                                                |
     * | versioningValuesDevFile                       | deployment/versioning/versioning.values-dev.yaml.vm                                       | apps/${appName}/values-dev.yaml                                            |
     * | versioningValuesFile                          | deployment/versioning/versioning.values.yaml.vm                                           | apps/${appName}/values.yaml                                                |
     * | zookeeperAlertValuesDevFile                   | deployment/zookeeper-alert/zookeeper.alert.values-dev.yaml.vm                             | apps/${appName}/values-dev.yaml                                            |
     * | zookeeperAlertValuesFile                      | deployment/zookeeper-alert/zookeeper.alert.values.yaml.vm                                 | apps/${appName}/values.yaml                                                |
     */

    @Override
    public void generate(GenerationContext context) {
        VelocityContext vc = this.configureWithoutGeneration(context);
        generateFile(context, vc);
    }

}
