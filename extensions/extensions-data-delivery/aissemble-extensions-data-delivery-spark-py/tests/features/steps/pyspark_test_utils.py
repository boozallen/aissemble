from behave import *
import nose.tools as nt
from pyspark.sql import SparkSession
from data_delivery_spark_py.test_utils.spark_session_manager import *


@when("a configured spark session is requested for unit testing")
def step_impl(context):
    create_standalone_spark_session("tests/resources/apps/application-test.yaml")


@when("a configured spark session with no unsafe line is requested for unit testing")
def step_impl(context):
    create_standalone_spark_session(
        "tests/resources/apps/application-test-no-unsafe.yaml"
    )


@then("an appropriately configured session can be retrieved")
def step_impl(context):
    session = SparkSession.builder.getOrCreate()
    nt.assert_equals(
        session.conf.get("spark.app.name"), "example-data-delivery-pipeline-py-spark"
    )
    nt.assert_in(session.conf.get("spark.jars.excludes"), "org.geotools:gt-main:*")
    nt.assert_in(
        session.conf.get("spark.jars.packages"), "io.delta:delta-storage:2.0.0"
    )
    nt.assert_in(
        "https://repo1.maven.org/maven2/io/delta/delta-core_2.12/2.0.0/delta-core_2.12-2.0.0.jar",
        session.conf.get("spark.jars"),
    )
    nt.assert_in(
        "https://repo1.maven.org/maven2",
        session.conf.get("spark.jars.repositories"),
    )
