pipeline {
  agent { label "android" }

  stages {
    stage("File Setup") {
      steps {
        sh "mv .jenkins/ci-local.properties local.properties"
        sh "docker create -t -i -v $WORKSPACE:/opt/project-android --name jd-container simplymadeapps/docker-android:1.0.0"
        sh "docker start jd-container"
      }
    }
    
    //We are running as a circleci user and not as root so we have to add sudo to everything
    
    stage("Tests") {
      steps {
        sh "docker exec jd-container sudo ./gradlew clean"
        sh "docker exec jd-container sudo ./gradlew createOfflineTestCoverageReport"
      }
   	}
    
    stage("Coverage") {
      steps {
        sh "docker exec jd-container sudo ./gradlew jacocoTestCoverageVerification -x test"
        archiveArtifacts 'app/build/test-results/jacocoHtml/**/*.*'
      }
    }
  }

  post {
    cleanup {
      sh 'docker rm -f $(docker ps -a -q)' // remove docker containers
      sh 'docker rmi -f $(docker images -a -q)' // remove docker images
      sh 'sudo rm -rf .gradle build app/build'
      deleteDir()
    }
    
    failure {
      sh 'git log --format="%an -> %s" | head -1 > commit-author.txt'
      mail body: "<h2>Jenkins Build Failure</h2>Build Number: ${env.BUILD_NUMBER}<br>Commit: ${readFile('commit-author.txt').trim()}<br>Branch: ${env.GIT_BRANCH}<br>Build URL: ${env.JENKINS_URL}/blue/organizations/jenkins/${env.JOB_NAME.minus(env.GIT_BRANCH)}detail/${env.GIT_BRANCH}/${env.BUILD_NUMBER}/pipeline",
           charset: 'UTF-8',
           from: 'notice@simpleinout.com',
           mimeType: 'text/html',
           subject: "Jenkins Build Failure: ${env.JOB_NAME}",
           to: "contact@simplymadeapps.com";
    }
  }
}