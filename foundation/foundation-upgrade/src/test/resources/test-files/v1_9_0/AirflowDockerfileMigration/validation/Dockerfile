# Script for creating policy decision point service image
#
# GENERATED DOCKERFILE - please ***DO*** modify.
#
# Generated from: ${templateName}


FROM apache/airflow:2.9.3

# Airflow variables
ARG AIRFLOW_USER_HOME=/home/airflow
ARG AIRFLOW_HOME=/opt/airflow

USER root

RUN apt-get update && apt-get install make

USER airflow

#Run pip install on all the requirements.txt if any are found - could be none for a project without training steps
#Install requirements first to leverage docker build cache when requirements don't change
COPY ./target/dockerbuild/requirements/. /installation/requirements/

ENV PIP_USER=false
RUN python3 -m virtualenv pipelines-env
ENV VENV=$AIRFLOW_HOME/pipelines-env
ENV KRAUSENING_BASE ${AIRFLOW_HOME}/config/

WORKDIR /
RUN set -e && files=$(find /installation/requirements -path '/installation/requirements/*/*/*' -name requirements.txt -type f); for file in $files; do "$VENV/bin/python3" -m pip install --no-cache-dir -r $file || exit 1; done;

#PIPELINES
COPY ./target/dockerbuild/. $AIRFLOW_HOME/pipelines/

#Run pip install on *.tar.gz if any are found -could be none for a project without training steps
RUN set -e && files=$(find $AIRFLOW_HOME/pipelines -path "$AIRFLOW_HOME/pipelines/*/*/*" -name *.tar.gz -type f); for file in $files; do "$VENV/bin/python3" -m pip install --no-deps --no-cache-dir $file || exit 1; done;

ENV PIP_USER=true

COPY ./src/main/dags/* $AIRFLOW_HOME/dags/

#if ($dataFlowPipelines)
#The jobs folder is populated with generated charts that are pulled in from each pipeline's target directory
COPY ./src/main/jobs/* $AIRFLOW_HOME/jobs/
#end

COPY ./src/main/resources/krausening/base/ ${AIRFLOW_HOME}/config/

WORKDIR /opt/airflow
