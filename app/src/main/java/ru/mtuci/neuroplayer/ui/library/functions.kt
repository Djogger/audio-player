package ru.mtuci.neuroplayer.ui.library

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import ru.mtuci.neuroplayer.models.Song

fun getAudio(context: Context) : ArrayList<Song> {
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.TITLE,
//        MediaStore.Audio.Media.DATA,
//        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA
    )
//    cursorLoader.selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
    val selection = null
    val songs = ArrayList<Song>()
    context.contentResolver.query(
        uri,
        projection,
        selection,
        null,
        null
    )?.use { cursor ->
        while (cursor.moveToNext()){
            val song = Song(
                id = cursor.getString(0),
                artist = cursor.getString(1),
                title = cursor.getString(2),
                duration = cursor.getInt(3),
                path = Uri.parse(cursor.getString(4))
            )
            songs.add(song)
        }
    }

    return songs
}