package com.example.ader

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable

import com.example.ader.smil.*
import com.example.ader.utilities.*

/**
 * Represents bookmarks for a given book.
 *
 * Currently the code is limited to processing the existing automatically
 * maintained bookmark. I plan to extend the class to use and process the
 * DAISY 3 bookmarks file. To do so, we will need to:
 * - parse the XML file format and be able to write it out, probably
 * on serialization and explicitly when called to do so.
 * - enable callers to add, list and retrieve individual bookmarks.
 *
 * @author jharty
 */
class Bookmark : Serializable {
    private var pathToBook: String = ""
    /**
     * @return the Filename of the element stored in the bookmark.
     */
    /**
     * Set / Update the Automatic bookmark to store the filename provided.
     * @param filename
     */
    var filename: String? = null
        internal set
    /**
     * @return the NCC index stored in the bookmark.
     */
    /**
     * Set / Update the bookmark with the NCC index to store.
     * @param nccIndex
     */
    var nccIndex: Int = 0
    private var position: Int = 0

    /**
     * Load the bookmarks from a given path. Only called in this class.
     * @param path
     * @throws IOException
     */
    private constructor(path: String) {
        this.pathToBook = ensureTrailingSlash(path)
        this.loadBookmarks()
    }

    private constructor() {
        // Stop callers from calling new Bookmark();
    }

    /**
     * @return the position (offset) into the element referenced by the
     * bookmark.
     */
    fun getPosition(): Int {
        return position
    }

    /**
     * Set / Update the position (offset) into the current element referenced
     * by the bookmark.
     * @param position
     */
    fun setPosition(position: Int) {
        Logging.logInfo(TAG, "Setting position to " + position)
        this.position = position
    }

    /**
     * Save the contents of the bookmark to the specified filename.
     * @param bookmarkFilename
     */
    fun save(bookmarkFilename: String) {
        try {
            val fileOutputStream = FileOutputStream(bookmarkFilename)
            save(fileOutputStream)
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }

    }

    /**
     * Deletes the automatic bookmark.
     *
     * This can help with our automated testing and allow a user
     * to effectively reset the bookmark back to the beginning of their book.
     * From a user's perspective this can help reset the automatic bookmark to
     * the start of the book.
     *
     * Note: this is limited to the current (old format) file as the new
     * file hasn't been implemented yet.
     */
    fun deleteAutomaticBookmark() {
        val bookmarkFile = File(pathToBook + AUTO_BMK)
        if (bookmarkFile.exists()) {
            bookmarkFile.delete()
        }
        // Also reset the local variables.
        filename = null
        nccIndex = 0
        position = 0
    }

    /**
     * Update the automatic bookmark.
     *
     * This is typically used to enable the player to restart from the most
     * recent smilfile.
     */
    fun updateAutomaticBookmark(smilFile: SmilFile) {

        // Compare this.filename with the path to the book.
        if (smilFile.audioSegments.size > 0) {
            // TODO(jharty): This happens to work because audio is typically
            // in a common mp3 file even when there are several segments.
            // However it's gravely flawed and needs cleaning up.
            this.filename = pathToBook + smilFile.audioSegments[0].src!!

            // Only set the start if we don't already have an offset into
            // this file from an existing bookmark.
            // TODO(jharty): Again this code is flawed. Address with above fix.
            if (this.getPosition() <= 0) {
                this.setPosition(smilFile.audioSegments[0].clipBegin.toInt())
                Logging.logInfo(TAG, String.format(
                        "After calling setPosition SMILfile[%s] NCC index[%d] offset[%d]",
                        this.filename, this.nccIndex, this.getPosition()))
            }

        } else if (smilFile.textSegments.size > 0) {
            // TODO(jharty): ditto - fix this logic.
            this.filename = pathToBook + smilFile.textSegments[0].src
            this.setPosition(0)
        }
    }

    /* non javadoc
	 * Extracted this method to improve the testability of this class.
	 */
    @Throws(IOException::class)
    internal fun load(inputStream: InputStream) {
        val `in` = DataInputStream(inputStream)
        try {
            filename = `in`.readUTF()
            nccIndex = `in`.readInt()
            position = `in`.readInt()
            Logging.logInfo(TAG, String.format(
                    "Reading Bookmark details SMILfile[%s] NCC index[%d] offset[%d]",
                    filename, nccIndex, position))
        } catch (ioe: IOException) {
            Logging.logSevereWarning(TAG, "There is a problem reading the contents of the bookmark", ioe)
            // We rely on the rest of the logic to cope e.g. when the book
            // starts being read, the contents of the bookmark will be updated.
        } finally {
            `in`.close()
        }
    }

    /* non javadoc
	 * Extracted this method to improve the testability of this class.
	 */
    @Throws(IOException::class)
    internal fun save(outputStream: OutputStream) {
        val out = DataOutputStream(outputStream)

        Logging.logInfo(TAG, String.format(
                "Saving Bookmark details SMILfile[%s] NCC index[%d] offset[%d]",
                filename, nccIndex, position))
        out.writeUTF(filename)
        out.writeInt(nccIndex)
        out.writeInt(position)
        out.flush()
        out.close()
    }

    private fun ensureTrailingSlash(path: String): String {
        return if (path.endsWith("/") || path.endsWith("\\")) {
            path
        } else {
            path + File.separator
        }
    }

    private fun load(bookmarkFilename: String) {

        if (File(bookmarkFilename).exists()) {
            try {
                val fileInputStream = FileInputStream(bookmarkFilename)
                load(fileInputStream)
            } catch (ioe: IOException) {
                Logging.logSevereWarning(
                        TAG,
                        String.format("Problem opening the old Bookmark file: %s", bookmarkFilename),
                        ioe)
            }

        }
    }

    private fun loadBookmarks() {
        // Hmmm, what to do about dual bookmarks? old and new...
        // Some sort of migration path seems sensible.
        // Let's implement support to read from the current (old) file as I
        // need to write code to create the recommended DAISY 3 bookmark
        // structure (XML based).

        // The following file will not exist currently as we don't create it.
        val newBookmarkFile = pathToBook + BOOKMARKS_FILENAME
        if (File(newBookmarkFile).exists()) {
            Logging.logInfo(TAG, "Apparently the new bookmarks file exists!")
            // TODO(jharty): Add code to parse the XML contents
        } else {
            // Load the old automatic bookmark file, if it exists
            load(pathToBook + AUTO_BMK)
        }

    }

    companion object {
        protected val AUTO_BMK = "auto.bmk"
        private val TAG = "Bookmark"
        private val BOOKMARKS_FILENAME = "bookmarks.bmk"

        /**
         * Create and return a Bookmark
         * @param path
         * @return a new Bookmark if the underlying bookmark is found and loaded.
         */
        fun getInstance(path: String): Bookmark {
            return Bookmark(path)

        }
    }
}
