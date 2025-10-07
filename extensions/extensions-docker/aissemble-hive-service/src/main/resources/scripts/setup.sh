#!/bin/bash
###
# #%L
# aiSSEMBLE::Extensions::Docker::Spark
# %%
# Copyright (C) 2021 Booz Allen
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###

#---
## Same as `cut` but reads fields from the end. E.g.:
##     cutr -d : -f 2 <<< "one:two:three:four"
##     > 'three'
## Note that open-ended ranges might be opposite of intuition if you think of them as "before" and "after"
##     cutr -d : -f -2 <<< "one:two:three:four"
##     > 'three:four'
##     cutr -d : -f 2- <<< "one:two:three:four"
##     > 'one:two:three'
#---
cutr() {
  rev | cut "$@" | rev
}

#---
## Patches a lib directory with given updated JARs by matching on base name. For example, given the replace directory has
## the jar `commons-math-3.4.jar` and the target directory has `commons-math-2.1.jar`, the existing 2.1 JAR is deleted
## and the updated 3.4 jar is copied into the directory.
##
## @Param: location of replacement JARs
## @Param: location to search for JARs to replace
#---
update_jars() {
  updated="$1"
  target="$2"
  echo
  echo "Updating jars in $target with jars from $updated"
  echo

  while read -r jarpath; do
    #Parse GAV into separate variables, classifier may or may not be present as the last item
    group=$(echo "$jarpath" | cutr -d / -f 4-)
    group=$(echo "${group#$updated/}" | tr / .)
    artifact=$(echo "$jarpath" | cutr -d / -f 3)
    version=$(echo "$jarpath" | cutr -d / -f 2)
    jar=$(echo "$jarpath" | cutr -d / -f 1)
    classifier=${jar#$artifact-$version}
    classifier=${classifier%\.jar}
    echo "-------------------------------------------"
    echo "Replacing $group:$artifact with updated JAR ($artifact-$version$classifier.jar)"

    # Find the old jar that is being replaced
    REPLACED=1
    echo find "$target" -type f -regex ".*/$artifact-[^-]*$classifier.jar"
     while read -r replaceable; do
      echo "  replacing '$replaceable'"
      rm "$replaceable" || exit $?
      cp "$jarpath" "$(dirname "$replaceable")" || exit $?
      REPLACED=0
    done < <(find "$target" -type f -regex ".*/$artifact-[^-]*$classifier.jar")
    # Guava version sometimes includes `-jre` at the end
    if [ "$artifact" = "guava" ]; then
      #We could do something fancier/generic like count dashes in version and use sed regex like:
      #find "$target" -type f -regextype sed -regex ".*/$artifact\(-[^-]*\)\{1,$(VER_DASH_COUNT+1)\}.jar"
      # But only Guava has this issue so it's not worth it.
      while read -r replaceable; do
        echo "  replacing '$replaceable'"
        rm "$replaceable" || exit $?
        cp "$jarpath" "$(dirname "$replaceable")" || exit $?
        REPLACED=0
      done < <(find "$target" -type f -regex ".*/$artifact-[^-]*-jre.jar")
    fi
    echo
    if [ $REPLACED -ne 0 ]; then
      echo "No replacement candidates found for $jar"
      exit 1
    fi
  done < <(find "$updated" -type f -name '*.jar')
}

#---
## Downloads jackson-mapper JAR from RedHat
##
## @param: destination directory for downloaded JAR
#---
download_jackson() {
  ### The codehaus version of Jackson is defunct, but RedHat has published a patched version
  JACKSON_VER='1.9.14.jdk17-redhat-00001'
  echo "Downloading Jackson $JACKSON_VER"
  jackson="https://maven.repository.redhat.com/ga/org/codehaus/jackson/jackson-mapper-asl/$JACKSON_VER/jackson-mapper-asl-$JACKSON_VER.jar"
  wget -q "$jackson" -P "$1/org/codehaus/jackson/jackson-mapper-asl/$JACKSON_VER" || exit $?
}

update_jars "$1" "/opt"
