package ru.mtuci.neuroplayer.ui.transformer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.mtuci.neuroplayer.databinding.FragmentTransformerBinding

class TransformerFragment : Fragment() {
    private var _binding: FragmentTransformerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[TransformerViewModel::class.java]

        _binding = FragmentTransformerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTransformer


        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->

            if (uri == null){
                dashboardViewModel.text.observe(viewLifecycleOwner) {
                    textView.text = "Не найдено"
                }
            }
            else{
                dashboardViewModel.text.observe(viewLifecycleOwner) {
                    textView.text = uri.toString()
                }
            }

        }

//        getContent.launch("audio/*")

//        val textView: TextView = binding.textTransformer
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = result.toString()
//        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}