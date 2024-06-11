*** Settings ***
Documentation    SAA Resources for FT

Library     SeleniumLibrary
Library     Collections
Library     String
Library     RequestsLibrary
Library     BuiltIn
Library     OperatingSystem
Library     DateTime

Library     ./CustomLibraries/AuthHandling.py
Library     ./CustomLibraries/APIOperations.py
Library     ./CustomLibraries/TokenManager.py