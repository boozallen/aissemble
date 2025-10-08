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
from behave import given, when, then  # pylint: disable=no-name-in-module
import nose.tools as nt
from aissemble_test_data_delivery_pyspark_model.record.record_with_type_coercion_validation import (
    RecordWithTypeCoercionValidation,
)
from aissemble_test_data_delivery_pyspark_model.dictionary.float_with_validation import (
    FloatWithValidation,
)
from aissemble_test_data_delivery_pyspark_model.dictionary.integer_with_validation import (
    IntegerWithValidation,
)

use_step_matcher("re")


@given(
    'a dictionary type with range validation "(?P<minValue>.+)" and "(?P<maxValue>.+)"'
)
def step_impl(context, minValue, maxValue):
    """
    :type context: behave.runner.Context
    :type minValue: str
    :type maxValue: str
    """
    record = RecordWithTypeCoercionValidation()
    if "." in minValue or "." in maxValue:
        context.type = "float"
    else:
        context.type = "integer"

    context.record = record


@when('validation occurs on a "(?P<value>.+)" typed as a string')
def step_impl(context, value):
    """
    :type context: behave.runner.Context
    :type value: str
    """
    if value == "None":
        context.record.integer_validation = None
    elif context.type == "float":
        validation = FloatWithValidation(str(value))
        context.record.float_validation = validation
    else:
        validation = IntegerWithValidation(str(value))
        context.record.integer_validation = validation

    try:
        context.record.validate()
        context.successful = "true"
    except:
        context.successful = "false"


@then('validation is "(?P<successful>.+)"')
def step_impl(context, successful):
    """
    :type context: behave.runner.Context
    :type successful: str
    """
    nt.eq_(successful, context.successful, "Validation did not work as expected")
