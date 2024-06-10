# service-discovery-core

## Before we begin (notes)

This module provides an example of how Service Discovery can be implemented using [Quarkus](https://quarkus.io) and [Vert.x Service Discovery](https://vertx.io/docs/vertx-service-discovery/java).

### Available REST endpoints

The **ServiceDiscoveryResource** class exposes the following REST endpoints:
- POST `/service-discovery/registry`
	- Registers a service in Service Discovery.
	- The details of the service must be provided in the request body:
	```json
	{
	    "name": "service-name",
	    "type": "service-type",
	    "endpoint": "/service-endpoint",
	    "metadata": {
	        "key1": "val1",
	        "key2": "val2",
	        ...any number of key/value pairs
		}
	}
	```
	- A response containing the registered service record will be returned upon successful registration.

- PUT `/service-discovery/registry/{registrationId}`
	- Updates a service in Service Discovery with the given `registrationId`.
	- The details of the service must be provided in the request body:
  ```json
  {
      "name": "service-name",
      "type": "service-type",
      "endpoint": "/service-endpoint",
      "metadata": {
          "key1": "val1",
          "key2": "val2",
          ...any number of key/value pairs
      }
  }
  ```
	- A response indicating whether the updated was successful or not will be returned (true if successful, false if not).


- DELETE `/service-discovery/registry/{registrationId}`
	- Unregisters (removes) a service from Service Discovery.
	- The registration ID from the service registration record must be passed in the `{registrationId}` parameter.
	- A response indicating whether the unregistration was successful or not will be returned (true if successful, false if not).

- GET `/service-discovery/service/{name}`
	- Retrieves the registered services with the given name passed in the `{name}` parameter.
	  - Optional Query Param `includeOutOfService` will add services that are unavailable to the results if set to true
	- A response containing a list of the matching services' registration records will be returned.

- GET `/service-discovery/service/{type}`
	- Retrieves the registered services with the given type passed in the `{type}` parameter.
	  - Optional Query Param `includeOutOfService` will add services that are unavailable to the results if set to true
	- A response containing a list of the matching services' registration records will be returned.

## Building the project

### Run the build

`./mvnw clean install`

### Verify unit tests

Unit tests to verify registering, unregistering, and retrieving services are executed during the build. If the tests are successful, you will see output similar to the following:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.boozallen.servicediscovery.core.ServiceDiscoveryResourceTest
2021-03-08 12:38:14,747 INFO  [io.quarkus] (main) Quarkus 1.12.1.Final on JVM started in 1.615s. Listening on: http://localhost:8081
2021-03-08 12:38:14,748 INFO  [io.quarkus] (main) Profile test activated. 
2021-03-08 12:38:14,749 INFO  [io.quarkus] (main) Installed features: [cdi, mutiny, resteasy, resteasy-jsonb, resteasy-mutiny, smallrye-context-propagation, vertx]
2021-03-08 12:38:16,167 INFO  [com.boo.ser.cor.ServiceDiscoveryProvider] (executor-thread-1) Successfully registered service 'keJoK': eea8211f-92a7-4235-80f5-7aa4039f70fc
2021-03-08 12:38:16,680 INFO  [com.boo.ser.cor.ServiceDiscoveryProvider] (executor-thread-1) Successfully unregistered service eea8211f-92a7-4235-80f5-7aa4039f70fc
2021-03-08 12:38:16,750 INFO  [com.boo.ser.cor.ServiceDiscoveryProvider] (executor-thread-1) Successfully registered service '5Ooq9': 54d4494e-34fd-43c8-bda7-0a4351ef2788
2021-03-08 12:38:16,841 INFO  [com.boo.ser.cor.ServiceDiscoveryProvider] (executor-thread-1) Successfully registered service 'PW9az': 297c07b4-4497-4f0f-a97d-1f100cb2330b
2021-03-08 12:38:16,855 INFO  [com.boo.ser.cor.ServiceDiscoveryProvider] (executor-thread-1) 1 services found with name 'PW9az'
2021-03-08 12:38:16,900 ERROR [com.boo.ser.cor.ServiceDiscoveryProvider] (executor-thread-1) Failed to unregister service 4NPra: io.vertx.core.impl.NoStackTraceThrowable: Record '4NPra' not found

[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.403 s - in com.boozallen.servicediscovery.core.ServiceDiscoveryResourceTest
2021-03-08 12:38:16,928 INFO  [io.ver.ser.imp.DiscoveryImpl] (main) Stopping service discovery
2021-03-08 12:38:16,932 INFO  [io.ver.ser.imp.DiscoveryImpl] (main) Discovery bridges stopped
2021-03-08 12:38:16,945 INFO  [io.quarkus] (main) Quarkus stopped in 0.031s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```````

## Running manually

1. Run the following to start the Quarkus application in development-mode:
	- `./mvnw quarkus:dev -pl :service-discovery-core`
	- The following output will indicate when the application is up and running:
	```
	__  ____  __  _____   ___  __ ____  ______ 
	 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
	 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
	--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
	2021-03-08 13:15:13,348 INFO  [io.quarkus] (Quarkus Main Thread) service-discovery-core 1.0.0-SNAPSHOT on JVM (powered by Quarkus 1.12.1.Final) started in 1.309s. Listening on: http://localhost:8000
	2021-03-08 13:15:13,351 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
	2021-03-08 13:15:13,351 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, mutiny, resteasy, resteasy-jsonb, resteasy-mutiny, smallrye-context-propagation, vertx]
	````

2. Using a REST client such as Postman, send a POST request to `http://localhost:8000/service-discovery/registry` with the following request body to register a service:

   ```json
   {
       "name": "MyService",
       "type": "MyServiceType",
       "endpoint": "/my-service-endpoint",
       "metadata": {
           "key1": "val1",
           "key2": "val2"
       }
   }
   ```

   - Make sure to set request header 'Content-Type' to 'application/json'.

3. Verify that a successful response containing the service registration record is returned:

   ```json
   {
       "location": {
           "endpoint": "/my-service-endpoint"
       },
       "metadata": {
           "key1": "val1",
           "key2": "val2"
       },
       "name": "MyService",
       "registration": "357f6b9f-e1fd-4cb3-bf8f-cabd581ed406",
       "status": "UP",
       "type": "MyServiceType"
   }
   ```

4. Send a GET request to `http://localhost:8000/service-discovery/service/MyService` to retrieve the registered service.

5. Verify that the service's registration record is returned in the response list:

   ```json
   [
       {
           "location": {
               "endpoint": "/my-service-endpoint"
           },
           "metadata": {
               "key1": "val1",
               "key2": "val2"
           },
           "name": "MyService",
           "registration": "357f6b9f-e1fd-4cb3-bf8f-cabd581ed406",
           "status": "UP",
           "type": "MyServiceType"
       }
   ]
   ```

6. Send a DELETE request to `http://localhost:8000/service-discovery/registry/{registrationId}` with the registration ID of the service (unique identifier in the `registration` field) to unregister it.

7. Re-run step 4 and verify that the service is no longer returned in the response list.

8. The application can be stopped using ctrl+c in the window in which it was started (from step 1).
