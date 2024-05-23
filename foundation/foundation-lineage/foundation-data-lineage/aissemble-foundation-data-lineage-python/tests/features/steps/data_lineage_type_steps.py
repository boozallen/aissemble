import datetime
from uuid import uuid4

from aissemble_data_lineage import *
from behave import given, when, then  # pylint: disable=no-name-in-module
import nose.tools as nt


@when("a RunEvent is instantiated with minimal valid arguments")
def step_impl(context):
    context.minimal_run_event_run = Run(uuid4())
    context.minimal_run_event_job = Job("test_job")
    context.minimal_run_event_type = "test_event"
    context.minimal_run_event = RunEvent(
        context.minimal_run_event_run,
        context.minimal_run_event_job,
        context.minimal_run_event_type,
    )


@then("all public RunEvent fields will contain appropriate values")
def step_impl(context):
    nt.assert_equals(context.minimal_run_event.run, context.minimal_run_event_run)
    nt.assert_equals(context.minimal_run_event.job, context.minimal_run_event_job)
    nt.assert_equals(
        context.minimal_run_event.event_type, context.minimal_run_event_type
    )
    nt.assert_is_instance(context.minimal_run_event.producer, str)
    nt.assert_is_instance(context.minimal_run_event.schema_URL, str)
    nt.assert_is_instance(context.minimal_run_event.event_time, datetime.datetime)


@when("a Job is instantiated with minimal valid arguments")
def step_impl(context):
    context.minimal_job_name = "minimal_job"
    context.minimal_job = Job(context.minimal_job_name)


@then("all public Job fields will contain appropriate values")
def step_impl(context):
    nt.assert_equals(context.minimal_job.facets, {})
    nt.assert_equals(context.minimal_job.name, context.minimal_job_name)


@when("a Job is instantiated with a non-string name")
def step_impl(context):
    context.exception = None
    context.illegal_job = None
    context.illegal_job_name = 55
    try:
        context.illegal_job = Job(name=context.illegal_job_name)
    except ValueError as ve:
        context.exception = ve


@then("a ValueError will be raised during the construction of the Job")
def step_impl(context):
    nt.assert_is_none(context.illegal_job)
    nt.assert_is_not_none(context.exception)
    nt.assert_is_instance(context.exception, ValueError)


@when("a Facet is instantiated with minimal valid arguments")
def step_impl(context):
    context.minimal_facet_schema_url = "http://test-url.org/test-schema.json"
    context.minimal_facet = Facet(context.minimal_facet_schema_url)


@then("all public Facet fields will contain appropriate values")
def step_impl(context):
    nt.assert_equals(context.minimal_facet.schema_url, context.minimal_facet_schema_url)


@when("a Facet is instantiated with an illegal schema_url")
def step_impl(context):
    context.exception = None
    context.illegal_facet = None
    illegal_facet_url = 55
    try:
        context.illegal_facet = Facet(illegal_facet_url)
    except ValueError as ve:
        context.exception = ve


@then("a ValueError will be raised during the construction of the Facet")
def step_impl(context):
    nt.assert_is_none(context.illegal_facet)
    nt.assert_is_not_none(context.exception)
    nt.assert_is_instance(context.exception, ValueError)


@when("a Dataset is instantiated with minimal valid arguments")
def step_impl(context):
    context.minimal_dataset_name = "minimal_dataset"
    context.minimal_dataset = Dataset(context.minimal_dataset_name)


@then("all public Dataset fields will contain appropriate values")
def step_impl(context):
    nt.assert_equals(context.minimal_dataset.facets, {})
    nt.assert_equals(context.minimal_dataset.name, context.minimal_dataset_name)


@when("a Dataset is instantiated with a non-string name")
def step_impl(context):
    context.exception = None
    context.illegal_dataset = None
    context.illegal_dataset_name = 55
    try:
        context.illegal_job = Dataset(name=context.illegal_dataset_name)
    except ValueError as ve:
        context.exception = ve


@then("a ValueError will be raised during the construction of the Dataset")
def step_impl(context):
    nt.assert_is_none(context.illegal_dataset)
    nt.assert_is_not_none(context.exception)
    nt.assert_is_instance(context.exception, ValueError)


@when("an InputDataset is instantiated with an illegal input_facet dict")
def step_impl(context):
    context.exception = None
    context.illegal_dataset = None
    illegal_dict = {"some_str": 5}
    try:
        context.illegal_dataset = InputDataset(
            name="test_name", input_facets=illegal_dict
        )
    except ValueError as ve:
        context.exception = ve


@when("an OutputDataset is instantiated with an illegal output_facet dict")
def step_impl(context):
    context.exception = None
    context.illegal_dataset = None
    illegal_dict = {"some_str": 5}
    try:
        context.illegal_job = OutputDataset(
            name="test_name", output_facets=illegal_dict
        )
    except ValueError as ve:
        context.exception = ve


@when("a Run is instantiated with minimal valid arguments")
def step_impl(context):
    context.run_id = uuid4()
    context.run = Run(context.run_id)


@then("all public Run fields will contain appropriate values")
def step_impl(context):
    nt.assert_equals(context.run.run_id, context.run_id)


@when("a Run is instantiated with a non-UUID run_id")
def step_impl(context):
    context.illegal_run_id = "illegal"
    context.exception = None
    context.run = None
    try:
        context.run = Run(context.illegal_run_id)
    except ValueError as ve:
        context.exception = ve


@then("a ValueError will be raised during the construction of the Run")
def step_impl(context):
    nt.assert_is_none(context.run)
    nt.assert_is_not_none(context.exception)
    nt.assert_is_instance(context.exception, ValueError)


@when("a lineage object is instantiated with a facet dict containing an illegal value")
def step_impl(context):
    context.exception = None
    context.run = None
    illegal_dict = {"some_str": 5}
    try:
        context.run = Run("", illegal_dict)
    except ValueError as ve:
        context.exception = ve


@when("a lineage object is instantiated with a facet dict containing an illegal key")
def step_impl(context):
    context.exception = None
    context.run = None
    illegal_dict = {True: Facet("url")}
    try:
        context.run = Run("", illegal_dict)
    except ValueError as ve:
        context.exception = ve


@then("a ValueError will be raised during the construction of the Lineage Object")
def step_impl(context):
    nt.assert_is_none(context.run)
    nt.assert_is_not_none(context.exception)
    nt.assert_is_instance(context.exception, ValueError)
