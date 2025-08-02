pipeline {
    agent any
    options {
        timeout(time: 15, unit: 'MINUTES')
        retry(2) // Retry failed builds once
    }
    stages {

        stage('Verify Docker') {
    steps {
        script {
            try {
                sh 'docker ps'
                echo "✅ Docker access verified"
            } catch (err) {
                error("❌ Docker not accessible. Check volume mounts.")
            }
        }
    }
}
        stage('Checkout Code') {
            steps {
                checkout scm: [
                    $class: 'GitSCM',
                    branches: [[name: 'main']],
                    extensions: [[
                        $class: 'CleanBeforeCheckout' // Fresh workspace each time
                    ]],
                    userRemoteConfigs: [[
                        url: 'https://github.com/ChiragRohada7020/Smart-Shopping-.git'
                    ]]
                ]
            }
        }

        

        stage('Deploy') {
            steps {
                script {
                    // Force-clean previous deployment
                    sh 'docker-compose -f docker-compose.jenkins.yml down --remove-orphans || true'
                    
                    // Build and launch with force-recreate
                    sh 'docker-compose -f docker-compose.jenkins.yml up -d --build --force-recreate'
                    
                    // Health check
                    def running = sh(
                        script: 'docker-compose -f docker-compose.jenkins.yml ps -q | wc -l',
                        returnStdout: true
                    ).trim().toInteger()
                    
                    if (running < 2) { // Adjust number based on expected services
                        error("Deployment failed - ${running} containers running")
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Clean Docker cache to prevent disk bloat
            sh 'docker system prune -f --filter "until=24h"'
            cleanWs()
        }
        success {
            slackSend color: 'good',
                     message: "SUCCESS: ${env.JOB_NAME} build ${env.BUILD_NUMBER}"
        }
        failure {
            slackSend color: 'danger',
                     message: "FAILED: ${env.JOB_NAME} build ${env.BUILD_NUMBER}"
        }
    }
}