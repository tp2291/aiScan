*** Settings ***
Documentation   Load Test Suite for Agent Burnout
Resource    ../saa_resources.robot
Test Timeout    5 minutes

*** Test Cases ***
Start Time
    ${test_time}=      Get Current Date     UTC       05:30:00     exclude_millis=True
    Log To Console    ${\n}${\n}Suite Start Time:${test_time}${\n}

Consumer Config
    ${sentiment_config}=    Load From Confluent     sentiment
    ${agentburnout_config}=    Load From Confluent     agentburnout
    ${sentiment_config}=     Load Consumer Common Config     ${sentiment_config}      sentiment
    ${agentburnout_config}=     Load Consumer Common Config     ${agentburnout_config}      agentburnout
    Log To Console    ${\n}Sentiment Consumer Config-${sentiment_config}
    Log To Console    ${\n}Agentburnout Consumer Config-${agentburnout_config}
    Set Suite Variable    ${sentiment_config}
    Set Suite Variable    ${agentburnout_config}

Consume Transcription and Sentiment Message and Calculate Metrics
    ${delta_time_from_event}    ${delta_time_from_interaction}   ${event_count}=   Consume Message     ${sentiment_config}      ${agentburnout_config}
    Log     Average time taken to generate Burnout Index for ${event_count} interaction(s) from the time sentiment is generated is ${delta_time_from_event} seconds
    Log     Average time taken to generate Burnout Index for ${event_count} interaction(s) from the time interaction ended is ${delta_time_from_interaction} seconds
    Set Suite Variable      ${delta_time_from_event}
    Set Suite Variable      ${delta_time_from_interaction}
    Set Suite Variable      ${event_count}

Prometheus metrics update
    Push The Calculated Metrics To Prometheus       ${delta_time_from_event}        ${delta_time_from_interaction}      ${event_count}

End Time
    ${test_time}=      Get Current Date     UTC       05:30:00     exclude_millis=True
    Log To Console    ${\n}${\n}Suite End Time : ${test_time}${\n}
