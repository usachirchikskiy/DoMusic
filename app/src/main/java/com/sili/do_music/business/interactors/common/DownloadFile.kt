package com.sili.do_music.business.interactors.common

import android.app.Application
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.sili.do_music.business.datasources.network.main.OpenMainApiService
import com.sili.do_music.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream

private const val TAG = "DownloadFile"

class DownloadFile(
    private val openMainApiService: OpenMainApiService,
    private val context: Application
) {

    fun saveFile(
        uniqueName: String,
        fileName: String
    ): Flow<Resource<String>> = flow {

        emit(Resource.loading<String>())
        try {
            val responseBody = openMainApiService.downloadFile(uniqueName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // To Download File for Android 10 and above
                val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                val content = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS
                    )
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val uri = context.contentResolver.insert(
                    collection,
                    content
                )
                uri?.apply {
                    responseBody.byteStream().use { inputStream ->
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            writeOutStream(inputStream, outputStream)
                        }
                    }
                    content.clear()
                    content.put(MediaStore.Video.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, content, null, null)
                }

            } else { // For Android versions below than 10
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    ).absolutePath
                ).apply {
                    if (!exists()) {
                        mkdir()
                    }
                }

                val file = File(directory, fileName)
                responseBody.byteStream().use { inputStream ->
                    file.outputStream().use { outputStream ->
                        writeOutStream(inputStream, outputStream)
                    }
                }
            }
            emit(Resource.success("Finished"))
        } catch (ex: Exception) {
            Resource.error<String>(ex)
        }
    }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    private fun writeOutStream(
        inputStream: InputStream,
        outputStream: OutputStream
    ) {
        val data = ByteArray(8_192)

        while (true) {
            val bytes = inputStream.read(data)

            if (bytes == -1) {
                break
            }

            outputStream.write(data, 0, bytes)
        }
    }
}

