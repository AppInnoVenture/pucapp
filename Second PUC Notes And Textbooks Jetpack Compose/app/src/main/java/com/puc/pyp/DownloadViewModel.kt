package com.puc.pyp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

data class DownloadState(
    val workId: String? = null,
    val state: String? = null,
    val fileName: String? = null,
    val progress: Int = 0,
    val errorMessage: String? = null
)

class DownloadViewModel(application: Application) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)
    private val _downloadState = MutableStateFlow(DownloadState())
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    fun startDownload(url: String, fileName: String) {
        if (_downloadState.value.workId != null) return

        clearDownloadState()

        viewModelScope.launch {
            val finalFile = File(getApplication<Application>().filesDir, fileName)
            if (finalFile.exists()) {
                _downloadState.value = DownloadState(
                    workId = null,
                    state = "already_exists",
                    fileName = fileName,
                    progress = 100
                )
                return@launch
            }

            val workId = DownloadWorker.startDownload(getApplication(), url, fileName)
            _downloadState.value = DownloadState(
                workId = workId,
                state = "running",
                fileName = fileName,
                progress = 0
            )

            try {
                workManager.getWorkInfoByIdFlow(UUID.fromString(workId))
                    .flowOn(Dispatchers.IO)
                    .collect { workInfo ->
                        if (workInfo == null) {
                            return@collect
                        }

                        val newState = when (workInfo.state) {
                            WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> DownloadState(
                                workId = workId,
                                state = "running",
                                fileName = workInfo.progress.getString(AppConstants.FILE_NAME_KEY) ?: fileName,
                                progress = workInfo.progress.getInt(AppConstants.PROGRESS_KEY, 0),
                                errorMessage = null
                            )
                            WorkInfo.State.SUCCEEDED -> DownloadState(
                                workId = null,
                                state = workInfo.outputData.getString(AppConstants.STATE_KEY) ?: "complete",
                                fileName = workInfo.outputData.getString(AppConstants.FILE_NAME_KEY) ?: fileName,
                                progress = workInfo.outputData.getInt(AppConstants.PROGRESS_KEY, 100),
                                errorMessage = null
                            )
                            WorkInfo.State.FAILED -> DownloadState(
                                workId = null,
                                state = "failed",
                                fileName = workInfo.outputData.getString(AppConstants.FILE_NAME_KEY) ?: fileName,
                                progress = workInfo.outputData.getInt(AppConstants.PROGRESS_KEY, 0),
                                errorMessage = workInfo.outputData.getString(AppConstants.ERROR_MESSAGE_KEY) ?: "Unknown error"
                            )
                            WorkInfo.State.CANCELLED -> DownloadState(
                                workId = null,
                                state = "canceled",
                                fileName = workInfo.outputData.getString(AppConstants.FILE_NAME_KEY) ?: fileName,
                                progress = workInfo.outputData.getInt(AppConstants.PROGRESS_KEY, 0),
                                errorMessage = workInfo.outputData.getString(AppConstants.ERROR_MESSAGE_KEY) ?: "Download cancelled"
                            )
                            else -> DownloadState(
                                workId = workId,
                                state = "running",
                                fileName = fileName,
                                progress = 0
                            )
                        }

                        _downloadState.value = newState
                    }
            } finally {
                val context = getApplication<Application>()
                context.getSystemService(android.app.NotificationManager::class.java)?.cancel(
                    AppConstants.NOTIFICATION_ID)
                workManager.pruneWork()
            }
        }
    }

    fun cancelDownload() {
        val workId = _downloadState.value.workId ?: return
        DownloadWorker.stopDownload(getApplication(), workId)
    }

// some private codes are removed, currently it shows only simplified version 
    
   
}