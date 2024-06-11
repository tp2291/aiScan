# abac-transcription-processor

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
 
A Java maven application which checks for Agent Burnout and AutoCSAT feature enablement from redis cache and produce transciption-needed events.

# Architecture & Design <a name="archanddesign"></a>
=====================

This component is Java maven application, and deployed as K8 service.

*   This component consumes kafka events and produces transciption-needed events. 
*   Consumer consumes capture-available event from wxcc.contact.state topic.
*   Producer produces transcription-needed events to CCAI managed kafka topic.
*   Architecture document link: 
	https://confluence-eng-sjc12.cisco.com/conf/pages/viewpage.action?pageId=318656779
	https://confluence-eng-sjc12.cisco.com/conf/pages/viewpage.action?pageId=318663886


## Tech Stack <a name="techstack"></a>
----------

This is a Java maven application, and deployed as K8 service.

*   Programming Languages - Java, maven.
*   Tools - Docker

## How to Build , Deploy and Test <a name="buildanddeploy"></a>
------------------------------

Get started by

- Downloading/Cloning the Source code
- Install [java 11 or above](https://www.oracle.com/in/java/technologies/downloads/)

### Requirements for running with local python
- Do a maven update to download all the dependencies and do  clean install to build the app.
    
- Pass the properties file for kafka and media creds.
- Pass these as environment variables for running the app: DC, AppName
- Run Local abac-transcription-processor
- Produce the consumer events from another app.
- Implement Your Own Feature

### Requirements for running on Docker container
- Download Docker desktop
    * [Windows](https://docs.docker.com/desktop/install/windows-install/)
	* [Mac](https://docs.docker.com/desktop/install/mac-install/)
	* [Linux](https://docs.docker.com/desktop/install/linux-install/)
- For windows, make sure WSL2 is activated to proceed.
- To build docker images successfully, verify under Docker desktop Settings > Docker Engine "buildkit" is false.  
- Navigate to the folder `/saa-agentburnout/abac-transcription-processor/` where `Dockerfile` is present to proceed with Docker deployement.  
- Before docker build add these values as environment variables in the Dockerfile: DC, AppName


### Deploying on Docker Container

Build the Docker Image with this command (inside the folder where `Dockerfile` is present):
```
docker build --tag abac-transcription-processor .
```
Verify all Docker Images with:
```
docker images
```
Run the Docker Image on Container with this command:
```
docker run --name abac-transcription-processor
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

[Test Strategy](https://confluence-eng-sjc12.cisco.com/conf/pages/viewpage.action?pageId=318656779)

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

[Link to Grafana Dashboard](https://grafana-k8s.intgus1.ciscoccservice.com/d/a164a7f0339f99e89cea5cb47e9be617/kubernetes-compute-resources-workload?orgId=1&refresh=10s&var-datasource=default&var-cluster=&var-namespace=saa-agentburnout&var-workload=abac-transcription-processor&var-type=deployment)


# Deployment & Operations <a name="devops"></a>
=======================

This service can be deployed from the jenkins pipeline from this [link](https://engci-private-sjc.cisco.com/jenkins/ccbu-sunlight/job/saa-sentiment-deploy/).
  
  
## Troubleshooting <a name="troubleshooting"></a>
---------------

[Kibana Dashboard link](https://kibana-log.intgus1.ciscoccservice.com/_dashboards/goto/00eecf62b50c7d1686a5c65aae4d674d)


