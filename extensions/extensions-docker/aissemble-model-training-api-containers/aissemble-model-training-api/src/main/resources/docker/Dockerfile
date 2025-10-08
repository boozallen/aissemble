#HABUSHU_BUILDER_STAGE - HABUSHU GENERATED CODE (DO NOT MODIFY)
FROM docker.io/python:3.11 AS habushu_builder
ARG WHEEL_HOUSE="/tmp/wheels"
USER root
RUN --mount=type=bind,source=target/containerize-support,target=${WHEEL_HOUSE} \
    python3 -m venv /opt/venv && \
    /opt/venv/bin/python -m ensurepip --upgrade && \
    /opt/venv/bin/python -m pip install --no-deps \
      -r ${WHEEL_HOUSE}/requirements.txt && \
    /opt/venv/bin/python -m pip install --no-deps ${WHEEL_HOUSE}/aissemble_foundation_model_training_api-2.1.0.dev0-py3-none-any.whl

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