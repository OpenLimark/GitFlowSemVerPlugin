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
    return "master" == branch || gitClient.describeExactMatch().length() > 0
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
