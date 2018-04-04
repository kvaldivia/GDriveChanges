package com.sumaqideas.gdrivechanges

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.MediaCas
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.android.synthetic.main.activity_data.*
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class MainActivity : AppCompatActivity() {
    public val SCOPES: List<String> = arrayListOf(DriveScopes.DRIVE)
    public var progressBar: ProgressBar? = null
    public var signinButton: Button? = null
    public var datatv: TextView? = null
    public var findButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signin_button.setOnClickListener {
            var credential = GoogleAccountCredential.usingOAuth2(applicationContext, SCOPES)
                    .setBackOff(ExponentialBackOff())

            SessionManager.getNewServiceInstance(credential)

            if (SessionManager.service != null) {
                var intent = Intent(this, DataActivity::class.java)
                startActivity(intent)
            }
        }
    }



    @AfterPermissionGranted(Permissions.REQUEST_PERMISSION_GET_ACCOUNTS)
    public fun chooseAccount() {
    }


}

public object Permissions {
    public const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
    public const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    public const val REQUEST_AUTHORIZATION = 1001
    public const val PREF_ACCOUNT_NAME = "accountName"
    public const val REQUEST_ACCOUNT_PICKER = 1000
}
