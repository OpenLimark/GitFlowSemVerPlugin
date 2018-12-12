package com.limark.open.gradle.plugins.gitflowsemver.core.strategies

import com.limark.open.gradle.plugins.gitflowsemver.core.model.Version

interface VersioningStrategy {

  boolean supports(String branch)

  Version resolve()

}