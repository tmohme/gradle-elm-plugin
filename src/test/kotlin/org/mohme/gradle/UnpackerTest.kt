package org.mohme.gradle

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import strikt.assertions.isTrue
import java.io.File
import java.net.URI

internal class UnpackerTest {
    val logger : Logger = object : Logger {
        override fun debug(message: String, vararg objects: Any?) {}
        override fun error(message: String, vararg objects: Any?) {}
    }

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `successfully unpacks a valid gz archive to a new file`() {
        // given
        val sourceFile = fileFromResource("/test.txt.gz")

        // when
        val (unpackResult, exception) = Unpacker(logger).unpack(sourceFile)

        // then
        expect {
            that(exception).isNull()
            that(unpackResult) {
                isNotNull()
                        .isA<Unpacker.UnpackResult.NewFile>()
                        .get(Unpacker.UnpackResult::file)
                        .and {
                            get(File::getParentFile).isEqualTo(sourceFile.parentFile)
                            get(File::isFile).isTrue()
                            get(File::canRead).isTrue()
                            get(File::exists).isTrue()
                        }
            }
        }
    }

    @Test
    fun `skips unpacking when the targetFile exists`() {
        // given
        val sourceFile = fileFromResource("/test.txt.gz")
        val (intermediateResult, _) = Unpacker(logger).unpack(sourceFile)
        expectThat(intermediateResult)
                .isNotNull()
                .isA<Unpacker.UnpackResult.NewFile>()

        // when
        val (unpackResult, exception) = Unpacker(logger).unpack(sourceFile)

        // then
        expect {
            that(exception).isNull()
            that(unpackResult) {
                isNotNull()
                        .isA<Unpacker.UnpackResult.ExistingFile>()
                        .get(Unpacker.UnpackResult::file)
                        .and {
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
        val (file, exception) = Unpacker(logger).unpack(sourceFile)

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