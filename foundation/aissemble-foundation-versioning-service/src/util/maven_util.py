###
# #%L
# AIOps Docker Baseline::Versioning::Service
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
import subprocess


# Runs maven clean & deploy to package and push artifacts to Nexus.
# pom = the pom file to use for the command
# args = any additional arguments to pass to the command
def package_and_deploy(pom, args):
    print("Packaging & deploying artifacts...")

    goals = ["clean", "deploy"]
    result = run_maven_command(pom, goals, args, False)

    if result != 0:
        raise Exception("Failed to package & deploy data preparation artifacts")
    else:
        print("Finished packaging & deploying artifacts")


# Runs maven help:evaluate to evaluate and return a given expression.
# pom = the pom file to use for the command
# expression = the expression to evaluate
# args = any additional arguments to pass to the command
def evaluate_expression(pom, expression, args):
    goals = ["help:evaluate", "-Dexpression=" + expression, "-q", "-DforceStdout"]
    return run_maven_command(pom, goals, args, True)


# Runs a maven command.
# pom = the pom file to use for the command
# goals = the maven goals to run
# args = any additional arguments to pass to the command
# stdout = True to return the standard output from the command,
#          False to return the return code from the command
def run_maven_command(pom, goals, args, stdout):
    mvn_command = ["mvn", "-f", pom]
    mvn_command.extend(goals)
    mvn_command.extend(args)

    if stdout:
        result = subprocess.run(mvn_command, stdout=subprocess.PIPE)
        return result.stdout.decode("utf-8")
    else:
        print()
        result = subprocess.run(mvn_command)
        print()
        return result.returncode
