# Spring User Management

Important features: 
  - Sign-up 
  - Sign-in 
  - Email verification with AWS SES.
  - Password reset, update, delete. 
  - Deploy to Amazon AWS Cloud.

## Summary

This application is deployed in AWS. It uses AWS SES to send emails and an EC2 container with an Amazon Linux AMI which supports AWS command line tools and Java and its repositories include MySQL and Tomcat

The EC2 instance contains Tomcat version 8.5 and MySQL version 5.5.62. 
There are two applications, the first one *mobile-app-ws* corresponds to the user services that are exposed and the second one *verification-service* is the front-end side used to test services like email and password operations. Both applications are deployed into Tomcat server using a war file for each.

The back-end application uses Gradle as build automation tool, and the front-end uses maven.

## Configuration

As specified in the application.properties file, you must create a database named "photo_app" in order to execute successfully the application. In the same file you can change these properties, as well as the server port that the application will use and the credentials to access MySQL.

Since the application uses SES from AWS, you have to configure the credentials with the correct privileges to use this service. 

In a *local environment* you can configure these credentials by creating a file named "credentials" without a file extension, and must be located in "~/.aws/". This file should be structured as follows: 

```
[default]
aws_access_key_id={YOUR_ACCESS_KEY_ID}
aws_secret_access_key={YOUR_SECRET_ACCESS_KEY}
```

In a *prodution environment* you have to create and assign a **IAM role** with the corresponding permissions to your EC2 instance. For reference: [How to launch an EC2 instance with an IAM role](https://docs.aws.amazon.com/us_en/AWSEC2/latest/UserGuide/iam-roles-for-amazon-ec2.html)

There are a bunch of options to achieve the same result. For reference: [Configure credentials - SDK For Java](https://docs.aws.amazon.com/es_es/sdk-for-java/v1/developer-guide/credentials.html)

## Execution

### Local

Once you have configured and initialized MySQL you can run the application, it will be exposed through the port assigned in application.properties.

### Prod

With your EC2 instance running and MySQL and Tomcat initialized you have to deploy the application using the war file.

* Deploy into Tomcat
  * Use the Public DNS or Public IP of your EC2 instance and access to: `publicDNS`:`tomcatPort`/manager/html
  * Upload both war files (mobile-app-ws, verification-service) and deploy.

