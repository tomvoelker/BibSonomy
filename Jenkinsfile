// TODO: only build webapp and scrapingservice on success or unstable
pipeline {
  agent any
  triggers {
    pollSCM 'H/10 * * * *'
  }
  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '15', artifactNumToKeepStr: '15'))
    timeout(time: 2, unit: 'HOURS')
  }
  stages {
    stage ('Artifactory Config') {
      when {
        branch 'master'
      }
      steps {
          rtServer (
              id: "bibsonomy"
          )

          rtMavenDeployer (
              id: "MAVEN_DEPLOYER",
              serverId: "bibsonomy",
              releaseRepo: "bibsonomy-release",
              snapshotRepo: "bibsonomy-snapshot"
          )
      }
    }
    stage ('Build') {
      steps {
        script {
          if (env.BRANCH_NAME == 'master') {
            configFileProvider(
               [configFile(fileId: 'bibsonomy', variable: 'MAVEN_SETTINGS')]) {

               rtMavenRun (
                   tool: 'Maven 3.6.3',
                   pom: 'pom.xml',
                   goals: 'clean install -s $MAVEN_SETTINGS',
                   deployerId: "MAVEN_DEPLOYER"
               )
            }
          } else {
            withMaven(maven: 'Maven 3.6.3', mavenSettingsConfig: 'bibsonomy') {
              sh "mvn clean install"
            }
          }
        }
      }
      post {
        always {
          archive "**/target/**/*"
          junit '**/target/surefire-reports/*.xml'
        }
        changed {
          emailext attachLog: true, body: '${DEFAULT_CONTENT}', compressLog: true, subject: '${DEFAULT_SUBJECT}', to: 'bibsonomy2-devel@cs.uni-kassel.de'
        }
      }
    }
    stage ('Artifactory Deploy') {
      when {
        branch 'master'
      }
      steps {
        rtPublishBuildInfo (
          serverId: "bibsonomy"
        )
      }
    }
    stage ('Trigger PUMA build') {
      when {
        branch 'master'
      }
      steps {
        build wait: false, job: 'puma/master'
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