package ru.mtuci.neuroplayer.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(val id: String, val artist: String, val title: String, val duration: Int, val path: Uri) :
    Parcelable