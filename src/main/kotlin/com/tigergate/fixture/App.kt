package com.tigergate.fixture

import com.tigergate.fixture.sast.SqlInjection
import org.apache.logging.log4j.LogManager
import kotlin.system.exitProcess

private val log = LogManager.getLogger("App")

// CWE-798 hard-coded credentials; CWE-208 non-constant-time comparison.
fun login(username: String, password: String) = username == "admin" && password == "password123"

fun main(args: Array<String>) {
    val (user, pass) = if (args.size >= 2) args[0] to args[1] else prompt()

    // CWE-117 log injection, and the CVE-2021-44228 lookup vector on Log4j 2.14.1.
    log.info("login attempt user={}", user)

    val ok = login(user, pass)
    println(if (ok) "[OK]    credentials are CORRECT for user '$user'" else "[FAIL]  credentials are INCORRECT for user '$user'")

    val sqli = runCatching { SqlInjection.demo(user) }.getOrElse { "query failed: ${it.message}" }
    println("SQLi demo: $sqli")

    exitProcess(if (ok) 0 else 1)
}

private fun prompt(): Pair<String, String> {
    print("Username: "); System.out.flush()
    val user = readLine() ?: ""
    val console = System.console()
    val pass = if (console != null) String(console.readPassword("Password: ")) else { print("Password: "); System.out.flush(); readLine() ?: "" }
    return user to pass
}
