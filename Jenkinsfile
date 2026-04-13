
        }


        // ============ STAGE 6: DEPLOY TO STAGING (release branches) ============
        stage('Deploy to Staging') {
            when {
                expression { return (IS_RELEASE?.toBoolean()) }
            }
            agent {
                kubernetes {
                    yaml '''
apiVersion: v1
kind: Pod
spec:
  nodeSelector:
    role: secondary
  containers:
  - name: jnlp
    image: jenkins/inbound-agent:latest
    args: ["$(JENKINS_SECRET)", "$(JENKINS_NAME)"]
  - name: kubectl
    image: bitnami/kubectl:latest
    command: ["sleep"]
    args: ["infinity"]
  restartPolicy: Never
'''
                }
            }
            steps {
                container('kubectl') {
                    script {
                        def releaseVersion = env.BRANCH_NAME.replace('release/', '')
                        def imageTag = env.IMAGE_TAG

                        sh """
                            kubectl apply -f deployment.yaml
                            kubectl set image deployment/${APP_NAME} \
                              ${APP_NAME}=${DOCKER_HUB_REPO}:${IMAGE_TAG} \
                              -n ${STAGING_NAMESPACE}

                            kubectl rollout status deployment/${APP_NAME} -n ${STAGING_NAMESPACE}
                        """
                    }
                }
            }
        }

        /* stage('Approve Production Deploy') {
            when {
                expression { return (IS_MAIN?.toBoolean()) }
            }
            steps {
                input message: 'Deploy to Production?', ok: 'Deploy'
            }
        } */


        // ============ STAGE 7: DEPLOY TO PRODUCTION (main branch with approval) ============
        stage('Deploy to Production') {
            when {
                expression { return (IS_MAIN?.toBoolean()) }
            }
            agent {
                kubernetes {
                    yaml '''
apiVersion: v1
kind: Pod
spec:
  nodeSelector:
    role: secondary
  containers:
  - name: jnlp
    image: jenkins/inbound-agent:latest
    args: ["$(JENKINS_SECRET)", "$(JENKINS_NAME)"]
  - name: kubectl
    image: alpine/k8s:1.29.2
    command:
    - cat
    tty: true
  restartPolicy: Never
'''
                }
            }
            steps {

                container('kubectl') {
                    script {
                        def pomVersion = readMavenPom().version
                        def imageTag = env.IMAGE_TAG ?: pomVersion
                        echo "deploy to production with image with tag: ${imageTag} and pomVersion: ${pomVersion}"
                        sh """
                            # Create production namespace if not exists
                            kubectl create namespace ${PRODUCTION_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -

                            # Deploy to production
                            kubectl apply -f k8s/staging/deployment.yaml

                            # Wait for rollout
                            kubectl rollout status deployment/${APP_NAME} -n ${PRODUCTION_NAMESPACE} --timeout=5m

                            # Verify deployment
                            kubectl get pods -n ${PRODUCTION_NAMESPACE}
                        """
                    }
                }
            }
        }

        // ============ STAGE 8: CREATE GIT TAG (main branch) ============
        stage('Create Git Tag') {
            when {
                expression { return (IS_MAIN?.toBoolean()) }
            }
            steps {
                script {
                    def pomVersion = readMavenPom().version
                    withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                        sh """
                            git config user.email "jenkins@homelab.local"
                            git config user.name "Jenkins CI"
                            git tag -a v${pomVersion} -m "Release version ${pomVersion}"
                            git push https://skariajampi:\${GITHUB_TOKEN}@github.com/skariajampi/sample-gitflow-app.git v${pomVersion}
                            echo "Created tag: v${pomVersion}"
                        """
                    }
                }
            }
        }

        // ============ STAGE 9: CLEANUP (feature branches after merge) ============
        stage('Cleanup') {
            when {
                expression { return (IS_FEATURE?.toBoolean()) }
            }
            steps {
                echo "Feature branch ${env.BRANCH_NAME} build completed successfully."
                echo "This branch will be cleaned up after merge to develop."

                script {
                    // Optional: Add logic to delete feature branch from remote after merge
                    // This would typically be done manually after PR is merged
                    echo "No cleanup logic yet"
                }
            }
        }
    }

    post {
        success {
            echo "=========================================="
            echo "Pipeline completed successfully!"
            echo "Branch: ${env.BRANCH_NAME}"
            echo "Build: ${env.BUILD_URL}"
            echo "=========================================="
        }
        failure {
            echo "=========================================="
            echo "Pipeline failed!"
            echo "Branch: ${env.BRANCH_NAME}"
            echo "Build: ${env.BUILD_URL}"
            echo "=========================================="

            // Optional: Send email notification
            // emailext (
            //     subject: "Pipeline Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
            //     body: "The pipeline failed. Check console output at ${env.BUILD_URL}",
            //     to: 'team@example.com'
            // )
        }
        always {
            // Clean up workspace
            cleanWs()
        }
    }
}