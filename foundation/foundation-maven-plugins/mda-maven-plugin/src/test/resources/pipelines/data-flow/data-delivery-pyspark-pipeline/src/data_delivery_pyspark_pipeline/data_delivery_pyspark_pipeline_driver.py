###
# #%L
# example::Pipelines::Pyspark Pipeline
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from pyspark_pipeline.step.example_step import ExampleStep
from krausening.logging import LogManager

"""
Driver to run the PysparkPipeline.

GENERATED STUB CODE - PLEASE ***DO*** MODIFY

Originally generated from: templates/data-delivery-pyspark/pipeline.driver.py.vm 
"""

logger = LogManager.get_instance().get_logger("PysparkPipeline")


if __name__ == "__main__":
    logger.info("STARTED: PysparkPipeline driver")

    # TODO: Execute steps in desired order and handle any inbound and outbound types
    ExampleStep().execute_step()
