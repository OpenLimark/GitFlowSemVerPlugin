package com.limark.open.gradle.plugins.gitflowsemver.core

import com.limark.open.gradle.plugins.gitflowsemver.config.PluginConfig
import com.limark.open.gradle.plugins.gitflowsemver.core.exceptions.NonComplianceException
import com.limark.open.gradle.plugins.gitflowsemver.core.model.Version
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.VersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.DevelopBranchVersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.HotfixBranchVersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.MasterBranchVersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.ReleaseBranchVersioningStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Versioner {

  private static final Logger log = LoggerFactory.getLogger(this.getClass())

  GitClient gitClient
  List<VersioningStrategy> versioningStrategies

  Versioner(GitClient gitClient, PluginConfig config) {
    this.gitClient = gitClient
    this.versioningStrategies = [
        new MasterBranchVersioningStrategy(gitClient),
        new ReleaseBranchVersioningStrategy(gitClient, config),
        new HotfixBranchVersioningStrategy(gitClient, config),
        new DevelopBranchVersioningStrategy(gitClient, config)
    ]
  }

  Version resolve() {
    String branch = gitClient.getBranchName()
    for (VersioningStrategy strategy : versioningStrategies) {
      if (strategy.supports(branch)) {
        return strategy.resolve()
      }
    }

    throw new NonComplianceException("Unable to determine the version of current branch: $branch")
  }

}
