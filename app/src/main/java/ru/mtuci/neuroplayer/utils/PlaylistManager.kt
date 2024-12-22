package ru.mtuci.neuroplayer.utils

import android.content.Context
import android.media.MediaPlayer
import ru.mtuci.neuroplayer.models.Song

object PlaylistManager {
    private var playlist: Array<Song>? = null
    private var pos: Int = 0
    val song: Song? get() = playlist?.get(pos)

    fun playPlaylist(context: Context, playlist: Array<Song>, pos: Int): MediaPlayer{
        this.playlist = playlist
        this.pos = pos
        return SongManager.playSong(context, playlist[pos])
    }

    fun playNext(context: Context): MediaPlayer?{
        return playlist?.let {
            if (pos == it.size - 1){
                pos = 0
            }else{
                pos++
            }
            SongManager.playSong(context, it[pos])
        }
    }

    fun playPrevious(context: Context): MediaPlayer?{
        return playlist?.let {
            if (pos == 0){
                pos = it.size - 1
            }else{
                pos--
            }
            SongManager.playSong(context, it[pos])
        }
    }


}