FROM quay.io/wildfly/wildfly:latest-jdk21

COPY Presentacion/target/presentacion.war /opt/jboss/wildfly/standalone/deployments/

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", \
     "-bmanagement", "0.0.0.0", "-c", "standalone.xml"]
