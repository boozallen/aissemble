@attributes
Feature: Specify custom attributes

  Scenario Outline: specify attributes with shorthand types
    Given an attribute with aissemble type "<aissembleType>"
    When the attribute is read
    Then the fully qualified type "<fullyQualifiedType>" is returned

    Examples:
      | aissembleType | fullyQualifiedType                       |
      | string    | http://www.w3.org/2001/XMLSchema#string  |
      | boolean   | http://www.w3.org/2001/XMLSchema#boolean |
      | anyUri    | http://www.w3.org/2001/XMLSchema#anyURI  |
      | uri       | http://www.w3.org/2001/XMLSchema#anyURI  |
      | date      | http://www.w3.org/2001/XMLSchema#date    |
      | int       | http://www.w3.org/2001/XMLSchema#integer |
      | integer   | http://www.w3.org/2001/XMLSchema#integer |
      | double    | http://www.w3.org/2001/XMLSchema#double  |

  Scenario Outline: specify authorization by action and subject
    Given a resource action "<action>" and subject "<subject>"
    When a policy decision is requested
    Then a "<decision>" decision is returned

    Examples:
      | action    | subject       | decision       |
      | strikeout | tonyGwynn     | DENY           |
      | strikeout | alexOvechkin  | NOT_APPLICABLE |
      | strikeout | reggieJackson | PERMIT         |
      | atBat     | tonyGwynn     | PERMIT         |
      | atBat     | alexOvechkin  | PERMIT         |
      | atBat     | reggieJackson | PERMIT         |
      | scoreGoal | tonyGwynn     | NOT_APPLICABLE |

  Scenario Outline: specify authorization by an integer attribute loaded from an attribute store (jersey number is 44)
    Given a resource action "<action>" and subject "<subject>"
    When a policy decision is requested
    Then a "<decision>" decision is returned

    Examples:
      | action                            | subject       | decision       |
      | reggie-jackson-jersey-number-test | reggieJackson | PERMIT         |
      | reggie-jackson-jersey-number-test | tonyGwynn     | NOT_APPLICABLE |
      | reggie-jackson-jersey-number-test | alexOvechkin  | NOT_APPLICABLE |
      | reggie-jackson-jersey-number-test | anthonyRizzo  | PERMIT         |

  Scenario Outline: specify authorization by an any uri attribute loaded from an attribute store (profile contains MLB Hall of Frame URL)
    Given a resource action "<action>" and subject "<subject>"
    When a policy decision is requested
    Then a "<decision>" decision is returned

    Examples:
      | action                        | subject       | decision       |
      | hall-of-fame-profile-uri-test | reggieJackson | PERMIT         |
      | hall-of-fame-profile-uri-test | tonyGwynn     | PERMIT         |
      | hall-of-fame-profile-uri-test | marioMendoza  | NOT_APPLICABLE |
      | hall-of-fame-profile-uri-test | alexOvechkin  | NOT_APPLICABLE |

  Scenario Outline: specify authorization by a boolean attribute loaded from an attribute store (deny for suspected PED user)
    Given a resource action "<action>" and subject "<subject>"
    When a policy decision is requested
    Then a "<decision>" decision is returned

    Examples:
      | action            | subject       | decision |
      | peds-boolean-test | reggieJackson | PERMIT   |
      | peds-boolean-test | tonyGwynn     | PERMIT   |
      | peds-boolean-test | kenCaminiti   | DENY     |
      | peds-boolean-test | wallyJoyner   | DENY     |

  Scenario Outline: specify authorization by a double attribute loaded from an attribute store (hit above mendoza line)
    Given a resource action "<action>" and subject "<subject>"
    When a policy decision is requested
    Then a "<decision>" decision is returned

    Examples:
      | action                      | subject       | decision |
      | hit-above-mendoza-line-test | reggieJackson | PERMIT   |
      | hit-above-mendoza-line-test | tonyGwynn     | PERMIT   |
      | hit-above-mendoza-line-test | kenCaminiti   | PERMIT   |
      | hit-above-mendoza-line-test | marioMendoza  | DENY     |

  Scenario Outline: specify authorization by a date attribute loaded from an attribute store (can wear helmet without ear flaps)
    Given a resource action "<action>" and subject "<subject>"
    When a policy decision is requested
    Then a "<decision>" decision is returned

    Examples:
      | action           | subject       | decision |
      | no-ear-flap-test | reggieJackson | PERMIT   |
      | no-ear-flap-test | tonyGwynn     | PERMIT   |
      | no-ear-flap-test | kenCaminiti   | DENY     |
      | no-ear-flap-test | marioMendoza  | PERMIT   |

  Scenario Outline: specify authorization by finding a value in a collection attributes from an attribute store (hit over .350 in 1984)
    Given a resource action "<action>" and subject "<subject>"
    When a policy decision is requested
    Then a "<decision>" decision is returned

    Examples:
      | action               | subject       | decision       |
      | hit-over-350-in-1984 | reggieJackson | NOT_APPLICABLE |
      | hit-over-350-in-1984 | tonyGwynn     | PERMIT         |
      | hit-over-350-in-1984 | wadeBoggs     | PERMIT         |
      | hit-over-350-in-1984 | kirbyPuckett  | NOT_APPLICABLE |
