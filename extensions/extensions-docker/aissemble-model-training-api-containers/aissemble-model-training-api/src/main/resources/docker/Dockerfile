#HABUSHU_BUILDER_STAGE - HABUSHU GENERATED CODE (DO NOT MODIFY)
FROM docker.io/python:3.11 AS habushu_builder

# Install pipx
RUN python -m ensurepip --upgrade
RUN python -m pip install --user pipx

# Poetry and supporting plugin installations
RUN $HOME/.local/bin/pipx install poetry==2.0.1 && \
    $HOME/.local/bin/poetry self add poetry-plugin-bundle@1.5.0

WORKDIR /work-dir
COPY --chown=1001 target/containerize-support ./containerize-support/
RUN find . -type f -name pyproject.toml -exec sed -i 's|develop[[:blank:]]*=[[:blank:]]*true|develop = false|g' {} \;

USER root
WORKDIR /work-dir/containerize-support/foundation/aissemble-foundation-model-training-api
ENV POETRY_CACHE_DIR="/.cache/pypoetry"

# Export target project's virtual environment to /opt/venv
RUN --mount=type=cache,target=${POETRY_CACHE_DIR} \
    $HOME/.local/bin/poetry lock && \
    $HOME/.local/bin/poetry bundle venv /opt/venv
#HABUSHU_BUILDER_STAGE - HABUSHU GENERATED CODE (END)

#HABUSHU_FINAL_STAGE - HABUSHU GENERATED CODE (DO NOT MODIFY)
FROM docker.io/python:3.11-slim AS final

# Copy venv from builder and activate by adding to the path
COPY --from=habushu_builder --chown=1001 --chmod=744 /opt/venv /opt/venv/
ENV PATH="/opt/venv/bin:$PATH"
#HABUSHU_FINAL_STAGE - HABUSHU GENERATED CODE (END)

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

# Set/move any required environmental variables and key scripts/configs needed by FastAPI from the base image
# into the slimmed down final Python image
ENV PYTHONUNBUFFERED="1"
ENV GIT_PYTHON_REFRESH="quiet"
ENV API_HOST='0.0.0.0'
ENV API_PORT='80'
ENV EXTRA_OPTS=''

CMD python -m uvicorn "model_training_api.model_training_api:app" --host "$API_HOST" --port "$API_PORT" $EXTRA_OPTS