package com.bayandin.medicamentstateregister;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UpdateWorker extends Worker {

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Вызов  метода для скачивания, распаковки и обновления
        startExportDataService(getApplicationContext());

        // Возвращаем успешное завершение
        return Result.success();
    }


    // Метод для скачивания, распаковки и обновления
    public void startExportDataService(Context context) {
        Intent intent = new Intent(context, ExportDataService.class);
        Log.d("Точка276", "Запрос старта Foreground от UpdateWorker");
        context.startForegroundService(intent);
    }
}

