package com.shpagin.photoeditor.photoeditorlight.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shpagin.photoeditor.photoeditorlight.R
import com.shpagin.photoeditor.photoeditorlight.databinding.ListEmojiItemBinding
import com.shpagin.photoeditor.photoeditorlight.models.EmojiObject

class EmojiAdapter(parent: Fragment)
    : ListAdapter<EmojiObject, EmojiAdapter.EmojiHolder>(EmojiDiffUtilCallback()) {

    val callback: EmojiCallback = parent as EmojiCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiHolder {
        return EmojiHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_emoji_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EmojiHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EmojiHolder(private val binding: ListEmojiItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                callback.onEmojiSelected(getItem(layoutPosition).emoji)
            }
        }

        fun bind(emoji: EmojiObject) {
            with(binding) {
                viewModel = emoji
                executePendingBindings()
            }
        }
    }

    interface EmojiCallback {
        fun onEmojiSelected(emoji: String)
    }

}

class EmojiDiffUtilCallback: DiffUtil.ItemCallback<EmojiObject>() {
    override fun areItemsTheSame(oldItem: EmojiObject, newItem: EmojiObject): Boolean {
        return newItem == oldItem
    }

    override fun areContentsTheSame(oldItem: EmojiObject, newItem: EmojiObject): Boolean {
        return newItem.emoji == oldItem.emoji
    }

}