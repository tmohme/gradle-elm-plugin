package org.mohme.gradle

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import strikt.assertions.isTrue
import java.io.File
import java.net.URI

internal class UnpackerTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `successfully unpacks a valid gz archive`() {
        // given
        val sourceFile = fileFromResource("/test.gz")

        // when
        val (file, exception) = Unpacker.unpack(sourceFile)

        // then
        expect {
            that(exception).isNull()
            that(file) {
                isNotNull().and {
                    get(File::getParentFile).isEqualTo(sourceFile.parentFile)
                    get(File::isFile).isTrue()
                    get(File::canRead).isTrue()
                    get(File::exists).isTrue()
                }
            }
        }
    }

    @Test
    fun `fails to unpack a non-gz file`() {
        // given
        val sourceFile = fileFromResource("/test.txt")

        // when
        val (file, exception) = Unpacker.unpack(sourceFile)

        // then
        expect {
            that(exception).isNotNull()
            that(file).isNull()
        }
    }

    private fun fileFromResource(resource: String): File {
        val uri = javaClass.getResource(resource)!!.toURI()
        expectThat(uri).get(URI::getScheme).isEqualTo("file")
        val sourceFile = File(uri.path)
        return sourceFile.copyTo(tempDir.resolve(sourceFile.name))
    }

}