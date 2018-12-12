package com.limark.open.gradle.plugins.semver.core.exceptions

/**
 * An exception that is thrown when non-compliance to the expected process is detected.
 */
class NonComplianceException extends RuntimeException {

  NonComplianceException(String message) {
    super(message)
  }
}
