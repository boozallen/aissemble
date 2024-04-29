@jwtToken
Feature: Create JSON Web Tokens based on PDP rules

  Scenario Outline: create a token containing only default claims
    When a token is requested for "<subject>" and "<audience>"
    Then the token contains claims for "<subject>", "<audience>", and "<issuer>"

    Examples: 
      | subject       | audience                                      | issuer      |
      | tonyGwynn     | petco park fans                               | CN=aiops.bah, OU=bah, O=bah, L=DC, ST=DC, C=US |
      | trevorHoffman | fans that stuck it out to the end of the game | CN=aiops.bah, OU=bah, O=bah, L=DC, ST=DC, C=US |

  Scenario: create a token with PDP rule claims
    When a token is requested for "tonyGwynn" and "unitTest"
    And the following claims:
      | name                         | resource   | action                            |
      | canSingle                    | hit/single |                                   |
      | canStrikeout                 |            | strikeout                         |
    Then a claim is returned with the following rule and decision pairings:
      | name                         | resource   | action                            | result         |
      | canSingle                    | hit/single |                                   | PERMIT         |
      | canStrikeout                 |            | strikeout                         | DENY           |

  Scenario Outline: create token with Attribute Store values
    When a token is requested for "<player>" with an attribute value claim for seasons batting over .350
    Then a claim is returned with the attributes "<seasonsBattingOver350>"

    Examples:
      | player        | seasonsBattingOver350                    |
      | tonyGwynn     | 1984, 1987, 1993, 1994, 1995, 1996, 1997 |
      | reggieJackson |                                          |
      | kirbyPuckett  |                                     1988 |

