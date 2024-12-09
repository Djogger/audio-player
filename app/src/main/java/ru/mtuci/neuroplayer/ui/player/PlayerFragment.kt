package ru.mtuci.neuroplayer.ui.player

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import ru.mtuci.neuroplayer.R
import ru.mtuci.neuroplayer.databinding.FragmentPlayerBinding
import ru.mtuci.neuroplayer.models.Song
import ru.mtuci.neuroplayer.utils.SongManager
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PlayerFragment : Fragment() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var audioManager: AudioManager
    private val songManager = SongManager
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: PhoneStateListener
    private lateinit var _binding: FragmentPlayerBinding
    private val args: PlayerFragmentArgs by navArgs()

    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val playerViewModel =
            ViewModelProvider(this)[PlayerViewModel::class.java]
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root

    }

    private fun playSong(playlist: Array<Song>, position: Int){
        val song = playlist[position]
        binding.SongName.text = song.title
        binding.artistName.text = song.artist
        val duration = song.duration.toDuration(DurationUnit.MILLISECONDS)
        binding.sondDuration.text =
            String.format("%02d:%02d", duration.inWholeMinutes, duration.inWholeSeconds % 60)
        mediaPlayer = songManager.playSong(requireContext(), song)
        val buttonPlayStop: ImageButton = _binding.ButtonPlayStop
        seekBar = _binding.seekBar

        seekBar.max = mediaPlayer.duration

        updateSeekBar()
        binding.songBack.isEnabled = position > 0
        binding.songBack.setOnClickListener {
            playSong(playlist, position-1)
        }
        binding.songForward.isEnabled = position < playlist.size - 1
        println("$position, ${playlist.size}, ${position<playlist.size -1}")
        binding.songForward.setOnClickListener {
            playSong(playlist, position+1)
        }

        buttonPlayStop.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                buttonPlayStop.setImageResource(R.drawable.pause_button)
                updateSeekBar()
            } else {
                mediaPlayer.pause()
                buttonPlayStop.setImageResource(R.drawable.play_button)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        songManager.setOnCompleteListener {
            buttonPlayStop.setImageResource(R.drawable.play_button)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playSong(args.playlist, args.position)

        super.onViewCreated(view, savedInstanceState)

    }

    private fun updateSeekBar() {
        val duration = mediaPlayer.currentPosition.toDuration(DurationUnit.MILLISECONDS)
        binding.songPlayed.text =
            String.format("%02d:%02d", duration.inWholeMinutes, duration.inWholeSeconds % 60)
        seekBar.progress = mediaPlayer.currentPosition
        if (mediaPlayer.isPlaying) {
            handler.postDelayed({
                if (!isRemoving) {
                    updateSeekBar()
                }
            }, 10)
        }else{
            handler.removeCallbacksAndMessages(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)

    }
}