package com.shpagin.photoeditor.photoeditorlight.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.shpagin.photoeditor.photoeditorlight.R

class AlertDialogExit(var userMadeChanges: Boolean): DialogFragment(), DialogInterface.OnClickListener {

    private var callback: ExitCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as ExitCallback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val adb: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        if (userMadeChanges) {
            adb.setTitle(R.string.exit_title) // Заголовок
                .setMessage(R.string.exit_message) // Основной текст внутри фрагмента
                .setPositiveButton(R.string.accept_exit, this)   // Выход без сохранения
                .setNegativeButton(R.string.cancel_button, this) // Отмена выхода
                .setNeutralButton(R.string.save_and_exit, this)  // Выход с сохранением
        } else {
            adb.setPositiveButton(R.string.accept_exit, this) // Положительный ответ
                .setNegativeButton(R.string.cancel_button, this) // Отрицательный ответ
                .setMessage(R.string.exit_title) // Основной текст внутри фрагмента
        }
        return adb.create() // Создание диалогового окна
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            Dialog.BUTTON_POSITIVE -> callback?.exitDecision(1)
            Dialog.BUTTON_NEGATIVE -> callback?.exitDecision(0)
            Dialog.BUTTON_NEUTRAL  -> callback?.exitDecision(2)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    interface ExitCallback {
        fun exitDecision(exit: Int)
    }
}