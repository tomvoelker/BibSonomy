// TODO: email notification, only build webapp and scrapingservice on success or unstable
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

    def server
    def buildInfo
    def rtMaven

    stage ('Artifactory Config') {
      server = Artifactory.server 'bibsonomy'
      rtMaven = Artifactory.newMavenBuild()
      rtMaven.tool = 'Maven 3.6.3'
      rtMaven.deployer.deployArtifacts = false // Disable artifacts deployment during Maven run
      rtMaven.deployer server: server, releaseRepo: 'bibsonomy-release', snapshotRepo: 'bibsonomy-snapshot'
      buildInfo = Artifactory.newBuildInfo()
    }
    stage ('Build') {
      steps {
        withMaven(maven: 'Maven 3.6.3', mavenSettingsConfig: 'bibsonomy') {
          buildInfo = rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo
        }
      }
    }
    stage ('Artifactory Deploy') {
      steps {
        rtMaven.deployer.deployArtifacts buildInfo
        server.publishBuildInfo buildInfo
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
  }
}