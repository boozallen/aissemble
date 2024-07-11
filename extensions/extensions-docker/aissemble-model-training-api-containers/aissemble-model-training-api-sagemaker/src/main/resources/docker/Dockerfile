#HABUSHU_BUILDER_STAGE - HABUSHU GENERATED CODE (DO NOT MODIFY)
FROM python:3.11 AS habushu_builder
# Poetry and supporting plugin installations
RUN python -m ensurepip --upgrade && \
    pip install poetry && \
    poetry self add poetry-monorepo-dependency-plugin && \
    poetry self add poetry-plugin-bundle

WORKDIR /work-dir
COPY --chown=1001 target/containerize-support ./containerize-support/
RUN find . -type f -name pyproject.toml -exec sed -i 's|develop[[:blank:]]*=[[:blank:]]*true|develop = false|g' {} \;

USER root
WORKDIR /work-dir/containerize-support/extensions/aissemble-extensions-model-training-api-sagemaker
ENV POETRY_CACHE_DIR="/.cache/pypoetry"

# export target project's virtual environment to /opt/venv
RUN --mount=type=cache,target=/.cache/pypoetry/ \
    poetry lock && \
    poetry bundle venv /opt/venv
#HABUSHU_BUILDER_STAGE - HABUSHU GENERATED CODE (END)

#HABUSHU_FINAL_STAGE - HABUSHU GENERATED CODE (DO NOT MODIFY)
FROM python:3.11-slim AS final
# Copy venv from builder and activate by adding to the path
COPY --from=habushu_builder --chown=1001 /opt/venv /opt/venv/
ENV PATH="/opt/venv/bin:$PATH"
#HABUSHU_FINAL_STAGE - HABUSHU GENERATED CODE (END)

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

# Set/move any required environmental variables and key scripts/configs needed by FastAPI from the base image
# into the slimmed down final Python image
ENV API_HOST='0.0.0.0'
ENV API_PORT='80'
ENV EXTRA_OPTS=''

CMD python -m uvicorn "model_training_api_sagemaker.model_training_api_sagemaker:app" --host "$API_HOST" --port "$API_PORT" $EXTRA_OPTS
