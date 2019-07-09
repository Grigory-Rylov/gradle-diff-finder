package com.github.grishberg.finder

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Executes commands.
 */
class CommandLineExecutor(
        private val workingDir: File = File("")
) {
    /**
     * Execute command and return std out as String.
     */
    fun executeCommand(args: List<String>): String {
        try {
            val process = ProcessBuilder(args)
                    .directory(workingDir)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

            process.waitFor(60, TimeUnit.MINUTES)
            return process.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
    }
}