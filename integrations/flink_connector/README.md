<!-- This sample application is part of the Timestream prerelease documentation. The prerelease documentation is confidential and is provided under the terms of your nondisclosure agreement with Amazon Web Services (AWS) or other agreement governing your receipt of AWS confidential information. -->

# Amazon Timestream Sink for Apache Flink

Write records to Timestream from Flink.

----
## Overview

This is the root directory for samples which show you end-to-end process of working with Kinesis and Timestream.
The directory contains:
 - [sample data generator](/integrations/flink_connector/sample-data-generator) - python script which generates and ingests sample data to Kinesis
 - [sample Kinesis to Timestream Application](/integrations/flink_connector/sample-kinesis-to-timestream-app) - sample Flink application which reads records from Kinesis and uses the Timestream Sink to ingest data to Timestream
 - [Amazon Timestream Flink Sink](/integrations/flink_connector/flink-connector-timestream) - Timestream Flink Sink as Maven module


![design](images/root-diagram.png)

## Getting Started

### 1. Install prerequisites

 - Java 11 is the recommended version for using Kinesis Data Analytics for Apache Flink Application. If you have multiple Java versions ensure to export Java 11 to your `JAVA_HOME` environment variable.
 - Install [Apache Maven](https://maven.apache.org/install.html). You can test your Apache Maven install with the following command:
```
mvn -version
```

### 2. Compile the sample application:

```bash
cd flink-connector-timestream/
mvn clean compile && mvn package
# install it locally
mvn install

cd ../sample-kinesis-to-timestream-app
mvn clean compile && mvn package
```

### 3 (option A). Run Sample Flink Application locally
1. Compile and run the sample application. The application will create target Timestream database/table upon launch automatically, and will start pooling records from Kinesis and writing them to Timestream:
```
JSON_BASEPATH="${HOME}/.aws/cli/cache"

rm -rf "${JSON_BASEPATH}"

export AWS_PROFILE=cloudteam-dev-role
aws sso login --profile tc-dev

# make sure $JSON_BASEPATH is updated:
aws sts get-caller-identity | jq '.Arn'

sleep 5

# find the latest CLI JSON file
json_file=$(ls -tr "${JSON_BASEPATH}" | tail -n1)

# use jq to dump stuff in the right place
export AWS_ACCESS_KEY_ID=$(cat ${JSON_BASEPATH}/${json_file} | jq -r '.Credentials.AccessKeyId')
export AWS_SECRET_ACCESS_KEY=$(cat ${JSON_BASEPATH}/${json_file} | jq -r '.Credentials.SecretAccessKey')
export AWS_SESSION_TOKEN=$(cat ${JSON_BASEPATH}/${json_file} | jq -r '.Credentials.SessionToken')

mvn install exec:java -Dexec.mainClass="com.amazonaws.samples.kinesis2timestream.StreamingJob" -Dexec.args="--InputStreamName tenant-stream --Region us-west-2 --TimestreamDbName cloud-metrics-db --TimestreamTableName events" -Dexec.classpathScope=test
```

## 3 (option B). Run Sample Flink Application on Amazon Kinesis Data Analytics (NOT WORKING YET DUE TO LACK OF IAM PERMISSIONS)
1. Upload Flink Application Jar file to S3 bucket: 
```shell
aws s3 cp target/sample-kinesis-to-timestream-app-0.1-SNAPSHOT.jar s3://vitor-teleport-cloud-streaming-apps/sample-kinesis-to-timestream-app-0.1-SNAPSHOT.jar
```
2. Follow the steps in [Create and Run the Kinesis Data Analytics Application](https://docs.aws.amazon.com/kinesisanalytics/latest/java/get-started-exercise.html#get-started-exercise-7)
 - pick Apache Flink version 1.13.2
 - in "Edit the IAM Policy" step, add Timestream Write permissions to the created policy
```json
        {
            "Sid": "ReadFromKinesis",
            "Effect": "Allow",
            "Action": "kinesis:*",
            "Resource": "*",
        },
        {
            "Sid": "WriteToTimestream",
            "Effect": "Allow",
            "Action": "timestream:*",
            "Resource": "*",
        }
```

### 3. Generate and query events

1. Create tenants with https://github.com/gravitational/cloud/tree/vitor/kinesis-streaming and set `kinesisStreaming` to `true`.
2. The events now should be consumed by the sample application and written to Timestream table.
3. Query Timestream table using [AWS Console](https://docs.aws.amazon.com/timestream/latest/developerguide/console_timestream.html#console_timestream.queries.using-console) or AWS CLI:
```
JSON_BASEPATH="${HOME}/.aws/cli/cache"

# find the latest CLI JSON file
json_file=$(ls -tr "${JSON_BASEPATH}" | tail -n1)

# use jq to dump stuff in the right place
export AWS_ACCESS_KEY_ID=$(cat ${JSON_BASEPATH}/${json_file} | jq -r '.Credentials.AccessKeyId')
export AWS_SECRET_ACCESS_KEY=$(cat ${JSON_BASEPATH}/${json_file} | jq -r '.Credentials.SecretAccessKey')
export AWS_SESSION_TOKEN=$(cat ${JSON_BASEPATH}/${json_file} | jq -r '.Credentials.SessionToken')

aws timestream-query query --query-string "SELECT * FROM \"cloud-metrics-db\".events WHERE time >= ago (15m) LIMIT 20"
```