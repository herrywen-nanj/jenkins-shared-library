/**
 * @author: herrywen
 * @Date: 2022-05-12
 */

def GetCfg(PROJECT_NAME) {
    def yaml_file = libraryResource("pipelineCfg.yaml")
    def data = readYaml file : yaml_file
    GIT_URL = data["PROJECT_NAME"]['GIT_URL']
    PLAYBOOK_PATH = data["PROJECT_NAME"]['PLAYBOOK_PATH']
    INVENTORY_PATH = data["PROJECT_NAME"]['INVENTORY_PATH']
    return GIT_URL,PLAYBOOK_PATH,INVENTORY_PATH
}




