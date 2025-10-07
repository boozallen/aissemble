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
import os
import re
import nose.tools as nt
from behave import given, when, then  # pylint: disable=no-name-in-module
from inspect import signature
from aissemble_test_data_delivery_pyspark_model.record.person_with_m_to_one_relation import (
    PersonWithMToOneRelation,
)
from aissemble_test_data_delivery_pyspark_model.record.person_with_one_to_one_relation import (
    PersonWithOneToOneRelation,
)
from aissemble_test_data_delivery_pyspark_model.record.person_with_one_to_m_relation import (
    PersonWithOneToMRelation,
)
from aissemble_test_data_delivery_pyspark_model.record.address import (
    Address,
)
from aissemble_test_data_delivery_pyspark_model.dictionary.zipcode import (
    Zipcode,
)
from aissemble_test_data_delivery_pyspark_model.dictionary.state_address import (
    StateAddress,
)


def initialize(context):
    context.relation_class_name_map = {
        "1-1": PersonWithOneToOneRelation,
        "1-M": PersonWithOneToMRelation,
        "M-1": PersonWithMToOneRelation,
    }

    context.relation_json_map = {
        "1-1": '{"test": null, "Address": {"street": "123 Test St", "city": "Testville", "zipcode": "12345", "state": "Test"}}',
        "1-M": '{"Address": [{"street": "123 Test St", "city": "Testville", "zipcode": "12345", "state": "Test"}, {"street": "123 Test St", "city": "Testville", "zipcode": "12345", "state": "Test"}]}',
        "M-1": '{"customField": "Test Field", "Address": {"street": "123 Test St", "city": "Testville", "zipcode": "12345", "state": "Test"}}',
    }

    address = Address()
    address.street = "123 Test St"
    address.city = "Testville"
    address.state = StateAddress("Test")
    address.zipcode = Zipcode("12345")
    context.address = address


@given('a record "Person" that has a "{multiplicity}" relation to a record "Address"')
def step_impl(context, multiplicity):
    initialize(context)
    context.multiplicity = multiplicity

    match multiplicity:
        case "1-1":
            context.person_with_one_to_one_relation = PersonWithOneToOneRelation()
            context.person_with_one_to_one_relation.address = context.address
        case "1-M":
            context.person_with_one_to_m_relation = PersonWithOneToMRelation()
            context.person_with_one_to_m_relation.address = [
                context.address,
                context.address,
            ]
        case "M-1":
            context.person_with_m_to_one_relation = PersonWithMToOneRelation()
            context.person_with_m_to_one_relation.address = context.address
            context.person_with_m_to_one_relation.custom_field = "Test Field"


@given('a JSON string that has a "{multiplicity}" record relation encoded')
def step_impl(context, multiplicity):
    initialize(context)
    context.multiplicity = multiplicity
    context.json_string = context.relation_json_map.get(context.multiplicity)


@when('the "Person" class is generated')
def step_impl(context):
    context.person = context.relation_class_name_map.get(context.multiplicity)


@when("the record is serialized")
def step_impl(context):
    match context.multiplicity:
        case "1-1":
            context.json_string = context.person_with_one_to_one_relation.as_json()
        case "1-M":
            context.json_string = context.person_with_one_to_m_relation.as_json()
        case "M-1":
            context.json_string = context.person_with_m_to_one_relation.as_json()


@when("the JSON string is deserialized")
def step_impl(context):
    match context.multiplicity:
        case "1-1":
            context.person_with_one_to_one_relation = (
                PersonWithOneToOneRelation.from_json(context.json_string)
            )
        case "1-M":
            context.person_with_one_to_m_relation = PersonWithOneToMRelation.from_json(
                context.json_string
            )
        case "M-1":
            context.person_with_m_to_one_relation = PersonWithMToOneRelation.from_json(
                context.json_string
            )


@then('"Person" has a method address which returns "{type}"')
def step_impl(context, type):
    address = getattr(context.person, "address")
    return_type = str(signature(address.fget).return_annotation)
    # clean up the return annotation to only show the return type
    return_type = re.sub(
        "(typing\.)|(aissemble_test_data_delivery_pyspark_model\.record\.address\.)|(class)|(<)|(>)|(')|(\s)",
        "",
        str(return_type),
    )
    nt.assert_true(return_type == type)


@then("the record relations are maintained as JSON string")
def step_impl(context):
    expected_json_string = context.relation_json_map.get(context.multiplicity)

    nt.assert_equal(
        expected_json_string,
        context.json_string,
        msg="Serialized JSON string did not match the expected JSON string",
    )


@then("the record relations are maintained as a Record object")
def step_impl(context):
    match context.multiplicity:
        case "1-1":
            assert_address(
                context.address, context.person_with_one_to_one_relation.address
            )
        case "M-1":
            assert_address(
                context.address, context.person_with_m_to_one_relation.address
            )
            nt.assert_equal(
                "Test Field",
                context.person_with_m_to_one_relation.custom_field,
                msg="Deserialized JSON string did not have the expected Custom Field",
            )
        case "1-M":
            for deserialized_address in context.person_with_one_to_m_relation.address:
                assert_address(context.address, deserialized_address)


def assert_address(expected_address, actual_address):
    nt.assert_equal(
        expected_address.street,
        actual_address.street,
        msg="Deserialized JSON string did not have the expected Street",
    )
    nt.assert_equal(
        expected_address.city,
        actual_address.city,
        msg="Deserialized JSON string did not have the expected City",
    )
    nt.assert_equal(
        expected_address.state.value,
        actual_address.state.value,
        msg="Deserialized JSON string did not have the expected State",
    )
    nt.assert_equal(
        expected_address.zipcode.value,
        actual_address.zipcode.value,
        msg="Deserialized JSON string did not have the expected Zipcode",
    )
