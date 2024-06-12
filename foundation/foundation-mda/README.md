NB: OSS: we might want to cull this entire page

# foundation-mda
Controls the Model Driven Architecture (MDA) process for the Solution Baseline.  MDA is a technique where high level models can be used to generalize common components of an architecture.  These models are then fed through a code generation process at build-time that will produce much of the labor intensive technical underpinnings needed to stand up a solution.

For the aiSSEMBLE&trade; Solution Baseline, pipelines models allow this MDA process to create labor-intensive artifacts around the following concepts:
* Spark / PySpark job creation and configuration
* Docker / Kubernetes container creation and configuration
* Machine Learning training and inference

Another major benefit of this approach is that is helps team achieve strong architectural consistency that bakes in common features that are often otherwise missed due to lack of time (e.g., provenance tracking, alerting, security).  This consistency also helps make for more future-resilient solutions where updates to generated artifacts can add new features, fix defects, and improve performance over time.  

Generic content is maintained in a location within your projects that can be overwritten on each build - ensuring it is always the latest and greatest implementation.  At the same time, foundation-mda will generate out a stub implementation for the locations you'd want to add specific mission/business logic.  Once one of these stubs is written, it will  _never_  be overwritten to protect your custom logic.

# How to configure your Module to use foundation-mda
The easier way to leverage foundation-mda is to us the use the [aiSSEMBLE archetype process](NB: OOS: update with appropriate link to archetype).

Alternatively, as demonstrated in the [test-mda-models](https://github.com/boozallen/aissemble/blob/dev/test/test-mda-models/test-data-delivery-spark-model/pom.xml) module, you can add the following Maven plugin declaration and dependency to your project:

```xml
<builds>
	<build>
		<plugins>
			<plugin>
				<groupId>org.technologybrewery.fermenter</groupId>
				<artifactId>fermenter-mda</artifactId>
				<configuration>
					<basePackage>YOUR BASE PACKAGE NAME HERE (e.g., com.boozallen.aiops.example)</basePackage>
					<profile>YOUR DESIRED PROFILE HERE</profile>
				</configuration>
			</plugin>
		</plugins>
	</build>
</builds>
```

# Available Profiles
Various profiles are listed in the `src/main/resources/profiles.json` that can support MDA generation of various artifact sets. A subset of the high-level profiles are:
* `aissemble-maven-modules`: generates Maven POM files for each pipeline
* `aissemble-project-root`: generates root-level artifacts (e.g., Jenkins templates, gitignore files)
* `data-delivery-spark`: generates Spark Java pipelines
* `data-delivery-pyspark`: generates PySpark pipelines
* `aissemble-machine-learning-training`: generates machine learning python training pipeline steps
* `aissemble-machine-learning-inference`: generates machine learning python inference pipeline steps & associated Modzy resources
* `aissemble-spark-worker-docker`: generates Docker artifacts for Spark resources (Java, PySpark, etc.)
* `aissemble-airflow-docker`: generates Airflow Docker artifacts
* `aissemble-inference-docker`: generates inference Docker artifacts

# Model Specification
When using the aissemble archetype process, your pipeline models will be located in `<your artifact id>-pipeline-models` 
and named according to the specific model names.

# Additional Information
aiops-mda is an extension of the (fermenter)[https://github.com/TechnologyBrewery/fermenter/] project.  The Fermenter wiki has more information on (how generation is controlled and customized([https://fermenter.atlassian.net/wiki/spaces/FER/pages/48955396/Controlling+What+Gets+Generated].