def (defaultBranchName, project_name) = "${env.JOB_BASE_NAME}".split("-")
def call() {
pipeline {
    agent any
    stages {
        stage ('read') {
            steps {
                script {
                    def yaml_file = libraryResource 'com/aladingziben/devops/PiplineCfg.yaml'
                    //def data = readYaml file : '/opt/PiplineCfg.yaml'
                    def data = readYaml txt : yaml_file
                    println data.get(defaultBranchName).get(project_name).get('PRE_FIX')
               }
            }
        }
    }
}
}
