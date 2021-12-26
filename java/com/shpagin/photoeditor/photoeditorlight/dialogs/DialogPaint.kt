package com.shpagin.photoeditor.photoeditorlight.dialogs


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.shpagin.photoeditor.photoeditorlight.DataClass
import com.shpagin.photoeditor.photoeditorlight.R
import com.shpagin.photoeditor.photoeditorlight.adapters.ColorAdapter
import com.shpagin.photoeditor.photoeditorlight.databinding.DialogPaintBinding

class DialogPaint(
    private var brushColor   : Int,
    private var brushSize    : Float,
    private var brushOpacity : Int
    )
    : DialogFragment(), ColorAdapter.ColorCallback {

    private lateinit var binding: DialogPaintBinding
    private var callback: DialogCallback? = null

    private lateinit var seekBarSizeListener: SeekBar.OnSeekBarChangeListener
    private lateinit var seekBarOpacityListener: SeekBar.OnSeekBarChangeListener


    private fun reloadSliderPosition() {
        binding.sizeSeekBar.progress = brushSize.toInt()
        binding.opacitySeekBar.progress = brushOpacity
    }
    private fun applyValuesToExampleCircle(lp: ViewGroup.LayoutParams) {
        lp.width  = (brushSize * 1.2).toInt()
        lp.height = (brushSize * 1.2).toInt()
        binding.exampleCircle.requestLayout()

        binding.exampleCircle.background.alpha = brushOpacity
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as DialogCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.dialog_paint, container, false
        )


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false)
            adapter = ColorAdapter(this@DialogPaint).apply {
                submitList(DataClass.getColorList())
            }
        }

        val lp: ViewGroup.LayoutParams = binding.exampleCircle.layoutParams
        applyValuesToExampleCircle(lp)

        seekBarSizeListener = object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(view: SeekBar?, value: Int, b: Boolean) {
                brushSize = value.toFloat()
                if (value != 0) {
                    lp.width  = (brushSize * 1.2).toInt()
                    lp.height = (brushSize * 1.2).toInt()
                    binding.exampleCircle.requestLayout()
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        }

        seekBarOpacityListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(view: SeekBar?, value: Int, b: Boolean) {
                brushOpacity = value
                binding.exampleCircle.background.alpha = value
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        reloadSliderPosition()

        binding.sizeSeekBar.setOnSeekBarChangeListener(seekBarSizeListener)
        binding.opacitySeekBar.setOnSeekBarChangeListener(seekBarOpacityListener)

        binding.dismissButton.setOnClickListener {
            dismiss()
        }
        binding.acceptButton.setOnClickListener {
            callback?.onAcceptBrushSettings(brushColor, brushSize, brushOpacity)
            dismiss()
        }
    }

    override fun onColorSelected(@ColorInt color: Int) {
        binding.exampleCircle.background.setTint(color)
        brushColor = color
    }


    override fun onDetach() {
        super.onDetach()
        callback = null
    }
    interface DialogCallback {
        fun onAcceptBrushSettings(@ColorInt brushColor: Int, brushSize: Float, brushOpacity: Int)
    }

}