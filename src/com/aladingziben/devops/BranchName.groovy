/**
 * @author: herrywen
 * @Date: 2022-05-31
 */

package com.aladingziben.devops


def GetDefaultBranchName(String Environment_Prefix) {
        this.Environment_Prefix = Environment_Prefix 
	if (this.Environment_Prefix == 'prod') {
		this.defaultBranchName = "master"
	}  else {
		this.defaultBranchName = "${this.Environment_Prefix}"             
	}
	return "${this.defaultBranchName}"
}

















