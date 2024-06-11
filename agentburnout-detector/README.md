# agentburnout-detector

# Table of Contents
* [Introduction](#intro)
* [Architecture and Design](#archanddesign)
    * [Tech Stack](#techstack)
    * [Build, Deploy and Test](#buildanddeploy)
    * [Interface Specifications and SLA](#interfacespec)
        * [APIs](#httpapis)
        * [SLAs](#slas)
 * [Validation Strategy](#validationstrategy)
   * [Test Design](#testdesign)
     * [Test Scenarios](#testscenarios)
 * [Metrics and Monitoring](#metrics)
    * [JVM / Process Metrics](#processmetrics)  
    * [API / Service Specific Metrics](#servicemetrics) 
    * [Grafana Dashboards](#grafana)
 * [Deployment and Operations](#devops)
    * [Troubleshooting](#troubleshooting)  
            


# Introduction <a name="intro"></a>
============

A python application which acts as an orchestrator for burnout index calculation.

# Architecture & Design <a name="archanddesign"></a>
=====================

This component is python application, and deployed as K8 service.

*   This component is an orchestrator which consumes and produces kafka events. 
*   Consumer consumes sentiment-available events from wxcc.update.event topic. 
*   Producer produces autocsat calculated to the topic agentburnout.index.
*   The App calls the saa-models-hub API to train the agent level burnout ML models.
*   Model training and retraining happens when certain conditions gets satisfied.
*   Finally the burnout index is calculated to for the interaction using the inference api in saa-models-hub and the burnout_event event is produced.
*   Architecture document link: https://confluence-eng-sjc12.cisco.com/conf/pages/viewpage.action?pageId=318656779


## Tech Stack <a name="techstack"></a>
----------

This is a Python application, and deployed as K8 service.

*   Programming Languages - Python.
*   Tools - Docker

## How to Build , Deploy and Test <a name="buildanddeploy"></a>
------------------------------

Get started by

- Downloading/Cloning the Source code
- Install [python 3.10 version](https://www.python.org/downloads/)

### Requirements for running with local python
- Install all packages from `requirements.txt` file (From command line)
    * Navigate to the folder `/saa-agentburnout/agentburnout-detector/` and run the command:
```
pip3 install -r requirements.txt
```
- Pass the properties file for kafka and database creds.
- Pass these as environment variables for running the app: DC, AppName, gitCommit.
- Run Local autocsat
- Produce the consumer events from another app.
- Implement Your Own Feature

### Requirements for running on Docker container
- Download Docker desktop
    * [Windows](https://docs.docker.com/desktop/install/windows-install/)
	* [Mac](https://docs.docker.com/desktop/install/mac-install/)
	* [Linux](https://docs.docker.com/desktop/install/linux-install/)
- For windows, make sure WSL2 is activated to proceed.
- To build docker images successfully, verify under Docker desktop Settings > Docker Engine "buildkit" is false.  
- Navigate to the folder `/saa-agentburnout/agentburnout-detector/` where `Dockerfile` is present to proceed with Docker deployement.  
- Before docker build add these values as environment variables in the Dockerfile: DC, AppName, gitCommit.


### Deploying on Docker Container

Build the Docker Image with this command (inside the folder where `Dockerfile` is present):
```
docker build --tag agentburnout-detector .
```
Verify all Docker Images with:
```
docker images
```
Run the Docker Image on Container with this command:
```
docker run --name agentburnout-detector
```


## Interface Specifications and SLA <a name="interfacespec"></a>
================================  

### SLAs <a name="sla"></a>
----

*   API Latency or Message Processing Latency
    *   Provide 95th percentile
*   API / Incoming Message Traffic Load - Max Requests/Messages Per Second for which the latency related SLA is valid
*   Max Error Rate in a hourly window


## Validation Strategy <a name="validationstrategy"></a>
===================

### Test Design <a name="testdesign"></a>
-----------

[Test Strategy](https://confluence-eng-sjc12.cisco.com/conf/pages/viewpage.action?pageId=318656779#WXCC2562PublishAPIsfromAgentBurnoutdetectionmodelFeatureArchitecture&QualityStrategy-11.FeatureTestStrategy)

#### Test Scenarios<a name="testscenarios"></a>
--------------

Add the content In-Place here in tabular format or provide a link to wiki where the information is documented.

Note that these are not granular test cases

|Scenario |Description|Type of Test (UT/FT/Integration/ Load)|
|-----------| ---|-----------|
|        |         |          |

  

## Metrics & Monitoring <a name="metrics"></a>
====================

[Prometheus data](https://prometheus.intgus1.ciscoccservice.com/graph?g0.expr=&g0.tab=1&g0.stacked=0&g0.show_exemplars=0&g0.range_input=1h)


### JVM / Process Metrics <a name="processmetrics"></a>
---------------------

Add Link to the generic JVM/Process metrics dashboard and a common wiki link that lists down all the JVM /Process metrics as these are typically common across all services.


### API / Service Specific Metrics <a name="servicemetrics"></a>
---------------------

Provide a reference / link to standard set of metrics that are common across all services. Provide information on service specific metrics here.

|Metric |Description|Type of Metric (Counter/Guage/Historgram)|
|-----------| ---|-----------|
| |         |          |
| |         |          |
|CPU, Memory | Tracks the cpu and memory utilization.        |  Guage        |


#### Grafana Dashboard Link <a name="grafana"></a>
----------------------

[Link to Grafana Dashboard](https://grafana-k8s.intgus1.ciscoccservice.com/d/a164a7f0339f99e89cea5cb47e9be617/kubernetes-compute-resources-workload?orgId=1&refresh=10s&var-datasource=default&var-cluster=&var-namespace=saa-agentburnout&var-workload=agentburnout-detector&var-type=deployment)


# Deployment & Operations <a name="devops"></a>
=======================

This service can be deployed from the jenkins pipeline from this [link](https://engci-private-sjc.cisco.com/jenkins/ccbu-sunlight/job/saa-agentburnout-deploy/).
  
  
## Troubleshooting <a name="troubleshooting"></a>
---------------

[Kibana Dashboard link](https://kibana-log.intgus1.ciscoccservice.com/_dashboards/goto/c5dfc56124897015ea532c7aef0a20a2)


