package com.bayandin.medicamentstateregister;

/*
Пояснение к коду
Путь к директории MedCatData создаётся внутри getFilesDir(), доступного только для вашего приложения.
Удаление всех файлов внутри MedCatData перед скачиванием происходит методом file.delete() для каждого файла.
Скачивание файла по URL использует HttpURLConnection, при этом мы сохраняем поток данных в файл downloaded_file.zip.
Проверка успешного скачивания происходит путём проверки zipFile.exists(), и путь файла выводится в консоль для подтверждения.
Важные замечания
Убедитесь, что интернет-разрешение (<uses-permission android:name="android.permission.INTERNET" />) указано в манифесте.
Поскольку вы используете папку getFilesDir(), никаких дополнительных разрешений на запись или чтение не требуется.
 */

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileDownloader {

    private static FileDownloader instance;

    private static final String MAIN_URL = "https://grls.minzdrav.gov.ru/pricelims.aspx";
    private static final String FIRST_PART_URL = "https://grls.minzdrav.gov.ru/GetLimPrice.ashx?FileGUID=";
    private static boolean serverError = true;
    private static boolean htmlError = true;
    private static String newFileGUID;
    private final Context context;


    //Конструктор
    private FileDownloader(Context context) {
        this.context = context.getApplicationContext();
    }

    //Для моего приложения допустимо применение синглтона (вроде как)
    public static synchronized FileDownloader getInstance(Context context) {
        if (instance == null) {
            instance = new FileDownloader(context.getApplicationContext());
        }
        return instance;
    }


//    public String getFileName() {
//        return fileName;
//    }

    public boolean getServerError(){
        return serverError;
    }

    public boolean getHtmlError(){
        return htmlError;
    }

    //Метод для запуска проверки нового GUID (запускать в экзекуторе)
    public void checkDatabaseRelevance() {
        String newFileGuid = scanWeb();
        handleFileCheckResult(newFileGuid);
    }


    public String scanWeb() {
        try {
            //Загружаем страницу для анализа содержимого
            Document doc = Jsoup.connect(MAIN_URL).get();
            //Ищем кнопку для скачивания файла .xslx
            Element button = doc.selectFirst("button.btn_flat");
            if (button != null) {
                htmlError = false;
                serverError = false;
                //Если кнопку нашли, извлекаем GUID из элемента "onclick"
                String onclick = button.attr("onclick");
                newFileGUID = extractGUID(onclick);
            } else {
                htmlError = true;
                Log.e("FileDownloader", "Кнопка не найдена на странице.");
            }
        } catch (IOException e) {
            Log.e("FileDownloader", "Ошибка загрузки страницы: " + e.getMessage());
            serverError = true;
        }
        return newFileGUID;
    }

    //Метод, проверяющий, изменилась ли ссылка для скачивания файла .xslx
    //Если ссылка изменилась, значит, файл обновился
    public void handleFileCheckResult(String newFileGUID) {
        if (newFileGUID != null) {
            AppPreferences appPreferences = AppPreferences.getInstance(context);
            //Сохраняем новый GUID в настройках приложения (пока просто чтобы выводить на экран)
            appPreferences.setNewGuid(newFileGUID);
            //Получаем предыдущее значение GUID из файла настроек приложения
            String currentFileGUID = appPreferences.getCurrentGuid();
            if (!newFileGUID.equals(currentFileGUID)) {
                Log.d("Точка38", "Ссылка изменилась, нужно обновить базу данных.");
                appPreferences.setDatabaseOutdated(true);
            } else {
                appPreferences.setDatabaseOutdated(false);
                Log.d("Точка39", "Ссылка не изменилась, база актуальна.");
            }
        }
    }

    /*
    Метод extractGUID получает строку onclick, содержащую определенные параметры,
    и пытается извлечь значение параметра FileGUID. Вот как он работает:
Разделение по FileGUID=: Сначала строка разбивается по FileGUID=, что предполагает, что где-то
 в onclick присутствует текст наподобие ...FileGUID=<некоторый GUID>&....
Проверка длины массива: Если массив parts содержит более одного элемента
(значит, после FileGUID= что-то есть), метод берет вторую часть (parts[1]).
Извлечение значения GUID: Затем эта вторая часть разбивается по символу &,
 чтобы отделить GUID от любых последующих параметров.
Возвращение GUID: Метод возвращает первую часть после разделения (parts[1].split("&")[0]),
то есть только сам GUID. Если FileGUID= не найден, метод возвращает пустую строку.
*/
    private String extractGUID(String onclick) {
        String[] parts = onclick.split("FileGUID=");
        return parts.length > 1 ? parts[1].split("&")[0] : "";
    }

    //Метод, который скачивает архив с файлом .xslx в директорию приложения
    public void downloadZipFile(Context context) {
        //Директория для загруженного файла. Получем ее через синглтон
        File medCatDir = MedCatDirectory.getMedCatDir(context);
        String fileUrl = FIRST_PART_URL + newFileGUID;
        Log.d("ТОЧКА17", fileUrl);
        //Проверяем, пустая ли директория. Если пустая, скачиваем файл
        File[] files = medCatDir.listFiles();
        logMedCatDataFolderContents(context);
        if (files == null || files.length == 0) {
            // Директория пуста
            Log.d("Точка40", "Директория MedCatData пуста.");
            // Шаг 3. Скачивание файла в директорию MedCatData
            File zipFile = new File(medCatDir, "downloaded_file.zip");
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Устанавливаем свойство User-Agent для имитации запроса браузера
//            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("Точка41","Ошибка: сервер вернул " + connection.getResponseCode());
                    return;
                }
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(zipFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();
                connection.disconnect();
                // Шаг 4. Проверка, что файл действительно скачан
                if (zipFile.exists()) {
                    Log.d("Точка42", "Файл успешно загружен в " + zipFile.getAbsolutePath());
                    // Вызов проверки содержимого папки после скачивания файла
                    logMedCatDataFolderContents(context);
                } else {
                    Log.d("Точка43", "Файл не был загружен");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Точка44", "Ошибка при загрузке файла: " + e);
            }
        } else {
            // Директория не пуста
            Log.d("Точка45", "Директория MedCatData не пуста.");
            logMedCatDataFolderContents(context);
        }
    }

    // Метод для вывода содержимого папки MedCatData в лог
    public void logMedCatDataFolderContents(Context context) {
        //Директория для загруженного файла. Получем ее через синглтон
        File medCatDir = MedCatDirectory.getMedCatDir(context);
        if (medCatDir.exists() && medCatDir.isDirectory()) {
            File[] files = medCatDir.listFiles();

            // Поле класса
            String fileName;
            if (files != null && files.length > 0) {
                Log.d("Точка46", "Содержимое папки MedCatData:");
                for (File file : files) {
                    fileName = file.getName();
                    Log.d("Точка47", "Файл: " + fileName);
                }
            } else {
                Log.d("Точка48", "Папка MedCatData пуста.");
            }
        } else {
            Log.d("Точка49", "Папка MedCatData не существует.");
        }
    }

    public void unzipAndDeleteArchive(Context context) {

        //Директория для загруженного файла. Получем ее через синглтон
        File medCatDir = MedCatDirectory.getMedCatDir(context);
        File zipFile = new File(medCatDir, "downloaded_file.zip");

        if (!zipFile.exists()) {
            Log.d("Точка50", "Архив не найден для распаковки.");
            return;
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry;

            while ((zipEntry = zis.getNextEntry()) != null) {
                File extractedFile = new File(medCatDir, zipEntry.getName());

                // Если zipEntry — директория, создаем её
                if (zipEntry.isDirectory()) {
                    extractedFile.mkdirs();
                } else {
                    // Создаем необходимые родительские директории
                    extractedFile.getParentFile().mkdirs();

                    // Запись данных файла
                    try (FileOutputStream fos = new FileOutputStream(extractedFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zis.closeEntry();
            }

            // Удаляем архив после распаковки
            if (zipFile.delete()) {
                Log.d("Точка51", "Архив успешно удален после распаковки.");
            } else {
                Log.d("Точка52", "Не удалось удалить архив.");
            }
            logMedCatDataFolderContents(context);

        } catch (IOException e) {
            Log.d("Точка53", "Ошибка при распаковке архива: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Метод для удаления всех скачанных файлов обновления
    public void cleanDirectoryUpdate() {
//        initializeMedCatDir(context);
        //Директория для загруженного файла. Получем ее через синглтон
        File medCatDir = MedCatDirectory.getMedCatDir(context);
        for (File file : Objects.requireNonNull(medCatDir.listFiles())) {
            file.delete();
            Log.d("Точка54", "Папка MedCatData очищена.");
            logMedCatDataFolderContents(context);
        }
    }
}
