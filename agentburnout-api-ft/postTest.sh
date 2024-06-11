#!/bin/bash
mkdir $HOME/.aws
touch $HOME/.aws/credentials
credentials_file=$HOME/.aws/credentials

if [ "$vaultUrl" == "" ]; then
  echo No vaultUrl set, skipping post test ..
  exit $1
fi

awsCreds=`cat /vault/secrets/k8s-logs-creds.ini`

# Step1:  get the AWS creds
awsS3AccessKey=$(echo "$awsCreds" | grep -E "^id = " | cut -d'=' -f2)
awsS3SecretKey=$(echo "$awsCreds" | grep -E "^key = " | cut -d'=' -f2)

# Step 2 : update the AWS creds
echo [default] >> $credentials_file
echo aws_access_key_id=$awsS3AccessKey >> $credentials_file
echo aws_secret_access_key=$awsS3SecretKey >> $credentials_file

# Step 3 : compress the test artifacts and push to S3
mkdir -p testartifactstar
tar -zvcf testartifactstar/test-reports.tar.gz -C /$appName/TestScripts/logs .
aws s3 cp testartifactstar s3://$awsS3Bucket/$dataCenter/$clusterType/$appName/saa-agentburnout-$buildNumber/testartifacts --recursive