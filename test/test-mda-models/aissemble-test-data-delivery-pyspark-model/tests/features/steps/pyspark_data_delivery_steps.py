###
# #%L
# AIOps Foundation::AIOps MDA Patterns::Pyspark
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
"""
Implementation steps for pyspark_data_delivery.feature.

GENERATED STUB CODE - PLEASE ***DO*** MODIFY

Originally generated from: templates/behave.steps.py.vm
"""

from behave import given, when, then  # pylint: disable=no-name-in-module
from os import path, walk
import nose.tools as nt
import ast
import sys
from pathlib import Path


@given("python files are generated")
def step_impl(context):
    return


@then("the generated files are syntactically correct")
def step_impl(context):
    test_staging_directory = Path(__file__).resolve().parents[2]
    for root, dirs, files in walk(test_staging_directory):
        for file in files:
            if file.endswith(".py"):
                nt.ok_(
                    is_valid_python(path.join(root, file)),
                    "File {0} does not have valid python syntax".format(file),
                )


def is_valid_python(fname):
    with open(fname) as f:
        contents = f.read()
    try:
        ast.parse(contents)
        return True
    except SyntaxError:
        return False
