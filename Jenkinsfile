pipeline {
  agent any
  triggers {
    pollSCM 'H/10 * * * *'
  }
  options {
    disableConcurrentBuilds()
  }
  stages {
    stage ('Build') {
      steps {
        withMaven(maven: 'Maven 3.6.3', mavenSettingsConfig: 'bibsonomy') {
          sh "mvn clean install"
        }
      }
    }
    stage ('Deploy BibLicious') {
      when {
        branch 'master'
      }
      steps {
        withMaven(maven: 'Maven 3.6.3', mavenSettingsConfig: 'bibsonomy') {
          sh "mvn tomcat7:redeploy -Ddeploy-to=biblicious"
        }
      }
    }
    stage ('Deploy Artifacts') {
      when {
        branch 'master'
      }
      steps {
        withMaven(maven: 'Maven 3.6.3', mavenSettingsConfig: 'bibsonomy') {
          sh "echo TODO"
        }
      }
    }
  }
}