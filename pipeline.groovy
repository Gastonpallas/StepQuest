pipeline {
    agent any

    tools {
        // Installe la version de Maven configurée comme "maven" et l'ajoute au chemin.
        maven "maven"
    }

    environment {
        SONAR_TOKEN = "sqp_dec24da5aa5d1fd5722f2ccc252bddcfebe6f2a8" // Charge le jeton SonarQube dans une variable d'environnement
    }

    stages {
        stage('SCM') {
            steps {
                // Récupère le code depuis le gestionnaire de version (SCM)
                git branch: 'dev', url: 'https://github.com/Gastonpallas/StepQuest'
            }
        }
        stage('Build') {
            steps {
                dir('backend') {
                    // Exécute Maven pour compiler le projet et lancer les tests dans le répertoire 'backend' avec le profil Docker
                    sh "mvn clean package -Dspring.profiles.active=docker"
                }
            }
            post {
                success {
                    dir('backend') {
                        // Si les tests réussissent, enregistre les résultats et archive le fichier JAR
                        junit '**/target/surefire-reports/TEST-*.xml'
                        archiveArtifacts 'target/*.jar'
                    }
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                dir('backend') {
                    script {
                        // Exécute l'analyse SonarQube en utilisant Maven dans le répertoire 'backend'
                        def scannerHome = tool 'sonar'
                        withSonarQubeEnv('sonar') {
                            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=stepQuest \
                                -Dsonar.projectName=stepQuest -Dsonar.host.url=http://sonarqube:9000 \
                                -Dsonar.token=${SONAR_TOKEN} -Dsonar.java.binaries=target/classes"
                        }
                    }
                }
            }
        }
    }
}
