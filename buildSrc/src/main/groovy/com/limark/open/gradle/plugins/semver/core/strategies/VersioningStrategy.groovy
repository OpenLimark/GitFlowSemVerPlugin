package com.limark.open.gradle.plugins.semver.core.strategies

import com.limark.open.gradle.plugins.semver.core.model.Version

interface VersioningStrategy {

  boolean supports(String branch)

  Version resolve()

}