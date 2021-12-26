package com.shpagin.photoeditor.photoeditorlight.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.shpagin.photoeditor.photoeditorlight.R

class AlertDialogSaveNoChanges: DialogFragment(), DialogInterface.OnClickListener {

    private var callback: SaveCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as SaveCallback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val adb: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            .setTitle(R.string.save_empty_title) // Заголовок
            .setMessage(R.string.save_empty_message) // Основной текст внутри фрагмента
            .setPositiveButton(R.string.save_empty, this)   // Выход без сохранения
            .setNegativeButton(R.string.cancel_button, this) // Отмена выхода

        return adb.create() // Создание диалогового окна
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            Dialog.BUTTON_POSITIVE -> callback?.saveEmptyDecision(true)
            Dialog.BUTTON_NEGATIVE -> dismiss()
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    interface SaveCallback {
        fun saveEmptyDecision(save: Boolean)
    }
}