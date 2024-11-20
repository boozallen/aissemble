#
# Copyright 2017 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE

FROM docker.io/kubeflow/spark-operator:v1beta2-1.6.2-3.5.0 AS builder

# We would be able to use the kubeflow image directly, except that it is on Spark 3.5 instead of 3.4
FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-spark:${VERSION_AISSEMBLE}

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

USER root

RUN apt-get update \
    && apt-get install -y tini \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder --chmod=755 /usr/bin/spark-operator /usr/bin/
COPY --from=builder --chmod=755 /usr/bin/entrypoint.sh /usr/bin/

USER spark
ENTRYPOINT ["/usr/bin/entrypoint.sh"]
