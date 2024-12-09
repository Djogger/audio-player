package ru.mtuci.neuroplayer.ui.library

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import ru.mtuci.neuroplayer.R
import ru.mtuci.neuroplayer.models.Song

class SongButtonAdapter(
    private val buttonLabels: List<Song>,
    private val onClick: (Int) -> Unit
) :
    RecyclerView.Adapter<SongButtonAdapter.ButtonViewHolder>() {


    class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.songButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.button_layout, parent, false)
        return ButtonViewHolder(view)
    }

    override fun getItemCount(): Int = buttonLabels.size

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val song = buttonLabels[position]
        holder.button.text = "${song.artist} - ${song.title}"
        holder.button.setOnClickListener { onClick(position) }
    }
}