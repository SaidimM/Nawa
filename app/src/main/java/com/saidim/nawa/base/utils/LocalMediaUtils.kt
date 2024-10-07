package com.saidim.nawa.base.utils

import LogUtil
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.blankj.utilcode.util.ArrayUtils
import com.blankj.utilcode.util.Utils
import com.saidim.nawa.Constants.LYRIC_DIR
import com.saidim.nawa.media.local.bean.Music
import com.saidim.nawa.media.local.enums.MediaType
import java.io.*
import java.util.*


object LocalMediaUtils {
    //定义一个集合，存放从本地读取到的内容
    var list: MutableList<Music> = arrayListOf()

    private val TAG = this.javaClass.simpleName
    fun getMusic(context: Context): List<Music> {
        val list = mutableListOf<Music>()
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val music = Music()
                var name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                var singer =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val duration =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                val album =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val albumId =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                //list.add(song);
                //把歌曲名字和歌手切割开
                //song.setName(name);
                music.singer = singer
                music.path = path
                music.duration = duration.toLong()
                music.size = size
                music.id = id
                music.album = album
                music.albumId = albumId.toLong()
                if (size > 1000 * 800) {
                    if (name.contains("-")) {
                        val str = name.split("-".toRegex()).toTypedArray()
                        singer = str[0]
                        music.singer = singer
                        name = str[1]
                        if (name.indexOf('.') != -1) name = name.substring(0, name.indexOf('.'))
                        music.name = name
                    } else {
                        music.name = name
                    }
                    list.add(music)
                }
            }
        }
        cursor!!.close()
        return list
    }

    //    转换歌曲时间的格式
    fun formatTime(time: Int): String {
        return if (time / 1000 % 60 < 10) {
            (time / 1000 / 60).toString() + ":0" + time / 1000 % 60
        } else {
            (time / 1000 / 60).toString() + ":" + time / 1000 % 60
        }
    }

    /**
     * 根据专辑ID获取专辑封面图
     * @param album_id 专辑ID
     * @return
     */
    fun getAlbumArt(context: Context, album_id: Long): String? {
        val mUriAlbums = "content://media/external/audio/albums"
        val projection = arrayOf("album_art")
        val cur = context.contentResolver.query(
            Uri.parse("$mUriAlbums/$album_id"),
            projection,
            null,
            null,
            null
        )
        var album_art: String? = null
        if (cur!!.count > 0 && cur.columnCount > 0) {
            cur.moveToNext()
            album_art = cur.getString(0)
        }
        cur.close()
        var path: String? = null
        if (album_art != null) {
            path = album_art
        } else {
            //path = "drawable/music_no_icon.png";
            //bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
        }
        return path
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * bitmap保存为file
     */
    @Throws(IOException::class)
    fun bitmapToFile(
        filePath: String,
        bitmap: Bitmap?, quality: Int
    ): File? {
        if (bitmap != null) {
            val file = File(
                filePath.substring(
                    0,
                    filePath.lastIndexOf(File.separator)
                )
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            val bos = BufferedOutputStream(
                FileOutputStream(filePath)
            )
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
            bos.flush()
            bos.close()
            return file
        }
        return null
    }

    fun writeStringToFile(path: String, context: String) {
        val saveFile = File(path)
        if (!File(LYRIC_DIR).exists()) File(LYRIC_DIR).mkdir()
        if (!saveFile.exists()) saveFile.createNewFile()
        var fo: FileOutputStream? = null
        try {
            fo = FileOutputStream(saveFile)
            fo.write(context.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fo!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun readFile(path: String): String {
        val getFile = File(path)
        var fs: FileInputStream? = null
        var content = ""
        try {
            fs = FileInputStream(getFile)
            val length = fs.available()
            val bytes = ByteArray(length)
            fs.read(bytes)
            content = String(bytes, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fs!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return content
    }

    fun getAlbumArtBitmap(musicFilePath: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(musicFilePath)  // Set the file path as the data source

            // Extract the embedded album art as a byte array
            val art: ByteArray? = retriever.embeddedPicture

            // If album art is available, convert it to a Bitmap and return
            return if (art != null) {
                BitmapFactory.decodeByteArray(art, 0, art.size)
            } else {
                null  // Return null if no album art is found
            }
        } catch (e: Exception) {
            e.printStackTrace()  // Handle any errors that occur during metadata extraction
            return null
        } finally {
            retriever.release()  // Always release the retriever when done
        }
    }

}