pipeline {
  agent any
  tools {
    maven 'Maven 3.6.3'
  }
  stages {
    stage ('Build') {
      steps {
        withMaven(
          mavenSettingsConfig: 'bibsonomy'
        ) {
          sh 'mvn clean install'
        }
      }
    }
  }
}