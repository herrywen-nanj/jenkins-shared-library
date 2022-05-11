#!/usr/bin/env groovy
/**
 * @author: herrywen
 * @Date: 2022-05-09
 */
import com.aladingziben.devops.FormatPrint


def call(map) {
    pipeline {
        agent any
        options {
            disableConcurrentBuilds() //禁止同时执行
            buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '10')) // 构建记录保存7天,最多保存10个构建记录
            skipDefaultCheckout()
            timeout(time: 1, unit: 'HOURS')
            timestamps()
        }


        parameters {
            gitParameter branch: '',
                    branchFilter: 'origin/(.*)',
                    defaultValue: 'prv',
                    description: '选择分支默认，是当前环境分支',
                    name: 'BRANCH_NAME',
                    quickFilterEnabled: false,
                    selectedValue: 'NONE',
                    sortMode: 'NONE',
                    tagFilter: '*',
                    type: 'GitParameterDefinition'
        }

        environment {
            GIT_URL = "${map.GIT_URL}"
            WEB_PATH = sh(script: "echo ${env.JOB_BASE_NAME}|awk -F'-' '{printf \$NF}'", returnStdout: true).trim()
            PRE_FIX = "${map.PRE_FIX}"
            DEPLOY_PATH = "/app/" + "${WEB_PATH}"
            POINT = "${map.POINT}"
            DEPLOY_ENVIRONMENT = sh(script: "echo ${env.JOB_NAME}|awk -F'/' '{printf \$1}'", returnStdout: true).trim()
            DINGTALK_CREDS = credentials('dingTalk')
        }
        stages {
            stage('Get build user'){
                steps {
                    wrap([$class: 'BuildUser']) {
                        script {
                            env.BUILD_USER = "${env.BUILD_USER}"
                        }
                    }
                }
            }
            stage('Clean up workspace') {
                steps {
                    script {
                        cleanWs()
                    }
                }
            }
            stage('checkout from scm') {
                steps {
                    git branch: "${params.BRANCH_NAME}", credentialsId: 'gitee_account', url: "${GIT_URL}"
                    script {
                        dir("${env.workspace}"){
                            env.GIT_COMMIT = sh(script: "git log -1 --pretty=%B | cat", returnStdout: true).trim()
                        }
                    }
                }
            }
            stage('build') {
                tools {
                    nodejs 'NPM_HOME'
                    maven 'MAVEN_PATH'
                }
                steps {
                    script {
                        def FormatPrint = new FormatPrint()
                        FormatPrint.PrintMes("------ 开始编译 ------","green")
                        if (POINT == 'frontend') {
                            dir("${env.workspace}"){
                                sh 'npm install --unsafe-perm=true && npm run build:${DEPLOY_ENVIRONMENT}'
                                env.ARTIFACTS_PATH = "${env.WORKSPACE}" + "/dist/"
                            }
                        }else if (POINT == 'backend'){
                            dir("${env.workspace}"){
                                sh 'mvn clean package -DskipTests -P${DEPLOY_ENVIRONMENT}'
                                if (PRE_FIX != 'NULL'){
                                    JAR_NAME = sh(script: "ls $PRE_FIX/target/**.jar", returnStdout: true).trim()
                                    env.ARTIFACTS_PATH = "${env.WORKSPACE}" + "/" + "${JAR_NAME}"
                                }else {
                                    JAR_NAME = sh(script: "ls target/**.jar", returnStdout: true).trim()
                                    env.ARTIFACTS_PATH = "${env.WORKSPACE}" + "/" + "${JAR_NAME}"
                                }
                                // echo "JAR_NAME的路径是$JAR_HOME"

                            }
                        }
                    }
                }
            }
            stage('Deploy') {
                when {
                    expression {
                        currentBuild.result == null || currentBuild.result == 'SUCCESS'
                    }
                }
                steps {
                    ansiblePlaybook (
                            installation: "ansible",
                            disableHostKeyChecking: true,
                            //以颜色显示运行状态 colorized: true,
                            playbook: "/opt/ansible/deploy.yml",
                            inventory: "/etc/ansible/hosts",
                            forks: 5,
                            extraVars: [
                                    POINT: "${POINT}",
                                    DEPLOY_ENVIRONMENT: "${DEPLOY_ENVIRONMENT}",
                                    ARTIFACTS_PATH: [value: "${ARTIFACTS_PATH}", hidden: true],
                                    DEPLOY_PATH: [value: "${DEPLOY_PATH}", hidden: true]
                            ]
                    )
                }
            }
        }
        post {
            success {
                echo 'Congratulations!'
                sh """
                curl 'https://oapi.dingtalk.com/robot/send?access_token=${DINGTALK_CREDS_PSW}' \
                    -H 'Content-Type: application/json' \
                    -d '{
                        "msgtype": "markdown",
                        "markdown": {
                            "title":"发布详情",
                            "text": "😄👍 构建成功 👍😄  \n**阿拉丁项目名称**：${env.JOB_BASE_NAME}  \n**构建用户**: ${BUILD_USER}   \n**Git log**: ${env.GIT_COMMIT}   \n**构建分支**: ${params.BRANCH_NAME}   \n**构建地址**：${RUN_DISPLAY_URL}"
                        }
                    }'
            """
            }
            failure {
                echo 'Oh no!'
                sh """
                curl 'https://oapi.dingtalk.com/robot/send?access_token=${DINGTALK_CREDS_PSW}' \
                    -H 'Content-Type: application/json' \
                    -d '{
                        "msgtype": "markdown",
                        "markdown": {
                            "title":"发布详情",
                            "text": "😖❌ 构建失败 ❌😖  \n**阿拉丁项目名称**：${env.JOB_BASE_NAME}  \n**构建用户**: ${BUILD_USER}   \n**Git log**: ${env.GIT_COMMIT}   \n**构建分支**: ${params.BRANCH_NAME}  \n**构建地址**：${RUN_DISPLAY_URL}"
                        }
                    }'
            """
            }
            always {
                echo 'clean your workspace when build is done!'
            }
            cleanup {
                dir("${env.workspace}") {deleteDir()}
                dir("${env.workspace}@tmp") {deleteDir()}
            }
        }
    }
}