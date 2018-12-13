# GitFlow SemVer Gradle Plugin

This is an opinionated Gradle plugin that enables Semantic Versioning (2.0) for projects that follow GitFlow branching model. With this plugin, you can do semantic versioning for your Gradle projects with minimum configuration, as it uses an opinionated versioning model. This plugin uses Git Tags to track the version numbers, so it does not require maintaining separate files.

## Versioning Model

Based on the GitFlow model,  versioning is done as follows.

### Master Branch
 - `master` branch contains the production releases. Each production release should be tagged (annotated tags) in Git using the pattern `maj.min.patch` (ex. 1.0.0).
 - `master` branch should not have any direct commits other than tagged releases.
 - The version of `master` branch will be derived from the most recent tag. For example, if the last tag is `1.0.1`, when a Gradle build gets executed on the master branch, `project.version` property will be set to `1.0.1`.

### Release Branches
- Version of the release branches will be determined based on the branch name. For example, if the name of the release branch is `release/1.1.0`, then the *base version* for that release branch will be `1.1.0`.  This will be suffixed with a pre-release identifier named `rc` which stands for 'release candidate'.
- The pre-release suffix will include the text `rc` followed by a number, which indicates the number of commits that happened in that release branch since its creation (base lined out of develop branch) + 1.  
	- That is, when the release branch is created, it will have the pre-release suffix as `rc.1`. The number is 1 because number of commits since branching is zero (0 + 1).
	- When the first commit is done in the release branch, the suffix will be `rc.2` (number of commits = 1, so its 1 + 1 = 2).
- So the effective version after the above examples would be `1.1.0-rc.2`.

### Hotfix Branches

 - Hotfix branches follow the same model as the release branches. The suffix will be `rc` similar to release branches, since any build made out of the hotfix branch will also be a release candidate. 
 - As a convention, the branch names in hotfix branch should increment the `patch` segment of the version number.
 - Hotfix branches will be based lined out of the `master` branch to calculate the `rc` number.
 - As an example, if the name of the hotfix branch is `hotfix/1.1.1` and we have made 2 commits after branching out, the version number would be `1.1.1-rc.3`. 

### Develop Branch

 - Integration builds will be taken out of the develop branch. So the builds of this branch will have the pre-release suffix of `alpha`.
 - The base version for this branch will be determined as follows.
	 - Last released version will be identified by scanning for the most recent git tag. For example, if our last release was `1.2.5`, then there will be a Git tag in our repo with that number.
	 - The `minor` segment of the last released version will be incremented by 1. The `patch` segment of the last released version will be reset to 0.
	 - This gives us the base version of the development branch as `1.3.0`.
 - The base version will then be suffixed with the `alpha` pre-release tag, along with a number that is the number of commits on development branch since the last release tag was made + 1. 
	 - If there are no commits since the last tag, then the suffix would be `alpha.1`.
	 - If there are two commits since the last tag, it would be `alpha.3`.
 - Following the above example of last release version `1.2.5`, if we have 4 commits made on the development branch since then, our effective version would be `1.3.0-alpha.5`.

### Feature / Bug Fix / Etc
In most cases, these branches will not be deployed as is except for local testing. However calculating the unique version of such branches is not straight forward. As a work-around to this, this plugin relies on a calculated unique branch identifier that is SemVer safe. The versioning strategy in this case would be,
- The base version will be calculated as same as the `develop` branch.
- The pre-release version tag will be `feat`,`bugfix` or `unknown` in all other cases.  
- This will be suffixed with a unique branch identifier that will be calculated based on the branch name (ex. `l3jpwte7ehbenbh8lteyrn8`).
- After the branch id, an integer will indicate the number of commits made to the branch since develop branch. 
- Ex. `0.2.0-feat-l3jpwte7ehbenbh8lteyrn8.1`

## How To Use This Plugin


> **Pre-Requisites:** You need to have **at least one annotated tag** indicating the last released version / base line version of the project. This will be used to calculate the potential next version number. In case of a fresh project, just create a tag with the value `0.0.0`. For example: `git tag -a 0.0.0 -m "Base Version"`


### Quick Start

 1. Refer to the plugin in the `plugins` section of the Gradle build script as follows. 
```
plugins {     
  id 'com.limark.gitflowsemver' version '0.1.0'   
}
```
 2. That's it! When you run the next build, it will determine the next version automatically based on the versioning model outlined above and the plugin will update the `project.version` property in the Gradle script.
	 * You can refer to this property from anywhere within your Gradle script to take any action.
	 * If you would like to print the version number, run the `printVersion` task as follows. 
		 * Ex. `./gradlew printVersion`
	 * The plugin also adds a task for writing a property file in the `buildDir` of the project with the version number under `version` property name. This is useful in cases where you want to read the version as part of the CI pipeline. For example, we use this in Jenkins pipelines. 
		 * Ex. `./gradlew writeVersion`

### Customizing The Plugin

The behavior of the plugin can be customized by adding a configuration block to your Gradle build script as follows.

```
versionConfig {  
  alphaLabel = "pre"  
  rcLabel = "beta"  
  gitDescribeMatchRule = "*[0-9].[0-9]*.[0-9]*"  
  propertiesFile = "${project.buildDir}/META-INF/version.properties"  
}
```
Above configuration does the following changes,
* Instead of using the `alpha` pre-release identifier, plugin will use `pre`. For example, `1.1.0-pre.1`.
* Instead of the `rc` identifier for release versions / hotfix versions, plugin will use `beta`.
* The `gitDescribeMatchRule` allows you to customize how a release tag will be identified.
* Using the `propertiesFile`, you can change the location where the `version.properties` file will be created. By default, it gets written to `${project.buildDir}/version.properties`. In the above example, it is changed to be inside the `META-INF` directory.

## Integrations / Compatibility

### GitFlow AVH 
This plugin is fully compatible and tested with [GitFlow AVH Plugin](https://github.com/petervanderdoes/gitflow-avh) for Git.