# Script for creating base AIOps Model Training API image
ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE

#HABUSHU_BUILDER_STAGE - HABUSHU GENERATED CODE (DO NOT MODIFY)
FROM python:3.11-slim AS habushu_builder
# Poetry and supporting plugin installations
RUN python -m ensurepip --upgrade && \
    pip install poetry && \
    poetry self add poetry-monorepo-dependency-plugin && \
    poetry self add poetry-plugin-bundle

WORKDIR /work-dir
COPY --chown=1001 target/containerize-support ./containerize-support/
RUN find . -type f -name pyproject.toml -exec sed -i 's|develop[[:blank:]]*=[[:blank:]]*true|develop = false|g' {} \;

USER root
WORKDIR /work-dir/containerize-support/foundation/aissemble-foundation-model-training-api
ENV POETRY_CACHE_DIR="/.cache/pypoetry"

# export target project's virtual environment to /opt/venv
RUN --mount=type=cache,target=/.cache/pypoetry/ \
    poetry lock && \
    poetry bundle venv /opt/venv
#HABUSHU_BUILDER_STAGE - HABUSHU GENERATED CODE (END)

FROM ghcr.io/boozallen/aissemble-fastapi:1.8.0-SNAPSHOT AS builder

#HABUSHU_FINAL_STAGE - HABUSHU GENERATED CODE (DO NOT MODIFY)
FROM python:3.11-slim AS final
# Copy venv from builder and activate by adding to the path
COPY --from=habushu_builder --chown=1001 /opt/venv /opt/venv/
ENV PATH="/opt/venv/bin:$PATH"
#HABUSHU_FINAL_STAGE - HABUSHU GENERATED CODE (END)

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

# Set/move any required environmental variables and key scripts/configs needed by FastAPI from the base image
# into the slimmed down final Python image
ENV API_MAIN=model_training_api.model_training_api
ENV PYTHONUNBUFFERED="1"
ENV GIT_PYTHON_REFRESH="quiet"

COPY --from=builder --chown=1001 /start.sh /start.sh

CMD ["/start.sh"]
