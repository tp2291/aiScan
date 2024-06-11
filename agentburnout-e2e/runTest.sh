#!/bin/bash

mkdir -p ./TestScripts/logs/test-logs

mkdir -p ./TestScripts/logs/test-reports

robot -L DEBUG -l ./TestScripts/logs/test-logs/log.html -r ./TestScripts/logs/test-reports/report.html  ./TestScripts/teste2e.robot

./postTest.sh
