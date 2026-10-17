pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'M3'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }

    parameters {
        string(name: 'ENV', defaultValue: 'dev', description: 'dev / stage')
        string(name: 'GROUPS', defaultValue: '', description: 'Пусто = все тесты')
    }

    triggers {
        pollSCM('H/5 * * * *')
        cron('H 3 * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                withCredentials([string(credentialsId: 'db-postgres-pass', variable: 'DB_PASSWORD')]) {
                    script {
                        def groupsArg = params.GROUPS?.trim() ? "-Dgroups=${params.GROUPS}" : ""
                        sh "mvn clean test -Denv=${params.ENV} -Ddb.password=${DB_PASSWORD} ${groupsArg}"
                    }
                }
            }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            allure results: [[path: 'target/allure-results']]
        }

        success {
            withCredentials([
                    string(credentialsId: 'telegram_token', variable: 'TG_TOKEN'),
                    string(credentialsId: 'telegram_chat_id', variable: 'TG_CHAT')
            ]) {
                sh '''
          curl -s -X POST "https://api.telegram.org/bot$TG_TOKEN/sendMessage" \
            --data-urlencode "chat_id=$TG_CHAT" \
            --data-urlencode "text=SUCCESS: $JOB_NAME #$BUILD_NUMBER (env=$ENV) $BUILD_URL"
        '''
            }
        }

        failure {
            withCredentials([
                    string(credentialsId: 'telegram_token', variable: 'TG_TOKEN'),
                    string(credentialsId: 'telegram_chat_id', variable: 'TG_CHAT')
            ]) {
                sh '''
          curl -s -X POST "https://api.telegram.org/bot$TG_TOKEN/sendMessage" \
            --data-urlencode "chat_id=$TG_CHAT" \
            --data-urlencode "text=FAILED: $JOB_NAME #$BUILD_NUMBER (env=$ENV, groups=$GROUPS) $BUILD_URL"
        '''
            }
        }

        unstable {
            withCredentials([
                    string(credentialsId: 'telegram_token', variable: 'TG_TOKEN'),
                    string(credentialsId: 'telegram_chat_id', variable: 'TG_CHAT')
            ]) {
                sh '''
          curl -s -X POST "https://api.telegram.org/bot$TG_TOKEN/sendMessage" \
            --data-urlencode "chat_id=$TG_CHAT" \
            --data-urlencode "text=UNSTABLE: $JOB_NAME #$BUILD_NUMBER $BUILD_URL"
        '''
            }
        }
    }
}