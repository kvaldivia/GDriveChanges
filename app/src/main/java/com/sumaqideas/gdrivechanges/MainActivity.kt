package com.sumaqideas.gdrivechanges

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), SessionManager.Callback {
    var progressBar: ProgressBar? = null
    var signinButton: Button? = null
    var datatv: TextView? = null
    var findButton: FloatingActionButton? = null

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun startDataActivity() {
        var intent = Intent(this, FileListActivity::class.java)
        startActivity(intent)
    }
}

public object Preferences {
    public const val PREF_ACCOUNT_NAME = "accountName"
}

