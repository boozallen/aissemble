# foundation-drift-detection

Contains the modules used to set up drift invocation using configurable policies and rules. 

Examples of the policy files used for configuration can be found by looking in the target directory of foundation-drift-detection-engine.

## Running the tests
The tests currently run as part of the build, and can be run by building the project with Maven.

./mvnw clean install

## Deploying the drift detection rest service
The quarkus microservice for drift detection has an example drift policy included. It can be started up by running:

docker-compose up

The current example policy has a mean of 50, a standard deviation of 5, and a zScore of 2, which results in values between
40-60 as being within range and values below 40 or above 60 as being flagged for drift. 

## Setting the policy file location
The policy file location can be set within the drift-detection.properties file or by using a system property DRIFT_DETECTION_POLICIES that will override anything in the drift-detection.properties. 

## Example JSON for the drift rest service

Single drift variable input
{
   "input":{
      "type":"single",
      "name":"testSingleInput",
      "value":49.2
   },
   "control":null
}

Multiple drift variable inputs
{
   "input":{
      "type":"multiple",
      "name":"myLongList",
      "variables":[
         {
            "type":"single",
            "name":"oneValue",
            "value":51.3
         },
         {
            "type":"single",
            "name":"anotherValue",
            "value":48.7
         }
      ]
   },
   "control":null
}