package com.shpagin.photoeditor.photoeditorlight

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.shpagin.photoeditor.photoeditorlight.databinding.ActivityMainBinding
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoFilter
import com.shpagin.photoeditor.photoeditorlight.viewmodels.MainViewModel
import androidx.core.app.ActivityCompat
import com.shpagin.photoeditor.photoeditorlight.dialogs.*
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import java.lang.Exception


private const val WRITE_EXTERNAL_STORAGE_CODE = 185
private const val DIALOG_TAG = "dialog-tag"

class MainActivity : AppCompatActivity(), DialogAddText.DialogCallback,
    DialogAddEmoji.DialogCallback, DialogPaint.DialogCallback, AlertDialogExit.ExitCallback, AlertDialogSaveNoChanges.SaveCallback {

    private lateinit var binding: ActivityMainBinding
    // Данное свойство будет хранить информацию даже в случае изменения конфигурации устройства
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    // Свойство для запуска операционной системой приложения,
    private val getContent =   // которое смогло бы запросить нужный контент
        registerForActivityResult(ActivityResultContracts.GetContent()) {
                uri: Uri? ->   // Сохраняем uri фотографии
            if (uri != null) {
                viewModel.imageUri.value = uri
                supportActionBar?.show()
            } else if (photoEditor.isCacheEmpty && viewModel.imageUri.value == null) {
                binding.addImageGlobalButton.visibility = View.VISIBLE
                binding.startVisibility = View.INVISIBLE
            }
        }
    private lateinit var photoEditor: PhotoEditor
    private var someFilterApplied = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Уже было включено
        if (viewModel.activityWasDestroyed) {
            binding.addImageGlobalButton.visibility = View.GONE
            binding.startVisibility = View.VISIBLE
            // Включается впервые
        } else {
            supportActionBar?.hide()
            binding.startVisibility = View.INVISIBLE

            binding.addImageGlobalButton.setOnClickListener {
                binding.startVisibility = View.VISIBLE
                binding.addImageGlobalButton.visibility = View.GONE
                getContent.launch("image/*")
            }
        }
        viewModel.imageUri.observe(this@MainActivity) {
            photoEditor.clearAllViews()
            binding.photoEditorView.source.setImageURI(viewModel.imageUri.value)
        }
    }

    override fun onStart() {
        super.onStart()

        photoEditor = PhotoEditor.Builder(this, binding.photoEditorView)
            .setPinchTextScalable(true)
            //.setClipSourceImage(true)
            //.setDefaultEmojiTypeface()
            .build()

        binding.undoButton.setOnClickListener {
            photoEditor.undo()
        }
        binding.redoButton.setOnClickListener {
            photoEditor.redo()
        }


        binding.filtersButton.setOnClickListener {
            if (binding.filterScrollView.visibility == View.VISIBLE) {
                binding.filterScrollView.visibility = View.GONE
                binding.undoRedoBox.visibility = View.VISIBLE
            }
            else {
                binding.filterScrollView.visibility = View.VISIBLE
                binding.undoRedoBox.visibility = View.INVISIBLE
            }
        }
        binding.textButton.setOnClickListener {
            if (binding.filterScrollView.visibility == View.VISIBLE) {
                binding.filterScrollView.visibility = View.GONE
                binding.undoRedoBox.visibility = View.VISIBLE
            }
            DialogAddText().show(supportFragmentManager, DIALOG_TAG)
        }
        binding.emojiButton.setOnClickListener {
            if (binding.filterScrollView.visibility == View.VISIBLE) {
                binding.filterScrollView.visibility = View.GONE
                binding.undoRedoBox.visibility = View.VISIBLE
            }
            DialogAddEmoji().show(supportFragmentManager, DIALOG_TAG)
        }
        binding.drawButton.setOnClickListener {
            if (binding.filterScrollView.visibility == View.VISIBLE) {
                binding.undoRedoBox.visibility = View.VISIBLE
                binding.filterScrollView.visibility = View.GONE
            }
            DialogPaint(viewModel.brushColor, viewModel.brushSize, viewModel.brushOpacity)
                .show(supportFragmentManager, DIALOG_TAG)
        }
        binding.eraseButton.setOnClickListener {
            photoEditor.brushEraser()
        }

        activateFilterListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_exit)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> AlertDialogExit(!photoEditor.isCacheEmpty || someFilterApplied)
                .show(supportFragmentManager, DIALOG_TAG)
            R.id.save_image -> {
                if (photoEditor.isCacheEmpty && !someFilterApplied) {
                    AlertDialogSaveNoChanges().show(supportFragmentManager, DIALOG_TAG)
                } else {
                    savePhoto()
                    someFilterApplied = false
                }
            }
            R.id.load_new_image -> getContent.launch("image/*")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                savePhoto()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.activityWasDestroyed = true
    }

        private fun activateFilterListeners() {
        binding.apply {
            item1.setOnClickListener  { photoEditor.setFilterEffect(PhotoFilter.NONE); someFilterApplied = false}
            item2.setOnClickListener  { photoEditor.setFilterEffect(PhotoFilter.AUTO_FIX); someFilterApplied = true}
            item3.setOnClickListener  { photoEditor.setFilterEffect(PhotoFilter.BRIGHTNESS); someFilterApplied = true}
            item4.setOnClickListener  { photoEditor.setFilterEffect(PhotoFilter.CONTRAST); someFilterApplied = true}
            item5.setOnClickListener  { photoEditor.setFilterEffect(PhotoFilter.CROSS_PROCESS); someFilterApplied = true}
            item6.setOnClickListener  { photoEditor.setFilterEffect(PhotoFilter.DOCUMENTARY); someFilterApplied = true}
            item7.setOnClickListener  { photoEditor.setFilterEffect(PhotoFilter.DUE_TONE); someFilterApplied = true}
            item8.setOnClickListener  { photoEditor.setFilterEffect(PhotoFilter.FILL_LIGHT); someFilterApplied = true}
            item9.setOnClickListener  { photoEditor.setFilterEffect(PhotoFilter.FISH_EYE); someFilterApplied = true}
            item10.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.GRAIN); someFilterApplied = true}
            item11.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.GRAY_SCALE); someFilterApplied = true}
            item12.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.LOMISH); someFilterApplied = true}
            item13.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.NEGATIVE); someFilterApplied = true}
            item14.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.POSTERIZE); someFilterApplied = true}
            item15.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SATURATE); someFilterApplied = true}
            item16.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.SEPIA); someFilterApplied = true}
            item17.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.TEMPERATURE); someFilterApplied = true}
            item18.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.TINT); someFilterApplied = true}
            item19.setOnClickListener { photoEditor.setFilterEffect(PhotoFilter.VIGNETTE); someFilterApplied = true}
        }
    }
    private fun savePhoto() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_CODE)

        } else {
            photoEditor.saveAsFile(viewModel.getPicName(), object : OnSaveListener {
                override fun onSuccess(imagePath: String) {
                    Toast.makeText(this@MainActivity,
                        "Saved successfully", Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(exception: Exception) {
                    Log.e("MyTag", "Exception message: ${exception.message}")
                }
            })
        }
    }

    override fun onAcceptTextSettings(text: String, color: Int) {
        photoEditor.addText(text, color)
    }

    override fun onAcceptEmoji(emoji: String) {
        photoEditor.addEmoji(emoji)
    }

    override fun onAcceptBrushSettings(@ColorInt brushColor: Int,
                                       brushSize: Float,
                                       brushOpacity: Int) {
        photoEditor.setOpacity(brushOpacity)
        photoEditor.brushColor = brushColor
        photoEditor.brushSize  = brushSize

        viewModel.brushColor   = brushColor
        viewModel.brushSize    = brushSize
        viewModel.brushOpacity = brushOpacity
        photoEditor.setBrushDrawingMode(true)
    }

    override fun onBackPressed() {
        AlertDialogExit(!photoEditor.isCacheEmpty && someFilterApplied)
            .show(supportFragmentManager, DIALOG_TAG)
    }

    override fun exitDecision(exit: Int) {
        when (exit) {
            1 -> finish()
            2 -> {
                savePhoto()
                Thread.sleep(1000)
                finish()
            }
        }
    }

    override fun saveEmptyDecision(save: Boolean) {
        if (save) {
            savePhoto()
        }
    }
}
