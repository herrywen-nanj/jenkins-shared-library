/**
 * @author: herrywen
 * @Date: 2022-05-31
 */

package com.aladingziben.devops


def GetDefaultBranchName(String env) {
		if (env == 'prod') {
			defaultBranchName = "master"
		}  else {
			defaultBranchName = "${env}"               
		}
		return defaultBranchName
}