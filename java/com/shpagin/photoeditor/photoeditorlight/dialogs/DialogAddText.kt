package com.shpagin.photoeditor.photoeditorlight.dialogs

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shpagin.photoeditor.photoeditorlight.DataClass
import com.shpagin.photoeditor.photoeditorlight.R
import com.shpagin.photoeditor.photoeditorlight.adapters.ColorAdapter

/**
 * Диалоговое окно для добавления текста на фотографии
 */
class DialogAddText: DialogFragment(), View.OnClickListener, ColorAdapter.ColorCallback {

    /**  Элементы диалогового окна  */
    private lateinit var viewRoot     : View      // Глобальная разметка диалогового окна.
    private lateinit var acceptButton : Button    // Кнопка подтверждения.
    private lateinit var dismissButton: Button    // Кнопка отмены.
    private lateinit var editTextView : EditText  // Поле ввода текста.
    private lateinit var exampleCircle: ImageView // Цветной круг, отображающий выбранный цвет

    private var textColor: Int = -1  // Итоговый цвет, выбранный пользователем

    private var callbacks: DialogCallback? = null  // Интерфейс для отправки значений родителю

    /*
    Прикрепление к родителю
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Определение объекта интерфейса в момент запуска диалогового окна
        callbacks = context as DialogCallback?
    }

    /*
    Создание элементов диалогового окна
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewRoot = inflater // Надуваем на экране элементы основываясь на переданной разметке
            .inflate(R.layout.dialog_add_text, container, false)
        // От родительского элемента экрана находим дочерних
        dismissButton = viewRoot.findViewById(R.id.dismiss_button)
        acceptButton  = viewRoot.findViewById(R.id.accept_button)
        editTextView  = viewRoot.findViewById(R.id.input_text)
        exampleCircle = viewRoot.findViewById(R.id.example_circle)
        // Устанавливаем слушатели для кнопок отмены и подтверждения
        dismissButton.setOnClickListener(this)
        acceptButton.setOnClickListener(this)

        showKeyboard()  // Показываем клавиатуру

        val adapter = ColorAdapter(this)  // Создаем адаптер для списка
        adapter.submitList(DataClass.getColorList())  // Добавляем список цветов

        // Находим список на экране (RecyclerView) и устанавливаем layoutManager и adapter
        viewRoot.findViewById<RecyclerView>(R.id.recycler_view).apply {
            this.layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            this.adapter = adapter
        }
        return viewRoot  // Возвращаем корневой элемент разметки
    }

    /*
    Обработка нажатий для 2 кнопок
     */
    override fun onClick(view: View?) {
        // Обработка 1 из 2 вариантов
        when(view?.id) {
            // Нажатие на кнопку отмены
            dismissButton.id -> {
                dismiss()
            }
            // Нажатие на кнопку подтверждения
            acceptButton.id -> {
                val text = editTextView.text.toString()  // Получаем текст с поля ввода на экране
                // Если строка не пустая то передаем ее в родителя
                if (text.isNotEmpty()) {
                    callbacks?.onAcceptTextSettings(text, textColor)
                    dismiss()
                } else {  // Если строка пустая устанавливаем ошибку
                    editTextView.error = getString(R.string.error_empty_field)
                }
            }
        }
    }

    /**
     * Метод для выдвижения клавиатуры
     */
    private fun showKeyboard() {
        editTextView.requestFocus();
        editTextView.isFocusableInTouchMode = true
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editTextView, InputMethodManager.SHOW_FORCED)
    }

    /*
    Открепление от родителя
     */
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    /**
     * Интерфейс для передачи данных в другой объект
     */
    interface DialogCallback {
        fun onAcceptTextSettings(text: String, color: Int)
    }

    /*
    Определение метода интерфейса для получения выбранного цвета
     */
    override fun onColorSelected(color: Int) {
        textColor = color // Сохраняем цвет
        exampleCircle.background.setTint(color)
    }
}