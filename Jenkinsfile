pipeline {
  agent {
    node {
      label 'maven'
    }

  }
   parameters {
        string(name: 'PROJECT_VERSION',defaultValue: 'v0.0Beta', description:'项目版本')
        string(name: 'PROJECT_NAME',defaultValue: 'mall-auth-server', description:'构建模块')
    }
    
   environment {
        DOCKER_CREDENTIAL_ID = 'dockerhub-id'
        GITEE_CREDENTIAL_ID = 'gitee-id'
        KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
        REGISTRY = 'docker.io'
        DOCKERHUB_NAMESPACE = 'yimuziy'
        GITEE_ACCOUNT = 'yimuziy'
        SONAR_CREDENTIAL_ID = 'sonar-qube'
    }
  
  stages {
    stage('拉取代码') {
      steps {
        git(credentialsId: 'gitee-id', url: 'https://gitee.com/yimuziy/yimuziymall.git', branch: 'master', changelog: true, poll: false)
        sh 'echo 正在构建 $PROJECT_NAME  版本号：$PROJECT_VERSION 将会提交给 $REGISTRY 镜像仓库'
        container ('maven'){
             sh "echo 正在完整编译项目"
             sh " echo mvn clean install -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml"
        }
      }
      
    }
    
    stage ('构建镜像 & 推送镜像') {
            steps {
                container ('maven') {
                    sh 'mvn -o -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml clean package'
                    sh 'cd $PROJECT_NAME && docker build --no-cache -f Dockerfile -t $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER .'
                    withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                        sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                        sh 'docker tag  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:latest '
                        sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:latest '
                    }
                }
            }
        }
        
     stage('部署到k8s') {
          when{
            branch 'master'
          }
          steps {
            input(id: "deploy-to-dev-$PROJECT_NAME", message: "是否将$PROJECT_NAME 部署到集群中？")
            kubernetesDeploy(configs: "$PROJECT_NAME/deploy/**", enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }
        
    stage('发布版本'){
          when{
            expression{
              return params.PROJECT_VERSION =~ /v.*/
            }
          }
          steps {
            container ('maven') {
                input(id: 'release-image-with-tag', message: '发布当前版本镜像吗?')
                sh 'docker tag  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
                  withCredentials([usernamePassword(credentialsId: "$GITEE_CREDENTIAL_ID", passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    sh 'git config --global user.email "yimuziy@163.com" '
                    sh 'git config --global user.name "yimuziy" '
                    sh 'git tag -a $PROJECT_VERSION -m "$PROJECT_VERSION" '
                    sh 'git push http://$GIT_USERNAME:$GIT_PASSWORD@gitee.com/$GITEE_ACCOUNT/yimuziymall.git --tags --ipv4'
                  }
                
            }
          }
      }

  }
}
