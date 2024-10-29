pipeline {
    agent any

    tools {
        maven "maven"
    }

    stages {
        stage('SCM') {
            steps {
                git branch: 'dev', url: 'https://github.com/Gastonpallas/StepQuest'
            }
        }
        stage('Build') {
            steps {
                dir('backend') {
                    sh "mvn clean package -Dspring.profiles.active=docker"
                    sh 'ls -la target/classes'
                }
            }
            post {
                success {
                    dir('backend') {
                        junit '**/target/surefire-reports/TEST-*.xml'
                        archiveArtifacts 'target/*.jar'
                    }
                }
            }
        }
        stage('Run SonarQube') {
            environment {
                scannerHome = tool 'sonar'
            }
            steps {
                dir('backend') {
                    withSonarQubeEnv(credentialsId: 'token_sonar', installationName: 'sonar') {
                        sh """
                        ${scannerHome}/bin/sonar-scanner -X \
                          -Dsonar.projectKey=stepQuest \
                          -Dsonar.projectName=stepQuest \
                          -Dsonar.host.url=http://sonarqube:9000 \
                          -Dsonar.java.binaries=target/classes \
                          -Dsonar.sources=src/main/java
                        """
                    }
                }
            }
        }
    }
}
