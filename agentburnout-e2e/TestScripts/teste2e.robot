*** Settings ***
Documentation    Test Cases for Agent Burnout E2E
Resource    ../saa_resources.robot
Test Timeout    1 minute

*** Test Cases ***
Generic Tests
    ${test_time}=      Get Current Date     UTC       increment=05:30:00
    Log To Console    Test Start time : ${test_time}
    ${kafka_producer_config}=    Load From Confluent    producer
    ${kafka_consumer_config}=    Load From Confluent    consumer
    Set Suite Variable    ${kafka_producer_config}
    Set Suite Variable    ${kafka_consumer_config}

Update Producer and Consumer
    ${producer_config}=     Producer Update     ${kafka_producer_config}
    ${consumer_object}=     Consumer Update     ${kafka_consumer_config}
    Set Suite Variable    ${producer_config}
    Set Suite Variable    ${consumer_object}

Create and Send Message
    ${producer}=    Create Producer Object    ${producer_config}
    ${producer_message}     ${producer_message_dict}=     Create Message
    Send Message    ${producer}     ${producer_message}
    Set Suite Variable    ${producer_message_dict}

Consume Message and Verify
    ${message_from_consumer}    ${burnoutIndex}=   Consume Message     ${consumer_object}
    Dictionary Should Contain Key    ${message_from_consumer}    burnoutIndex
    ${is_Float}=    Evaluate    isinstance(${burnoutIndex}, float)
    Should Be True    ${is_Float}
    ${test_time}=      Get Current Date     UTC       increment=05:30:00
    Log To Console    Test End time : ${test_time}
