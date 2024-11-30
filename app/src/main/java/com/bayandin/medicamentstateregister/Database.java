package com.bayandin.medicamentstateregister;

import android.app.Application;

import androidx.room.Room;
import androidx.room.RoomDatabase;


@androidx.room.Database(entities = {MedicinalProduct.class}, version = 15)
public abstract class Database extends RoomDatabase {
    private static volatile Database instance = null;
    private static final String DB_NAME = "medicaments.db";

    public static Database getInstance(Application application) {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(application,
                                    Database.class, DB_NAME)
                            .createFromAsset("medicaments.db") // Используем .db файл
                            .fallbackToDestructiveMigration() //Удаляем все старые данные из базы и заменяем новыми при обновлении приложения
                            .build();
                }
            }
        }
        return instance;
    }
    public abstract MedicamentsDao medicamentsDao();
}
