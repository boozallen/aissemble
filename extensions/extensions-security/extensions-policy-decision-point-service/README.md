This module contains a Quarkus microservice serving endpoints for the Policy Decision Point.

To make a call to the decision point use the following REST endpoint

POST localhost:8080/api/pdp

Body:
```json
{
  "jwt": "YOUR_TOKEN",
  "resource": "YOUR_RESOURCE",
  "action": "YOUR_ACTION"
}
```
**Note:** The provided jwt has a long expiration date and is valid for several years.  Typically, you would configure your security policies to limit how long a token can be valid and require a new token on a regular basis. To generate a new token you can send a GET request to http://localhost:8080/api/token which will return a valid token.  You can then substitute this token in the example json above.


* jwt
    * A json web token which contains the subject (sub) and other attributes.
* resource
    * Used if you need a policy decision for accessing a resource.
* action
    * Used if you need a policy decision for performing an action.
