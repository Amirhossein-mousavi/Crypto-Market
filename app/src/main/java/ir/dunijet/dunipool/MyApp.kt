package ir.dunijet.dunipool

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
class MyApp :Application() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel("Crypto", "CryptoMarket", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Notification for Crypto"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}