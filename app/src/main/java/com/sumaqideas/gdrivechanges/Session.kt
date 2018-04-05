package com.sumaqideas.gdrivechanges

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaCas
import android.net.ConnectivityManager
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

public object SessionManager {
    public val SCOPES: List<String> = arrayListOf(DriveScopes.DRIVE)
    public const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
    public const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    public const val REQUEST_AUTHORIZATION = 1001
    public const val REQUEST_ACCOUNT_PICKER = 1000

    public interface Callback {
        public fun onPermissionGranted()
        public fun onPermissionRejected()
    }

    private lateinit var _credential: GoogleAccountCredential
    public val credential: GoogleAccountCredential
        get() { return _credential }
    private lateinit var _service: Drive
    public val service: Drive
        get() { return _service}

    public fun newServiceInstance(
            activity: Activity,
            callback: SessionManager.Callback
            , credential: GoogleAccountCredential
                = GoogleAccountCredential.usingOAuth2(activity, SCOPES)
                            .setBackOff(ExponentialBackOff())
            , transport: HttpTransport = AndroidHttp.newCompatibleTransport()
            , jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
    ) {
        _credential = credential
        _credential.selectedAccountName = activity.getPreferences(Context.MODE_PRIVATE)
                .getString(Preferences.PREF_ACCOUNT_NAME, null)
        if (_credential.selectedAccountName == null) {
            chooseAccount(activity, callback)
        }
        _service = com.google.api.services.drive.Drive.Builder(
               transport, jsonFactory, _credential
        ).setApplicationName("GDrive Changes").build()
        callback.onPermissionGranted()
    }

    private fun isGooglePlayServicesAvailable(activity: Activity): Boolean {
        var apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun acquireGooglePlayServices(activity: Activity) {
        var apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode, activity)
        }
    }

    fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int, activity: Activity) {
        var apiAvailability = GoogleApiAvailability.getInstance()
        var dialog = apiAvailability.getErrorDialog(
                activity, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog.show()
    }

    public fun isDeviceOnline(activity: Activity): Boolean {
        var connManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connManager.getActiveNetworkInfo()
        return (networkInfo != null && networkInfo.isConnected)
    }

    @AfterPermissionGranted(SessionManager.REQUEST_PERMISSION_GET_ACCOUNTS)
    public fun chooseAccount(activity: Activity, callback: SessionManager.Callback) {
        if (EasyPermissions.hasPermissions(activity, Manifest.permission.GET_ACCOUNTS)) {
            var accountName = activity.getPreferences(Context.MODE_PRIVATE)
                    .getString(Preferences.PREF_ACCOUNT_NAME, null)
            if (accountName != null) {
                _credential.setSelectedAccountName(accountName)
                newServiceInstance(activity, callback)
            } else {
                activity.startActivityForResult(
                        SessionManager.credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER
                )
            }
        } else {
            EasyPermissions.requestPermissions(
                    activity,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS
            )
        }
    }

    fun onActivityResult(
            requestCode: Int, resultCode: Int, data: Intent?, activity: Activity, callback: Callback) {
        when (requestCode) {
            SessionManager.REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(
                        activity,
                        "This app requires Google Play Services. Please install "
                                + "Google Play Services on your device and relaunch this app.",
                        Toast.LENGTH_LONG
                ).show()
            } else {
                callback.onPermissionGranted()
            }

            SessionManager.REQUEST_ACCOUNT_PICKER -> if (resultCode == Activity.RESULT_OK && data != null &&
                    data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    val settings = activity.getPreferences(Context.MODE_PRIVATE)
                    val editor = settings.edit()
                    editor.putString(Preferences.PREF_ACCOUNT_NAME, accountName)
                    editor.apply()
                    SessionManager.credential.selectedAccountName = accountName
                    SessionManager.newServiceInstance(activity, callback)
                }
            }
            SessionManager.REQUEST_AUTHORIZATION -> if (resultCode == Activity.RESULT_OK) {
                callback.onPermissionGranted()
            }
        }
    }
}
