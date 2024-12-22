package ru.mtuci.neuroplayer.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity.AUDIO_SERVICE
import androidx.appcompat.app.AppCompatActivity.TELEPHONY_SERVICE
import ru.mtuci.neuroplayer.models.Song

object SongManager {
    private var isPlaying = false
    var mediaPlayer: MediaPlayer? = null
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null
    private var onPlayListener: () -> Unit = {}
    private var onPauseListener: () -> Unit = {}


    private fun initPhoneStateListener(context: Context) {
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {

                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        if (mediaPlayer?.isPlaying == true) {
                            pause()
                        }
                    }

                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        if (mediaPlayer?.isPlaying == true) {
                            pause()
                        }
                    }

                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (mediaPlayer?.isPlaying != true && isPlaying) {
                            play()
                        }
                    }
                }
            }
        }
        telephonyManager = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        // Создание слушателя состояния вызова


        // Регистрация слушателя
        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }



    fun playSong(context: Context, song: Song): MediaPlayer {
        mediaPlayer?.stop()
        mediaPlayer = MediaPlayer.create(context, song.path)
        play()
        if (phoneStateListener == null) {
            initPhoneStateListener(context)
        }
        return mediaPlayer!!
    }

    fun setOnCompleteListener(listener: () -> Unit) {
        mediaPlayer?.setOnCompletionListener {
            isPlaying = false
            listener()
        }
    }

    fun setOnPauseListener(listener: () -> Unit) {
        onPauseListener = listener
    }

    fun setOnPlayListener(listener: () -> Unit) {
        onPlayListener = listener
    }

    fun play() {
        mediaPlayer?.start()
        isPlaying = true
        onPlayListener()
    }

    fun pause() {
        mediaPlayer?.pause()
        isPlaying = false
        onPauseListener()
    }

    fun destroy() {
        telephonyManager?.listen(
            phoneStateListener,
            PhoneStateListener.LISTEN_NONE
        )
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}