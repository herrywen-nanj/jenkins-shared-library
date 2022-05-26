//def (defaultBranchName, project_name) = "${env.JOB_BASE_NAME}".split("-")
def call() {
def (defaultBranchName, project_name) = "${env.JOB_BASE_NAME}".split("-")

pipeline {
    agent any
    stages {
        stage ('read') {
            steps {
                script {
		    println("$defaultBranchName")
                    // def yaml_file = libraryResource('PiplineCfg.yaml')
                    def data = readYaml file : '/opt/PiplineCfg.yaml'
		    sh "echo $pwd && ls -l"
                    //def data = readYaml txt : 'PiplineCfg.yaml'
                    println data.get(defaultBranchName).get(project_name).get('PRE_FIX')
               }
            }
        }
    }
}
}
