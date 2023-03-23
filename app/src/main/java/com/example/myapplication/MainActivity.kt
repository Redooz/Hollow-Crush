package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import com.example.myapplication.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var widthOfScreen = 0
    private var heigthOfScreen = 0
    private val numOfBlocks = 8 //Per row & column
    private var widthOfBlock = 0
    private val charms = listOf(
        R.drawable.fragile_strength, R.drawable.grubsong,
        R.drawable.hiveblood, R.drawable.jonis_blessing,
        R.drawable.quick_focus, R.drawable.void_heart
    )

    override fun onCreate(savedInstanceState: Bundle?) = try {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        widthOfScreen = displayMetrics.widthPixels
        heigthOfScreen = displayMetrics.heightPixels

        widthOfBlock = widthOfScreen / numOfBlocks
        loadGameBoard()
    } catch (ex: Exception) {
        error(ex)
    }

    private fun loadGameBoard() {
        binding.gameContainer.rowCount = numOfBlocks
        binding.gameContainer.columnCount = numOfBlocks
        binding.gameContainer.layoutParams.width = widthOfScreen
        binding.gameContainer.layoutParams.height = widthOfScreen

        for (i in 0 until numOfBlocks*numOfBlocks) {
            val imageView = ImageView(this)
            imageView.id = i
            imageView.layoutParams = LayoutParams(widthOfBlock,widthOfBlock)
            imageView.maxHeight = widthOfBlock
            imageView.maxWidth = widthOfBlock

            val randomImage = Random.nextInt(0,charms.size)
            imageView.setImageResource(charms[randomImage])
            binding.gameContainer.addView(imageView)
        }
    }
}