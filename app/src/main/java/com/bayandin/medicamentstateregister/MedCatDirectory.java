package com.bayandin.medicamentstateregister;

import android.content.Context;

import java.io.File;

public class MedCatDirectory {
    private static File medCatDir;

    // Возвращаем директорию, инициализируя её при первом вызове
    public static File getMedCatDir(Context context) {
        if (medCatDir == null) {
            medCatDir = new File(context.getFilesDir(), "MedCatData");
            if (!medCatDir.exists()) {
                medCatDir.mkdirs();
            }
        }
        return medCatDir;
    }
}
