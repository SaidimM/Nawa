package com.saidim.nawa.base.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import coil.Coil
import coil.request.ImageRequest
import com.saidim.nawa.ServiceLocator
import com.saidim.nawa.media.local.bean.Music
import java.util.regex.Pattern
import kotlin.random.Random

fun String.toFilenameWithoutExtension() = try {
    Pattern.compile("(?<=.)\\.[^.]+$").matcher(this).replaceAll("")
} catch (e: Exception) {
    e.printStackTrace()
    this
}

fun Long.toAlbumArtURI(): Uri = ContentUris.withAppendedId(
    "content://media/external/audio/albumart".toUri(),
    this
)

fun Long.toContentUri(): Uri = ContentUris.withAppendedId(
    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    this
)

fun List<Music>.findIndex(song: Music?) = indexOfFirst {
    it.id == song?.id && it.albumId == song.albumId
}

fun Long.waitForCover(context: Context, onDone: (Bitmap?, Boolean) -> Unit) {
    Coil.imageLoader(context).enqueue(
        ImageRequest.Builder(context)
            .data(if (ServiceLocator.getPreference().isCovers) toAlbumArtURI() else null)
            .target(
                onSuccess = { onDone(it.toBitmap(), false) },
                onError = { onDone(null, true) }
            )
            .build()
    )
}

fun <T> List<T>.getRandomItem(): T {
    if (this.isEmpty()) {
        throw NoSuchElementException("List is empty")
    }
    val randomIndex = Random.nextInt(this.size)
    return this[randomIndex]
}
