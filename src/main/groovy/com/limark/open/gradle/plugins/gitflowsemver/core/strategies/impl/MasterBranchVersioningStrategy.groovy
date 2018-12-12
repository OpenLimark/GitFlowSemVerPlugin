package com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl


import com.limark.open.gradle.plugins.gitflowsemver.core.GitClient
import com.limark.open.gradle.plugins.gitflowsemver.core.exceptions.NonComplianceException
import com.limark.open.gradle.plugins.gitflowsemver.core.model.Version
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.VersioningStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MasterBranchVersioningStrategy implements VersioningStrategy {

  private static final Logger log = LoggerFactory.getLogger(this.getClass())

  private GitClient gitClient

  MasterBranchVersioningStrategy(GitClient gitClient) {
    this.gitClient = gitClient
  }

  @Override
  boolean supports(String branch) {
    return "master" == branch
  }

  @Override
  Version resolve() {
    def tag = gitClient.describeExactMatch()

    if (tag.length() == 0) {
      // Master branch should not have any commits post a tag - in which case the tag should have a value.
      log.error("Compliance error - Master branch should not have commits after the last tag or no last tag found")
      throw new NonComplianceException("Master branch should not have commits after the last tag or no last tag found")    }

    return Version.parse(tag)
  }
}
