package by.korsakovegor.photomap.utils

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        fun isInternetAvailable(context: Context?): Boolean {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }

        fun showConnectionAlertDialog(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Connection Alert")
                .setMessage("Please check your internet connection")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        fun showAlertDialog(
            context: Context,
            title: String,
            text: String,
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
                .setMessage(text)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        fun showAlertDialog(
            context: Context,
            title: String,
            text: String,
            l: DialogInterface.OnClickListener?
        ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
                .setMessage(text)
                .setPositiveButton("Yes") { dialog, which -> l?.onClick(dialog, which) }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        fun getFormattedDate(date: Long): String =
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(Date(date * 1000))

        fun getFormattedDateTime(date: Long): String =
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            .format(Date(date * 1000))

        @RequiresApi(Build.VERSION_CODES.S)
        fun doVibrate(vib: VibratorManager) {
            vib.defaultVibrator.vibrate(
                VibrationEffect.createOneShot(
                    100,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }
}