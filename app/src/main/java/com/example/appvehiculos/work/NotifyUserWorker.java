package com.example.appvehiculos.work;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.appvehiculos.MainActivity3;
import com.example.appvehiculos.R;

public class NotifyUserWorker extends Worker {

    private static final String CHANNEL_ID = "notify_channel";
    private static final String TAG = "NotifyUserWorker";

    public NotifyUserWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Worker is starting...");

        try {
            sendNotification();
            Log.d(TAG, "Notification sent successfully.");
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Failed to send notification.", e);
            return Result.failure();
        }
    }

    private void sendNotification() {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear canal de notificaciones para Android Oreo y versiones superiores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Intent para abrir la MainActivity cuando se toque la notificación
        Intent intent = new Intent(context, MainActivity3.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Crear el PendingIntent con FLAG_IMMUTABLE
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // Reemplaza con el ícono adecuado
                .setContentTitle("Actualización de Kilometraje")
                .setContentText("¡Recuerda actualizar el kilometraje de tus vehículos!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Mostrar la notificación
        notificationManager.notify(1, builder.build());
    }
}