package com.tigergate.fixture.sast

import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream
import org.apache.commons.io.FilenameUtils

object PathTraversal {
    private const val BASE = "/var/app/uploads/"

    // CWE-22: file name joined to a base directory without canonicalisation.
    fun read(fileName: String): ByteArray = File(BASE + fileName).readBytes()

    fun open(fileName: String): FileInputStream = FileInputStream(File(BASE, fileName))

    fun delete(fileName: String): Boolean = File(BASE + fileName).delete()

    // CWE-22 via commons-io 2.6: FilenameUtils.normalize is bypassable (CVE-2021-29425).
    fun readNormalised(fileName: String): String = File(BASE, FilenameUtils.normalize(fileName)).readText()

    // CWE-22 "Zip Slip": entry names written to disk unchecked.
    fun unzip(zip: File, dest: File) {
        ZipInputStream(zip.inputStream()).use { zin ->
            generateSequence { zin.nextEntry }.forEach { entry ->
                val out = File(dest, entry.name)
                if (!entry.isDirectory) {
                    out.parentFile.mkdirs()
                    out.outputStream().use { zin.copyTo(it) }
                }
            }
        }
    }
}
