package com.sumaqideas.gdrivechanges

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import pub.devrel.easypermissions.EasyPermissions

public object SessionManager {
    private lateinit var _credential: GoogleAccountCredential
    public val credential: GoogleAccountCredential
        get() { return _credential }
    private lateinit var _service: Drive
    public val service: Drive
        get() { return _service}

    init {
        _service = com.google.api.services.drive.Drive.Builder(
                AndroidHttp.newCompatibleTransport()
                , JacksonFactory.getDefaultInstance()
                , _credential
        ).setApplicationName("GDrive Changes").build()
    }

    public fun getNewServiceInstance(
            credential: GoogleAccountCredential
            , transport: HttpTransport = AndroidHttp.newCompatibleTransport()
            , jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
    ): Drive {
        _credential = credential
        _service = com.google.api.services.drive.Drive.Builder(
               transport, jsonFactory, _credential
        ).setApplicationName("GDrive Changes").build()
        return _service
    }

    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        var apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun acquireGooglePlayServices(context: Context, activity: Activity) {
        var apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode, activity)
        }
    }

    fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int, activity: Activity) {
        var apiAvailability = GoogleApiAvailability.getInstance()
        var dialog = apiAvailability.getErrorDialog(
                activity, connectionStatusCode, Permissions.REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog.show()
    }

    public fun isDeviceOnline(context: Context): Boolean {
        var connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connManager.getActiveNetworkInfo()
        return (networkInfo != null && networkInfo.isConnected)
    }

    public fun chooseAccount(activity: Activity) {
        if (EasyPermissions.hasPermissions(activity, Manifest.permission.GET_ACCOUNTS)) {
            var accountName = activity.getPreferences(Context.MODE_PRIVATE)
                    .getString(Permissions.PREF_ACCOUNT_NAME, null)
            if (accountName != null) {
                _credential.setSelectedAccountName(accountName)
                _service = getNewServiceInstance(_credential)
            } else {
                activity.startActivityForResult(
                        SessionManager.credential.newChooseAccountIntent(), Permissions.REQUEST_ACCOUNT_PICKER
                )
            }
        } else {
            EasyPermissions.requestPermissions(
                    activity,
                    "This app needs to access your Google account (via Contacts).",
                    Permissions.REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS
            )
        }
    }
}
