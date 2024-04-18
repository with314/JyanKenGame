package com.example.myapplicationj

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class PlayActivity(
        private val handSigns: Map<String, Int> = mapOf(
            "Rock" to R.drawable.rock,
            "Scissor" to R.drawable.scissor,
            "Paper" to R.drawable.paper
        )
    ) : AppCompatActivity() {

        // ゲームの状態を追跡する変数
        private var winCount = 0
        private var loseCount = 0
        private var drawCount = 0
        private var roundsPlayed = 0 // 5回のじゃんけん結果を追跡
        private var playerLives = 3

        // UI 要素
        private lateinit var rockButton: ImageButton
        private lateinit var scissorButton: ImageButton
        private lateinit var paperButton: ImageButton
        private lateinit var playerMove: ImageView
        private lateinit var enemyMove: ImageView
        private lateinit var resultText: TextView

        // ハートアイコンのImageView
        private lateinit var heart1: ImageView
        private lateinit var heart2: ImageView
        private lateinit var heart3: ImageView

        // プレイヤーのライフを管理する変数

        private lateinit var mediaPlayer: MediaPlayer

        // じゃんけん用ボタンとその他UI要素
        private val handsList = handSigns.keys.toList()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_play)

            initializeUIElements()
            initializeMediaPlayer()
            setButtonListeners()
        }

        // UI要素の初期化
        private fun initializeUIElements() {
            heart1 = findViewById(R.id.heart1)
            heart2 = findViewById(R.id.heart2)
            heart3 = findViewById(R.id.heart3)

            rockButton = findViewById(R.id.rockButton)
            scissorButton = findViewById(R.id.scissorButton)
            paperButton = findViewById(R.id.paperButton)
            playerMove = findViewById(R.id.playerMove)
            enemyMove = findViewById(R.id.enemyMove)
            resultText = findViewById(R.id.resultText)
        }

        // MediaPlayerの初期化
        private fun initializeMediaPlayer() {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.sound1)
            mediaPlayer.start()
        }

        // ボタンリスナーの設定
        private fun setButtonListeners() {
            val buttonToMainActivity = findViewById<Button>(R.id.button_to_main)
            buttonToMainActivity.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // 現在のアクティビティを終了
            }

            val buttonClickSound = MediaPlayer.create(applicationContext, R.raw.btn08)
            rockButton.setOnClickListener {
                playRPSGame(handsList.indexOf("Rock"))
                buttonClickSound.start()
            }
            scissorButton.setOnClickListener {
                playRPSGame(handsList.indexOf("Scissor"))
                buttonClickSound.start()
            }
            paperButton.setOnClickListener {
                playRPSGame(handsList.indexOf("Paper"))
                buttonClickSound.start()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            mediaPlayer.release()
            if (this::mediaPlayer.isInitialized) {
                mediaPlayer.release()
            }
        }

        // じゃんけんのプレイロジック
        private fun playRPSGame(playerSign: Int) {
            val DRAW = 0
            val LOSE = 1
            val WIN = 2

            val gameResult = mapOf(
                DRAW to "あいこ",
                LOSE to "負けだ～",
                WIN to "勝った～",
            )

            val enemySign = kotlin.random.Random.nextInt(3)
            val result = (playerSign - enemySign + 3) % 3

            handSigns[handsList[playerSign]]?.let { playerMove.setImageResource(it) }
            handSigns[handsList[enemySign]]?.let { enemyMove.setImageResource(it) }

            resultText.text = gameResult[result]

            // 結果に応じて勝ち負けを追跡
            when (result) {
                WIN -> winCount++
                LOSE -> loseCount++
                DRAW -> drawCount++
            }

            roundsPlayed++ // プレイした回数を追跡

            // 5回のじゃんけんが終了したら
            if (roundsPlayed >= 5) {
                navigateToGameOverActivity()
            } else if (result == LOSE) {
                    handleLoss()
            }
        }

        // ハートアイコンの更新
        private fun updateHeartIcons() {
            when (playerLives) {
                2 -> heart3.visibility = View.INVISIBLE
                1 -> heart2.visibility = View.INVISIBLE
                0 -> heart1.visibility = View.INVISIBLE
            }
        }

        // ライフが減った際の処理
        private fun handleLoss() {
            playerLives--
            updateHeartIcons()

            if (playerLives <= 0) {
                navigateToGameOverActivity()
            }
        }

        // ゲームオーバー時の画面遷移
        private fun navigateToGameOverActivity() {
            val intent = Intent(this, GameOverActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            // 勝ち負けの結果を `Intent` に含める
            intent.putExtra("winCount", winCount)
            intent.putExtra("loseCount", loseCount)
            intent.putExtra("drawCount", drawCount)

            startActivity(intent)
            finish()
        }
}