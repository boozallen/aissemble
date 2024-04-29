#!/bin/bash

###
# #%L
# AIOps Foundation::Archetype::Project
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###

echo -e "\n\n **** TESTING ARCHETYPE GENERATION **** \n\n"

if [ $# -ne 1 ]; then
    echo "Incorrect number of arguments. Usage:"
    echo "   test-project-archetype.sh <archetype_version>"
    exit 1
fi

mkdir -p target/temp
cd target/temp

function updatePomBasedOnChildDirs {
	# The 'TODO' text line that we're going to replace in the file
    # should be passed in as the first param to this method. i.e.
    #    updatePomBasedOnChildDirs '<!-- TODO: this is where manual actions go -->'
	todoText=$1

    modules=""
	for d in */ ; do
	  if [ $d != "target/" ]; then
      # Drop / from the end of the directory
      d="${d/\//}"
      modules+="<module>$d</module>"
		fi
	done

	echo -e "\nINFO: Adding \n\t$modules\n\tto $PWD/pom.xml\n"
	fileContents=`cat pom.xml`
	fileContents="${fileContents/$todoText/$modules}"

	# NOTE - the formatting get's all messed up
	echo -e $fileContents > pom.xml
}

package='com.bah.aiops'
echo "Using Project Version: $1"
echo -e "\nINFO: Generating a new project from the archetype\n"
rm -rf test-generator
mvn archetype:generate -B \
	-DarchetypeGroupId=com.boozallen.aissemble \
	-DarchetypeArtifactId=foundation-archetype \
  -DarchetypeVersion="$1" \
	-DgroupId=com.bah.aiops \
	-DartifactId=test-generator \
	-Dversion=1.0.0-SNAPSHOT \
	-Dpackage=$package \
	-DprojectGitUrl=https://github.com/boozallen/aissemble-archetype-test \
	-DprojectName=Generator_Test

cd test-generator/

# Add pipelines files necessary for testing.

echo -e "\nINFO: Generating test pipelines"

#ExampleDictionary.json
echo "{
  \"name\": \"ExampleDictionary\",
  \"package\": \"${package}\",
  \"dictionaryTypes\": [
    {
      \"name\": \"id\",
      \"simpleType\": \"string\"
    }
  ]
}" > test-generator-pipeline-models/src/main/resources/dictionaries/ExampleDictionary.json

#ExampleRecord.json
echo "{
  \"name\": \"ExampleRecord\",
  \"package\": \"${package}\",
  \"dataAccess\": {
    \"enabled\": \"false\"
  },
  \"fields\": [
    {
      \"name\": \"recordId\",
      \"type\": {
        \"name\": \"id\",
        \"package\": \"${package}\"
      }
    }
  ]
}" > test-generator-pipeline-models/src/main/resources/records/ExampleRecord.json

# ExampleDataDelivery.json
echo "{
      	\"name\": \"ExampleDataDeliveryPipeline\",
      	\"package\": \"${package}\",
      	\"type\": {
      		\"name\": \"data-flow\",
      		\"implementation\": \"data-delivery-spark\"
      	}
      }" > test-generator-pipeline-models/src/main/resources/pipelines/ExampleDataDeliveryPipeline.json

# ExampleDataDeliveryPySparkPipeline.json
echo "{
      	\"name\": \"ExampleDataDeliveryPySparkPipeline\",
      	\"package\": \"${package}\",
      	\"type\": {
      		\"name\": \"data-flow\",
      		\"implementation\": \"data-delivery-pyspark\"
      	},
      	\"steps\": [
      		{
      			\"name\": \"Ingest\",
      			\"type\": \"synchronous\",
      			\"persist\": {
      				\"type\": \"hive\"
      			}
      		},
      		{
      			\"name\": \"Transform\",
      			\"type\": \"synchronous\",
      			\"persist\": {
      				\"type\": \"hive\"
      			}
      		},
      		{
      			\"name\": \"Enrich\",
      			\"type\": \"synchronous\",
      			\"persist\": {
      				\"type\": \"hive\"
      			}
      		}
      	]
      }" > test-generator-pipeline-models/src/main/resources/pipelines/ExampleDataDeliveryPySparkPipeline.json

# ExampleMachineLearningPipeline.json
echo "{
      	\"name\": \"ExampleMachineLearningPipeline\",
      	\"package\": \"com.boozallen.aiops.cookbook\",
      	\"type\": {
      		\"name\": \"machine-learning\",
      		\"implementation\": \"machine-learning-mlflow\",
      		\"versioning\": {
      			\"enabled\": false
      		}
      	},
      	\"steps\": [
      		{
      			\"name\": \"MachineLearningTraining\",
      			\"type\": \"training\",
      			\"inbound\": {
      				\"type\": \"messaging\",
      				\"channelName\": \"train\"
      			}
      		},
      		{
      			\"name\": \"MachineLearningInference\",
      			\"type\": \"inference\"
      		}
      	]
      }" > test-generator-pipeline-models/src/main/resources/pipelines/ExampleMachineLearningPipeline.json

# Create build cache files to make builds more efficient, when possible - testing the parts of the archetype impacted by change:
# ExampleDataDelivery.json
cp ../../../../../.mvn/extensions.xml .mvn/extensions.xml
cp ../../../src/test/resources/test-project-archetype-script/maven-build-cache-config.xml .mvn/maven-build-cache-config.xml

# The tiltfile is only testable locally and not on the CI build
# Thus not updating it with file contents with manual actions
echo -e "\n# maven-suppress-warnings" >> Tiltfile

echo -e "\nINFO: Running full build to generate project structure\n"
mvn clean install || { echo -e '\n\n\t**** MAVEN BUILD FAILED ****\n\n' ; exit 1; }


cd test-generator-shared/
updatePomBasedOnChildDirs "<!-- TODO: replace with your project-specific modules here -->"

cd ../test-generator-docker/
updatePomBasedOnChildDirs "<!-- TODO: Add docker modules here -->"

cd ../test-generator-pipelines/
updatePomBasedOnChildDirs "<!-- TODO: replace with your pipeline-specific modules here -->"

cd ..

echo -e "\nINFO: Generating project structure for pipelines and data records\n"
mvn clean install || { echo -e '\n\n\t**** MAVEN BUILD FAILED ****\n\n' ; exit 1; }

cd test-generator-pipelines/example-machine-learning-pipeline/
updatePomBasedOnChildDirs "<!-- TODO: replace with your step-specific modules here -->"

cd ../..

echo -e "\nINFO: Generating project structure for ml pipelines\n"
mvn clean install || { echo -e '\n\n\t**** MAVEN BUILD FAILED ****\n\n' ; exit 1; }


echo -e "\nINFO: updating the test-generator-deploy/pom.xml based on static code as it's hard to predict without folder structure\n"
executions=`cat ../../../extensions-to-add-to-archetype_test-generator-deploy_pom.xml`

fileContents=`cat test-generator-deploy/pom.xml`
todoText="<!-- Add executions for each deployment module -->"
fileContents="${fileContents/$todoText/$executions}"
echo -e $fileContents > test-generator-deploy/pom.xml



echo -e "\nINFO: Running full build to check that the build passes w/o any manual actions needed\n"
mvn clean install || { echo -e '\n\n\t**** MAVEN BUILD FAILED ****\n\n' ; exit 1; } > maven-build.log
buildLog=`cat maven-build.log`
if [[ $buildLog == *"MANUAL ACTION NEEDED"* ]]; then
  echo -e "\n\n **** ERROR: Manual action still found in build **** \n    Look at **archetype/target/temp/test-generator/maven-build.log** to see what the problem was. \n\n"
  exit 1
else
  echo -e "\n\n **** SUCCESS: Completed the full build successfully ****\n\n"
fi

