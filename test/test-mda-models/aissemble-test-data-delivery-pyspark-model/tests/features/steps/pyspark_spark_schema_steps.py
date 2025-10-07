###
# #%L
# aiSSEMBLE::Test::MDA::Data Delivery Pyspark
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
from typing import List

import nose.tools as nt
from behave import given, when, then

from aissemble_test_data_delivery_pyspark_model.dictionary.integer_with_validation import (
    IntegerWithValidation,
)
from aissemble_test_data_delivery_pyspark_model.dictionary.string_with_validation import (
    StringWithValidation,
)
from aissemble_test_data_delivery_pyspark_model.record.city import (
    City,
)
from aissemble_test_data_delivery_pyspark_model.record.mayor import (
    Mayor,
)
from aissemble_test_data_delivery_pyspark_model.record.state import (
    State,
)
from aissemble_test_data_delivery_pyspark_model.record.street import (
    Street,
)
from aissemble_test_data_delivery_pyspark_model.record.record_with_required_validation import (
    RecordWithRequiredValidation,
)

from aissemble_test_data_delivery_pyspark_model.record.record_with_non_required_validation import (
    RecordWithNonRequiredValidation,
)
from aissemble_test_data_delivery_pyspark_model.schema.city_schema import (
    CitySchema,
)
from aissemble_test_data_delivery_pyspark_model.record.citizen import (
    Citizen,
)
from aissemble_test_data_delivery_pyspark_model.schema.record_with_required_validation_schema import (
    RecordWithRequiredValidationSchema,
)
from aissemble_test_data_delivery_pyspark_model.schema.record_with_non_required_validation_schema import (
    RecordWithNonRequiredValidationSchema,
)

NULL_OR_EMPTY_ARRAY = ["null", "[]"]


@given('the record "City" exists with the following relations')
def step_impl(context):
    # Records and relations are handled in MDA generation
    nt.assert_true(CitySchema() is not None, "City Schema was not generated correctly")


@given('the spark schema is generate for the "City" record')
def step_impl(context):
    context.schema = CitySchema()


@given("a city record is created")
def step_impl(context):
    context.city = _create_city()


@given('a record with a "{requirement}" field with validation rules')
def step_impl(context, requirement):
    context.record_with_validated_field_requirement = requirement

    context.record_with_requirement_validation = (
        RecordWithRequiredValidation()
        if requirement == "required"
        else RecordWithNonRequiredValidation()
    )


@given('the field is set to a "{validity}" value')
def step_impl(context, validity):
    # set valid fields to verify validation still works with multiple fields
    context.record_with_requirement_validation.string_validation = StringWithValidation(
        "Test123"
    )
    context.record_with_requirement_validation.string_simple = "Test123"

    if validity == "valid":
        context.record_with_requirement_validation.integer_validation = (
            IntegerWithValidation(150)
        )
    elif validity == "invalid":
        context.record_with_requirement_validation.integer_validation = (
            IntegerWithValidation(50)
        )
    else:
        pass  # Do nothing to keep the field None


@given("a dataSet containing the record")
def step_impl(context):
    if context.record_with_validated_field_requirement == "required":
        row = RecordWithRequiredValidation.as_row(
            context.record_with_requirement_validation
        )
    else:
        row = RecordWithNonRequiredValidation.as_row(
            context.record_with_requirement_validation
        )

    context.record_with_requirement_validation_rows = [row]


@given("the dataset contains one valid record")
def step_impl(context):
    if context.record_with_validated_field_requirement == "required":
        # Create other valid row for the data frame to test filtering
        valid_record_with_requirement_validation = RecordWithRequiredValidation()
        valid_record_with_requirement_validation.integer_validation = (
            IntegerWithValidation(150)
        )
        valid_record_with_requirement_validation.string_validation = (
            StringWithValidation("Test123")
        )
        valid_record_with_requirement_validation.string_simple = "Test123"
        valid_row = RecordWithRequiredValidation.as_row(
            valid_record_with_requirement_validation
        )
    else:
        valid_record_with_non_requirement_validation = RecordWithNonRequiredValidation()
        valid_row = RecordWithNonRequiredValidation.as_row(
            valid_record_with_non_requirement_validation
        )

    context.record_with_requirement_validation_rows.append(valid_row)


@when("the generated spark schema validation is performed on the dataSet")
def step_impl(context):
    if context.record_with_validated_field_requirement == "required":
        record_with_requirement_validation_schema = RecordWithRequiredValidationSchema()
    else:
        record_with_requirement_validation_schema = (
            RecordWithNonRequiredValidationSchema()
        )

    record_with_validated_field_dataset = context.test_spark_session.createDataFrame(
        context.record_with_requirement_validation_rows,
        schema=record_with_requirement_validation_schema.struct_type,
    )

    context.validated_dataframe = (
        record_with_requirement_validation_schema.validate_dataset(
            record_with_validated_field_dataset
        )
    )


@given("the following City dataset:")
def step_impl(context):
    cities = []
    context.schema = CitySchema()
    for row in context.table:
        cities.append(
            City.as_row(
                _create_city(row["Mayor"], row["State"], row["Streets"], row["Citizen"])
            )
        )
    context.city_dataset = _create_city_dataframe(context, cities)


@when("the dataset is validated against the schema")
def step_impl(context):
    context.validated_dataset = context.schema.validate_dataset(context.city_dataset)


@then("the result dataset should match:")
def step_impl(context):
    rows = []
    for row in context.table:
        rows.append(
            City.as_row(
                _create_city(row["Mayor"], row["State"], row["Streets"], row["Citizen"])
            )
        )

    expected_dataset = context.test_spark_session.createDataFrame(
        rows, context.schema.struct_type
    )

    nt.assert_true(
        expected_dataset.count() == context.validated_dataset.count(),
        "The validated dataset has expected size.",
    )
    nt.assert_true(
        expected_dataset.exceptAll(context.validated_dataset).isEmpty(),
        "The validated dataset has the expected results.",
    )


@when('a "City" object is mapped to a spark dataset using the record')
def step_impl(context):
    context.city_dataset = _create_city_dataframe(context, [City.as_row(context.city)])


@then('the schema data type for "{record}" is "{type}"')
def step_impl(context, record, type):
    nt.assert_equal(str(context.schema.get_data_type(record.upper())), type)


@then("the dataset has the correct values for the relational objects")
def step_impl(context):
    expected_city = context.city
    for row in context.city_dataset.collect():
        actual_city = City.from_row(row)
        nt.assert_equal(
            actual_city.as_dict(),
            expected_city.as_dict(),
            "The city was not mapped correctly",
        )


@then("the resulting dataSet contains {numRows} row(s)")
def step_impl(context, numRows):
    nt.assert_equal(
        int(numRows),
        context.validated_dataframe.count(),
        "The validated dataSet contained the incorrect number of rows",
    )


def _create_city_dataframe(context, cities):
    return context.test_spark_session.createDataFrame(
        cities, context.schema.struct_type
    )


def _create_mayor(validity: str) -> Mayor:
    mayor = Mayor()
    if validity in NULL_OR_EMPTY_ARRAY:
        mayor = None
    elif validity.startswith("valid"):
        mayor.name = "Valid Mayor"
        mayor.integer_validation = IntegerWithValidation(100)
    else:
        mayor.name = "invalid Mayor"
        mayor.integer_validation = IntegerWithValidation(1000)

    return mayor


def _create_citizen(validity: str) -> Citizen:
    citizen = Citizen()
    if validity == "null":
        citizen = None
    elif validity.startswith("valid"):
        citizen.name = "Valid Citizen"
        citizen.integer_validation = IntegerWithValidation(100)
    else:
        citizen.name = "invalid Citizen"
        citizen.integer_validation = IntegerWithValidation(1000)
    return citizen


def _create_citizens(validity: List[str]) -> List[Citizen]:
    if len(validity) == 1 and validity[0] in NULL_OR_EMPTY_ARRAY:
        return _get_null_or_empty_list(validity[0])

    citizens = []
    for item_type in validity:
        citizens.append(_create_citizen(item_type))

    return citizens


def _create_street(validity: str):
    street = Street()
    if validity.startswith("valid"):
        street.name = "Valid Street"
        street.county = "Valid County"
        street.integer_validation = IntegerWithValidation(100)
    else:
        street.name = "Invalid Street"
        street.county = "Invalid County"
        street.integer_validation = IntegerWithValidation(1000)
    return street


def _create_streets(validity: List[str]) -> List[Street]:
    if len(validity) == 1 and validity[0] in NULL_OR_EMPTY_ARRAY:
        return _get_null_or_empty_list(validity[0])

    streets = []
    for item_type in validity:
        streets.append(_create_street(item_type))

    return streets


def _create_state(validity: str):
    state = State()
    if validity in NULL_OR_EMPTY_ARRAY:
        state = None
    elif validity.startswith("valid"):
        state.name = "Valid State"
        state.integer_validation = IntegerWithValidation(100)
    else:
        state.name = "Invalid State"
        state.integer_validation = IntegerWithValidation(100)
    return state


# todo: should I aggregate the validity inside the context?
def _create_city(
    mayor_validity="valid",
    state_validity="valid",
    street_validity="valid",
    citizen_validity="valid",
):
    city = City()
    city.mayor = _create_mayor(mayor_validity)
    city.state = _create_state(state_validity)
    city.street = _create_streets(street_validity.split(","))
    city.citizen = _create_citizens(citizen_validity.split(","))
    return city


def _get_null_or_empty_list(validity):
    if validity == "null":
        return None
    return []
