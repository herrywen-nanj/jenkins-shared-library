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
import com.aladingziben.devops.LoadCfg
import com.aladingziben.devops.GitServer
import com.aladingziben.devops.ansible
import com.aladingziben.devops.build



def call() {
    def FormatPrint = new FormatPrint()
    def CfgMessage = new LoadCfg()
    def GitServer = new GitServer()
    def ansible = new ansible()
    def build = new build()
    def (defaultBranchName, project_name) = "${env.JOB_BASE_NAME}".split("-")
    //def GIT_URL = CfgMessage.GetCfg(defaultBranchName,project_name).GIT_URL
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
        

	/*
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
	*/


	parameters {
            gitParameter (
                    defaultValue: defaultBranchName,
                    branchFilter: 'origin/(.*)',
                    name: 'BRANCH_NAME',
                    quickFilterEnabled: false,
                    selectedValue: 'DEFAULT',
                    sortMode: 'DESCENDING_SMART',
                    tagFilter: '*',
                    type: 'GitParameterDefinition',
                    description: '选择分支默认，是当前环境分支',
		    useRepository: 'https://gitee.com/mdald/pre.git'
            )
        }

        environment {
            WEB_PATH = "${project_name}"
            DEPLOY_PATH = "/app/" + "${WEB_PATH}"
            DEPLOY_ENVIRONMENT = "${defaultBranchName}"
            DINGTALK_CREDS = credentials('dingTalk')
	    //GIT_URL = CfgMessage.GetCfg(defaultBranchName,project_name).GIT_URL
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
            stage('Get all variables ') {
                steps {
                    script {
                        println("---------------$defaultBranchName--------------------")
                        def GIT_URL = CfgMessage.GetCfg(defaultBranchName,project_name).GIT_URL
			println("---------------$GIT_URL------------------------------")
			CfgMessage.GetCfg(defaultBranchName,project_name)
                    }
                }
            }


	    stage('Print all variables ') {
                steps {
                    script {
                        println("---------------${GIT_URL}---------------------------")
                        println("---------------${INVENTORY_PATH}---------------------------")
                        println("---------------${params.BRANCH_NAME}---------------------------")
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
                    script {
                        FormatPrint.PrintMes("------ 拉取代码并获取git log ------", "green")
                        GitServer.CheckOutCode("${params.BRANCH_NAME}")
                    }
                }
            }
            stage('build') {
                steps {
                    script {
                        FormatPrint.PrintMes("------ 正在打包 ------", "green")
                        build.Build()
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
                    script {
                        ansible.deploy()
                    }
                }
            }
        }
    }
}
        /*
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
    */
