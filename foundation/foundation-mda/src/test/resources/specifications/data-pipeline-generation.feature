@manual
Feature: Generating pipeline steps
# We don't have a current template for testing, so this file is for documentation purposes only
# This assumes small-rye will execute a publisher/source after
#  wiring. I'm pretty sure that's true but we should verify
	Scenario Outline: Step execution is handled via messaging framework
	  Given a Java data delivery pipeline
	  And the pipeline has a step
	  And the step has a "<messagingType>" messaging type
	  When the MDA generation is run
	  Then the driver impl does not execute the step
	
	Examples: 
	  | messagingType	|
	  | outbound		|
	  | inbound			|
	  
	Scenario: Step execution produces output
		Given a Java data delivery pipeline
		And the pipeline has a step
		And the step has a native outbound
		And the step does not have a messaging inbound
		When the MDA generation is run
		Then the driver impl executes the step
		And captures the execution result in a variable
		
	Scenario: Step execution does not produce output
		Given a Java data delivery pipeline
		And the pipeline has a synchronous step
		And the step has no outbound
		And the step does not have a messaging inbound
		When the MDA generation is run
		Then the driver impl executes the step
	
	Scenario: Step execution does not produce output
		Given a Java data delivery pipeline
		And the pipeline has a asynchronous step
		And the step has no outbound
		And the step does not have a messaging inbound
		When the MDA generation is run
		Then the driver impl executes the step
		And captures the execution result in a variable

	Scenario: Pipeline steps generate with a common mutable parent class
		Given a project named "example"
		And a "data-flow" pipeline using "data-delivery-pyspark"
		When the profile "data-delivery-pyspark-pipeline" is generated
		Then a common step parent is created
		And the common step parent is customizable
