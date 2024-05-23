from uuid import uuid4

from aissemble_data_lineage import *
from openlineage.client.run import (
    Run as OLRun,
    Job as OLJob,
    RunEvent as OLRunEvent,
    Dataset as OLDataset,
    InputDataset as OLInputDataset,
    OutputDataset as OLOutputDataset,
)
from openlineage.client.facet import BaseFacet as OLFacet, ParentRunFacet
from behave import given, when, then  # pylint: disable=no-name-in-module
import nose.tools as nt
import time


@given("a valid Facet object")
def step_impl(context):
    context.facet = Facet("test_url")


@when("the Facet is translated to an OpenLineage Facet")
def step_impl(context):
    context.ol_facet = context.facet.get_open_lineage_facet()


@then("the resulting OpenLineage Facet will retain all relevant properties")
def step_impl(context):
    nt.assert_is_instance(context.ol_facet, OLFacet)
    nt.assert_equals(context.ol_facet._schemaURL, context.facet.schema_url)
    nt.assert_equals(context.ol_facet._producer, context.facet.producer)


@given("a valid Run object")
def step_impl(context):
    facets = {"facet1": Facet("test_url"), "facet2": Facet("test_url_2")}
    context.run_obj = Run(uuid4(), facets)


@when("the Run is translated to an OpenLineage Run")
def step_impl(context):
    context.ol_run = context.run_obj.get_open_lineage_run()


@then("the resulting OpenLineage Run will retain all relevant properties")
def step_impl(context):
    nt.assert_is_instance(context.ol_run, OLRun)
    nt.assert_equals(context.ol_run.runId, str(context.run_obj.run_id))
    nt.assert_equals(len(context.ol_run.facets), len(context.run_obj.facets))
    for facet in context.ol_run.facets.values():
        nt.assert_is_instance(facet, OLFacet)


@given('a valid Job object named "{job_name}"')
def step_impl(context, job_name):
    facets = {"facet1": Facet("test_url"), "facet2": Facet("test_url_2")}
    context.job_obj = Job(job_name, facets)


@when("the Job is translated to an OpenLineage Job")
def step_impl(context):
    try:
        context.ol_job = context.job_obj.get_open_lineage_job()
    except ValueError as ve:
        context.exception = ve


@then("the resulting OpenLineage Job will retain all relevant properties")
def step_impl(context):
    nt.assert_is_instance(context.ol_job, OLJob)
    nt.assert_equals(context.ol_job.name, context.job_obj.name)
    nt.assert_equals(len(context.ol_job.facets), len(context.job_obj.facets))
    nt.assert_equals(
        context.ol_job.namespace,
        context.job_obj._config.job_namespace(context.job_obj.name),
    )
    nt.assert_equals(context.ol_job.namespace, "test_namespace")
    for facet in context.ol_job.facets.values():
        nt.assert_is_instance(facet, OLFacet)


@given('a valid Dataset object named "{dataset_name}"')
def step_impl(context, dataset_name):
    facets = {"facet1": Facet("test_url"), "facet2": Facet("test_url_2")}
    context.dataset_obj = Dataset(dataset_name, facets)


@when("the Dataset is translated to an OpenLineage Dataset")
def step_impl(context):
    try:
        context.ol_dataset = context.dataset_obj.get_open_lineage_dataset()
    except ValueError as ve:
        context.exception = ve


@then("the resulting OpenLineage Dataset will retain all relevant properties")
def step_impl(context):
    nt.assert_is_instance(context.ol_dataset, OLDataset)
    nt.assert_equals(context.ol_dataset.name, context.dataset_obj.name)
    nt.assert_equals(
        context.ol_dataset.namespace,
        context.dataset_obj._config.dataset_namespace(context.dataset_obj.name),
    )
    nt.assert_equals(context.ol_dataset.namespace, "test_data_source")
    nt.assert_equals(len(context.ol_dataset.facets), len(context.dataset_obj.facets))
    for facet in context.ol_dataset.facets.values():
        nt.assert_is_instance(facet, OLFacet)


@given('a valid InputDataset object named "{input_dataset_name}"')
def step_impl(context, input_dataset_name):
    facets = {"facet1": Facet("test_url"), "facet2": Facet("test_url_2")}
    input_facets = {"facet3": Facet("test_url_3"), "facet4": Facet("test_url_4")}
    context.input_dataset_obj = InputDataset(input_dataset_name, facets, input_facets)


@when("the InputDataset is translated to an OpenLineage InputDataset")
def step_impl(context):
    context.ol_input_dataset = context.input_dataset_obj.get_open_lineage_dataset()


@then("the resulting OpenLineage InputDataset will retain all relevant properties")
def step_impl(context):
    nt.assert_is_instance(context.ol_input_dataset, OLInputDataset)
    nt.assert_equals(context.ol_input_dataset.name, context.input_dataset_obj.name)
    nt.assert_equals(
        context.ol_input_dataset.namespace,
        context.input_dataset_obj._config.dataset_namespace(
            context.input_dataset_obj.name
        ),
    )
    nt.assert_equals(context.ol_input_dataset.namespace, "test_data_source")
    nt.assert_equals(
        len(context.ol_input_dataset.facets), len(context.input_dataset_obj.facets)
    )
    nt.assert_equals(
        len(context.ol_input_dataset.inputFacets),
        len(context.input_dataset_obj.input_facets),
    )
    for facet in context.ol_input_dataset.facets.values():
        nt.assert_is_instance(facet, OLFacet)
    for facet in context.ol_input_dataset.inputFacets.values():
        nt.assert_is_instance(facet, OLFacet)


@given('a valid OutputDataset object named "{output_dataset_name}"')
def step_impl(context, output_dataset_name):
    facets = {"facet1": Facet("test_url"), "facet2": Facet("test_url_2")}
    output_facets = {"facet3": Facet("test_url_3"), "facet4": Facet("test_url_4")}
    context.output_dataset_obj = OutputDataset(
        output_dataset_name, facets, output_facets
    )


@when("the OutputDataset is translated to an OpenLineage OutputDataset")
def step_impl(context):
    context.ol_output_dataset = context.output_dataset_obj.get_open_lineage_dataset()


@then("the resulting OpenLineage OutputDataset will retain all relevant properties")
def step_impl(context):
    nt.assert_is_instance(context.ol_output_dataset, OLOutputDataset)
    nt.assert_equals(context.ol_output_dataset.name, context.output_dataset_obj.name)
    nt.assert_equals(
        context.ol_output_dataset.namespace,
        context.output_dataset_obj._config.dataset_namespace(
            context.output_dataset_obj.name
        ),
    )
    nt.assert_equals(context.ol_output_dataset.namespace, "test_data_source")
    nt.assert_equals(
        len(context.ol_output_dataset.facets), len(context.output_dataset_obj.facets)
    )
    nt.assert_equals(
        len(context.ol_output_dataset.outputFacets),
        len(context.output_dataset_obj.output_facets),
    )
    for facet in context.ol_output_dataset.facets.values():
        nt.assert_is_instance(facet, OLFacet)
    for facet in context.ol_output_dataset.outputFacets.values():
        nt.assert_is_instance(facet, OLFacet)


@given("a valid RunEvent object")
def step_impl(context):
    run_event_run = Run(uuid4())
    run_event_job = Job("test_pipeline.test_step")
    run_event_type = "START"
    context.run_event = RunEvent(
        run_event_run,
        run_event_job,
        run_event_type,
    )


@given('the RunEvent has an InputDataset named "{input_dataset_name}"')
def step_impl(context, input_dataset_name):
    context.run_event.add_input(InputDataset(input_dataset_name, {}, {}))


@given('the RunEvent has an OutputDataset named "{output_dataset_name}"')
def step_impl(context, output_dataset_name):
    context.run_event.add_output(OutputDataset(output_dataset_name, {}, {}))


@when("the RunEvent is translated to an OpenLineage RunEvent")
def step_impl(context):
    context.ol_run_event = context.run_event.get_open_lineage_run_event()


@then("the resulting OpenLineage RunEvent will retain all relevant properties")
def step_impl(context):
    nt.assert_is_instance(context.ol_run_event, OLRunEvent)
    nt.assert_equal(
        context.ol_run_event.eventTime,
        context.run_event.event_time.isoformat(timespec="milliseconds") + "Z",
    )
    nt.assert_equal(
        context.ol_run_event.run, context.run_event.run.get_open_lineage_run()
    )
    nt.assert_equal(
        context.ol_run_event.job, context.run_event.job.get_open_lineage_job()
    )
    nt.assert_equal(context.ol_run_event.producer, context.run_event.producer)
    nt.assert_equals(
        len(context.run_event.get_inputs()), len(context.ol_run_event.inputs)
    )
    nt.assert_equals(
        len(context.run_event.get_outputs()), len(context.ol_run_event.outputs)
    )


@given(
    'I have configured the property "{property_name}" with the value "{property_value}"'
)
def step_impl(context, property_name: str, property_value: str):
    context.lineage_properties[property_name] = property_value


@given('a job named "{job_name}" for a RunEvent')
def step_impl(context, job_name: str):
    run = Run(uuid4())
    job = Job(job_name)
    job.set_facets({"facet": Facet("fake.schema.url")})
    run_event_type = "START"
    context.run_event = RunEvent(
        run,
        job,
        run_event_type,
    )


@then('the producer value of the event is set to "{producer_value}"')
def step_impl(context, producer_value: str):
    nt.assert_equal(
        producer_value,
        context.ol_run_event.producer,
    )


@then('the Job namespace value of the event is set to "{namespace_value}"')
def step_impl(context, namespace_value):
    nt.assert_equal(
        namespace_value,
        context.ol_run_event.job.namespace,
    )


@given('the property "{property_name}" is not set')
def step_impl(context, property_name):
    if property_name in context.lineage_properties.stringPropertyNames():
        del context.lineage_properties[property_name]


@given('a job named "{job_name}" has a default namespace "{default_namespace}"')
def step_impl(context, job_name, default_namespace):
    facets = {"facet1": Facet("test_url"), "facet2": Facet("test_url_2")}
    context.job_obj = Job(job_name, facets, default_namespace)


@then('the Job namespace value is set to "{namespace_value}"')
def step_impl(context, namespace_value):
    nt.assert_equal(
        context.ol_job.namespace,
        namespace_value,
    )


@then('the namespace value of the Dataset is set to "{namespace_value}"')
def step_impl(context, namespace_value):
    nt.assert_equal(
        context.ol_dataset.namespace,
        namespace_value,
    )


@then("an exception is raised")
def step_impl(context):
    nt.assert_is_not_none(context.exception)
    nt.assert_is_instance(context.exception, ValueError)
