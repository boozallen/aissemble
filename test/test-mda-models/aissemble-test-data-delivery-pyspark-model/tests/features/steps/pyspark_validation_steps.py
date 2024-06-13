###
# #%L
# AIOps Foundation::AIOps MDA Patterns::Pyspark
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
    if context.type == "float":
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
