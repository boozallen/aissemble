# Script for creating base AIOps Model Training API image

ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE
FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-fastapi:${VERSION_AISSEMBLE} AS builder

RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

COPY ./target/wheels/* /tmp/wheels/
# Re-install any dependencies defined in the base image into the virtual environment, then install new requirements
RUN pip install -r /tmp/requirements.txt && \
    set -e && \
    cd /tmp/wheels/; for x in *.whl; do pip install $x --no-cache-dir; done


# Start a new build stage based on a slim Python image, which will have the minimal amount of code, FastAPI
# components, and installed dependencies copied into it from the initial builder image
FROM python:3.11-slim as final

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

WORKDIR /app

# Set/move any required environmental variables and key scripts/configs needed by FastAPI from the base image
# into the slimmed down final Python image
ENV API_MAIN model_training_api_sagemaker
ENV PATH="/opt/venv/bin:$PATH"
ENV PYTHONPATH=/app
COPY --from=builder /start.sh /start.sh

# Copy over the virtual environment (and all pip installed packages) from the initial builder stage
COPY --from=builder /opt/venv /opt/venv

COPY ./target/model_training_api_sagemaker.py /app/model_training_api_sagemaker.py

CMD ["/start.sh"]
