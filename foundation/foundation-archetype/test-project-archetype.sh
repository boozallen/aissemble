#!/bin/bash

set -o pipefail
echo -e "\n\n **** TESTING ARCHETYPE GENERATION **** \n\n"

if [ $# -ne 1 ]; then
    echo "Incorrect number of arguments. Usage:"
    echo "   test-project-archetype.sh <archetype_version>"
    exit 1
fi

mkdir -p target/temp
cd target/temp

#---
## Capture non-zero exits and clear the build-cache. This prevents the cache from hiding failures.
#---
function exit {
  if [ "$1" -ne 0 ]; then
    rm -rf ~/.m2/build-cache/v1.1/com.bah.aiops
  fi
  POSIXLY_CORRECT=1; unset exit
  exit "$1"
}

#---
## Replacement for `sed -i` that formats the command correctly for MacOS and Linux
##
## @args: $1 and $2 are passed in unchanged to `sed`
#---
function sub {
  if [[ $OSTYPE == "darwin"* ]]; then
    sed -i '' "$1" "$2" || exit 1
  else
    sed -i "$1" "$2" || exit 1
  fi
}

#---
## Escapes the contents of a stdin for use with a `sed` substitution command. Namely ensures dollar signs, new lines
## and backslashes are escaped.
##
## @stdin: the content to escape
## @stdout: the input with '$', '\', and '\n' escaped
#---
function esc {
  # sed is used for a straightforward substitution of \ and $
  # awk is used to escape newlines. Essentially, replaces the newlines between lines of input with a backslash and newline.
  # Hence ORS (output record sep) is `\\\n`. However, because trailing newlines are always chomped in pipes, the last
  # newline in the input must not be escaped. Therfore, input is buffered by one line in the `last` var so we can turn set
  # the ORS to blank for the final line.
  sed 's/\([\/\$]\)/\\\1/g' | awk 'NR > 1 {ORS="\\\n"} END {ORS=""; print last} {print last} {last=$0}' ORS=''
}

function updatePomBasedOnChildDirs {
  # The 'TODO' text line that we're going to replace in the file
    # should be passed in as the first param to this method. i.e.
    #    updatePomBasedOnChildDirs '<!-- TODO: this is where manual actions go -->'
  todoText=$1
  modules=""
  # Instead of only looping on directories, we loop over all files and only add if it's a directory. Because pom.xml
  # always exists, this mitigates the errant behavior caused by the build cache completely skipping execution which was
  # resulting in a module named "*" being inserted into the pom file
  for d in * ; do
    if [ -d "$d" ] && [ "$d" != "target" ]; then
      # Drop / from the end of the directory
      d="${d/\//}"
      modules+="<module>$d</module>"$'\n        '
    fi
  done

  if [ -n "$modules" ]; then
    echo -e "\nINFO: Adding \n\t$modules\n\tto $PWD/pom.xml\n"
    # command substitution trims trailing newlines so add it back
    sub "s/$todoText/$(esc <<< "$modules")/" pom.xml
  else
    return 1
  fi
}

#---
## Runs maven build and captures any manual actions for adding Fermenter executions to the deploy POM and helmfile
# release additions. It applies these manual actions to the deploy POM and the helmfile.yaml.gotmpl.
#---
function runBuildAndApplyManualActions {
  deployInsert="<!-- Add executions for each deployment module -->"
  helmfileInsert="# Add deployment releases here"
  deployOutputStart='executions to test-generator-deploy'
  helmfileOutputStart='add the following to helmfile.yaml.gotmpl'
  helmfileAppsOutputStart='add the following to helmfile-apps.yaml.gotmpl'
  outputEnd='\[WARNING\]'

  # $deployOutputStart match at end ensures the match line isn't captured. NF ensures blank lines aren't captured.
  ./mvnw -T8 clean install -Dhabushu.usePyenv=false | \
      tee >(awk "BEGIN {output=0} /$outputEnd/ {output=0} NF && output {print} /$deployOutputStart/ {output=1}">deploy.out ) | \
      tee >(awk "BEGIN {output=0} /$outputEnd/ {output=0} NF && output {print} /$helmfileOutputStart/ {output=1}">helmfile.out ) | \
      tee >(awk "BEGIN {output=0} /$outputEnd/ {output=0} NF && output {print} /$helmfileAppsOutputStart/ {output=1}">helmfile-apps.out ) \
      || { echo -e '\n\n\t**** MAVEN BUILD FAILED ****\n\n' ; exit 1; }

    execs=$(cat deploy.out)
    releases=$(cat helmfile.out)
    apps=$(cat helmfile-apps.out)
  rm -f deploy.out
  rm -f helmfile.out
  rm -f helmfile-apps.out
  updates=0
  if [ -n "$execs" ]; then
    #re-adding insert comment allows for subsequent insertions
    execs=$deployInsert$'\n'$execs
    execs=$(esc <<< "$execs")
    echo -e "\n INFO: Adding execution profiles to deploy POM: \n$execs"
    sub "s/$deployInsert/$execs/" test-generator-deploy/pom.xml
  else
    updates=1
  fi
  if [ -n "$releases" ]; then
    releases=$helmfileInsert$'\n'$releases
    releases=$(esc <<< "$releases")
    echo -e "\n INFO: Adding releases to helmfile: \n$releases"
    sub "s/$helmfileInsert/$releases/" helmfile.yaml.gotmpl
  fi
  if [ -n "$apps" ]; then
    apps=$helmfileInsert$'\n'$apps
    apps=$(esc <<< "$apps")
    echo -e "\n INFO: Adding releases to helmfile-apps: \n$apps"
    sub "s/$helmfileInsert/$apps/" helmfile-apps.yaml.gotmpl
  fi
  return $updates
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
  -DprojectName=Generator_Test \
  -DmavenRepositoryUrl=https://repo1.maven.org/maven2 \
  -DmavenSnapshotRepositoryUrl=https://s01.oss.sonatype.org/content/repositories/snapshots

cd test-generator/ || exit

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
        },
        \"steps\": [
          {
            \"name\": \"Ingest\",
            \"type\": \"synchronous\",
            \"inbound\": {
              \"type\": \"messaging\",
              \"channelName\": \"example-channel\",
              \"recordType\": {
                \"name\": \"ExampleRecord\",
                \"package\": \"${package}\"
              }
            },
            \"persist\": {
              \"type\": \"hive\"
            }
          },
          {
            \"name\": \"Transform\",
            \"type\": \"synchronous\",
            \"persist\": {
              \"type\": \"delta-lake\"
            }
          }
        ]
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
            \"inbound\": {
              \"type\": \"messaging\",
              \"channelName\": \"example-channel\"
            },
            \"persist\": {
              \"type\": \"hive\"
            }
          },
          {
            \"name\": \"Transform\",
            \"type\": \"synchronous\",
            \"persist\": {
              \"type\": \"delta-lake\"
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

echo -e "\nINFO: Running full build to generate project structure\n"
updates=false
if runBuildAndApplyManualActions; then
  updates=true
fi

echo -e "\nINFO: updating the test-generator-deploy/pom.xml based on static code as it's hard to predict\n"
plugins=$(esc < ../../../plugins-to-add-to-archetype_test-generator-deploy_pom.xml)
sub "s/ *<\/plugins>/$plugins        <\/plugins>/" test-generator-deploy/pom.xml

cd test-generator-shared/ || exit
if updatePomBasedOnChildDirs "<!-- TODO: replace with your project-specific modules here -->"; then
  updates=true
fi

cd ../test-generator-docker/ || exit
if updatePomBasedOnChildDirs "<!-- TODO: Add docker modules here -->"; then
  updates=true
fi

cd ../test-generator-pipelines/ || exit
if updatePomBasedOnChildDirs "<!-- TODO: replace with your pipeline-specific modules here -->"; then
  updates=true
fi

if [ $updates = false ]; then
  echo -e "\nINFO: Build cached. No POM updates made. Exiting early."
  exit 0
fi

cd ..

echo -e "\nINFO: Generating project structure for pipelines and data records\n"
runBuildAndApplyManualActions

cd test-generator-pipelines/example-machine-learning-pipeline/ || exit
updatePomBasedOnChildDirs "<!-- TODO: replace with your step-specific modules here -->"

cd ../..

echo -e "\nINFO: Generating project structure for ml pipelines\n"
runBuildAndApplyManualActions


echo -e "\nINFO: Running final build to ensure success"
./mvnw -T8 clean install -Dhabushu.usePyenv=false || { echo -e '\n\n\t**** MAVEN BUILD FAILED ****\n\n' ; exit 1; }

echo -e "\nINFO: Running fermenter generation to check for left over manual actions\n"
# NOTE: because fermenter results are cached, the build-cache will hide remaining manual actions that were missed in previous steps

./mvnw -T8 clean generate-sources -Dhabushu.usePyenv=false -Dmaven.build.cache.skipCache -Dfermenter.display.message.keys=true | tee >(awk '/WARNING/ {print}' > maven-build.log) \
    || { echo -e '\n\n\t**** MAVEN BUILD FAILED ****\n\n' ; exit 1; }
if grep -iq 'Manual action' maven-build.log; then
  echo -e "\n\n **** ERROR: Manual action still found in build **** \n    Look at **archetype/target/temp/test-generator/maven-build.log** to see what the problem was. \n\n"
  exit 1
else
  echo -e "\n\n **** SUCCESS: Completed the full build successfully ****\n\n"
fi
