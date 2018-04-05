package com.sumaqideas.gdrivechanges

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList


public object TasksCoordinator {

}

public class FileListFetch : AsyncTask<GoogleAccountCredential, Int, List<File>>() {
    private lateinit var service: com.google.api.services.drive.Drive
    override fun doInBackground(vararg params: GoogleAccountCredential?): List<File> {
        val credential = params[0] as GoogleAccountCredential
        return emptyList()
    }

}

public class MakeRequestTask(val credential: GoogleAccountCredential, val activity: Activity, val callback: MakeRequestTask.Callback) : AsyncTask<Void, Void, List<String>>() {
    private var lastError: Exception? = null
    private var transport: HttpTransport = AndroidHttp.newCompatibleTransport()
    private var jsonFactory = JacksonFactory.getDefaultInstance()
    private var service = com.google.api.services.drive.Drive.Builder(
            transport, jsonFactory, credential
    ).setApplicationName("GDrive Changes").build()

    public interface Callback {
        public fun onTaskStarted()
        public fun onTaskFinished(results: List<String>)
        public fun onTaskFailed(error: Exception)
    }

    override fun doInBackground(vararg params: Void?): List<String>? {
        try {
            return getDataFromApi()
        } catch (e: Exception) {
            lastError = e
            cancel(true)
            return null
        }
    }

    public fun getDataFromApi() : List<String> {
        var fileInfo = ArrayList<String>()
        var result: FileList = service.files().list().setPageSize(10)
                .setFields("nextPageToken, files(id, name)").execute()
        var files: List<File> = result.files

        if (files != null) {
            for (file: File in files) {
                fileInfo.add(String.format("%s (%s)\n", file.name, file.id))
            }
        }
        return fileInfo
    }

    override fun onPreExecute() {
    }

    override fun onPostExecute(result: List<String>?) {
        if (result == null || result.size == 0) {
            Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
            return
        }
        callback.onTaskFinished(result)
    }

    override fun onCancelled(result: List<String>?) {
        if (lastError != null) {
            callback.onTaskFailed(lastError as Exception)
        } else {
            Toast.makeText(activity, "Request cancelled", Toast.LENGTH_LONG).show()
        }
    }
}
