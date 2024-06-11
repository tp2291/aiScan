*** Settings ***
Documentation    Test Cases for Agent Burnout API
Resource    ../saa_resources.robot

*** Test Cases ***
Pass Bearer and Get Access Token
    IF  ${RUN_ON_LOCAL}
    Log To Console    Local
        ${bearer}=     Get Bearer Token
        ${access_token}=    Get Access Token    ${bearer}
    ELSE
        Token Manager
        ${access_token}=    Get Token
    END
    Set Global Variable    ${access_token}

Subscription Authorization
    ${response}     ${body}=    Agent Subscription      ${access_token}
    Log To Console    ${\n}${response}
    Log To Console    ${\n}${body}
    IF  ${response} != None
        ${status}=      Verify Subscription Response    ${response}     ${body}
        Should Be True    ${status}
    ELSE
        Fail    Response is None
    END

Burnout Metrics of Agent
    ${burnoutScore}=     Get Burnout Metrics And Verify Current Interaction      ${access_token}
    IF  ${burnoutScore} != None
        Log    Burnout Score is : ${burnoutScore}
        Set Global Variable    ${burnoutScore}
    ELSE
        Fail    Burnout Score Not Generated
    END

Break Eligibility of Agent
    ${response}=    Check Break Eligibility     ${access_token}
    IF  ${response} != None
        ${status}=      Verify Break Eligibility Response    ${response}
        Should Be True    ${status}
    ELSE
        Fail    Response is None
    END

Give Break corresponding to Interaction ID
    ${response}=    Give break to agent     ${access_token}
    IF  ${response} != None
        ${status}=      Verify Give Break Response    ${response}
        Should Be True    ${status}
    ELSE
        Fail    Response is None
    END


GET Agent Config
    ${response}=    Get Agent Config     ${access_token}
    IF  ${response} != None
        ${status}=      Verify Agent Config    ${response}
        Should Be True    ${status}
    ELSE
        Fail    Response is None
    END

POST Agent Config
    ${response}     ${body}=    Post Agent Config    ${access_token}
    IF  ${response} != None
        ${status}=      Verify Agent Config    ${response}      ${body}
        Should Be True    ${status}
    ELSE
        Fail    Response is None
    END

