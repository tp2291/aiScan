#!/bin/bash

mkdir -p ./TestScripts/logs/test-logs

mkdir -p ./TestScripts/logs/test-reports

export RUN_ON_LOCAL=False

robot -L DEBUG -l ./TestScripts/logs/test-logs/log.html -r ./TestScripts/logs/test-reports/report.html -v RUN_ON_LOCAL:$RUN_ON_LOCAL ./TestScripts

./postTest.sh