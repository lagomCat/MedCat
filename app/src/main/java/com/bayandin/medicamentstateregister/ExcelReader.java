package com.bayandin.medicamentstateregister;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.pjfanning.xlsx.StreamingReader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ExcelReader {

    private static ExcelReader instance;

    private static int rowHeadingIndex;//Номер строки с заголовками
    private Database database;

    private final static String COLUMN_HEADING_1 = "МНН";
    private final static String COLUMN_HEADING_2 = "Торговое наименование лекарственного препарата";
    private final static String COLUMN_HEADING_3 = "Лекарственная форма, дозировка, упаковка (полная)";
    private final static String COLUMN_HEADING_4 = "Владелец РУ/производитель/упаковщик/Выпускающий контроль";
    private final static String COLUMN_HEADING_5 = "Код АТХ";
    private final static String COLUMN_HEADING_6 = "Количество в потреб. упаковке";
    private final static String COLUMN_HEADING_7 = "Предельная цена руб. без НДС";
    private final static String COLUMN_HEADING_8 = "Цена указана для первич. упаковки";
    private final static String COLUMN_HEADING_9 = "№ РУ";
    private final static String COLUMN_HEADING_10 = "Дата регистрации цены(№ решения)";
    private final static String COLUMN_HEADING_11 = "Штрихкод (EAN13)";
    private final static String COLUMN_HEADING_12 = "Дата вступления в силу";

    private final MutableLiveData<String> rowsCountLD = new MutableLiveData<>();
    private final MutableLiveData<String> oldRowsLD = new MutableLiveData<>();
    private final MutableLiveData<String> newRowsLD = new MutableLiveData<>();
    private final MutableLiveData<String> countRowsLD = new MutableLiveData<>();

    private static final String CORRECT_SHEET_NAME = "Действующие"; //Имя целевого листа в таблице
    private static boolean isCorrectSheetName = false; //Флаг обнаружения листа с корректным именем

    private final Context context;


    private final String[] referenceHeadings = {
            COLUMN_HEADING_1,
            COLUMN_HEADING_2,
            COLUMN_HEADING_3,
            COLUMN_HEADING_4,
            COLUMN_HEADING_5,
            COLUMN_HEADING_6,
            COLUMN_HEADING_7,
            COLUMN_HEADING_8,
            COLUMN_HEADING_9,
            COLUMN_HEADING_10,
            COLUMN_HEADING_11,
            COLUMN_HEADING_12,
    };


    //Конструктор
    private ExcelReader(Context context) {
        this.context = context;
    }

    //А сделаю как я его синглтоном, ибо зачем?
    public static synchronized ExcelReader getInstance(Context context) {
        if (instance == null) {
            instance = new ExcelReader(context.getApplicationContext());
        }
        return instance;
    }

    //Метод для проверки отсутствия изменений в структуре таблицы
    //Находим первый заголовок столбца и сравниваем все остальные заголовки с константами
    public boolean checkTableExcel(File excelFile) {
        FileDownloader fileDownloader = FileDownloader.getInstance(context);
        AppPreferences appPreferences = AppPreferences.getInstance(context);
        boolean tableCorrect;
        boolean firstHeading = false;
        Sheet targetSheet;
        Log.d("SheetName1", "targetSheet = ");
        String[] excelHeadings = new String[referenceHeadings.length]; // Инициализируем массив
        try (
                FileInputStream fis = new FileInputStream(excelFile);
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(10)    // Размер кеша строк в памяти
                        .bufferSize(4096)    // Размер буфера чтения
                        .open(fis)) {

            //Добавим логику выбора листа по имени CORRECT_SHEET_NAME
           try {
               targetSheet = workbook.getSheet(CORRECT_SHEET_NAME);
               Log.d("SheetName2", "targetSheet = " + targetSheet);
           } catch (Exception e) {
               isCorrectSheetName = false;//Сбрасываем флаг (корректный лист не найден)
               appPreferences.setCorrectSheetName(false);
               Log.d("Исключение1", "targetSheet = ");
               return false;
           }
            //Тут наверняка есть избыточность
            if (targetSheet == null) {
                Log.d("Исключение2", "targetSheet = ");
                isCorrectSheetName = false;//Сбрасываем флаг (корректный лист не найден)
                appPreferences.setCorrectSheetName(isCorrectSheetName);
                return false;
            }
            isCorrectSheetName = true;
            appPreferences.setCorrectSheetName(true);
            for (Row row : targetSheet) {
                // Обработка строки
                int i = 0;
                for (Cell cell : row) {
                    if (Objects.equals(cleanCellValue(cell), COLUMN_HEADING_1)) {
                        Log.d("Excel3", "Заголовок " + cleanCellValue(cell));
                        firstHeading = true; //найден первый заголовок
                    }
                    if (firstHeading) {
                        excelHeadings[i] = cleanCellValue(cell);
                        Log.d("Excel2", "Заголовок " + i + ": " + excelHeadings[i]);
                        i++;
                    }
                }
                rowHeadingIndex = row.getRowNum(); // Получаем номер строки заголовков
                Log.d("Точка1", "Номер строки с заголовками: " + rowHeadingIndex);
                if (firstHeading) break;
            }
        } catch (
                IOException e) {
            e.printStackTrace();
            fileDownloader.cleanDirectoryUpdate();
            //Переход на главную страницу
            Intent intent = HeadingsList.newIntent(context);
            Log.d("Точка2", "Файл поврежден");
            context.startActivity(intent);
        }
        tableCorrect = Arrays.equals(excelHeadings, referenceHeadings);
        Log.d("Точка3", "tableCorrect = " + tableCorrect);
        return tableCorrect;
    }

    //Метод, экспортирующий данные из Excel в базу данных
    public void exportToDatabase(File excelFile) {
        AppPreferences appPreferences = AppPreferences.getInstance(this.context);
        MedicinalProduct medicinalProduct;
        List<MedicinalProduct> medicinalProductList = new ArrayList<>();
        Object[] arrayCell = new Object[referenceHeadings.length];
        Sheet targetSheet;
        try (
                FileInputStream fis = new FileInputStream(excelFile);
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(10)    // Размер кеша строк в памяти
                        .bufferSize(4096)    // Размер буфера чтения
                        .open(fis)) {

            //Добавим логику выбора листа по имени CORRECT_SHEET_NAME
            Log.d("SheetName7", "targetSheet = ");
            try {
                targetSheet = workbook.getSheet(CORRECT_SHEET_NAME);
                Log.d("SheetName8", "targetSheet = " + targetSheet);

            //Тут наверняка есть избыточность
            if (targetSheet == null) {
                Log.d("SheetName10", "targetSheet = " + targetSheet);
                isCorrectSheetName = false;//Сбрасываем флаг (корректный лист не найден)
                appPreferences.setCorrectSheetName(false);
            }
            isCorrectSheetName = true;
            appPreferences.setCorrectSheetName(true);
            appPreferences.setFileError(false);

            //Посчитаем, сколько всего строк в новой таблице для визуализации прогресса обновления
//            int allRowCount = -rowHeadingIndex - 1, n = 0;//Подсчет строк будет выполняться после строки с заголовками
//            Log.d("SheetName11", "targetSheet = " + targetSheet);
//            for (Row row : targetSheet) {
//                allRowCount++;
//                n++;
//                Log.d("RowCount", "Подсчет общего количества строк: " + allRowCount);
//                //С произвольным промежутком выводим данные в консоль
//                if(n == 237) {
//                    countRowsLD.postValue("Подсчёт количества данных: " + allRowCount);
//                    Log.d("Точка4", "LiveData обновлено: " + allRowCount);
//                    n = 0;
//                }
//            }
            int rowCount = 0;
                assert targetSheet != null;
                for (Row row : targetSheet) {
                rowCount++;
                if (row.getRowNum() <= rowHeadingIndex) {
                    continue; //Пропускаем строки, включая заголовки
                }
                // Обработка строки
                int i = 0;
                for (Cell cell : row) {
                    Object value = getCellValue(cell);
                    arrayCell[i] = value;
                    i++;

                }
                medicinalProduct = new MedicinalProduct(row.getRowNum(), (String) arrayCell[0], (String) arrayCell[1], (String) arrayCell[2],
                        (String) arrayCell[3], (String) arrayCell[4], (Double) arrayCell[5], (Double) arrayCell[6], parseCellValue(arrayCell[7]),
                        (String) arrayCell[8], (String) arrayCell[9], (String) arrayCell[10], formatDate((Date) arrayCell[11]));
                database = Database.getInstance((Application) context.getApplicationContext());
                medicinalProductList.add(medicinalProduct);
                //Через какой-то промежуток строк будем выводить проценты на экран
                if (rowCount == 237) {
                    rowsCountLD.postValue("Обработка строки №: " + medicinalProductList.size());
                    rowCount = 0;
                    Log.d("Точка8", "Размер массива " + medicinalProductList.size());
                }
            }
            newRowsLD.postValue("Добавлено строк: " + medicinalProductList.size());
            appPreferences.setRowsCurrentDb(medicinalProductList.size());
            updateDatabase(medicinalProductList);
                //Обработка исключения, когда не найден целевой лист
            } catch (Exception e) {
                Log.d("Исключение2", "targetSheet = ");
                isCorrectSheetName = false;//Сбрасываем флаг (корректный лист не найден)
                appPreferences.setCorrectSheetName(isCorrectSheetName);
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public LiveData<String> getRowsPercentagesCount() {
       return rowsCountLD;
    }

    public LiveData<String> getOldRows() {
       return oldRowsLD;
    }

    public LiveData<String> getNewRows() {
        return newRowsLD;
    }

    public LiveData<String> getCountRows() {
        return countRowsLD;
    }

    private void updateDatabase(List<MedicinalProduct> products) {
        AppPreferences appPreferences = AppPreferences.getInstance(this.context);
        // Удаляем старые данные и отслеживаем завершение операции
        int rowsDeleted = database.medicamentsDao().clearTable(); // Получаем количество удалённых записей
        Log.d("Точка9", "Table cleared. Rows deleted: " + rowsDeleted);
        oldRowsLD.postValue("Удалено строк: " + rowsDeleted);
        appPreferences.setRowsOldDb(rowsDeleted);


        // После завершения удаления вставляем новые данные
        database.medicamentsDao().insertAll(products);
        Log.d("Точка10", "Insert completed.");
        // После вставки можно установить флаг окончания обновления
        //Устанавливаю его в DatabaseInfo
    }


    private static String cleanCellValue(Cell cell) {
        if (cell == null) {
            return ""; // Если ячейка пустая
        }
        switch (cell.getCellType()) {
            case STRING:
                return cleanString(cell.getStringCellValue());
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cleanString(cell.getCellFormula());
            default:
                return "";
        }
    }

    //Метод, возвращающий данные из ячейки, сохраняя тип данных
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null; // Если ячейка пустая
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue(); // Вернуть строку
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue(); // Вернуть дату, если это дата
                }
                return cell.getNumericCellValue(); // Вернуть число
            case BOOLEAN:
                return cell.getBooleanCellValue(); // Вернуть логическое значение
            case FORMULA:
                // Если нужна формула, возвращаем её как строку
                return cell.getCellFormula();
            case BLANK:
                return null; // Пустая ячейка
            default:
                return null; // Неподдерживаемый тип
        }
    }

    //Метод, который удаляет лишние знаки. Будем использовать при проверке заголовков столбцов
    private static String cleanString(String value) {
        if (value == null) {
            return "";
        }
        // Удаляем символы "\n", "-", и обрезаем пробелы в начале и конце строки
        return value.replace("\n", "").replace("-", "").trim();
    }

    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()); // Формат даты
        return sdf.format(date);
    }

    //Метод для возвращения Double из ячеек, которые оказались не Double
    public Double parseCellValue(Object cellValue) {
        if (cellValue == null) {
            return null;  // Возвращаем null, если данные отсутствуют
        }

        if (cellValue instanceof String) {
            // Если это строка, проверяем на наличие символа "+"
            String strValue = (String) cellValue;
            if (strValue.contains("+")) {
                // Если символ "+" есть, убираем его или обрабатываем по-другому
                strValue = strValue.replace("+", "").trim();
            }
            try {
                return Double.parseDouble(strValue);  // Преобразуем строку в Double
            } catch (NumberFormatException e) {
                // Если не можем преобразовать строку в число, возвращаем null
                return 0.0;
            }
        }

        if (cellValue instanceof Double) {
            // Если это уже Double, просто возвращаем его
            return (Double) cellValue;
        }

        // Для других типов, если требуется, можно добавить обработку
        return null;
    }

}