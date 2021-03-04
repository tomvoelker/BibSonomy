// TODO: email notification, artifactory, only build webapp and scrapingservice on success or unstable
pipeline {
  agent any
  triggers {
    pollSCM 'H/10 * * * *'
  }
  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '15', artifactNumToKeepStr: '15'))
  }
  stages {
    stage ('Build') {
      steps {
        withMaven(maven: 'Maven 3.6.3', mavenSettingsConfig: 'bibsonomy') {
          sh "mvn clean install"
        }
      }
    }
    stage ('Deploy BibLicious Webapp') {
      when {
        branch 'master'
      }
      steps {
        dir("bibsonomy-webapp") {
            withMaven(maven: 'Maven 3.6.3', mavenSettingsConfig: 'bibsonomy') {
              sh "mvn tomcat7:redeploy -Ddeploy-to=biblicious"
            }
        }
      }
    }
    stage ('Deploy BibLicious Scraping Service') {
      when {
        branch 'master'
      }
      steps {
        dir("bibsonomy-scrapingservice") {
            withMaven(maven: 'Maven 3.6.3', mavenSettingsConfig: 'bibsonomy') {
              sh "mvn tomcat7:redeploy -Ddeploy-to=biblicious"
            }
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