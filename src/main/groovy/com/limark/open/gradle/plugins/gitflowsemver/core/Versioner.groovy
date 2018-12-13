/**********************************************************************************************************************
 * Copyright (c) 2018 Limark Technologies (Pvt) Ltd.                                                                  *
 *                                                                                                                    *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated      *
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation   *
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and  *
 *  to permit persons to whom the Software is furnished to do so, subject to the following conditions:                *
 *                                                                                                                    *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of   *
 * the Software.                                                                                                      *
 *                                                                                                                    *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO   *
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL         *
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF      *
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE                               *
 **********************************************************************************************************************/

package com.limark.open.gradle.plugins.gitflowsemver.core

import com.limark.open.gradle.plugins.gitflowsemver.config.PluginConfig
import com.limark.open.gradle.plugins.gitflowsemver.core.exceptions.NonComplianceException
import com.limark.open.gradle.plugins.gitflowsemver.core.model.Version
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.VersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.BugfixBranchVersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.DevelopBranchVersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.FeatureBranchVersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.HotfixBranchVersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.MasterBranchVersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.ReleaseBranchVersioningStrategy
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl.UnknownBranchVersioningStrategy
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
        new DevelopBranchVersioningStrategy(gitClient, config),
        new FeatureBranchVersioningStrategy(gitClient, config),
        new BugfixBranchVersioningStrategy(gitClient, config),
        new UnknownBranchVersioningStrategy(gitClient, config) // Should be the last strategy
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
