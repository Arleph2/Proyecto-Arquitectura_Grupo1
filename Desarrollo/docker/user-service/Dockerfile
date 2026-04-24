FROM quay.io/wildfly/wildfly:latest-jdk21

RUN curl -fL -o /tmp/postgresql.jar \
  https://jdbc.postgresql.org/download/postgresql-42.7.3.jar

COPY docker/user-service/configure.cli /tmp/configure.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/configure.cli

COPY user-service/target/user-service.war /opt/jboss/wildfly/standalone/deployments/

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", \
     "-bmanagement", "0.0.0.0", "-c", "standalone-full.xml"]
