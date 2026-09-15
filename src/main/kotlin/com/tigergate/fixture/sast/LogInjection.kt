package com.tigergate.fixture.sast

import org.apache.logging.log4j.LogManager

object LogInjection {
    private val log = LogManager.getLogger(LogInjection::class.java)

    // CWE-117: unsanitised user input written to the log; also the Log4Shell lookup vector.
    fun audit(user: String, action: String) = log.info("user=" + user + " action=" + action)

    // CWE-532: credentials and tokens written to the log.
    fun debugLogin(user: String, password: String, token: String) =
        log.debug("login user={} password={} token={}", user, password, token)

    // CWE-209/CWE-778: error detail discarded.
    fun swallow(e: Exception) = log.error(e.message)
}
