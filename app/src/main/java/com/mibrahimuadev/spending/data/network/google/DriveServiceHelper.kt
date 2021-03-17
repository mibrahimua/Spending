package com.mibrahimuadev.spending.data.network.google

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.util.Pair
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.mibrahimuadev.spending.data.entity.DriveEntity
import com.mibrahimuadev.spending.ui.backup.DriveData
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
class DriveServiceHelper(private val mDriveService: Drive) {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    fun createFolderDrive(): DriveEntity {
        /**
         * Disini dilakukan pemanggilan function searchFolderDrive
         * jika folder tidak ditemukan maka buat folder baru
         */
        val fileMetadata: File = File()
        fileMetadata.name = "SpendingAppBackup"
        fileMetadata.mimeType = "application/vnd.google-apps.folder"

        val file: File = mDriveService.files().create(fileMetadata)
            .setFields("id")
            .execute()
        Log.i("GoogleDrive", "Folder ID: ${file.name}, ${file.id}")

        return DriveEntity(
            fileType = "folder",
            fileId = file.id,
            fileName = file.name,
            lastModified = null
        )
    }

    fun searchFolderDrive(): DriveEntity {
        /**
         * Disini dilakukan pencarian nama folder
         * jika folder tidak ditemukan maka return false dan sebaliknya
         */
        var pageToken: String? = null
        var countFolder = 0
        do {
            val result: FileList = mDriveService.files().list()
                .setQ("mimeType = 'application/vnd.google-apps.folder' and trashed = false")
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .setPageToken(pageToken)
                .execute()
            for (file in result.files) {
                Log.i("GoogleDrive", "Found folder: %s (%s) ${file.name}, ${file.id}")
                file.name.let {
                    if (it.equals("SpendingAppBackup")) {
                        countFolder += 1
                        return DriveEntity(
                            fileType = "folder",
                            fileId = file.id,
                            fileName = file.name,
                            lastModified = null
                        )
                    }

                }
            }
            pageToken = result.nextPageToken
        } while (pageToken != null)
        return DriveEntity(
            fileType = "folder",
            fileId = null,
            fileName = null,
            lastModified = null
        )
    }

    @SuppressLint("SdCardPath")
    fun uploadFileBackupDrive(folderId: String): DriveData {
        try {
            val fileMetadata = File()
            fileMetadata.name = "spending_database.db"
            fileMetadata.parents = Collections.singletonList(folderId)
            val filePath =
                java.io.File("/data/data/com.mibrahimuadev.spending/files/BackupDB/spending_database.db")
            val mediaContent = FileContent("application/x-sqlite3", filePath)
            val file: File = mDriveService.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute()

            Log.i(
                "GoogleDrive",
                "uploading file backup to drive with detail file : ${file.createdTime}"
            )

            return DriveData(
                fileType = "files",
                fileId = file.id,
                fileName = file.name,
                lastModified = null
            )
        } catch (e: IOException) {
            Log.i("GoogleDrive", "failed to upload file because : $e")

            return DriveData(
                fileType = "files",
                fileId = null,
                fileName = null,
                lastModified = null
            )
        }
    }

    fun searchFileBackupDrive(): MutableList<String> {

        var pageToken: String? = null
        val listFileId = mutableListOf<String>()
        do {
            val result: FileList = mDriveService.files().list()
                .setQ("mimeType = 'application/x-sqlite3' and trashed = false")
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .setPageToken(pageToken)
                .execute()
            for (file in result.files) {
                Log.i("GoogleDrive", "Found old file: %s (%s) ${file.name}, ${file.id}")
                file.name.let {
                    listFileId.add(file.id)
                }
            }
            pageToken = result.nextPageToken
        } while (pageToken != null)

        return listFileId
    }

    fun deleteOldFileBackupDrive(listFileId: MutableList<String>) {
        Log.i("GoogleDrive", "There is total : ${listFileId.size} old file backup")

        for (fileId in listFileId) {
            try {
                Log.i("GoogleDrive", "Deleting file backup with fileId : $fileId")
                mDriveService.files().delete(fileId).execute()
            } catch (e: IOException) {
                Log.i("GoogleDrive", "Cannot delete file backup with fileId : $fileId")
            }

        }
    }

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    fun createFile(): Task<String> {
        return Tasks.call(mExecutor, {
            val metadata =
                File()
                    .setParents(listOf("root"))
                    .setMimeType("text/plain")
                    .setName("Untitled file")
            val googleFile =
                mDriveService.files().create(metadata).execute()
                    ?: throw IOException("Null result when requesting file creation.")
            googleFile.id
        })
    }

    /**
     * Opens the file identified by `fileId` and returns a [Pair] of its name and
     * contents.
     */
    fun readFile(fileId: String?): Task<Pair<String, String>> {
        return Tasks.call(mExecutor,
            {

                // Retrieve the metadata as a File object.
                val metadata =
                    mDriveService.files()[fileId].execute()
                val name = metadata.name
                mDriveService.files()[fileId].executeMediaAsInputStream().use { `is` ->
                    BufferedReader(InputStreamReader(`is`)).use { reader ->
                        val stringBuilder = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line)
                        }
                        val contents = stringBuilder.toString()
                        return@call Pair.create(
                            name,
                            contents
                        )
                    }
                }
            })
    }

    /**
     * Updates the file identified by `fileId` with the given `name` and `content`.
     */
    fun saveFile(fileId: String?, name: String?, content: String?): Task<Void?> {
        return Tasks.call(mExecutor, {

            // Create a File containing any metadata changes.
            val metadata =
                File().setName(name)

            // Convert content to an AbstractInputStreamContent instance.
            val contentStream = ByteArrayContent.fromString("text/plain", content)

            // Update the metadata and contents.
            mDriveService.files().update(fileId, metadata, contentStream).execute()
            null
        })
    }

    /**
     * Returns a [FileList] containing all the visible files in the user's My Drive.
     *
     *
     * The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the [Google
 * Developer's Console](https://play.google.com/apps/publish) and be submitted to Google for verification.
     */
    fun queryFiles(): Task<FileList> {
        return Tasks.call(mExecutor,
            { mDriveService.files().list().setSpaces("drive").execute() })
    }

    /**
     * Returns an [Intent] for opening the Storage Access Framework file picker.
     */
    fun createFilePickerIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        return intent
    }

    /**
     * Opens the file at the `uri` returned by a Storage Access Framework [Intent]
     * created by [.createFilePickerIntent] using the given `contentResolver`.
     */
    fun openFileUsingStorageAccessFramework(
        contentResolver: ContentResolver, uri: Uri?
    ): Task<Pair<String, String>> {
        return Tasks.call(mExecutor,
            {

                // Retrieve the document's display name from its metadata.
                var name: String
                contentResolver.query(uri!!, null, null, null, null).use { cursor ->
                    name = if (cursor != null && cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        cursor.getString(nameIndex)
                    } else {
                        throw IOException("Empty cursor returned for file.")
                    }
                }

                // Read the document's contents as a String.
                var content: String
                contentResolver.openInputStream(uri).use { `is` ->
                    BufferedReader(InputStreamReader(`is`)).use { reader ->
                        val stringBuilder = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line)
                        }
                        content = stringBuilder.toString()
                    }
                }
                Pair.create(name, content)
            })
    }
}
