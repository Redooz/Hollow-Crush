package com.example.hollowcrush

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
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
    private var charmToBeDragged = 0
    private var charmToBeReplaced = 0
    private var notCharm = R.drawable.ic_launcher_background
    private lateinit var mHandler: Handler
    private val interval = 100

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

        mHandler = Handler()
        startRepeat()

    } catch (ex: Exception) {
        error(ex)
    }

    private fun loadGameBoard() {
        binding.gameContainer.rowCount = numOfBlocks
        binding.gameContainer.columnCount = numOfBlocks
        binding.gameContainer.layoutParams.width = widthOfScreen
        binding.gameContainer.layoutParams.height = widthOfScreen

        for (i in 0 until numOfBlocks * numOfBlocks) {
            val imageView = ImageView(this)
            imageView.id = i
            imageView.layoutParams = LayoutParams(widthOfBlock, widthOfBlock)
            imageView.maxHeight = widthOfBlock
            imageView.maxWidth = widthOfBlock

            val randomImage = Random.nextInt(0, charms.size)
            imageView.setImageResource(charms[randomImage])
            imageView.tag = charms[randomImage]
            binding.gameContainer.addView(imageView)
            charmsImgViews.add(imageView)
        }
    }

    private fun addListenersToCharms() {
        for (imgView in charmsImgViews) {
            imgView.setOnTouchListener(object : OnSwipeListener(this) { //Anonymous class
                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    charmToBeDragged = imgView.id
                    charmToBeReplaced = charmToBeDragged - 1
                    charmInterchange()
                }

                override fun onSwipeRight() {
                    super.onSwipeRight()
                    charmToBeDragged = imgView.id
                    charmToBeReplaced = charmToBeDragged + 1
                    charmInterchange()
                }

                override fun onSwipeTop() {
                    super.onSwipeTop()
                    charmToBeDragged = imgView.id
                    charmToBeReplaced = charmToBeDragged - numOfBlocks
                    charmInterchange()
                }

                override fun onSwipeBottom() {
                    super.onSwipeBottom()
                    charmToBeDragged = imgView.id
                    charmToBeReplaced = charmToBeDragged + numOfBlocks
                    charmInterchange()
                }
            })
        }
    }

    private fun charmInterchange() {
        val background = charmsImgViews[charmToBeReplaced].tag as Int
        val background1 = charmsImgViews[charmToBeDragged].tag as Int

        charmsImgViews[charmToBeDragged].setImageResource(background)
        charmsImgViews[charmToBeReplaced].setImageResource(background1)
        charmsImgViews[charmToBeDragged].tag = background
        charmsImgViews[charmToBeReplaced].tag = background1
    }

    private fun checkRowForThree() {
        for (i in 0 until 62) {
            val choseCharm = charmsImgViews[i].tag as Int
            var isBlank = charmsImgViews[i].tag == notCharm
            val notValid = listOf(6, 7, 14, 15, 22, 23, 31, 38, 39, 46, 47, 54, 55)
            if (!notValid.contains(i)) {
                var x = i
                if (charmsImgViews[x++].tag as Int == choseCharm && !isBlank
                    && charmsImgViews[x++].tag as Int == choseCharm
                    && charmsImgViews[x].tag as Int == choseCharm) {

                    charmsImgViews[x].setImageResource(notCharm)
                    charmsImgViews[x].tag = notCharm
                    x--

                    charmsImgViews[x].setImageResource(notCharm)
                    charmsImgViews[x].tag = notCharm
                    x--

                    charmsImgViews[x].setImageResource(notCharm)
                    charmsImgViews[x].tag = notCharm
                }
            }
        }
    }

    var repeatChecker:Runnable = Runnable {
        run {
            try {
                checkRowForThree()
            } finally {
                mHandler.postDelayed(repeatChecker, interval.toLong())
            }
        }
    }

    fun startRepeat() {
        repeatChecker.run()
    }

}

