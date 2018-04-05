package com.sumaqideas.gdrivechanges

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.drive.DriveScopes
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import android.R.id.edit
import android.content.SharedPreferences
import android.content.Context.MODE_PRIVATE
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.media.MediaCas
import android.widget.Toast
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), SessionManager.Callback {
    public var progressBar: ProgressBar? = null
    public var signinButton: Button? = null
    public var datatv: TextView? = null
    public var findButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signin_button.setOnClickListener {
            SessionManager.newServiceInstance(this, this)
        }
    }

    override fun onPermissionGranted() {
        startDataActivity()
    }

    override fun onPermissionRejected() {
        return
    }

    override fun onActivityResult(
            requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SessionManager.onActivityResult(requestCode, resultCode, data, this, this)
    }

    override public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun startDataActivity() {
        var intent = Intent(this, DataActivity::class.java)
        startActivity(intent)
    }
}

public object Preferences {
    public const val PREF_ACCOUNT_NAME = "accountName"
}

