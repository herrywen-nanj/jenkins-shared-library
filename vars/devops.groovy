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
            // 禁止同时运行多个流水线
            disableConcurrentBuilds()
        }
        environment {
            examples_var1 = sh(script: 'echo "当前的时间是: `date`"', returnStdout: true).trim()
        }
        stages{
            stage("声明式流水线: 拉一个代码") {
                steps {
                    git url: "${map.git_url}"
                }
            }
            stage("声明式流水线: 执行一个命令"){
                steps {
                    script {
                        /**
                        def tools = new FormatPrint()
                        tools.PrintMes("获取代码",'green')
                        */
                        def tools=new PrintMes()
                        PrintMes("获取命令内容","green")
                        sh "${map.cmd}"
                    }
                }
            }
            stage("声明式流水线: 打印现在的时间"){
                steps {    
                        println "${map.input_id}" 
                        println "${map.input_msg}"
                }
            }
        }
    }
}