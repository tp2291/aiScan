# syntax=docker/dockerfile:1

FROM python:3.9.6

WORKDIR /agentburnout-e2e

COPY . .

RUN pip3 install --no-cache-dir -r automationRequirements.txt

RUN apt-get update

RUN apt-get -y install awscli

RUN apt-get -y install jq

RUN chmod +x ./*.sh

ENTRYPOINT ./runTest.sh
