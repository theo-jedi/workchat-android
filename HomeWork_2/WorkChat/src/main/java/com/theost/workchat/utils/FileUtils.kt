package com.theost.workchat.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File

object FileUtils {

    fun createTempFileCopy(context: Context, uri: Uri): File? {
        val file = createTempFile(context, getFileName(context, uri))
        val inputStream = context.contentResolver.openInputStream(uri)
        return if (inputStream != null && file.exists()) {
            val outputStream = file.outputStream()
            outputStream.write(inputStream.readBytes())
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            file
        } else {
            null
        }
    }

    private fun createTempFile(context: Context, name: String): File {
        return File(getTempDirectory(context), name).apply {
            if (exists()) delete()
            createNewFile()
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        return DocumentFile.fromSingleUri(context, uri)?.name ?: "unknown"
    }

    fun deleteTempFiles(context: Context) {
        val tempDir = getTempDirectory(context)
        if (tempDir.isDirectory) tempDir.listFiles()
            ?.let { files ->
                files.forEach { file ->
                    file.delete()
                }
            }
    }

    private fun getTempDirectory(context: Context): File {
        return File(context.cacheDir, "temp").apply { if (!exists()) mkdirs() }
    }

}