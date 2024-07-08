FROM python:3.11

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

RUN pip install \
    mlflow==2.3.1 \
    psycopg2 \
    protobuf \
    boto3 && \
    mkdir /mlflow/

ENV PYTHONUNBUFFERED="1"
ENV GIT_PYTHON_REFRESH="quiet"