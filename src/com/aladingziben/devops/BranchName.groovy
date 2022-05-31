/**
 * @author: herrywen
 * @Date: 2022-05-31
 */

package com.aladingziben.devops


def GetDefaultBranchName(String Environment_Prefix) {
        this.Environment_Prefix = Environment_Prefix 
		if (env == 'prod') {
			def defaultBranchName = "master"
		}  else {
			def defaultBranchName = "${this.Environment_Prefix}"             
		}
		return defaultBranchName
}