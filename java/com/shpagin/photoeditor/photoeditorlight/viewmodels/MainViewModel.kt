package com.shpagin.photoeditor.photoeditorlight.viewmodels

import android.net.Uri
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class MainViewModel: ViewModel() {
    val imageUri = MutableLiveData<Uri>()
    var activityWasDestroyed: Boolean = false

    var brushColor  : Int = -1
    var brushSize   : Float = 25f
    var brushOpacity: Int = 255

    private val path: File =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

    fun getPicName(): String {
        // Текущее время
        val currentDate = Date()
        // Форматирование времени как "часы:минуты:секунды"
        val timeText: String
        SimpleDateFormat("HHmmss", Locale.getDefault()).apply {
            timeText = this.format(currentDate)
        }
        return File(path, "photo_editor_light_$timeText.jpg").toString()
    }

}