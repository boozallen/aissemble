FROM docker.io/jenkins/ssh-agent:latest-debian-jdk17

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

USER root
RUN apt --yes update && apt install --yes pipx && pipx ensurepath && apt clean
RUN pipx install poetry

RUN mkdir /var/pyenv
RUN ls -l -a /var/pyenv
RUN git clone https://github.com/pyenv/pyenv.git /var/pyenv

RUN echo 'export PYENV_ROOT=$(echo "/var/pyenv")' >> /etc/bash.bashrc
RUN echo 'command -v pyenv >/dev/null || export PATH=$(echo "$PYENV_ROOT/bin:$PATH")' >> /etc/bash.bashrc
RUN echo 'eval "$(pyenv init -)"'  >> /etc/bash.bashrc

RUN echo 'export PYENV_ROOT=$(echo "/var/pyenv")' >> /etc/profile
RUN echo 'command -v pyenv >/dev/null || export PATH=$(echo "$PYENV_ROOT/bin:$PATH")' >> /etc/profile
RUN echo 'eval "$(pyenv init -)"' >> /etc/profile

RUN chown -R jenkins:jenkins /var/pyenv