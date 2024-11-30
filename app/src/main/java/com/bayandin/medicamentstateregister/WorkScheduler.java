package com.bayandin.medicamentstateregister;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorkScheduler {

    public static void scheduleUpdateWorker(Context context) {
        // Получаем текущее время
        Calendar currentTime = Calendar.getInstance();

        // Определяем время для следующего запуска (3:37 ночи) чтоб никто не догадался (операция Ы)
        Calendar targetTime = Calendar.getInstance();
        targetTime.set(Calendar.HOUR_OF_DAY, 3);
        targetTime.set(Calendar.MINUTE, 37);
        targetTime.set(Calendar.SECOND, 0);

        Constraints constraints = new Constraints.Builder()
                .setRequiresDeviceIdle(false) // Работает даже в режиме экономии (но это не точно, это же андроид блеать)
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .setRequiresStorageNotLow(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Если текущее время уже позже 3:37, сдвигаем на следующий день
        if (targetTime.before(currentTime)) {
            targetTime.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Вычисляем задержку до первого запуска
        long initialDelay = targetTime.getTimeInMillis() - currentTime.getTimeInMillis();

        // Создаем PeriodicWorkRequest для выполнения задачи каждые 24 часа
        PeriodicWorkRequest updateRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class,
                1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
                .build();

        // Запускаем WorkManager
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "UpdateDatabaseTask", // Уникальное имя задачи
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // Заменяет предыдущую задачу.
                updateRequest
        );
    }
}

