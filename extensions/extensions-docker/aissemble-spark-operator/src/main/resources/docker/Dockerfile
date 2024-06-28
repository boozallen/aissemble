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

FROM golang:1.22.2-alpine as builder

WORKDIR /workspace

# Copy the Go Modules manifests
COPY ./target/checkout/go* ./
# Cache deps before building and copying source so that we don't need to re-download as much
# and so that source changes don't invalidate our downloaded layer
RUN go mod download

# Copy the go source code
COPY ./target/checkout/main.go main.go
COPY ./target/checkout/pkg/ pkg/
COPY ./target/checkout/hack/gencerts.sh /tmp/scripts/gencerts.sh
COPY ./target/checkout/entrypoint.sh /tmp/scripts/entrypoint.sh

RUN go mod tidy
# Build
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 GO111MODULE=on go build -a -o /usr/bin/spark-operator main.go

FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-spark:${VERSION_AISSEMBLE}

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

USER root

COPY --from=builder /usr/bin/spark-operator /usr/bin/
COPY --from=builder /tmp/scripts/* /usr/bin/
RUN apt-get update --allow-releaseinfo-change \
    && apt-get update \
    && apt-get install -y openssl curl tini \
    && rm -rf /var/lib/apt/lists/* \
    && chmod +x /usr/bin/entrypoint.sh \
    && chmod +x /usr/bin/gencerts.sh

USER spark
ENTRYPOINT ["/usr/bin/entrypoint.sh"]
