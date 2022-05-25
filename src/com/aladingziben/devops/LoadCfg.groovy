/**
 * @author: herrywen
 * @Date: 2022-05-12
 */

package com.aladingziben.devops


def GetCfg(PROJECT_NAME) {
    def yaml_file = libraryResource("PiplineCfg.yaml")
    def data = readYaml file : yaml_file
    env.GIT_URL = data["PROJECT_NAME"]['GIT_URL']
    env.PRE_FIX = data["PROJECT_NAME"]['POINT']
    env.POINT = data["PROJECT_NAME"]['PRE_FIX']
    env.PLAYBOOK_PATH = data["PROJECT_NAME"]['PLAYBOOK_PATH']
    env.INVENTORY_PATH = data["PROJECT_NAME"]['INVENTORY_PATH']
    env.JVM_OPS = data["PROJECT_NAME"]['JVM_OPS']
}




