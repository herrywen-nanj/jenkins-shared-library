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

def call(PROJECT_NAME) {
    def FormatPrint = new FormatPrint()
    def CfgMessage = new LoadCfg()
    def GitServer = new GitServer()
    def ansible = new ansible()
    def build = new build()
    pipline {
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

        stage('Get all variables ') {
            steps {
                script {
                    CfgMessage.GetCfg(PROJECT_NAME)
                }
            }
        }

        stage('Get build user') {
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
                script {
                    FormatPrint.PrintMes("------ 拉取代码并获取git log ------", "green")
                    GitServer.CheckOutCode("${params.BRANCH_NAME}")
                }
            }
        }
        stage('build') {
            steps {
                script {
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