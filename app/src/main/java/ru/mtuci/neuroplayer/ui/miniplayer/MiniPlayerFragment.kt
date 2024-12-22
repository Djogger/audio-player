package ru.mtuci.neuroplayer.ui.miniplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import ru.mtuci.neuroplayer.R
import ru.mtuci.neuroplayer.databinding.FragmentMiniplayerBinding
import ru.mtuci.neuroplayer.utils.PlaylistManager
import ru.mtuci.neuroplayer.utils.SongManager
import java.util.Locale
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class MiniPlayerFragment : Fragment() {
    private var _binding: FragmentMiniplayerBinding? = null
    private val handler = Handler(Looper.getMainLooper())

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var notInPlayer: Boolean = true
        set(value) {
            if (value && isPlaying) {
                updateInfo()
                binding.root.visibility = View.VISIBLE
            } else {
                binding.root.visibility = View.GONE
            }
            field = value
        }
    private var isPlaying: Boolean = false
        set(value) {
            if (value && notInPlayer) {
                updateInfo()
                binding.root.visibility = View.VISIBLE
            } else {
                binding.root.visibility = View.GONE
            }
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMiniplayerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        root.visibility = View.GONE

        SongManager.setOnPauseListener {
            isPlaying = false
        }

        SongManager.setOnPlayListener {
            isPlaying = true
        }

        val navController =
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main)

        root.setOnClickListener {
            navController.navigate(R.id.navigation_player)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            notInPlayer = when (destination.id) {
                R.id.navigation_player -> false
                else -> true
            }
        }

        binding.seekBar.setOnTouchListener { _, _ -> navController.navigate(R.id.navigation_player);true }

        return root
    }

    private fun updateInfo() {
        if (PlaylistManager.song == null) {
            isPlaying = false
            return
        }
        val song = PlaylistManager.song!!
        binding.songTitleMiniPlayer.text = song.artist + "-" + song.title
        val duration = song.duration.toDuration(DurationUnit.MILLISECONDS)
        binding.songDuration.text =
            String.format(
                Locale.ROOT,
                "%02d:%02d",
                duration.inWholeMinutes,
                duration.inWholeSeconds % 60
            )
        binding.seekBar.max = song.duration
        handler.removeCallbacksAndMessages(null)
        updateSeekBar(SongManager.mediaPlayer!!)
    }

    private fun updateSeekBar(mediaPlayer: MediaPlayer) {
        val duration = mediaPlayer.currentPosition.toDuration(DurationUnit.MILLISECONDS)
        binding.SongPlayed.text =
            String.format("%02d:%02d", duration.inWholeMinutes, duration.inWholeSeconds % 60)
        binding.seekBar.progress = mediaPlayer.currentPosition
        if (mediaPlayer.isPlaying) {
            handler.postDelayed({
                if (!isRemoving) {
                    updateSeekBar(mediaPlayer)
                }
            }, 10)
        } else {
            handler.removeCallbacksAndMessages(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}