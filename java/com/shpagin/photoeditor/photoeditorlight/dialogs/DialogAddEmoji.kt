package com.shpagin.photoeditor.photoeditorlight.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.shpagin.photoeditor.photoeditorlight.DataClass
import com.shpagin.photoeditor.photoeditorlight.R
import com.shpagin.photoeditor.photoeditorlight.adapters.EmojiAdapter
import com.shpagin.photoeditor.photoeditorlight.databinding.DialogAddEmojiBinding

class DialogAddEmoji: DialogFragment(), EmojiAdapter.EmojiCallback {

    private lateinit var binding: DialogAddEmojiBinding

    private var callback: DialogCallback? = null

    /*
    Прикрепление к родителю
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Определение объекта интерфейса в момент запуска диалогового окна
        callback = context as DialogCallback?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.dialog_add_emoji, container, false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val emojiAdapter = EmojiAdapter(this)
        emojiAdapter.submitList(DataClass.getEmojiList(requireContext().assets))

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 6)
            adapter = emojiAdapter
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    /*
    Открепление от родителя
     */
    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onEmojiSelected(emoji: String) {
        callback?.onAcceptEmoji(emoji)
        dismiss()
    }

    interface DialogCallback {
        fun onAcceptEmoji(emoji: String)
    }
}