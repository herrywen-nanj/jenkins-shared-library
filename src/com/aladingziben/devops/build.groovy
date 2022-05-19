package com.aladingziben.devops

def Build() {
    if (POINT == 'frontend') {
        dir("${env.workspace}") {
            sh 'npm install --unsafe-perm=true && npm run build:${DEPLOY_ENVIRONMENT}'
            env.ARTIFACTS_PATH = "${env.WORKSPACE}" + "/dist/"
        }
    } else if (POINT == 'backend') {
        dir("${env.workspace}") {
            sh 'mvn clean package -DskipTests -P${DEPLOY_ENVIRONMENT}'
            if (PRE_FIX != 'NULL') {
                JAR_NAME = sh(script: "ls $PRE_FIX/target/**.jar", returnStdout: true).trim()
                env.ARTIFACTS_PATH = "${env.WORKSPACE}" + "/" + "${JAR_NAME}"
            } else {
                JAR_NAME = sh(script: "ls target/**.jar", returnStdout: true).trim()
                env.ARTIFACTS_PATH = "${env.WORKSPACE}" + "/" + "${JAR_NAME}"
            }
            // echo "JAR_NAME的路径是$JAR_HOME"

        }
    }
}
