package ru.mtuci.myapplication

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.missio_bottom_of_the_deep_blue_sea)

        val buttonPlayStop: Button = findViewById(R.id.ButtonPlayStop)
        seekBar = findViewById(R.id.seekBar) // Инициализация SeekBar

        // Установка максимального значения SeekBar
        seekBar.max = mediaPlayer.duration

        buttonPlayStop.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                buttonPlayStop.setText(getString(R.string.pause_str))
                updateSeekBar() // Начинаем обновлять SeekBar
            } else {
                mediaPlayer.pause()
                buttonPlayStop.setText(getString(R.string.play_str))
            }
        }

        // Установка слушателя для SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress) // Перемотка аудио
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Можно добавить логику, если нужно
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Можно добавить логику, если нужно
            }
        })
    }

    private fun updateSeekBar() {
        seekBar.progress = mediaPlayer.currentPosition // Обновляем позицию SeekBar
        if (mediaPlayer.isPlaying) {
            handler.postDelayed({ updateSeekBar() }, 1000) // Обновление SeekBar каждую секунду
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release() // Освобождение ресурсов
    }
}