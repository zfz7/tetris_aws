# tetris_aws
Fullstack serverless template: Backend: Kotlin/Smithy/Lambda/APIGateway, Frontend: React/CloudFront, Infra: CDK


## Prerequisites 
```
#Yarn
brew install yarn
#Gradle
brew install gradle
#AWS cli
curl "https://awscli.amazonaws.com/AWSCLIV2.pkg" -o "AWSCLIV2.pkg"
sudo installer -pkg ./AWSCLIV2.pkg -target /
#cdkcli
npm install -g aws-cdk
```

## Setup
```
export AWS_PROFILE=YOUR_PROFILE
export AWS_ACCOUNT='12312123123'
in root-hosted-zone.ts set hostedZoneId
```

## Commands
```
./gradlew build #build all
./gradlew backend:build
./gradlew frontend:build
./gradlew model:build
./gradlew cdk:build
./gradlew deploy #deploy all
```