package com.theost.workchat.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.util.*


object FileUtils {

    fun createTempFileCopy(context: Context, uri: Uri): File? {
        val file = createTempFile(context, getFileExtension(context, uri))
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

    private fun createTempFile(context: Context, extension: String): File {
        return File.createTempFile(
            UUID.randomUUID().toString(),
            extension,
            getTempDirectory(context)
        )
    }

    private fun getFileExtension(context: Context, uri: Uri): String {
        return when {
            uri.scheme == ContentResolver.SCHEME_CONTENT -> {
                val extension = MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(context.contentResolver.getType(uri)).toString()
                return ".$extension"
            }
            uri.path != null -> {
                val extension =
                    MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path!!)).toString())
                return ".$extension"
            }
            else -> ""
        }
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