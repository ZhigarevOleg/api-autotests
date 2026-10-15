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
        string(name: 'GROUPS', defaultValue: '', description: 'Пусто = все 25 тестов, например smoke')
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Test') {
            steps {
                script {
                    def groupsArg = params.GROUPS?.trim() ? "-Dgroups=${params.GROUPS}" : ""
                    sh "mvn clean test -Denv=${params.ENV} ${groupsArg}"
                }
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            allure results: [[path: 'target/allure-results']]
        }
    }
}