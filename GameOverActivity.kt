package com.example.myapplicationj

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat


class GameOverActivity : AppCompatActivity() {
    private lateinit var rootLayout: LinearLayout
    private lateinit var resultImageView: ImageView
    private var mp14: MediaPlayer? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)

        // UI要素を取得
        rootLayout = findViewById(R.id.root_layout)
        resultImageView = findViewById(R.id.result_image_view)

        // 他のUI要素を定義
        val heartImageView = findViewById<ImageView>(R.id.heart)
        val honeImageView = findViewById<ImageView>(R.id.hone)
        val birdImageView = findViewById<ImageView>(R.id.bird)
        val againImageView = findViewById<ImageView>(R.id.again)
        val resultTextView = findViewById<TextView>(R.id.result_text_view)
        val buttonToMainActivity = findViewById<Button>(R.id.button_to_main)

        val commonAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_animation)

        // Intentから勝ち負けの結果を取得
        val winCount = intent.getIntExtra("winCount", 0)
        val loseCount = intent.getIntExtra("loseCount", 0)
        val drawCount = intent.getIntExtra("drawCount", 0)

        againImageView.setOnClickListener {

            buttonToMainActivity.performClick()
        }

        // ボタンリスナーの設定
        buttonToMainActivity.setOnClickListener {
            // MainActivityに遷移
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // メディアプレーヤーのリソース解放
            mp14?.release()
            mp14 = null
            // 現在のアクティビティを終了
            finish()
        }

        // heart、hone、birdのImageViewに対してアニメーションを設定
        animateImageView(R.id.heart, R.drawable.heart, 3000L)
        animateImageView(R.id.hone, R.drawable.hone, 3000L)
        animateImageView(R.id.bird, R.drawable.bird, 3000L)

        val resultMessage = when {
            winCount > loseCount && winCount > drawCount -> "勝ったね！！"
            loseCount > winCount && loseCount > drawCount -> "負けた＞＜"
            drawCount > winCount && drawCount > loseCount -> "引き分けだよ～"
            else -> "もう一回～"
        }

        // 結果を画面に表示
        val spannableText = SpannableString("""
            $resultMessage
        
            ★勝ち: $winCount
            ★負け: $loseCount
            ★引き分け: $drawCount
            """.trimIndent())

        // resultMessageの部分に大きい文字サイズを適用
        spannableText.setSpan(
            RelativeSizeSpan(1.7f),
            0,
            resultMessage.length,
            0
        )

        resultTextView.text = spannableText

        applyImageAndAnimation(resultMessage,  resultImageView, commonAnimation)

        // resultImageViewのZ値を取得
        val resultZ = ViewCompat.getZ(resultImageView)

        // heartImageView, honeImageView, birdImageViewのZ値をresultZより高く設定
        ViewCompat.setZ(heartImageView, resultZ + 1.0f)
        ViewCompat.setZ(honeImageView, resultZ + 1.0f)
        ViewCompat.setZ(birdImageView, resultZ + 1.0f)
    }

    private fun animateImageView(imageViewId: Int, imageResource: Int, duration: Long) {
        val imageView = findViewById<ImageView>(imageViewId)
        imageView.setImageResource(imageResource)

        // アニメーションの作成
        val translateAnimation = TranslateAnimation(0f, 0f, rootLayout.height.toFloat(), 0f)
        translateAnimation.duration = duration
        translateAnimation.interpolator = AccelerateDecelerateInterpolator()

        // アニメーション終了時のリスナー
        translateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // アニメーション終了後にImageViewを削除
                rootLayout.removeView(imageView)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        // アニメーションの開始
        imageView.startAnimation(translateAnimation)
    }

    // 結果に応じた画像とアニメーションを適用する関数
    private fun applyImageAndAnimation(resultMessage: String, resultImageView: ImageView, commonAnimation: Animation) {
        // 結果に応じた画像の設定
        when (resultMessage) {
            "勝ったね！！" -> {
                resultImageView.setImageResource(R.drawable.win)
                resultImageView.visibility = View.VISIBLE
                addRandomImages(R.drawable.heart, 30, commonAnimation)
            }
            "負けた＞＜" -> {
                resultImageView.setImageResource(R.drawable.lose)
                resultImageView.visibility = View.VISIBLE
                addRandomImages(R.drawable.hone, 30, commonAnimation)
            }
            "引き分けだよ～" -> {
                resultImageView.setImageResource(R.drawable.aiko)
                resultImageView.visibility = View.VISIBLE
                addRandomImages(R.drawable.bird, 30, commonAnimation)
            }
            "もう一回～" -> {
                resultImageView.setImageResource(R.drawable.again)
                resultImageView.visibility = View.VISIBLE
                addRandomImages(R.drawable.again, 30, commonAnimation)
            }
        }
    }

    private fun addRandomImages(imageResource: Int, count: Int, animation: Animation) {
        // ルートレイアウトの幅と高さを取得
        val rootWidth = rootLayout.width
        val rootHeight = rootLayout.height

        // レイアウトのサイズが有効でない場合はレイアウトリスナーを追加して再度実行
        if (rootWidth <= 0 || rootHeight <= 0){
            rootLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    // リスナーを削除
                    rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    // サイズが正確に取得できたら画像を追加
                    addRandomImages(imageResource, count, animation)
                }
            })
            return
        }

        // 指定された数の画像を追加
        for(i in 1..count) {
            val imageView = ImageView(this)
            imageView.setImageResource(imageResource)

            // ランダムな幅と高さを設定
            val width = (50..200).random()
            val height = (50..200).random()

            // ランダムな位置を設定
            val layoutParams = FrameLayout.LayoutParams(width, height)
            layoutParams.leftMargin = (0 until (rootWidth - width)).random().coerceIn(0, rootWidth - width)
            layoutParams.topMargin = (0 until (rootHeight - height)).random().coerceIn(0, rootHeight - height)

            imageView.layoutParams = layoutParams

            // ビューが上に移動するアニメーション
            val translateAnimation = TranslateAnimation(0f, 0f, 0f, -rootHeight.toFloat())
            translateAnimation.startOffset = 0L // 遅延なしで即時開始
            translateAnimation.duration = 4000L
            translateAnimation.interpolator = AccelerateInterpolator()  // イージングを変える

            // アニメーション終了時にビューを削除
            translateAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    rootLayout.removeView(imageView)
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })

            imageView.startAnimation(translateAnimation)
            rootLayout.addView(imageView)
        }
    }
}