package ru.mtuci.myapplication

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private val handler = Handler()
    private lateinit var audioManager: AudioManager
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: PhoneStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.lady_gaga_if_my_friends_could_see_me_now)

        val buttonPlayStop: Button = findViewById(R.id.ButtonPlayStop)
        seekBar = findViewById(R.id.seekBar)

        seekBar.max = mediaPlayer.duration

        buttonPlayStop.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                buttonPlayStop.setText(R.string.pause_str)
                updateSeekBar()
            } else {
                mediaPlayer.pause()
                buttonPlayStop.setText(R.string.play_str)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress) // Перемотка аудио
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mediaPlayer.setOnCompletionListener {
            buttonPlayStop.setText(R.string.play_str)
        }

        // Инициализация AudioManager и TelephonyManager для отслеживания поступающего звонка
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        // Создание слушателя состояния вызова
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {

                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause() // Остановка воспроизведения при входящем звонке
                            buttonPlayStop.setText(R.string.play_str)
                        }
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause() // Остановка воспроизведения при звонке с телефона
                            buttonPlayStop.setText(R.string.play_str)
                        }
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (mediaPlayer.currentPosition != 0) {
                            mediaPlayer.start()
                            buttonPlayStop.setText(R.string.pause_str)
                            updateSeekBar()
                        }
                    }
                }
            }
        }

        // Регистрация слушателя
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
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
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE) // Отмена регистрации слушателя
    }
}