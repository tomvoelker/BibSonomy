pipeline {
  agent any
  stages {
    stage ('Build') {
      steps {
        withMaven(maven: 'Maven 3.6.3', mavenSettingsConfig: 'bibsonomy') {
          sh "mvn clean install"
        }
      }
    }
  }
}