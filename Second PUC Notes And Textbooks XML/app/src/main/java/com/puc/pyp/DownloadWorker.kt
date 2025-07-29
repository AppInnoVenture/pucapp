package com.puc.pyp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class DownloadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private var lastProgress: Int = 0
    private var downloadStartTime: Long = 0L
    private var totalFileSize: Long = -1L
    private var lastUpdateTime: Long = 0L
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private const val CHANNEL_ID = "DownloadChannel"

        private val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .header("Accept-Encoding", "identity")
                    .header("Cache-Control", "no-cache")
                    .build()
                chain.proceed(request)
            }
            .build()

        suspend fun startDownload(context: Context, url: String, fileName: String): String {
            val data = workDataOf(
                AppConstants.EXTRA_URL to url,
                AppConstants.EXTRA_FILE_NAME to fileName
            )

            val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(data)
                .setBackoffCriteria(
                    androidx.work.BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .build()

            withContext(Dispatchers.IO) {
                WorkManager.getInstance(context).enqueueUniqueWork(
                    AppConstants.WORK_NAME,
                    androidx.work.ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            }
            return workRequest.id.toString()
        }

        fun stopDownload(context: Context, workId: String) {
            try {
                WorkManager.getInstance(context).cancelWorkById(java.util.UUID.fromString(workId))
                context.getSystemService(NotificationManager::class.java)?.cancel(AppConstants.NOTIFICATION_ID)
            } catch (e: IllegalArgumentException) {
                // Ignore error
            }
        }
    }

    override suspend fun doWork(): Result {
        createNotificationChannel()
        val url = inputData.getString(AppConstants.EXTRA_URL)
            ?: return Result.failure(workDataOf(
                AppConstants.STATE_KEY to "failed",
                AppConstants.FILE_NAME_KEY to "",
                AppConstants.PROGRESS_KEY to 0,
                AppConstants.ERROR_MESSAGE_KEY to "Missing URL"
            ))

        val fileName = inputData.getString(AppConstants.EXTRA_FILE_NAME)
            ?: return Result.failure(workDataOf(
                AppConstants.STATE_KEY to "failed",
                AppConstants.FILE_NAME_KEY to "",
                AppConstants.PROGRESS_KEY to 0,
                AppConstants.ERROR_MESSAGE_KEY to "Missing fileName"
            ))

        setForegroundAsync(createForegroundInfo(0, 0L, -1L, 0.0))

        val tempFile = File(applicationContext.filesDir, "temp.pdf")
        tempFile.delete()
        downloadStartTime = System.currentTimeMillis()
        lastUpdateTime = downloadStartTime

        var call: Call? = null
        try {
            setDownloadProgress("running", fileName, 0, -1L, 0.0)
            val request = Request.Builder().url(url).build()
            call = client.newCall(request)
            call.execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Server error: ${response.code}")
                }

                totalFileSize = response.body.contentLength()
                val inputStream = response.body.byteStream()
                
// some private codes are removed, currently it shows only simplified version 
    

    }

    private suspend fun setDownloadProgress(
        state: String,
        fileName: String,
        progress: Int,
        etaSeconds: Long,
        speed: Double,
        errorMessage: String? = null
    ) {
        // some private codes are removed, currently it shows only simplified version 
    
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Download Progress",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows download progress"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return if (bytes >= 0) "%.1f MB".format(bytes / 1024.0 / 1024.0) else "Unknown"
    }

    private fun formatEta(seconds: Long): String {
        return if (seconds >= 0) {
            if (seconds >= 60) {
                val minutes = seconds / 60
                "ETA: $minutes min"
            } else {
                "ETA: ${seconds}s"
            }
        } else {
            "ETA: --"
        }
    }

    private fun formatSpeed(speed: Double): String {
        return if (speed > 0) "%.2f MB/s".format(speed) else "Unknown"
    }

    private fun createForegroundInfo(
        progress: Int,
        downloadedBytes: Long,
        etaSeconds: Long,
        downloadSpeed: Double
    ): ForegroundInfo {
        // some private codes are removed, currently it shows only simplified version 
    
    }
}