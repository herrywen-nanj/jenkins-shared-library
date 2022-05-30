/**
 * @author: herrywen
 * @Date: 2022-05-09
 */

package com.aladingziben.devops


def CheckOutCode(BRANCHNAME) {
    /*checkout([$class                           : 'GitSCM', branches: [[name: BRANCHNAME]],
              doGenerateSubmoduleConfigurations: false,
              extensions                       : [],
              submoduleCfg                     : [],
              userRemoteConfigs                : [[credentialsId: 'gitee_account', url: "${env.GIT_URL}"]]])
	
    */
    git branch: BRANCHNAME, credentialsId: 'gitee_account', url: "${GIT_URL}"
    dir("${env.workspace}"){
        env.GIT_COMMIT = sh(script: "git log -1 --pretty=%B | cat", returnStdout: true).trim()
    }
}

