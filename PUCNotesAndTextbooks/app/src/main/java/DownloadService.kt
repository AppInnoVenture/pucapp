package com.puc.pyp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CancellationException

class DownloadService : Service() {
    private val CHANNEL_ID = "DownloadChannel"
    private var downloadJob: Job? = null

    companion object {
        const val NOTIFICATION_ID = 1
        const val EXTRA_FILE_NAME = "extra_file_name"
        const val EXTRA_URL = "extra_url"
        const val ACTION_PROGRESS_UPDATE = "action_progress_update"
        const val ACTION_DOWNLOAD_COMPLETE = "action_download_complete"
        const val ACTION_DOWNLOAD_FAILED = "action_download_failed"
        const val EXTRA_PROGRESS = "extra_progress"

        fun startDownload(context: Context, url: String, fileName: String) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_FILE_NAME, fileName)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopDownload(context: Context) {
            val intent = Intent(context, DownloadService::class.java)
            context.stopService(intent)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val url = intent.getStringExtra(EXTRA_URL) ?: return START_NOT_STICKY
        val fileName = intent.getStringExtra(EXTRA_FILE_NAME) ?: return START_NOT_STICKY

        startForeground(NOTIFICATION_ID, buildNotification(0, "Downloading"))

        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        if (downloadJob?.isActive == true)
            return START_NOT_STICKY

        val file = File(filesDir, fileName)

        if (file.exists()){
            broadcastDownloadComplete(fileName)
            stopSelf()
            return START_NOT_STICKY
        }

        sharedPreferences.edit().apply {
            putBoolean("dwd", true)
            putString("dwdPdf", fileName)
            apply()
        }

        downloadJob = CoroutineScope(Dispatchers.IO).launch {
            var inputStream: BufferedInputStream? = null
            var fileOutputStream: FileOutputStream? = null
            var connection: HttpURLConnection? = null

            try {
                connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode in 200..299) {
                    val totalFileSize = connection.contentLength.toLong()
                    inputStream = BufferedInputStream(connection.inputStream, 8192)
                    fileOutputStream = FileOutputStream(file)

                    val buffer = ByteArray(8192)
                    var totalBytesRead = 0L
                    var lastProgressUpdate = 0
                    var lastUpdateTime = System.currentTimeMillis()

                    while (isActive) {
                        val bytesRead = inputStream.read(buffer)
                        if (bytesRead == -1) break
                        fileOutputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead

                        val progress = if (totalFileSize > 0) {
                            ((totalBytesRead * 100) / totalFileSize).toInt()
                        } else 100

                        val currentTime = System.currentTimeMillis()
                        if (progress - lastProgressUpdate >= 2 && currentTime - lastUpdateTime >= 500) {
                            lastProgressUpdate = progress
                            lastUpdateTime = currentTime
                            updateNotification(progress)
                            broadcastProgress(progress, fileName)
                        }
                    }

                    if (isActive) broadcastDownloadComplete(fileName)
                    else throw CancellationException("Download canceled")
                } else {
                    throw Exception("Server Error")
                }
            } catch (e: CancellationException) {
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                broadcastDownloadFailed(fileName)
                if (file.exists()) file.delete()
            } finally {
                broadcastProgress(0, fileName)
                inputStream?.close()
                fileOutputStream?.close()
                connection?.disconnect()
                delay(300)
                withContext(Dispatchers.Main) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        downloadJob?.cancel()
        downloadJob = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Download Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows download progress"
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(progress: Int, message: String? = null) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Downloading: $progress%")
            .setContentText(message ?: "In progress...")
            .setProgress(100, progress, progress == 0 && message == null)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    private fun updateNotification(progress: Int, message: String? = null) {
        getSystemService(NotificationManager::class.java)?.notify(
            NOTIFICATION_ID, buildNotification(progress, message)
        )
    }

    private fun broadcastProgress(progress: Int, fileName: String) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(ACTION_PROGRESS_UPDATE).apply {
            putExtra(EXTRA_PROGRESS, progress)
            putExtra(EXTRA_FILE_NAME, fileName)
        })
    }

    private fun broadcastDownloadComplete(fileName: String) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(ACTION_DOWNLOAD_COMPLETE).apply {
            putExtra(EXTRA_FILE_NAME, fileName)
        })
    }

    private fun broadcastDownloadFailed(fileName: String) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(ACTION_DOWNLOAD_FAILED).apply {
            putExtra(EXTRA_FILE_NAME, fileName)
        })
    }
}