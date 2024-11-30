package com.bayandin.medicamentstateregister;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


public class DatabaseInfoViewModel extends AndroidViewModel {


    ExportData exportData = ExportData.getInstance(getApplication());


    AppPreferences appPreferences = AppPreferences.getInstance(getApplication());
    ExcelReader excelReader = ExcelReader.getInstance(getApplication());
    FileDownloader fileDownloader = FileDownloader.getInstance(getApplication());

    public DatabaseInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public void startExportDataService(Context context) {
        Intent intent = new Intent(context, ExportDataService.class);
        Log.d("Точка20", "Запрос старта Foreground от DatabaseViewModel");
        context.startForegroundService(intent);
    }

    //Метод для получения количества прочитанных строк (LiveData) из ExcelReader
    public LiveData<String> getRowsPercentagesCount() {
        return excelReader.getRowsPercentagesCount();
    }

    public LiveData<String> getOldRows() {
        return excelReader.getOldRows();
    }

    public LiveData<String> getNewRows() {
        return excelReader.getNewRows();
    }

    public LiveData<String> getCountRows() {
        return excelReader.getCountRows();
    }


    public LiveData<Boolean> getIsUpdateInProgressLD() {
        return exportData.getUpdateProgressLD();
    }

    public LiveData<Boolean> getIsCorrectSheetNameLD() {
        return appPreferences.getIsCorrectSheetNameLD();
    }

    public LiveData<Boolean> getIsFileErrorLD() {
        return appPreferences.getIsFileErrorLD();
    }

    //Методы для работы с FileDownloader
    public void checkDatabaseRelevance() {
        fileDownloader.checkDatabaseRelevance();
    }

    public void setSuccessfulUpdateDB(boolean isSuccessfulUpdateDB){
        appPreferences.setSuccessfulUpdateDB(isSuccessfulUpdateDB);
    }

    public LiveData<Boolean> isSuccessfulUpdateLD() {
        return appPreferences.getIsSuccessfulUpdateLD();
    }

}
