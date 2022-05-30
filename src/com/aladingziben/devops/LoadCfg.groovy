/**
 * @author: herrywen
 * @Date: 2022-05-12
 */

package com.aladingziben.devops


def GetCfg(DEPLOY_ENVIRONMENT,PROJECT_NAME) {
    def yaml_file = libraryResource('PiplineCfg.yaml')
    def data = readYaml text : yaml_file
    this.GIT_URL = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('GIT_URL')
    this.PRE_FIX = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('PRE_FIX')
    this.POINT = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('POINT')
    this.INVENTORY_PATH = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('INVENTORY_PATH')
    this.PLAYBOOK_PATH = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('PLAYBOOK_PATH')
    this.JVM_OPS = data.get(DEPLOY_ENVIRONMENT).get(PROJECT_NAME).get('JVM_OPS')
}

return this




