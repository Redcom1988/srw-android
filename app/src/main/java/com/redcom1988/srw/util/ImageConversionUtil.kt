package com.redcom1988.srw.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object ImageConversionUtil {
    /**
     * Converts an image file to PNG format
     * @param inputFile The source image file (JPEG, PNG, etc.)
     * @param outputFile The destination PNG file
     * @return True if conversion was successful, false otherwise
     */
    fun convertToPng(inputFile: File, outputFile: File): Boolean {
        return try {
            // Decode the image file to a Bitmap
            val bitmap = BitmapFactory.decodeFile(inputFile.absolutePath) ?: return false

            // Save as PNG
            FileOutputStream(outputFile).use { outputStream ->
                val success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                bitmap.recycle()
                success
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Converts a JPEG file to PNG if needed
     * @param file The source file
     * @return The PNG file (either converted or original if already PNG)
     */
    fun ensurePng(file: File): File {
        // Check if file is already PNG
        val fileBytes = file.readBytes()
        val isPng = fileBytes.size >= 8 &&
            fileBytes[0] == 0x89.toByte() &&
            fileBytes[1] == 0x50.toByte() &&
            fileBytes[2] == 0x4E.toByte() &&
            fileBytes[3] == 0x47.toByte()

        if (isPng) {
            return file
        }

        // Convert to PNG
        val pngFile = File(file.parentFile, file.nameWithoutExtension + "_converted.png")
        return if (convertToPng(file, pngFile)) {
            pngFile
        } else {
            file
        }
    }
}

