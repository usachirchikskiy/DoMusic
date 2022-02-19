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
import com.example.do_music.util.getMimeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import okhttp3.RequestBody
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
        try {
            val response = openMainApiService.downloadFile(uniqueName)
            Log.d(TAG, "downloadFile: ${response.raw()}")
            Log.d(TAG, "downloadFile: $uniqueName")
            if(response.code()==200){
                emit(Resource(response.body()))
            }
            else if(response.code()==429){
                throw Exception(Constants.DOWNLOAD_LIMIT)
            }
//            response.body()?.let { responseBody ->
//                saveFile(
//                    responseBody = responseBody,
//                    fileName = fileName,
//                    context = context
//                ).onEach {
//                    emit(Resource.success(it))
//                    Log.d(TAG, "downloadFile: $it")
//                }
//            }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // To Download File for Android 10 and above
            val mimeTypeMap = getMimeType(fileName.split(".").last())
            val content = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeTypeMap)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                content
            )
            Log.d(TAG, "saveFile: $uri")
            uri?.apply {
                Log.d(TAG, "saveFile: URI ${this.path}")
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
            }
        } else { // For Android versions below than 10
            val directory = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ).absolutePath
            ).apply {
                Log.d(TAG, "saveFile: FILE ${this.path}")
                if (!exists()) {
                    mkdir()
                }
            }

            val file = File(directory, fileName)
            Log.d(TAG, "saveFile: ${responseBody.byteStream()}")
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
        outStream: OutputStream,
//        contentLength: Long
    ): Flow<Int> = flow {
        val contentLength = inStream.toString().split("hex")[0].split("=")[1].replace(" ","").toInt()
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
    }

}