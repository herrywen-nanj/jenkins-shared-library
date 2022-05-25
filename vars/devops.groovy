#!/usr/bin/env groovy
/**
 * @author: herrywen
 * @Date: 2022-05-09
 */

/**
 * @param GIT_URL, transmit GIT_URL
 * @param PRE_FIX, Java packaged pre directory,value is NULL or Real secondary directory
 * @param POINT, Used to package points,value is fronted or backend
 */
import com.aladingziben.devops.FormatPrint
import org.apache.tools.ant.Project


def call() {
    def (defaultBranchName,project_name) = "${env.JOB_BASE_NAME}".split("-")
    pipeline {
        agent any
        tools {
            nodejs 'NPM_HOME'
            maven 'MAVEN_PATH'
        }
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
                    defaultValue: defaultBranchName,
                    description: '选择分支默认，是当前环境分支',
                    name: 'BRANCH_NAME',
                    quickFilterEnabled: false,
                    selectedValue: 'DEFAULT',
                    sortMode: 'NONE',
                    tagFilter: '*',
                    type: 'GitParameterDefinition'
        }

        environment {
            WEB_PATH = "${project_name}"
            DEPLOY_PATH = "/app/" + "${WEB_PATH}"
            DEPLOY_ENVIRONMENT = "${defaultBranchName}"
            DINGTALK_CREDS = credentials('dingTalk')
        }

        stages {
            stage('获取用户名') {
                steps {
                    wrap([$class: 'BuildUser']) {
                        script {
                            env.BUILD_USER = "${env.BUILD_USER}"
                        }
                    }
                }
            }
            stage("初始化步骤") {
                steps {
                    script{
                        println("${DEPLOY_ENVIRONMENT}","${WEB_PATH}")
                        switch (DEPLOY_ENVIRONMENT) {
                            case {DEPLOY_ENVIRONMENT == "test"}:
                                TEST_ENVIRONMENT.call(${WEB_PATH})
                                break
                            case {DEPLOY_ENVIRONMENT == "prv"}:
                                PRV_ENVIRONMENT.call(WEB_PATH)
                                break
                            case {DEPLOY_ENVIRONMENT == "prod"}:
                                PROD_ENVIRONMENT.call(WEB_PATH)
                                break
                        }
                    }

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