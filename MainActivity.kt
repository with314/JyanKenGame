package com.example.myapplicationj

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var mp14: MediaPlayer? = null // 追加

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ボタン要素（オブジェクト）を取得
        val buttonToPlayActivity = findViewById<Button>(R.id.button_to_play)
        mp14 = MediaPlayer.create(applicationContext, R.raw.btn14) // 追加
        // ボタンタップ時のイベントリスナー
        buttonToPlayActivity.setOnClickListener {
            val intent = Intent(this, PlayActivity::class.java)
            startActivity(intent)
            finish()
            mp14?.start()
        }
    }
}