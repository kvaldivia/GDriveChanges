package com.sumaqideas.gdrivechanges

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException

import kotlinx.android.synthetic.main.activity_data.*

class DataActivity : AppCompatActivity(), MakeRequestTask.Callback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        setSupportActionBar(toolbar)

        pick_file_fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onTaskStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTaskFinished(results: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTaskFailed(error: Exception) {
        if (error is GooglePlayServicesAvailabilityIOException) {
            SessionManager.showGooglePlayServicesAvailabilityErrorDialog(
                    (error as GooglePlayServicesAvailabilityIOException)
                            .connectionStatusCode
            , this)
        } else if (error is UserRecoverableAuthIOException) {
            startActivityForResult((error as UserRecoverableAuthIOException).intent
                    , Permissions.REQUEST_AUTHORIZATION)
        } else {

        }
    }

}
