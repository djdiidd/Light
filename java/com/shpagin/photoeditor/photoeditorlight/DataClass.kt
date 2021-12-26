package com.shpagin.photoeditor.photoeditorlight

import android.content.res.AssetManager
import android.graphics.Color
import android.util.Log
import com.shpagin.photoeditor.photoeditorlight.models.ColorObject
import com.shpagin.photoeditor.photoeditorlight.models.EmojiObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class DataClass {
    // Доступные извне, методы для получения списков
    companion object {
        fun getColorList() : List<ColorObject> {
            return listOf(
                ColorObject( Color.WHITE  ),
                ColorObject( Color.CYAN   ),
                ColorObject( Color.YELLOW ),
                ColorObject( Color.BLUE   ),
                ColorObject( Color.GRAY   ),
                ColorObject( Color.GREEN  ),
                ColorObject( Color.LTGRAY ),
                ColorObject( Color.MAGENTA),
                ColorObject( Color.rgb(32, 178,170)),
                ColorObject( Color.rgb(240,116,39 )),
                ColorObject( Color.rgb(240,234,214)),
                ColorObject( Color.rgb(204,57, 123)),
                ColorObject( Color.rgb(71, 45, 204)),
                ColorObject( Color.rgb(204,149,114)),
                ColorObject( Color.rgb(146,204,114)),
            )
        }
        fun getEmojiList(assets: AssetManager) : List<EmojiObject> {
            var reader: BufferedReader? = null
            val list = arrayListOf<String>()
            val finalList = arrayListOf<EmojiObject>()

            try {
                reader = BufferedReader(InputStreamReader(assets.open("emojis.txt"), "UTF-8"));
                list.addAll(reader.readText().split(" "))
                list.forEach {
                    finalList.add(EmojiObject(it))
                }
            } catch (e: Exception) {
                Log.e("MyTag", "Reading asset error")
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (e: IOException) {
                        Log.e("MyTag", "Cannot close BufferedReader: $reader")
                    }
                }
            }
            return finalList
        }
    }
}