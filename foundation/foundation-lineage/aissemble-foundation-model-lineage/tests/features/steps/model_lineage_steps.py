from aissemble_data_lineage import Job, Run, InputDataset, from_open_lineage_facet
from aissemble_model_lineage.facets import (
    SourceCodeDirectoryJobFacet,
    MLflowRunFacet,
    HardwareDetailsRunFacet,
    HardwareComponent,
    HyperparameterRunFacet,
    Hyperparameter,
    PerformanceMetricRunFacet,
    PerformanceMetric,
)
from aissemble_model_lineage.builder import LineageBuilder
from behave import *
import nose.tools as nt
from uuid import uuid4

from openlineage.client.run import Job as OLJob
from openlineage.client.run import Run as OLRun


@given("a model lineage job")
def step_impl(context):
    context.job_obj = Job("test_job", default_namespace="test_namespace")


@given("a model lineage run")
def step_impl(context):
    context.run_obj = Run(uuid4())
    context.lineage_builder = LineageBuilder()


@given("a source code facet with a specified type, URL and path")
def step_impl(context):
    context.sourcefacet = SourceCodeDirectoryJobFacet(
        sourceType="git", url="test_url", path="test_path"
    )


@given("a Hyperparameter facet with a specificied parameter and value")
def step_impl(context):
    context.hyperparameter = from_open_lineage_facet(
        HyperparameterRunFacet([Hyperparameter(parameter="n_estimator", value=500)])
    )


@given("a MLflowRunFacet is defined")
def step_impl(context):
    context.mlflowfacet = MLflowRunFacet(runUUID="1234")


@given("a HardwareDetailsRunFacet is defined")
def step_impl(context):
    context.lineage_builder.set_hardware_details_run_facet(
        [HardwareComponent("GPU", "NVIDIA Quadro 5500")]
    )
    context.hardwarefacet = context.lineage_builder.get_hardware_details_run_facet()


@given("a PerformanceMetricRunFacet is defined")
def step_impl(context):
    context.performancefacet = PerformanceMetricRunFacet(
        [PerformanceMetric("accuracy", "0")]
    )


@when("the job is updated with the source code facet")
def step_impl(context):
    job_facet = {"sourceCodeLocation": from_open_lineage_facet(context.sourcefacet)}
    context.job_obj.set_facets(facets=job_facet)
    context.ol_job = context.job_obj.get_open_lineage_job()


@when("the run is updated with the Hyperparameter facet")
def step_impl(context):
    input_facets = {"hyperparameters": context.hyperparameter}
    context.run_obj.set_facets(input_facets)
    context.ol_run = context.run_obj.get_open_lineage_run()


@when("the run is updated with the MLflowRunFacet")
def step_impl(context):
    mlrun_facet = {"mlflowRunId": from_open_lineage_facet(context.mlflowfacet)}
    context.run_obj.set_facets(facets=mlrun_facet)
    context.ol_run = context.run_obj.get_open_lineage_run()


@when("the run is updated with the HardwareDetailsRunFacet")
def step_impl(context):
    hardware_facet = {"hardwareDetails": from_open_lineage_facet(context.hardwarefacet)}
    context.run_obj.set_facets(facets=hardware_facet)
    context.ol_run = context.run_obj.get_open_lineage_run()


@when("the run is updated with the PerformanceMetricRunFacet")
def step_impl(context):
    performance_facet = {
        "performanceMetric": from_open_lineage_facet(context.performancefacet)
    }
    context.run_obj.set_facets(facets=performance_facet)
    context.ol_run = context.run_obj.get_open_lineage_run()


@then("the job retains the properties of the source code facet")
def step_impl(context):
    nt.assert_is_instance(context.ol_job, OLJob)
    nt.assert_equals(context.ol_job.name, context.job_obj.name)
    nt.assert_equals(len(context.ol_job.facets), len(context.job_obj.facets))
    # Check if sourceType 'git' is stored in OLJob facets
    source_facet = context.ol_job.facets.get("sourceCodeLocation", None)
    nt.assert_is_not_none(
        source_facet, "OLJob facets do not contain sourceCodeLocation"
    )
    nt.assert_equals(
        source_facet.sourceType, "git", "sourceType 'git' not found in OLJob facets"
    )


@then("the run retains the properties of the Hyperparameter facet")
def step_impl(context):
    hyperparameter_facet = context.ol_run.facets.get("hyperparameters", None)
    nt.assert_is_instance(hyperparameter_facet, HyperparameterRunFacet)
    nt.assert_equals(
        hyperparameter_facet.hyperparameters[0].parameter,
        "n_estimator",
    )
    nt.assert_equals(
        hyperparameter_facet.hyperparameters[0].value,
        500,
    )


@then("the run retains the properties of the MLflowRunFacet")
def step_impl(context):
    nt.assert_is_instance(context.ol_run, OLRun)
    mlflow_facet = context.ol_run.facets.get("mlflowRunId", None)
    nt.assert_is_not_none(mlflow_facet, "OLRun facets do not contain mlflowRunId")
    nt.assert_equals(
        mlflow_facet.runUUID,
        "1234",
        "runUUID '1234' not found in mlflowRunId of run facets",
    )


@then("the run retains the properties of the HardwareDetailsRunFacet")
def step_impl(context):
    nt.assert_is_instance(context.ol_run, OLRun)
    hardware_facet = context.ol_run.facets.get("hardwareDetails", None)
    nt.assert_is_not_none(hardware_facet, "OLRun facets do not contain hardwareDetails")
    nt.assert_equals(
        hardware_facet.hardware[0].component,
        "GPU",
        "GPU not found in the component of the hardware details facet",
    )
    nt.assert_equals(
        hardware_facet.hardware[0].spec,
        "NVIDIA Quadro 5500",
        "NVIDIA Quadro 5500 not found in the spec of the hardware details facet",
    )


@then("the run retains the properties of the PerformanceMetricRunFacet")
def step_impl(context):
    nt.assert_is_instance(context.ol_run, OLRun)
    performance_metric_facet = context.ol_run.facets.get("performanceMetric", None)
    nt.assert_is_not_none(
        performance_metric_facet, "OLRun facets do not contain hardwareDetails"
    )
    nt.assert_equals(
        performance_metric_facet.performance[0].metric,
        "accuracy",
        "accuracy is not found in the metric of the performance metric facet",
    )
    nt.assert_equals(
        performance_metric_facet.performance[0].value,
        "0",
        "0 is not found in the value of the performance metric facet",
    )
