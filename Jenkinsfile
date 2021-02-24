pipeline {
  agent any
  triggers {
    pollSCM 'H/10 * * * *'
  }
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