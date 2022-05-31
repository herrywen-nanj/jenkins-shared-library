def GetDefaultBranchName(String env) {
		if (env == 'prod') {
			defaultBranchName = "master"
		}  else {
			defaultBranchName = "${env}"               
		}
		return defaultBranchName
}