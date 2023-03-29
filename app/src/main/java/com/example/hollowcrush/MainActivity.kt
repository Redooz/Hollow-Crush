package com.example.hollowcrush

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
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
    private var notCharm = R.drawable.damage
    private lateinit var mHandler: Handler
    private val interval = 100
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) = try {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        score = 0
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
            val isBlank = charmsImgViews[i].tag == notCharm
            val notValid = listOf(6, 7, 14, 15, 22, 23, 31, 38, 39, 46, 47, 54, 55)
            if (!notValid.contains(i)) {
                var x = i
                if (charmsImgViews[x++].tag as Int == choseCharm && !isBlank
                    && charmsImgViews[x++].tag as Int == choseCharm
                    && charmsImgViews[x].tag as Int == choseCharm
                ) {
                    score += 3
                    this.binding.scoreBar.progress = score
                    this.binding.txtViewScore.text = this.binding.scoreBar.progress.toString()

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
        moveDownCharms()
    }

    private fun checkColumnForThree() {
        for (i in 0 until 47) {
            val chosedCharm = charmsImgViews[i].tag as Int
            val isBlank = charmsImgViews[i].tag == this.notCharm

            var x = i
            if (charmsImgViews[x].tag as Int == chosedCharm && !isBlank
                && charmsImgViews[x + numOfBlocks].tag as Int == chosedCharm
                && charmsImgViews[x + 2 * numOfBlocks].tag as Int == chosedCharm
            ) {
                score += 3
                this.binding.scoreBar.progress = score
                this.binding.txtViewScore.text = this.binding.scoreBar.progress.toString()

                charmsImgViews[x].setImageResource(notCharm)
                charmsImgViews[x].tag = notCharm
                x += numOfBlocks

                charmsImgViews[x].setImageResource(notCharm)
                charmsImgViews[x].tag = notCharm
                x += numOfBlocks

                charmsImgViews[x].setImageResource(notCharm)
                charmsImgViews[x].tag = notCharm
            }

        }
        moveDownCharms()
    }

    private var repeatChecker: Runnable = Runnable {
        run {
            try {
                checkRowForThree()
                checkColumnForThree()
                moveDownCharms()
            } finally {
                mHandler.postDelayed(repeatChecker, interval.toLong())
            }
        }
    }

    fun moveDownCharms() {
        val firstRow = listOf(0, 1, 2, 3, 4, 5, 6, 7)

        for (i in 55 downTo 0) {
            if (charmsImgViews[i + numOfBlocks].tag as Int == notCharm) {
                charmsImgViews[i + numOfBlocks].setImageResource(charmsImgViews[i].tag as Int)
                charmsImgViews[i + numOfBlocks].tag = charmsImgViews[i].tag as Int
                charmsImgViews[i].setImageResource(notCharm)
                charmsImgViews[i].tag = notCharm

                if (firstRow.contains(i) && charmsImgViews[i].tag as Int == notCharm) {
                    val randomCharm = Random.nextInt(0, charms.size)
                    charmsImgViews[i].setImageResource(charms[randomCharm])
                    charmsImgViews[i].tag = charms[randomCharm]
                }
            }
        }

        for (i in 0 until 8) {
            if (charmsImgViews[i].tag as Int == notCharm){
                val randomCharm = Random.nextInt(0, charms.size)
                charmsImgViews[i].setImageResource(charms[randomCharm])
                charmsImgViews[i].tag = charms[randomCharm]
            }
        }
    }

    private fun startRepeat() {
        repeatChecker.run()
    }

}

