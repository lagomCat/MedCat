package com.bayandin.medicamentstateregister;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        Log.d("MyApplication", "Application started!");
        super.onCreate();

        // Запланировать выполнение задачи
        WorkScheduler.scheduleUpdateWorker(this);
    }
}
