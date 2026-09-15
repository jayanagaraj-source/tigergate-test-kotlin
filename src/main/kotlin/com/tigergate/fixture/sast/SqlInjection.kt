package com.tigergate.fixture.sast

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object SqlInjection {
    // CWE-89: query built by string interpolation.
    fun buildQuery(input: String) = "SELECT * FROM users WHERE name = '$input'"

    // CWE-89: concatenated query executed through Statement.
    fun findUser(conn: Connection, name: String): ResultSet =
        conn.createStatement().executeQuery("SELECT * FROM users WHERE name = '$name'")

    // CWE-89: String.format into a PreparedStatement defeats parameterisation.
    fun findByEmail(conn: Connection, email: String): ResultSet =
        conn.prepareStatement(String.format("SELECT id FROM users WHERE email = '%s'", email)).executeQuery()

    fun deleteUser(conn: Connection, id: String) {
        conn.createStatement().executeUpdate("DELETE FROM users WHERE id = " + id)
    }

    // CWE-798: hard-coded database credentials.
    fun connect(): Connection =
        DriverManager.getConnection("jdbc:postgresql://db.internal:5432/app", "app_admin", "Pr0d-DB-Passw0rd!")

    // Runs the injection end-to-end against in-memory H2 so `gradle run` shows the effect.
    fun demo(name: String): String {
        DriverManager.getConnection("jdbc:h2:mem:fixture;DB_CLOSE_DELAY=-1").use { conn ->
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS users(id INT PRIMARY KEY, name VARCHAR, role VARCHAR)")
            conn.createStatement().execute("MERGE INTO users KEY(id) VALUES (1, 'admin', 'root'), (2, 'alice', 'user')")
            val rs = findUser(conn, name)
            val rows = generateSequence { if (rs.next()) rs.getString("name") + "/" + rs.getString("role") else null }.toList()
            return "query for '$name' returned ${rows.size} row(s): $rows"
        }
    }
}
