## aiSSEMBLE&trade; Foundation Data Lineage Python

[![PyPI](https://img.shields.io/pypi/v/aissemble-foundation-data-lineage-python?logo=python&logoColor=gold)](https://pypi.org/project/aissemble-foundation-data-lineage-python/)
![PyPI - Python Version](https://img.shields.io/pypi/pyversions/aissemble-foundation-data-lineage-python?logo=python&logoColor=gold)
![PyPI - Wheel](https://img.shields.io/pypi/wheel/aissemble-foundation-data-lineage-python?logo=python&logoColor=gold)

This module serves as a generic wrapper for data lineage 
types, objects, and functions.  While it is designed to be 
leveraged through the aiSSEMBLE&trade; ecosystem, it is not dependent
on aiSSEMBLE for execution.  It is intentionally designed for 
portability.

This Readme is intended to provide technical insight into the
implementation of this package.  For consumption guidance,
please refer to the [aiSSEMBLE Github Pages](https://boozallen.github.io/aissemble/current/data-lineage.html)

### Core Functionality

This module presently provides three main capabilities:

* Generic types to represent Data Lineage metadata
* Convenience methods for emitting Data Lineage through various mediums
* Conversion functions to transform the generic types to and from popular Data Lineage formats.

Through these capabilities, this module fulfills the need for an easy-to-use, implementation-agnostic 
Data Lineage interface.

### Developer Guidance

* This module should display little to no dependence on any other aiSSEMBLE module.  It is intentionally generic.
* Any changes to method or function signatures must be reflected in any relevant template code within `foundation-mda`.
* Any change or addition in functionality must be accompanied by associated automated tests.
  * As we are serving as an interface to third party libraries and services, any input parameters must be exhaustively validated.

### Use Custom Microprofile config properties
By default, foundation-data-lineage uses the below microprofile-config.properties for the messaging service configurations
```properties
kafka.bootstrap.servers=kafka-cluster:9093
mp.messaging.outgoing.lineage-event-out.cloud-events=false
mp.messaging.outgoing.lineage-event-out.connector=smallrye-kafka
mp.messaging.outgoing.lineage-event-out.topic=lineage-event-out
mp.messaging.outgoing.lineage-event-out.key.serializer=org.apache.kafka.common.serialization.StringSerializer
mp.messaging.outgoing.lineage-event-out.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```

You can also use your own microprofile-config.properties file. To do that, you must provide your own property path to the code:

1. Set the path of the microprofile-config.properties on your file system
```python
  # specify the new microprofile-config properties path
  self.emitter.set_messaging_properties_path(string_path_to_the_property_file)

```
2. Set the new emitter topic  
   add below property to the data-lineage.properties file of the appropriate docker image (e.g. spark-worker)
```text
data.lineage.emission.topic=replace-with-your-topic
```

3. (Optional) If you are using the v1 kafka-cluster helm chart, add the new topic to kafka-cluster values.yaml file; e.g.: add `replace-with-your-topic:1:1` to the **KAFKA_CREATE_TOPICS** environment variable in the `-deploy/src/main/resources/apps/kafka-cluster/values.yaml`

## NOTE:
There is a known issue with the confluent-kafka dependency that is brought in by the openlineage-client library that prevents it from installing this requirement on some arm64 environments. The problem is that librdkafka does not provide a wheel for all operating systems in the arm64 architecture, requiring that it be built from source in order for confluent-kafka to be installed. If you run into this problem, you can install it from source using the below commands:

* apt-get update
* apt install -y python3-dev git
* git clone https://github.com/confluentinc/librdkafka.git
* cd librdkafka
* ./configure --arch=aarch64
* make
* make install