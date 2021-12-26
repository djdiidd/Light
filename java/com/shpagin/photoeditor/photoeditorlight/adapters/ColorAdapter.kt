package com.shpagin.photoeditor.photoeditorlight.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shpagin.photoeditor.photoeditorlight.R
import androidx.databinding.DataBindingUtil
import com.shpagin.photoeditor.photoeditorlight.models.ColorObject
import com.shpagin.photoeditor.photoeditorlight.databinding.ListColorItemBinding

/**
 * Адаптер для списка RecyclerView, который производит управление и заполнение самого списка
 */
class ColorAdapter(parent: Fragment)
    : ListAdapter<ColorObject, ColorAdapter.ColorHolder>(ColorDiffUtilCallback()) {

    // Интерфейс для отправки значений родителю
    private var callback: ColorCallback = parent as ColorCallback

    /*
    Создание элемента списка (цветной карты)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
        // Определение элемента списка основываясь на файле разметки
        return ColorHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_color_item,
                parent,
                false
            )
        )
    }
    /*
    Заполнение ранее созданного элемента списка необходимым цветом
     */
    override fun onBindViewHolder(holder: ColorHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Внутренний класс, который заполняет элемент списка соответствующим позиции значением
     */
    inner class ColorHolder(private val binding: ListColorItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {  // Установка слушателя на корневое представление (View) при создании объекта;
            binding.root.setOnClickListener {  // по нажатию передаем цвет выбранный цвет в родителя
                callback.onColorSelected(getItem(layoutPosition).color)
            }
        }

        /**
         * Метод для заполнения элемента списка необходимым значением
         */
        fun bind(color: ColorObject) {
            with(binding) {
                viewModel = color
                executePendingBindings()
            }
        }
    }

    /**
     * Интерфейс для сохранения цвета, который выбрал пользователь
     */
    interface ColorCallback {
        fun onColorSelected(color: Int)
    }
}

/**
 * Класс, который необходим классу выше для определения разницы между списками
 */
class ColorDiffUtilCallback: DiffUtil.ItemCallback<ColorObject>() {

    override fun areItemsTheSame(oldItem: ColorObject, newItem: ColorObject): Boolean {
        return oldItem.color == newItem.color
    }

    override fun areContentsTheSame(oldItem: ColorObject, newItem: ColorObject): Boolean {
        return oldItem == newItem
    }
}