# aiSSEMBLE&trade; Lineage HTTP Consumer Helm Chart
Baseline Helm chart for packaging and deploying the [aiSSEMBLE lineage HTTP consumer](https://boozallen.github.io/aissemble/current/lineage-medatada-capture-overview.html#_http_consumer). Chart is built and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See https://github.com/kokuwaio/helm-maven-plugin for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and tag with the desired 
image built from the aiSSEMBLE Quarkus baseline image. For example:
```bash
helm install lineage-http-consumer ghcr.io/boozallen/aissemble-lineage-http-consumer-chart --version <AISSEMBLE-VERSION> 
```
**Note**: *the version should match the aiSSEMBLE project version.*

# Properties
The following properties are overridden from the base [aissemble-quarkus chart](../aissemble-quarkus-chart/README.md).
Overriding inherited options requires prepending `aissemble-lineage-http-consumer.aissemble-quarkus`, following the standards for working with [Helm subcharts](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/).

| Property              | Description                           | Required Override | Default                                        |
|-----------------------|---------------------------------------|-------------------|------------------------------------------------|
| app.name              | Sets label for app.kubernetes.io/name | No                | aissemble-lineage-http-consumer                |    
| deployment.image.name | The image name                        | No                | boozallen/aissemble-data-lineage-http-consumer | 

# Quarkus Configuration

The following configuration of the consumer service is provided.  Additional configuration may be provided in accordance with [Quarkus](https://quarkus.io/guides/all-config) documentation.


| Property                                                                                   | Description                                                                                                                                                   | Accepted Values                                                               |
|--------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| quarkus.rest-client."com.boozallen.aissemble.datalineage.consumer.HttpProducerService".uri | Specifies the HTTP endpoint                                                                                                                                   | Any valid URI                                                                 |
| mp.messaging.incoming.data-lineage-in.*                                                    | Specifies and configures the smallrye connector to use.  Supported connectors are `smallrye-amqp`, `smallrye-kafka`, `smallrye-mqtt`, and `smallrye-rabbitmq` | See xref:messaging-details.adoc[the Messaging documentation] for more details |
| datalineage.consumer.onFail                                                                | Determine the desired behavior if, for any reason, an error is encountered in processing and re-publishing the event.                                         | `NACK` (default), `DROP`                                                      |
| datalineage.consumer.showStackTrace                                                        | In the event of an error, should the stack trace be printed?                                                                                                  | `true`, `false` (default)                                                     |                                          
