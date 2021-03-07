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
    withMaven(maven: 'Maven 3.6.3', mavenSettingsConfig: 'bibsonomy') {
      stage ('Build') {
        steps {
          sh "mvn clean install"
        }
      }
      stage ('Deploy BibLicious Webapp') {
        when {
          branch 'master'
        }
        steps {
          dir("bibsonomy-webapp") {
            sh "mvn tomcat7:redeploy -Ddeploy-to=biblicious"
          }
        }
      }
      stage ('Deploy BibLicious Scraping Service') {
        when {
          branch 'master'
        }
        steps {
          dir("bibsonomy-scrapingservice") {
            sh "mvn tomcat7:redeploy -Ddeploy-to=biblicious"
          }
        }
      }
    }
  }
}