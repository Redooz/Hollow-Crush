package com.example.hollowcrush

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.Toast
import com.example.hollowcrush.databinding.ActivityMainBinding
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
    private val charmsImgViews = mutableListOf<ImageView>()

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
        addListenersToCharms()
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
            charmsImgViews.add(imageView)
        }
    }

    private fun addListenersToCharms() {
        for (imgView in charmsImgViews) {
            imgView.setOnTouchListener(object : OnSwipeListener(this) { //Anonymous class
                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    Toast.makeText(this@MainActivity, "Left", Toast.LENGTH_SHORT).show()
                }

                override fun onSwipeRight() {
                    super.onSwipeRight()
                    Toast.makeText(this@MainActivity, "Right", Toast.LENGTH_SHORT).show()
                }

                override fun onSwipeTop() {
                    super.onSwipeTop()
                    Toast.makeText(this@MainActivity, "Top", Toast.LENGTH_SHORT).show()
                }

                override fun onSwipeBottom() {
                    super.onSwipeBottom()
                    Toast.makeText(this@MainActivity, "Bottom", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}