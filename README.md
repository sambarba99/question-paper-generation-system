<p align="center">
	<img src="src/logo.png" width="557" height="220"/>
</p>

# Overview
QPGS is a JavaFX-based system which uses a Genetic Algorithm to automatically generate question papers. This GA requires the user to identify 3 key criteria: the academic subject of the paper; its overall skill level (derived from Bloom's taxonomy); and a duration estimate.

# Setting up QPGS

## Requirements
- JDK 1.8 (Java 8 is needed)
- JUnit 5

## Online-sourced multiple-choice questions

1. Download the folder `#QPGS`
2. Move this folder directly into the C drive, i.e. `C:\#QPGS`

# Running QPGS

## Creating a paper
1. In the IDE, find `Login.java` in `src/controller` and run it
2. Create a user account with any username, and a password that contains 1 of each: a-z, A-Z and 0-9, and is at least 8 characters long - "aaaaaaA1" for example
3. Head to the Academic Material page from the admin panel
4. Click 'Generate question paper' and enter the desired parameters
5. Click 'Generate!' and wait a few seconds
6. You can now head back to Academic Material, select the question paper from the table, and click 'View/export question paper'.

## Running unit tests
1. Delete `#QPGS` from the C drive
2. In the IDE, right-click the folder `test`
3. Run this as a JUnit test.

# Author
Sam Barba (https://www.linkedin.com/in/sam-barba-31033b179/)