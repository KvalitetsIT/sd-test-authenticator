pipeline {
  agent {
    kubernetes {
      defaultContainer 'docker'
      yaml """
    apiVersion: v1
    kind: Pod
    metadata:
      labels:
        some-label: some-label-value
    spec:
      containers:
      - name: docker
        image: kvalitetsit/docker:latest
        command:
        - cat
        tty: true
        volumeMounts:
          - name: docker-sock
            mountPath: /var/run/docker.sock
      volumes:
      - name: docker-sock
        hostPath:
          path: /var/run/docker.sock
      """
    }
  }

  stages {
    stage('Initialize') {
      steps{
        script {
          currentBuild.displayName = "$currentBuild.displayName-${env.GIT_COMMIT}"
        }
      }
    }
    stage('Build And Test') {
      steps {
        script {
          def maven = docker.image('maven:3-jdk-11')
          maven.pull()
          maven.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
	    sh './install-jars.sh'
            sh 'mvn install'
          }
        }
      }
    }
    
    stage('Tag Docker Images And Push') {
      steps {
        script {
          docker.withRegistry('','dockerhub') {
	    docker.image("kvalitetsit/sd-test-authenticator:${env.GIT_COMMIT}").push("${env.GIT_COMMIT}")
	    docker.image("kvalitetsit/sd-test-login-theme:${env.GIT_COMMIT}").push("${env.GIT_COMMIT}")
          }
        }
      }
    }
  }
}
