/**
 * @author: herrywen
 * @Date: 2022-05-12
 */

package com.aladingziben.devops


def GetCfg(DEPLOY_ENVIRONMENT,PROJECT_NAME) {
    def yaml_file = libraryResource('PiplineCfg.yaml')
    def data = readYaml text : yaml_file
    env.GIT_URL = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('GIT_URL')
    env.PRE_FIX = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('PRE_FIX')
    env.POINT = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('POINT')
    env.INVENTORY_PATH = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('INVENTORY_PATH')
    env.PLAYBOOK_PATH = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('PLAYBOOK_PATH')
    env.JVM_OPS = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('JVM_OPS')
}

return this




