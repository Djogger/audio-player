package ru.mtuci.neuroplayer.ui.transformer

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.mtuci.neuroplayer.databinding.FragmentTransformerBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class TransformerFragment : Fragment() {
    private var _binding: FragmentTransformerBinding? = null
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

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) {
                dashboardViewModel.text.observe(viewLifecycleOwner) {
                    textView.text = "Не найдено"
                }
            } else {
                sendAudioFileToServer(uri)
            }
        }

        // Запускаем выбор аудиофайла
        getContent.launch("audio/*")

        return root
    }

    private fun sendAudioFileToServer(uri: Uri) {
        Thread {
            try {
                // Получаем имя файла из Uri
                val fileName = getFileName(uri)
                Log.d("TransformerFragment", "Выбранный файл: $fileName")

                // Получаем InputStream из URI
                val inputStreamFromUri: InputStream? = requireContext().contentResolver.openInputStream(uri)
                if (inputStreamFromUri == null) {
                    throw Exception("Не удалось открыть InputStream из URI")
                }

                // Подключаемся к серверу
                val socket = Socket("10.42.0.1", 8888)
                val outputStream: OutputStream = socket.getOutputStream()
                val inputStream = socket.getInputStream()

                // Читаем аудиофайл и отправляем его на сервер
                val buffer = ByteArray(40096)
                var bytesRead: Int

                // Отправляем данные на сервер
                while (inputStreamFromUri.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.flush()

                // Читаем обработанный файл, который сервер отправляет обратно
                val outputFile = File(requireContext().cacheDir, "processed_audio_file.wav") // Сохраняем в кэш
                val fileOutputStream = FileOutputStream(outputFile)

                // Читаем данные из responseInputStream и записываем в файл
                while (true) {
                    bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) break // Конец потока
                    fileOutputStream.write(buffer, 0, bytesRead)
                }

                fileOutputStream.flush()
                fileOutputStream.close()

                // Закрываем соединение
                inputStream.close()
                outputStream.close()
                socket.close()

                // Показать Toast в основном потоке
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Файл успешно отправлен и обработан: ${outputFile.absolutePath}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("TransformerFragment", "Ошибка при отправке или получении файла: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "УРА Ошибка, кто бы мог подумать: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst()) {
                    result = it.getString(nameIndex)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}