package com.tigergate.fixture

import com.tigergate.fixture.sast.SqlInjection
import com.tigergate.fixture.sast.WeakCrypto
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppTest {
    @Test fun acceptsFixtureCredentials() = assertTrue(login("admin", "password123"))
    @Test fun rejectsWrongPassword() = assertFalse(login("admin", "wrong-password"))

    @Test fun sqlInjectionReturnsEveryRow() {
        assertTrue(SqlInjection.demo("admin").contains("1 row(s)"))
        assertTrue(SqlInjection.demo("' OR '1'='1").contains("2 row(s)"))
    }

    @Test fun md5IsStillMd5() = assertEquals("482c811da5d5b4bc6d497ffa98491e38", WeakCrypto.md5("password123"))
}
