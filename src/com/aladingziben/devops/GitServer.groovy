/**
 * @author: herrywen
 * @Date: 2022-05-09
 */

package com.aladingziben.devops


def CheckOutCode(PROJECT_NAME,BRANCHNAME) {
    def CfgMessage = new LoadCfg()
    def (GIT_URL,PLAYBOOK_PATH,INVENTORY_PATH) = CfgMessage.GetCfg(PROJECT_NAME)
    checkout([$class                           : 'GitSCM', branches: [[name: "${BRANCHNAME}"]],
              doGenerateSubmoduleConfigurations: false,
              extensions                       : [],
              submoduleCfg                     : [],
              userRemoteConfigs                : [[credentialsId: 'gitee_account', url: "${GIT_URL}"]]])
}

def GetGitMessage() {
    dir("${env.workspace}"){
        env.GIT_COMMIT = sh(script: "git log -1 --pretty=%B | cat", returnStdout: true).trim()
    }
}
