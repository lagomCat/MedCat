package com.bayandin.medicamentstateregister;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ExportDataService extends Service {

    private static boolean isRunning = false;//Флаг старта сервиса

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Точка24", "Foreground: сервис инициализируется");
        // Регистрируем канал уведомлений (если еще не зарегистрирован)
        createNotificationChannel();

        // Создаем уведомление
        Notification notification = new NotificationCompat.Builder(this, "EXPORT_CHANNEL")
                .setContentTitle("Реестр цен")
                .setContentText("Обновление базы данных...")
                .setPriority(NotificationCompat.PRIORITY_MAX) // приоритет для фоновой работы
                .setSmallIcon(R.drawable.ic_notification)
                .build();

        // Запускаем Foreground Service
        startForeground(1, notification);
        Log.d("Точка225", "Foreground Worker: инициализация сервиса. Флаг = " + isRunning);

    }

    /**
     * Метод для создания канала уведомлений
     */
    private void createNotificationChannel() {
        String channelId = "EXPORT_CHANNEL";
        String channelName = "Обновление базы данных";
        String channelDescription = "Уведомления для фонового обновления базы данных";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setDescription(channelDescription);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
            Log.d("Точка26", "Канал уведомлений 'EXPORT_CHANNEL' успешно зарегистрирован");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) {
            // Сервис уже запущен, останавливаем запуск
            Log.d("Точка 333", "Foreground уже запущен. Отмена повторного запуска!");
            return START_NOT_STICKY;
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            Log.d("Точка27", "Foreground: старт");
            AppPreferences appPreferences = AppPreferences.getInstance(this);
            FileDownloader fileDownloader = FileDownloader.getInstance(this);
            // Получаем экземпляр ExportData
            ExportData exportData = ExportData.getInstance(this);
            exportData.setExportDataService(this); // Регистрируем ссылку на сервис в классе ExportData

            Thread thread = new Thread(() -> {
                //Если это не апдейт, инициированный главной страницей
                if (!appPreferences.isAutoUpdate()) {
                    //Запускаем проверку наличия нового файла на сервере
                    fileDownloader.checkDatabaseRelevance();
                }
                handler.post(() -> {
                    if (appPreferences.isDatabaseOutdated()) {
                        Log.d("Точка28", "Foreground: вызов updateDatabase");
                        exportData.updateDatabase();
                    } else stopSelf();
                });
            });
            thread.start();
            isRunning = true;//Взводим флаг старта Foreground
//                stopSelf(); // Завершаем сервис после выполнения (перенес непосредственно в ExportData)

            return START_STICKY; // Не перезапускать сервис при уничтожении
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.d("Точка30", "onDestroy:Foreground сервис завершен. Флаг = " + isRunning);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Сервис не поддерживает привязку
    }
}
