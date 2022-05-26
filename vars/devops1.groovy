//def (defaultBranchName, project_name) = "${env.JOB_BASE_NAME}".split("-")
def call() {
def (defaultBranchName, project_name) = "${env.JOB_BASE_NAME}".split("-")

pipeline {
    agent any
    stages {
        stage ('read') {
            steps {
                script {
                    def yaml_file = libraryResource('PiplineCfg.yaml')
                    def data = readYaml text : yaml_file
                    println data.get(defaultBranchName).get(project_name).get('PRE_FIX')
               }
            }
        }
    }
}
}
