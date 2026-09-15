package com.tigergate.fixture.sast

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions

object InsecureFiles {
    // CWE-377 predictable temp file in a shared directory; CWE-732 world-writable permissions.
    fun scratch(): File = File("/tmp/tigergate-" + System.currentTimeMillis()).apply {
        writeText("scratch")
        setReadable(true, false)
        setWritable(true, false)
        setExecutable(true, false)
    }

    fun worldReadableKey(): File {
        val f = Files.createTempFile("key", ".pem").toFile()
        Files.setPosixFilePermissions(f.toPath(), PosixFilePermissions.fromString("rw-rw-rw-"))
        return f
    }

    // CWE-1333: catastrophic backtracking regex evaluated on user input.
    fun isEmail(input: String): Boolean = Regex("^([a-zA-Z0-9]+)*@([a-z0-9]+\\.)+[a-z]{2,}$").matches(input)
}
