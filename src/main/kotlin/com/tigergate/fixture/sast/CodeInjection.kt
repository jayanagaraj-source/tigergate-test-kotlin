package com.tigergate.fixture.sast

import javax.script.ScriptEngineManager
import org.apache.commons.text.StringSubstitutor

object CodeInjection {
    // CWE-94/CWE-95: user input evaluated by a script engine.
    fun eval(expression: String): Any? = ScriptEngineManager().getEngineByName("JavaScript")?.eval(expression)

    // CWE-470: class name from user input instantiated reflectively.
    fun instantiate(className: String): Any = Class.forName(className).getDeclaredConstructor().newInstance()

    // CWE-94 via commons-text 1.9: ${script:...} interpolation on untrusted input (CVE-2022-42889).
    fun interpolate(template: String): String = StringSubstitutor.createInterpolator().replace(template)
}
