package com.example.do_music.business.interactors.common

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.do_music.business.datasources.network.main.OpenMainApiService
import com.example.do_music.util.Constants
import com.example.do_music.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream
import java.io.OutputStream

private const val TAG = "DownloadFile"

class DownloadFile(
    private val openMainApiService: OpenMainApiService
) {
    private val bufferLengthBytes: Int = 1024 * 4

    fun downloadFile(
        uniqueName: String
    ): Flow<Resource<ResponseBody>> = flow {
        emit(Resource.loading())
        try {
            val response = openMainApiService.downloadFile(uniqueName)
            Log.d(TAG, "downloadFile: $uniqueName")
            if (response.code() == 200) {
                emit(Resource(response.body()))
            } else if (response.code() == 429) {
                throw Exception(Constants.DOWNLOAD_LIMIT)
            }
        } catch (e: Exception) {
            Log.d(TAG, "downloadFile Error: $e")
            emit(Resource.error<ResponseBody>(e))
        }
    }

    fun saveFile(
        responseBody: ResponseBody,
        context: Context,
        fileName: String
    ): Flow<Int> = flow {

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
            Log.d(TAG, "saveFile: $uri")
            uri?.apply {
                responseBody.byteStream().use { inputStream ->
                    context.contentResolver.openOutputStream(uri)?.use { fileOutStream ->
                        writeOutStream(
                            inStream = inputStream,
                            outStream = fileOutStream
                        ).collect {
                            emit(it)
                        }
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
                file.outputStream().use { fileOutStream ->
                    writeOutStream(
                        inStream = inputStream,
                        outStream = fileOutStream
                    ).collect {
                        emit(it)
                    }
                }
            }
        }
    }

    private suspend fun writeOutStream(
        inStream: InputStream,
        outStream: OutputStream
    ): Flow<Int> = flow {
        val contentLength =
            inStream.toString().split("hex")[0].split("=")[1].replace(" ", "").toInt()
        Log.d(TAG, "writeOutStream: $contentLength")
        var bytesCopied = 0
        val buffer = ByteArray(bufferLengthBytes)
        var bytes = inStream.read(buffer)

        while (bytes >= 0) {
            outStream.write(buffer, 0, bytes)
            bytesCopied += bytes
            bytes = inStream.read(buffer)
            emit((bytesCopied * 100) / contentLength)
        }

        outStream.flush()
        outStream.close()
        inStream.close()

        delay(300)
        emit(-100)
    }

}