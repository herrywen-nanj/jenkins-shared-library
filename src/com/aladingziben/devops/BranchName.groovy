/**
 * @author: herrywen
 * @Date: 2022-05-31
 */

package com.aladingziben.devops


def GetDefaultBranchName(String Environment_Prefix) {
    this.Environment_Prefix = Environment_Prefix 
	if (this.Environment_Prefix == 'prod') {
		env.defaultBranchName = "master"
	}  else {
		env.defaultBranchName = "${this.Environment_Prefix}"             
	}
	// return "${this.defaultBranchName}"
}

















