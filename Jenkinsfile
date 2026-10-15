pipeline {
    agent any
    tools {
        maven 'M3'
        jdk 'JDK17'
    }
    parameters {
        choice(name: 'GROUPS', choices: ['', 'smoke', 'regression', 'crud', 'auth'], description: 'JUnit tag. Empty = all')
        choice(name: 'ENV', choices: ['dev', 'stage', 'prod'], description: 'Config file')
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Test') {
            steps { sh "mvn clean test -Denv=${params.ENV} -Dgroups=${params.GROUPS}" }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
            allure includeProperties: false, results: [[path: 'target/allure-results']]
        }
    }
}