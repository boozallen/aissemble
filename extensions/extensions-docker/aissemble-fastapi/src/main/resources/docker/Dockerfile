# Script for creating base FastAPI Docker image

FROM python:3.11-slim

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

WORKDIR /app

# Install base requirements
COPY ./src/main/resources/docker/requirements.txt /tmp/requirements.txt
RUN pip install --no-cache-dir --upgrade -r /tmp/requirements.txt

# Custom start script to run fastAPI with specific 
# module (defined using MODULE environment variable)
COPY ./src/main/resources/docker/scripts/start.sh /start.sh
RUN chmod +x /start.sh

# Add FastAPI module
COPY ./src/main/resources/docker/scripts/main.py /app/main.py

# Run the start script
CMD ["/start.sh"]


