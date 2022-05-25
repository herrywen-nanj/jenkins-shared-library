/**
 * @author: herrywen
 * @Date: 2022-05-12
 */

package com.aladingziben.devops


def GetCfg(DEPLOY_ENVIRONMENT,PROJECT_NAME) {
    def yaml_file = libraryResource("PiplineCfg.yaml")
    def data = readYaml file : yaml_file
    env.GIT_URL = data["DEPLOY_ENVIRONMENT"]["PROJECT_NAME"]['GIT_URL']
    env.PRE_FIX = data["DEPLOY_ENVIRONMENT"]["PROJECT_NAME"]['POINT']
    env.POINT = data["DEPLOY_ENVIRONMENT"]["PROJECT_NAME"]['PRE_FIX']
    env.PLAYBOOK_PATH = data["DEPLOY_ENVIRONMENT"]["PROJECT_NAME"]['PLAYBOOK_PATH']
    env.INVENTORY_PATH = data["DEPLOY_ENVIRONMENT"]["PROJECT_NAME"]['INVENTORY_PATH']
    // env.JVM_OPS = data["DEPLOY_ENVIRONMENT"]["PROJECT_NAME"]['JVM_OPS']
}




