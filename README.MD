# AlphaSense Test Framework

Home task from AlphaSense.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

You need to install the following:
- Apache Maven 3.6.0
- Allure 2.13.1 (see https://docs.qameta.io/allure/#_installing_a_commandline)

## Running the tests

1. Tests can be run by executing the following command in project directory:
```
mvn clean test -Dcucumber.options="--tags '@category-all' --plugin io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm" -am -pl backend-tests
```
where you can specify annotation to choose which tests you want to execute. It is possible to specify the following annotations:
@category-all - allows you to run all tests in project
@category-one - allows you to run 1st and 2nd tests
@category-two - allows you to run 3rd and 4th tests

All tests are stored in _PetShop.feature_ file.

2. Base URL of application is configurable and is stored in _env-config.yaml_ file.

3. After execution of the tests result's artifacts are stored in ../backend-tests/target/allure-results directory. So you can open Allure report by executing the following command in terminal:
```
allure serve ${YOUR_PROJECT_DIR}/backend-tests/target/allure-results
```

## Authors

* **Mikhail Yakushev** - *Automation QA Engineer*
