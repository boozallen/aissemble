FROM mysql/mysql-server:8.0.30

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

# MySQL places files into the root directory as a part of the startup process.
# Since the root directory isn't owned by the mysql user, it has to run as root normally in order to create those files.
# To run container as a non-root user, we need to manually add those files and give permissions to them so they can
# be overwritten by the user during startup.
COPY ./src/main/resources/healthcheck.cnf /
COPY ./src/main/resources/mysql-init-complete /
USER root

# When using buildkit, the command 'RUN chown -R mysql:mysql /etc' would fail the build due to resolv.conf and hosts
# being read only files. This can be resolved by using findutils, allowing the chown command to be applied to all
# files except those two.
# This was not an issue without buildkit as the standard build would ignore those errors and keep building without
# actually changing the owner of those files.
RUN microdnf install findutils
RUN find /etc -not -name resolv.conf -not -name hosts -exec chown mysql:mysql {} + && \
    chown -R mysql:mysql /healthcheck.cnf && \
    chown -R mysql:mysql /healthcheck.sh && \
    chown -R mysql:mysql /mysql-init-complete

# By adding this sql script to the initdb directory it will run whenever the container is initialized
ADD ./src/main/resources/scripts/hive-schema-3.1.0.mysql.sql /docker-entrypoint-initdb.d

USER mysql
