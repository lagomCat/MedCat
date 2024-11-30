package com.bayandin.medicamentstateregister;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.os.Process;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExportData {
    private static ExportData instance; // Единственный экземпляр
    private final Context context;
    private File excelFile;
    Handler handler = new Handler(Looper.getMainLooper());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static boolean UPDATE_PROGRESS = false;

    private ExportDataService exportDataService; // Ссылка на сервис

    private final MutableLiveData<Boolean> updateProgressLD = new MutableLiveData<>();

    public void setExportDataService(ExportDataService service) {
        this.exportDataService = service;
    }

    //Передаем в конструктор именно тот объект ExcelReader, который был создан в DatabaseInfo (подписчике),
    //иначе данные LiveData не поймаем, так как будет создан здесь новый объект ExcelReader, а подписаны на другой.
    private ExportData(Context context) {
        this.context = context.getApplicationContext(); // Используем ApplicationContext для избежания утечек
    }

    // Метод для получения единственного экземпляра
    public static synchronized ExportData getInstance(Context context) {
        if (instance == null) {
            instance = new ExportData(context);
        }
        return instance;
    }



    public boolean isUpdateProgress() {
        return UPDATE_PROGRESS;
    }

    public void setUpdateProgress(boolean updateProgress) {
        UPDATE_PROGRESS = updateProgress;
    }

    public void updateDatabase() {
        Log.d("Точка31", "updateDatabase: старт метода");
        ExcelReader excelReader = ExcelReader.getInstance(context.getApplicationContext());
        FileDownloader fileDownloader = FileDownloader.getInstance(context.getApplicationContext());
        AppPreferences appPreferences = AppPreferences.getInstance(context);
        setUpdateProgress(true);
        updateProgressLD.postValue(UPDATE_PROGRESS);//Отправляем в лайвдата
        Log.d("updateProgressLD1", "updateProgressLD = " + UPDATE_PROGRESS);
        Log.d("Точка32", "updateDatabase: взводим флаг обновления");
        appPreferences.setFileError(false);
        ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setPriority(Thread.MAX_PRIORITY); // Java-приоритет
            Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND); // Android-приоритет
            return thread;
        });
        executor.execute(() -> {
            Log.d("Точка33", "updateDatabase: выполнение логики обновления");
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakeLock");
            wakeLock.acquire(10 * 60 * 1000L);

            boolean fileErrorException = false;
            File medCatDir = MedCatDirectory.getMedCatDir(context);

            downloadUpdate(fileDownloader);
            File[] files = medCatDir.listFiles();
            if (files != null && files.length > 0) {
                excelFile = files[0];
            }
            try {
                //Проверяем заголовки таблицы Excel
                if (!excelReader.checkTableExcel(excelFile)){
                    appPreferences.setFileError(true);
                }
            } catch (Exception e) {
                //В случае битого файла пытаемся прочитать три раза
                Log.e("Точка34", "updateDatabase: ошибка", e);
                if (appPreferences.getCountExceptions() > 3) {
                    appPreferences.setCountExceptions(0);
                    appPreferences.setFileError(true);
                }
                fileDownloader.cleanDirectoryUpdate();
                appPreferences.setDatabaseOutdated(true);
                setUpdateProgress(false);
                updateProgressLD.postValue(UPDATE_PROGRESS);//Отправляем флаг в лайвдата
                Log.d("updateProgressLD2", "updateProgressLD = " + UPDATE_PROGRESS);
                appPreferences.setSuccessfulUpdateDB(false);

                Log.d("Точка35", "Файл поврежден");
                fileErrorException = true;
                appPreferences.setCountExceptions(appPreferences.getCountExceptions() + 1);
                updateDatabase();
            }

            excelReader.exportToDatabase(excelFile);
            fileDownloader.cleanDirectoryUpdate();
            boolean finalFileErrorException = fileErrorException;
            handler.post(() -> {
                if (!finalFileErrorException && !appPreferences.isFileError() && appPreferences.isCorrectSheetName()) {
                    appPreferences.setCurrentGuid(appPreferences.getNewGuid());
                    appPreferences.setSuccessfulUpdateDB(true);
                    appPreferences.setDateOfUpdate(LocalDateTime.now().format(formatter));
                    setUpdateProgress(false);
                    updateProgressLD.setValue(false);//Отправляем флаг в лайвдата
                    Log.d("updateProgressLD3", "updateProgressLD = " + UPDATE_PROGRESS);
                    Log.d("Точка36", "updateDatabase: сброс флага обновления");
                    appPreferences.setDatabaseOutdated(false);
                    //Останавливаем Foreground через зарегистрированную в нем ссылку на сервис
                    if (exportDataService != null) {
                        exportDataService.stopSelf();
                    }
                    appPreferences.setAutoUpdate(false);//Сбрасываем флаг обновления с главной страницы
                    appPreferences.setSuccessfulUpdateDB(true);
                }
            });
            wakeLock.release();
            Log.d("Точка37", "updateDatabase: завершение кода в экзекуторе");
        });
    }

    private void downloadUpdate(FileDownloader fileDownloader) {
        fileDownloader.downloadZipFile(context);
        fileDownloader.unzipAndDeleteArchive(context);
    }

    //Метод для подписки на флаг UPDATE_PROGRESS
    public LiveData<Boolean> getUpdateProgressLD() {
        return updateProgressLD;
    }

}

