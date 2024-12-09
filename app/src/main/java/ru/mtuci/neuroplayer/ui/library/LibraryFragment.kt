package ru.mtuci.neuroplayer.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.content.pm.PackageManager
import ru.mtuci.neuroplayer.MainActivity
import ru.mtuci.neuroplayer.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            (activity as MainActivity).requestPermission.launch(Manifest.permission.READ_MEDIA_AUDIO)
            return onCreateView(inflater, container, savedInstanceState)
        }
        val libraryViewModel =
            ViewModelProvider(this)[LibraryViewModel::class.java]

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView: RecyclerView = binding.songsLayout

        recyclerView.layoutManager = LinearLayoutManager(context)

        val audio = context?.let { getAudio(it) }

        if (audio != null) {
            recyclerView.adapter = SongButtonAdapter(
                buttonLabels = audio.toList(),
                onClick = { position ->
                    val action = LibraryFragmentDirections.actionPlaySong(
                        audio.toTypedArray(),
                        position
                    )
                    findNavController().navigate(action)
                }
            )
        }

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        println((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}