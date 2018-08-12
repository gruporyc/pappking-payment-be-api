# README #

This README would normally document whatever steps are necessary to get your application up and running.

### Payment microservice for Pappking project ###

* Quick summary
* Version

### How do I get set up? ###


## Generate payu dependency into project ##

mvn install:install-file -Dfile=[path-tofile]/[file.jar] -DgroupId=[group-id] -DartifactId=[artifact-id] -Dversion=[artifact-version] -Dpackaging=jar -DgenratePom=true

#Example:# mvn install:install-file -Dfile=/media/jhonnyquest/Data/pappking/libraries/payu-java-sdk-1.3.2.jar -DgroupId=com.payu -DartifactId=payu -Dversion=1.3.2 -Dpackaging=jar -DgenratePom=true



## Setup environment variables ##

PAYMENTS_ACCOUNT_ID, default 512321
PAYMENTS_TAX_VALUE, default 19
PAYMENTS_RESPONSE_URL, default http://www.pappking.com/payment-response/
PAYMENTS_CHECK_INTERVAL_MINUTES, default 10
PAYMENTS_MAX_PENDING_HOURS, default 48

### Contribution guidelines ###

/******************************************************************
 *
 * This code is for the Pappking service project.
 *
 *
 * © 2018, Pappking Management All rights reserved.
 *
 *
 ******************************************************************/