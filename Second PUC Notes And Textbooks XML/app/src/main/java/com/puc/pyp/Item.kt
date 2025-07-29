package com.puc.pyp

data class Language(val name: String, val code: String, val displayName: String)
data class Item(val img: Int, val description: String, val extra: String)
data class YearItem(val descLan: String, val extra: String)
data class TextItem(val descLan: String, val extra: String, val s1: Int, val e1: Int, val s2: Int, val e2: Int)

object AppConstants {
    const val EXTRA_FILE_NAME = "extra_file_name"
    const val EXTRA_URL = "extra_url"
    const val PROGRESS_KEY = "progress"
    const val FILE_NAME_KEY = "file_name"
    const val ETA_SECONDS_KEY = "eta_seconds"
    const val SPEED_KEY = "download_speed"
    const val STATE_KEY = "state"
    const val ERROR_MESSAGE_KEY = "error_message"
    const val WORK_NAME = "single_download_worker"
    const val NOTIFICATION_ID = 1
}