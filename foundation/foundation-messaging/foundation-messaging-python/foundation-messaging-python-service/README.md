## Testing consumer code using the library
To leverage the messaging library in your integration tests, you will need to both bring in the python client as a dependency for your project, and you will need to bring in a copy of the library's service jar, and then start the jar during test execution. To get a copy of the service jar, you can bring in the foundation-messaging-python-service dependency, and then put the jar somewhere that your tests can access it. Take the following example, which puts a copy of the service jar, along with aiSSEMBLE's core messaging and java jars, under target/dependency/ during the build:

pypoetry.toml
```toml
[tool.poetry.dependencies]
foundation-messaging-python = "^1.3.0"
```

pom.xml
```xml
<dependencies>
    <dependency>
        <groupId>com.boozallen.aissemble</groupId>
        <artifactId>foundation-messaging-python-service</artifactId>
        <version>1.3.0</version>
    </dependency>
    <dependency>
        <groupId>com.boozallen.aissemble</groupId>
        <artifactId>foundation-messaging-java</artifactId>
        <version>1.3.0</version>
    </dependency>
    <dependency>
        <groupId>${project.groupId}</groupId>
        <artifactId>foundation-core-java</artifactId>
        <version>1.3.0</version>
    </dependency>
</dependencies>

<build>
    <plugin>
        <artifactId>maven-dependency-plugin</artifactId>
        <executions>
            <execution>
                <id>copy-service-jar</id>
                <phase>pre-integration-test</phase>
                <goals>
                    <goal>copy-dependencies</goal>
                </goals>
                <configuration>
                    <stripVersion>true</stripVersion>
                    <includeArtifactIds>foundation-messaging-python-service,foundation-messaging-java,foundation-core-java</includeArtifactIds>
                </configuration>
            </execution>
            <execution>
                <id>copy-dependencies</id>
                <phase>pre-integration-test</phase>
                <goals>
                    <goal>copy-dependencies</goal>
                </goals>
                <configuration>
                    <stripVersion>false</stripVersion>
                    <excludeArtifactIds>foundation-messaging-python-service,foundation-messaging-java,foundation-core-java</excludeArtifactIds>
                </configuration>
            </execution>
        </executions>
    </plugin>
</build>
```

In your test code, you can run the service jar using py4j's launch_gateway() function. This will create a jvm that can be interacted with in python, allowing the messaging client to talk to the service.

```python
@given('your test precondition')
def step_impl(context):
    context.service_port = launch_gateway(
        classpath="target/dependency/*", die_on_exit=True, redirect_stdout=1
    )
```

Additionally, you can provide configuration options for your tests to set up topics, channels, brokers, and more. Microprofile configurations can be provided in a couple different ways, an easy way to provide them to your service's jvm during a test is to set your properties as environment variables. To do this, you can modify the Given function from the previous example like the following. Here, we are configuring a topic named test_topic_name that points to a channel named test-channel-name, which is backed by smallrye's in-memory connector.

```python
@given('your test precondition')
def step_impl(context):
    os.environ["mp.messaging.outgoing.test-channel-name.connector"] = "smallrye-in-memory"
    os.environ["mp.messaging.outgoing.test-channel-name.value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
    os.environ["mp.messaging.outgoing.test-channel-name.topic"] = "test_topic_name"
    context.service_port = launch_gateway(
        classpath="target/dependency/*", die_on_exit=True, redirect_stdout=1
    )
```

Using an in-memory connector is the recommended approach for testing in order to reduce overhead, however it is possible to use any of the brokers available. 
Currently, supported brokers are listed below:

* smallrye-kafka
* smallrye-amqp


Alternatively, you can provide your test's microprofile configurations through a custom jar by putting a microprofile.properties file in the jar's classpath at src/main/resources/META-INF/.

## Packaging the library for deployment
### Packaging in a containerized microservice
The messaging library can be deployed in a container by running the jar through py4j, and then pointing the python messaging client to that jvm. For use in pipelines, this will mean making the service jar available on your spark worker image where the pipeline executes. For all other use cases, the same principle applies, you will need to make a copy of the library's service available wherever your python client code is running. First, let's take a look at how to package the service jar with custom configurations.

The most common option for providing your configurations is to make a microprofile.properties file available on the service's classpath. To do this, you can create a custom java module that contains a src/main/resources/META-INF/microprofile.properties.

For smallrye-kafka:
```properties
mp.messaging.outgoing.example-channel.connector=smallrye-kafka
mp.messaging.outgoing.example-channel.value.serializer=org.apache.kafka.common.serialization.StringSerializer
mp.messaging.outgoing.example-channel.topic=example-topic

mp.messaging.outgoing.another-channel.connector=smallrye-kafka
mp.messaging.outgoing.another-channel.value.serializer=org.apache.kafka.common.serialization.StringSerializer
mp.messaging.outgoing.another-channel.topic=another-topic
```

In this example, we see a pair of channels that are mapped to a corresponding pair of topics. The messaging library's client code interacts with topics, which are backed by a channel in whichever messaging connector has been configured for it. Using the example above, this means that a call from the messaging topic to send a message to "example-topic" will result in the service emitting a message to a channel named "example-channel". It is worth noting that it is possible to map one topic to both an incoming and outgoing channel, so that messages on the client can be read from and written to the same place. Such a configuration would look like this.

```properties
mp.messaging.outgoing.example-channel.connector=smallrye-kafka
mp.messaging.outgoing.example-channel.value.serializer=org.apache.kafka.common.serialization.StringSerializer
mp.messaging.outgoing.example-channel.topic=example-topic

mp.messaging.incoming.another-channel.connector=smallrye-kafka
mp.messaging.incoming.another-channel.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
mp.messaging.incoming.another-channel.topic=example-topic
```

Finally, add the kafka server that you want to connect. 
```properties
kafka.bootstrap.servers=127.0.0.1:9092
```

For more information about smallrye-kafka configuration, refer to https://smallrye.io/smallrye-reactive-messaging/smallrye-reactive-messaging/3.4/kafka/kafka.html


Alternatively, you can use the amqp as your broker and have the configuration properties files as below:

```properties

amqp-host=localhost
amqp-port=5672
amqp-username=smallrye
amqp-password=smallrye

# AMQP sink (we write to using an Emitter)
mp.messaging.outgoing.my-channel.connector=smallrye-amqp
mp.messaging.outgoing.my-channel.address=data
mp.messaging.outgoing.my-channel.durable=true

# AMQP source (we read from)
mp.messaging.incoming.my-topic.connector=smallrye-amqp
mp.messaging.incoming.my-topic.address=data
mp.messaging.incoming.my-topic.durable=true
mp.messaging.incoming.my-topic.failure-strategy=accept
```

Once you have your microprofile configurations, you need to package this module as a jar, and add it as a dependency in your deployment module. You will also need a copy of the foundation-messaging to run the service, listed below.

```text
<your-custom-java-module>
py4j
foundation-messaging-python-service
```

The maven-dependency-plugin's copy-dependencies goal can be used to write the dependency jars you need to your deployment module's target directory, and then the maven-assembly-plugin can be used to zip up all relevant dependencies, like the following examples.

Add dependencies via `copy-dependencies`
pom.xml
```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>3.1.1</version>
                <executions>
                    <execution>
                        <id>copy-resources</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>target/dependency</outputDirectory>
                            <includeArtifactIds>foundation-messaging-python-service</includeArtifactIds>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```

Zip up all the relevant dependencies
pom.xml
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <descriptors>
            <descriptor>service.xml</descriptor>
        </descriptors>
        <finalName>messaging-service</finalName>
        <appendAssemblyId>true</appendAssemblyId>
    </configuration>
</plugin>
```

service.xml
```xml
<assembly
        xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.2"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.2 http://maven.apache.org/xsd/assembly-1.1.2.xsd">
    <id>app</id>

    <includeBaseDirectory>false</includeBaseDirectory>

    <formats>
        <format>zip</format>
    </formats>

    <fileSets>
        <fileSet>
            <directory>${project.build.directory}/</directory>
            <outputDirectory>./</outputDirectory>
        </fileSet>
    </fileSets>

</assembly>
```

Once you have a zip file that contains the messaging service with your configurations, you simply need to put it in the relevant docker container and unzip it somewhere you can access it from your python code. The following example shows how you can add the service to a spark worker image, by making the following addition to its Dockerfile.

```dockerfile
COPY target/messaging-service-app.zip /opt/spark/work-dir/messaging-service/messaging-service-app.zip
WORKDIR /opt/spark/work-dir/messaging-service/
RUN apt-get update
RUN apt-get install zip unzip
RUN unzip messaging-service-app.zip
```

You would then run the service from your python code using the launch_gateway function, pointed at the directory where your service jar is available.

```python
service_port = launch_gateway(
        classpath="/opt/spark/work-dir/messaging-service/*", die_on_exit=True, redirect_stdout=1
    )
```

Another option to containerize the messaging service is to bring the service jar itself into your container, and then provide your configurations as environment variables. The downside to this is that you would not be able to define managed beans for connectors by going this route. If all you're looking for is to use smallrye's in-memory connector, this is of no concern, however you may otherwise find this option to be limited. Nonetheless, if this is an option you want to pursue, it can be done with a Dockerfile update like the following.

```dockerfile
#An example configuration for a messaging channel
ENV mp.messaging.outgoing.example-channel.connector=smallrye-in-memory
ENV mp.messaging.outgoing.example-channel.value.serializer=org.apache.kafka.common.serialization.StringSerializer
ENV mp.messaging.outgoing.example-channel.topic=example-topic

#A second channel configuration
ENV mp.messaging.outgoing.another-channel.connector=smallrye-in-memory
ENV mp.messaging.outgoing.another-channel.value.serializer=org.apache.kafka.common.serialization.StringSerializer
ENV mp.messaging.outgoing.another-channel.topic=another-topic

COPY <path-to-the-service-jar> /home/messaging-service/foundation-messaging-python-service.jar
```

### Extending the docker image
As an alternative to modifying the docker image where your python code is going to run, you can extend the image to provide your customizations through a mounted volume rather than copying them all over. To do this, you will need to create a docker volume on the host machine's filesystem (docker volume create your-vol) and then put your configurations in the volume's directory. Then, you can leverage the volume in a new docker image that mounts that volume to a directory in its filesystem. Look at the following example.

```dockerfile
FROM your-domain/your-base-image

VOLUME your-vol
# Any other configuration steps you want to take, like setting environment variables 
```

If you are extending an image that does not have a java installation with which to start the service jvm, you will need to install it in your image. You can do this using the following commands in your Dockerfile

```dockerfile
RUN apt-get update && \
    apt-get install -y <your-preferred-java-version> && \
    apt-get install -y ant && \
    apt-get clean;
    
RUN apt-get update && \
    apt-get install ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f;

ENV JAVA_HOME /usr/lib/jvm/<your-preferred-java-version>/
RUN export JAVA_HOME
```

### Overriding customizations
Microprofile allows you to set ordinal values on configuration sources to prioritize them, allowing for overrides. This can be used to provide a default configuration, and then provide environment-specific values, like for development and test environments. To do this, you will need to make sure that all the microprofile properties files are on the classpath for the service, and then set a config_ordinal value for each. The default ordinal value for a properties file is 100, with higher numbers taking precedence over lower numbers. For instance, the following properties file will override one that uses the default ordinal value

```properties
config_ordinal=101
mp.messaging.outgoing.channel-name.connector=smallrye-kafka
mp.messaging.outgoing.channel-name.value.serializer=org.apache.kafka.common.serialization.StringSerializer
mp.messaging.outgoing.channel-name.topic=topic-name
```

