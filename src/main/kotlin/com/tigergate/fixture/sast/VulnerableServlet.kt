package com.tigergate.fixture.sast

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class VulnerableServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val name = req.getParameter("name") ?: ""

        // CWE-79: reflected XSS.
        resp.writer.println("<h1>Hello " + name + "</h1>")

        // CWE-601: open redirect.
        req.getParameter("next")?.let { resp.sendRedirect(it) }

        // CWE-113: response header injection.
        resp.setHeader("X-Forwarded-For", req.getParameter("ip") ?: "")

        // CWE-614 / CWE-1004: cookie without Secure or HttpOnly, with a one-year lifetime.
        val session = Cookie("SESSIONID", WeakCrypto.sessionToken())
        session.maxAge = 60 * 60 * 24 * 365
        resp.addCookie(session)

        // CWE-209: stack trace returned to the client.
        try {
            SqlInjection.demo(name)
        } catch (e: Exception) {
            e.printStackTrace(resp.writer)
        }
    }

    // CWE-352 no CSRF token; CWE-862 no authorisation check; CWE-89 via request parameter.
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        SqlInjection.deleteUser(SqlInjection.connect(), req.getParameter("id"))
        resp.writer.write("deleted")
    }
}
