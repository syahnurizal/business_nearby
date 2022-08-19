package com.vorto.businessnearby.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.vorto.businessnearby.R

class Permission

    //CONSTRUCTOR
    (private val mContext: Context) {

    fun requestLocation() {
        Dexter.withContext(mContext)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    locationCallback?.invoke()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        showSettingsDialog("location permission to proceed")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    var locationCallback: (() -> Unit)? = null

    fun requestCamera() {
        Dexter.withContext(mContext)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    cameraCallback?.invoke()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        showSettingsDialog("camera permission to proceed")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    var cameraCallback: (() -> Unit)? = null

    fun requestStorage() {
        Dexter.withContext(mContext)
            .withPermission(Manifest.permission.READ_PHONE_STATE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    storageCallback?.invoke()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        showSettingsDialog("storage permission to get user device id")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    var storageCallback: (() -> Unit)? = null

    fun requestPhoneState() {
        Dexter.withContext(mContext)
            .withPermission(Manifest.permission.READ_PHONE_STATE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    phoneStateCallback?.invoke(true)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    phoneStateCallback?.invoke(false)
                    if (response.isPermanentlyDenied) {
                        showSettingsDialog("phone state permission to proceed")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    var phoneStateCallback: ((Boolean) -> Unit)? = null

    fun requestCall() {
        Dexter.withContext(mContext)
            .withPermission(Manifest.permission.CALL_PHONE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    callCallback?.invoke()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        showSettingsDialog("call permission to proceed")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    var callCallback: (() -> Unit)? = null

    //SHOW SETTING DIALOG
    private fun showSettingsDialog(message: String) {

        if (!(mContext as Activity).isFinishing) {
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Need Permissions")
            builder.setMessage(mContext.getString(R.string.app_name) + " needs " + message + ". You can grant them in app settings.")
            builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->
                dialog.cancel()
                openSettings()
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()

        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", mContext.packageName, null)
        intent.data = uri
        (mContext as Activity).startActivityForResult(intent, 101)
    }
}

