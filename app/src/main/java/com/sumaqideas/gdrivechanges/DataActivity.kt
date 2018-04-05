package com.sumaqideas.gdrivechanges

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException

import kotlinx.android.synthetic.main.activity_data.*
import kotlinx.android.synthetic.main.content_data.*

public open class DataActivity : AppCompatActivity(), MakeRequestTask.Callback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        setSupportActionBar(toolbar)
        data_tv.visibility = View.VISIBLE

        /*
        pick_file_fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        */

        pick_file_fab.setOnClickListener { view ->
            MakeRequestTask(SessionManager.credential, this, this).execute()
        }
    }

    override fun onTaskStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTaskFinished(results: List<String>) {
        var result = ""
        for (item: String in results) {
            result += item + "\n"
        }
        data_tv.setText(result)
    }

    override fun onTaskFailed(error: Exception) {
        if (error is GooglePlayServicesAvailabilityIOException) {
            SessionManager.showGooglePlayServicesAvailabilityErrorDialog(
                    (error as GooglePlayServicesAvailabilityIOException).connectionStatusCode
                    , this@DataActivity)
        } else if (error is UserRecoverableAuthIOException) {
            this@DataActivity.startActivityForResult((error as UserRecoverableAuthIOException).intent
                    , SessionManager.REQUEST_AUTHORIZATION)
        } else {

        }
    }

}

