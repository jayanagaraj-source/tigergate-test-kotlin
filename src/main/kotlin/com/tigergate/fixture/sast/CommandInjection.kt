package com.tigergate.fixture.sast

object CommandInjection {
    // CWE-78: user input interpolated into a shell command.
    fun ping(host: String): Process = Runtime.getRuntime().exec("ping -c 1 $host")

    fun listDir(dir: String): Process = ProcessBuilder("sh", "-c", "ls -la $dir").start()

    fun archive(path: String): Process = Runtime.getRuntime().exec(arrayOf("bash", "-c", "tar czf backup.tgz " + path))
}
