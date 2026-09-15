package com.tigergate.fixture.sast

import java.util.Hashtable
import javax.naming.Context
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls

object LdapInjection {
    // CWE-90 filter built from user input; CWE-798 hard-coded bind credentials; CWE-319 cleartext ldap://.
    fun findUser(uid: String) {
        val env = Hashtable<String, String>()
        env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
        env[Context.PROVIDER_URL] = "ldap://ldap.internal:389"
        env[Context.SECURITY_AUTHENTICATION] = "simple"
        env[Context.SECURITY_PRINCIPAL] = "cn=admin,dc=example,dc=com"
        env[Context.SECURITY_CREDENTIALS] = "ldap-admin-pass-2024"
        val ctx = InitialDirContext(env)
        ctx.search("ou=people,dc=example,dc=com", "(uid=$uid)", SearchControls().apply { searchScope = SearchControls.SUBTREE_SCOPE })
    }
}
