Feature: Common File Operations

  Scenario Outline: Retrieve lines
    Given a file with the contents:
      """
      first line
      second line
      third line
      fourth line
      """
    When I retrieve the lines <start> to <end> of the file
    Then the lines should be "<lines>"

    Examples:
      | start | end | lines                                            |
      | 0     | 2   | first line\nsecond line\nthird line              |
      | 1     | 2   | second line\nthird line                          |
      | 2     | 3   | third line\nfourth line                          |
      | 0     | 99  | first line\nsecond line\nthird line\nfourth line |

