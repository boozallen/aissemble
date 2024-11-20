FROM docker.io/jenkins/jenkins:lts-jdk17

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

USER root
USER jenkins

RUN jenkins-plugin-cli --plugins git:5.2.2
RUN jenkins-plugin-cli --plugins github:1.39.0
RUN jenkins-plugin-cli --plugins templating-engine:2.4
RUN jenkins-plugin-cli --plugins adoptopenjdk:1.4
RUN jenkins-plugin-cli --plugins pipeline-maven:1421.v610fa_b_e2d60e
RUN jenkins-plugin-cli --plugins slack:608.v19e3b_44b_b_9ff

# these can likely be removed once the Jenkins instnace in the docker container is updated:
RUN jenkins-plugin-cli --plugins workflow-cps:2692.v76b_089ccd026
RUN jenkins-plugin-cli --plugins script-security:1172.v35f6a_0b_8207e

COPY ./src/main/resources/jenkins/ "$REF"

ENV TZ="EST"
RUN date

USER jenkins