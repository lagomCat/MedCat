package com.bayandin.medicamentstateregister;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AppPreferences {

    private static AppPreferences instance;
    private final SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "app_preferences";
    private static final String KEY_DATABASE_OUTDATED = "isDatabaseOutdated";
    private static final String NEW_GUID = "isFileGuid";
    private static final String CURRENT_GUID = "f25639f5-0b79-4544-9bb8-af5e57e83c8d2"; //GUID интегрированной базы данных
    private static final String FIRST_START = "isFirstStart"; //Флаг первого старта приложения
    private static final String LAST_MODIFIED_DB_FILE = "isLastModifiedDbFile";
    private static final String SUCCESSFUL_UPDATE_DB = "isSuccessfulUpdateDB";
    private static final String ROWS_OLD_DB = "isRowsOldDB"; //Количество строк в предыдущей базе данных
    private static final String ROWS_CURRENT_DB = "isRowsCurrentDB"; //Количество строк в новой базе данных
    private static final String DATE_OF_UPDATE = "isDateUpdate"; //Дата успешного обновления базы данных
    private static final String COUNT_EXCEPTIONS = "isCountExceptions"; //Счетчик срабатываний исключений
    private static final String FILE_ERROR = "isFileError"; //Флаг проблем со скачанным файлом
    private static final String AUTO_UPDATE = "isAutoUpdate"; //Флаг проблем автоматического обновления
    private static final String CORRECT_SHEET_NAME = "isCorrectSheetName"; //Флаг корректного имени целевого листа в таблице Excel

    private final MutableLiveData<Boolean> isCorrectSheetNameLD = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isFileErrorLD = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isSuccessfulUpdateLD = new MutableLiveData<>();

    public LiveData<Boolean> getIsSuccessfulUpdateLD() {
        return isSuccessfulUpdateLD;
    }

    public LiveData<Boolean> getIsCorrectSheetNameLD() {
        return isCorrectSheetNameLD;
    }

    public LiveData<Boolean> getIsFileErrorLD() {
        return isFileErrorLD;
    }

    // Конструктор или метод для инициализации, например, в Application классе
    private AppPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    //Воспользуемся синглтоном, чтобы этот объект был единым для всех классов, чтобы работала подписка на LiveData
    public static synchronized AppPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreferences(context.getApplicationContext());
        }
        return instance;
    }


    // Метод для установки флага корректного имени целевого листа в таблице Ecxel
    public void setCorrectSheetName(Boolean isCorrectSheetName) {
        Log.d("Флаги", "Запись флага isCorrectSheetName = " + isCorrectSheetName);
        isCorrectSheetNameLD.postValue(isCorrectSheetName);
        sharedPreferences.edit().putBoolean(CORRECT_SHEET_NAME, isCorrectSheetName).apply();
    }

    // Метод для получения флага корректного имени целевого листа в таблице Ecxel
    public boolean isCorrectSheetName() {
        return sharedPreferences.getBoolean(CORRECT_SHEET_NAME, true);
    }

    // Метод для установки флага автоматического обновления базы данных
    public void setAutoUpdate(boolean isAutoUpdate) {
        sharedPreferences.edit().putBoolean(AUTO_UPDATE, isAutoUpdate).apply();
    }

    // Метод для получения флага автоматического обновления базы данных
    public boolean isAutoUpdate() {
        return sharedPreferences.getBoolean(AUTO_UPDATE, false);
    }

    // Метод для установки флага проблем со скачанным файлом
    public void setFileError(boolean isFileError) {
        sharedPreferences.edit().putBoolean(FILE_ERROR, isFileError).apply();
        isFileErrorLD.postValue(isFileError);
    }

    // Метод для получения флага проблемного файла
    public boolean isFileError() {
        return sharedPreferences.getBoolean(FILE_ERROR, false);
    }

    //Метод для сохранения количества срабатываний счетчика исключений
    public void setCountExceptions (int countExceptions) {
        sharedPreferences.edit().putInt(COUNT_EXCEPTIONS, countExceptions).apply();
    }

    //Метод для получения количества срабатываний счетчика исключений
    public int getCountExceptions() {
        return sharedPreferences.getInt(COUNT_EXCEPTIONS, 0);
    }

    //Метод для сохранения даты последнего успешного обновления
    public void setDateOfUpdate (String dateUpdate) {
        sharedPreferences.edit().putString(DATE_OF_UPDATE, dateUpdate).apply();
    }

    //Метод для получения даты последнего успешного обновления
    public String getGetDateOfUpdate() {
        return sharedPreferences.getString(DATE_OF_UPDATE, "16 ноября 2024 г.");
    }

    //Метод для сохранения количества строк старой базы данных
    public void setRowsOldDb (long rowsOldDb) {
        sharedPreferences.edit().putLong(ROWS_OLD_DB, rowsOldDb).apply();
    }

    //Метод для получения количества строк старой базы данных
    public long getRowsOldDB () {
        return sharedPreferences.getLong(ROWS_OLD_DB, 0);
    }

    //Метод для сохранения количества новой базы данных
    public void setRowsCurrentDb (long rowsCurrentDb) {
        sharedPreferences.edit().putLong(ROWS_CURRENT_DB, rowsCurrentDb).apply();
    }

    //Метод для получения количества строк новой базы данных
    public long getRowsCurrentDB () {
        return sharedPreferences.getLong(ROWS_CURRENT_DB, 0);
    }


    // Метод для установки флага успешного обновления базы данных
    public void setSuccessfulUpdateDB(boolean isSuccessfulUpdateDB) {
        isSuccessfulUpdateLD.postValue(isSuccessfulUpdateDB);
        sharedPreferences.edit().putBoolean(SUCCESSFUL_UPDATE_DB, isSuccessfulUpdateDB).apply();
    }

    // Метод для получения флага успешного обновления базы данных
//    public boolean isSuccessfulUpdateDB() {
//        return sharedPreferences.getBoolean(SUCCESSFUL_UPDATE_DB, false);
//    }

    //Метод для сохранения даты последнего изменения файла базы данных
//    public void setLastModifiedDbFile (long lastModifiedDbFile) {
//        sharedPreferences.edit().putLong(LAST_MODIFIED_DB_FILE, lastModifiedDbFile).apply();
//    }

    //Метод для получения даты последнего изменения файла базы данных
//    public long getLastModifiedDbFile () {
//        return sharedPreferences.getLong(LAST_MODIFIED_DB_FILE, 0);
//    }


    //Метод для сохранения текущего GUID
    public void setCurrentGuid(String currentGuid) {
        sharedPreferences.edit().putString(CURRENT_GUID, currentGuid).apply();
    }

    //Метод для получения текущего GUID
    public String getCurrentGuid() {
        return sharedPreferences.getString(CURRENT_GUID, "DEFAULT");
    }

    //Метод для сохранения нового GUID файла .xslx (который появился на сайте)
    public void setNewGuid(String newGuid) {
        sharedPreferences.edit().putString(NEW_GUID, newGuid).apply();
    }

    //Метод для получения сохраненного GUID файла .xslx
    public String getNewGuid() {
        return sharedPreferences.getString(NEW_GUID, "no_new_guid");
    }

    // Метод для установки флага устаревшей базы данных
    public void setDatabaseOutdated(boolean isOutdated) {
        sharedPreferences.edit().putBoolean(KEY_DATABASE_OUTDATED, isOutdated).apply();
    }

    // Метод для получения флага устаревшей базы данных
    public boolean isDatabaseOutdated() {
        return sharedPreferences.getBoolean(KEY_DATABASE_OUTDATED, false);
    }

    //Метод для сброса флага первого запуска приложения
//    public void resetFirstStart(boolean isFirstStart) {
//        sharedPreferences.edit().putBoolean(FIRST_START, isFirstStart).apply();
//    }

    //Метод для получения флага первого старта приложения
//    public boolean isFirstStart() {
//        return sharedPreferences.getBoolean(FIRST_START, true);
//    }

    // Другие методы для работы с настройками можно добавлять здесь
}