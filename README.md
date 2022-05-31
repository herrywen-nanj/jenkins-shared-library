# Function
根据构建参数与ansible执行部署并隐藏你的部署细节

# Detailed Description
* @param GIT_URL, transmit GIT_URL
* @param PRE_FIX, Java packaged pre directory,value is NULL or Real secondary directory
* @param POINT, Used to package points,value is fronted or backend
* @param DEPLOY_PRE_PATH, The front directory for deployment
* @param INVENTORY_PATH, Ansible configuration file
* @param PLAYBOOK_PATH,  Ansible execution script
将以上参数通过传入project_name方式进行查询，并依次传入至各个部署逻辑中去，无需二次声明变量


 # Usage
 创建流水线后，增加jenkins-shared-library的git地址，其他默认即可，如下图：
![输入图片说明](https://images.gitee.com/uploads/images/2022/0531/101435_ebff0585_10349853.png "11.png")

 新增项目，只需要在resources/PiplineCfg.yaml内添加对应配置即可

