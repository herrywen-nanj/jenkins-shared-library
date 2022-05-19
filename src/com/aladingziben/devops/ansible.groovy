package com.aladingziben.devops

def deploy(){
    ansiblePlaybook (
            installation: "ansible",
            disableHostKeyChecking: true,
            //以颜色显示运行状态 colorized: true,
            playbook: "${env.PLAYBOOK_PATH}",
            inventory: "${env.INVENTORY_PATH}",
            forks: 5,
            extraVars: [
                    POINT: "${env.POINT}",
                    DEPLOY_ENVIRONMENT: "${DEPLOY_ENVIRONMENT}",
                    ARTIFACTS_PATH: [value: "${ARTIFACTS_PATH}", hidden: true],
                    DEPLOY_PATH: [value: "${DEPLOY_PATH}", hidden: true]
            ]
    )
}

