package by.korsakovegor.photomap.utils

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog

class Utils {
    companion object {
        fun isInternetAvailable(context: Context?): Boolean {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }

        fun showDeleteAlertDialog(
            context: Context,
            text: String,
            l: DialogInterface.OnClickListener
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Alert")
                .setMessage(text)
                .setPositiveButton("Yes") { dialog, which -> l.onClick(dialog, which) }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        @RequiresApi(Build.VERSION_CODES.S)
        fun doVibrate(vib: VibratorManager){
            vib.defaultVibrator.vibrate(
                VibrationEffect.createOneShot(
                    100,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }
}