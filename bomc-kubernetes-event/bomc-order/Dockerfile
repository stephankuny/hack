# ____________________________________________________
# docker build -t bomc/bomc-order:v1.0.0 .
# docker run -it -d -p 8080:8080 -p 9090:9090 --name bomc-order -h bomc-order bomc/bomc-order:latest
# ----------------------------------------------------

FROM bomc/wildfly:v13.0.0

COPY ./target/bomc-order.war ${DEPLOYMENT_DIR}

ENTRYPOINT ${WILDFLY_HOME}/bin/standalone.sh --server-config=${WILDFLY_CONFIG_FILE} -b=0.0.0.0 -bmanagement=0.0.0.0 -Djboss.node.name=local.bomc-order -Dee8.preview.mode=true
