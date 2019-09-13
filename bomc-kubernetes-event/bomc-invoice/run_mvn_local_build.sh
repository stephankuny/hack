#!/bin/bash
export BOMC_ORDER_SERVICE_SERVICE_HOST=127.0.0.1
export BOMC_ORDER_SERVICE_SERVICE_PORT=8080

echo ### Start with following parameters ###
echo hostname=${BOMC_ORDER_SERVICE_SERVICE_HOST}
echo port=${BOMC_ORDER_SERVICE_SERVICE_PORT}

mvn clean install -Plocal && java -jar ./target/bomc-invoice-thorntail.jar -Slocal
