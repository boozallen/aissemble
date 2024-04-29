###
# #%L
# aiSSEMBLE Data Encryption::Encryption (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from krausening.logging import LogManager
import subprocess
import time
import os
import requests

logger = LogManager.get_instance().get_logger("Environment")


def before_all(context):
    krauseningBasePath = os.path.abspath("tests/resources/config")
    krauseningBaseArg = "-DKRAUSENING_BASE=" + krauseningBasePath
    driftDetectionPolicyPath = os.path.abspath("tests/resources/drift-policies")
    driftDetectionPolicyArg = "-DDRIFT_DETECTION_POLICIES=" + driftDetectionPolicyPath
    servicePortArg = "-Dquarkus.http.port=8084"
    driftDetectionServicePath = os.path.abspath(
        "../foundation-drift-detection-service/target/quarkus-app/quarkus-run.jar"
    )
    logger.info(f"Starting Drift Detection Service")
    context.driftDetectionService = subprocess.Popen(
        [
            "java",
            krauseningBaseArg,
            servicePortArg,
            driftDetectionPolicyArg,
            "-jar",
            driftDetectionServicePath,
        ]
    )

    # call to health check to verify Drift Detection Services are started
    health_check_url = "http://localhost:8084/q/health/live"
    success = False
    retries = 1
    wait = 0.1
    while not success and retries < 10:
        try:
            resp = requests.get(url=health_check_url).json()
            if resp["status"] == "UP":
                success = True
        except:
            logger.info(
                f"Waiting {wait} seconds for Drift Detection Services to start - waiting for a response from {health_check_url}"
            )
            time.sleep(wait)
            retries += 1
            wait *= 2

    (
        logger.info(f"Drift Detection Service started")
        if success
        else logger.info(f"Drift Detection Service failed to start")
    )


def after_all(context):
    logger.info(f"Stopping Drift Detection Service")
    context.driftDetectionService.kill()


def before_tag(context, tag):
    pass


def after_tag(context, tag):
    pass


def before_scenario(context, scenario):
    pass


def after_scenario(context, scenario):
    pass
