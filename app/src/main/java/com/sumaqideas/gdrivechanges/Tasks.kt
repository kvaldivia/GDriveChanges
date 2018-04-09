package com.sumaqideas.gdrivechanges

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.model.*


public object TasksCoordinator {

}

public class WatchFile(
        val credential: GoogleAccountCredential, val activity: Activity
        , val callback: MakeRequestTask.Callback)
    : AsyncTask<GoogleAccountCredential, Int, ChangeList>() {

    public lateinit var onFileChangeListener: WatchFile.OnFileChangeListener

    public interface OnFileChangeListener {
        public fun update(change: Change)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        if (onFileChangeListener == null)
            throw ExceptionInInitializerError("There must be a OnFileChangeListener")
    }

    private lateinit var savedStartPageToken: String
    private var lastError: Exception? = null
    private var transport: HttpTransport = AndroidHttp.newCompatibleTransport()
    private var jsonFactory = JacksonFactory.getDefaultInstance()
    private var service = com.google.api.services.drive.Drive.Builder(
            transport, jsonFactory, credential
    ).setApplicationName("GDrive Changes").build()
    override fun doInBackground(vararg params: GoogleAccountCredential?): ChangeList? {
        val credential = params[0] as GoogleAccountCredential
        val pageToken = StartPageToken().startPageToken;
        var changes: ChangeList? = null

        while (pageToken != null) {
            changes = service.changes().list(pageToken).execute()
            for (change: Change in changes.changes) {
                onFileChangeListener.update(change)
            }
        }

        if (changes?.newStartPageToken != null)
            savedStartPageToken = changes.newStartPageToken

        return changes
    }

}

public class MakeRequestTask(
        val credential: GoogleAccountCredential, val activity: Activity
        , val callback: MakeRequestTask.Callback) : AsyncTask<Void, Void, List<File>>() {
    private var lastError: Exception? = null
    private var transport: HttpTransport = AndroidHttp.newCompatibleTransport()
    private var jsonFactory = JacksonFactory.getDefaultInstance()
    private var service = com.google.api.services.drive.Drive.Builder(
            transport, jsonFactory, credential
    ).setApplicationName("GDrive Changes").build()

    public interface Callback {
        public fun onTaskStarted()
        public fun onTaskFinished(results: List<File>)
        public fun onTaskFailed(error: Exception)
    }

    override fun doInBackground(vararg params: Void?): List<File>? {
        try {
            return getDataFromApi()
        } catch (e: Exception) {
            lastError = e
            cancel(true)
            return null
        }
    }

    public fun getDataFromApi() : List<File> {
        var fileInfo = ArrayList<String>()
        var result: FileList = service.files().list().setPageSize(10)
                .setFields("nextPageToken, files(id, name)").execute()
        return result.files
    }

    override fun onPreExecute() {
        Log.d("MakeRequestTask", "task started")
    }

    override fun onPostExecute(result: List<File>?) {
        if (result == null || result.size == 0) {
            Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
            return
        }
        Log.d("MakeRequestTask", "task finished")
        callback.onTaskFinished(result)
    }

    override fun onCancelled(result: List<File>?) {
        if (lastError != null) {
            callback.onTaskFailed(lastError as Exception)
        } else {
            Toast.makeText(activity, "Request cancelled", Toast.LENGTH_LONG).show()
        }
    }
}
