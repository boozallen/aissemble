FROM jenkins/jenkins:lts-jdk11

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

USER root
USER jenkins

RUN jenkins-plugin-cli --plugins git:4.11.2
RUN jenkins-plugin-cli --plugins github:1.34.3
RUN jenkins-plugin-cli --plugins templating-engine:2.4
RUN jenkins-plugin-cli --plugins adoptopenjdk:1.4
RUN jenkins-plugin-cli --plugins pipeline-maven:3.10.0
RUN jenkins-plugin-cli --plugins slack:608.v19e3b_44b_b_9ff

# these can likely be removed once the Jenkins instnace in the docker container is updated:
RUN jenkins-plugin-cli --plugins workflow-cps:2692.v76b_089ccd026
RUN jenkins-plugin-cli --plugins script-security:1172.v35f6a_0b_8207e

COPY ./src/main/resources/jenkins/ "$REF"

ENV TZ="EST"
RUN date

# 5/12/2022 the following should be on the jenkins-agent, but we won't start using that until we get to a follow-on
# task of making jenkins agents use Kubernetes.  Prior tickets are being worked that will smooth this approach,
# so it will be handled once that work is completed.

# Install the libraries needed on Debian Bullseye (base Jenkins image) to run Jenkins
# python-is-python3 is used to workaround nothing being mapped to python on Bullseye, which messes w/ pyenv

USER root
RUN apt-get update && apt-get remove libssl-dev && apt-get install -y \
    build-essential \
    libbz2-dev \
    libedit-dev \
    libffi-dev \
    libglib2.0-0 \
    liblzma-dev \
    libncursesw5-dev \
    libreadline-dev \
    libsm6 \
    libsndfile1-dev \
    libsqlite3-dev \
    libssl-dev \
    libxml2-dev \
    libxmlsec1-dev \
    llvm \
    make \
    python-is-python3 \
    python3-distutils \
    tk-dev \
    xz-utils \
    zlib1g-dev \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

RUN mkdir /var/pyenv
RUN ls -l -a /var/pyenv
RUN git clone https://github.com/pyenv/pyenv.git /var/pyenv

USER root
RUN echo 'export PYENV_ROOT=$(echo "/var/pyenv")' >> /etc/bash.bashrc
RUN echo 'command -v pyenv >/dev/null || export PATH=$(echo "$PYENV_ROOT/bin:$PATH")' >> /etc/bash.bashrc
RUN echo 'eval "$(pyenv init -)"'  >> /etc/bash.bashrc

RUN echo 'export PYENV_ROOT=$(echo "/var/pyenv")' >> /etc/profile
RUN echo 'command -v pyenv >/dev/null || export PATH=$(echo "$PYENV_ROOT/bin:$PATH")' >> /etc/profile
RUN echo 'eval "$(pyenv init -)"' >> /etc/profile

RUN chown -R jenkins:jenkins /var/pyenv

RUN mkdir /var/poetry && \
    export POETRY_HOME=/var/poetry && \
    curl -sSL https://install.python-poetry.org -o /tmp/install-poetry.py && \
    python3 /tmp/install-poetry.py --version 1.2.2 && \
    echo 'export PATH="/var/poetry/bin:$PATH"' >> /etc/bash.bashrc

USER jenkins