package com.bayandin.medicamentstateregister;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HeadingsList extends AppCompatActivity {
    //Инициализация таблицы:

    private static final String SQL_ID1 = "internationalName",       COLUMN_HEADING1 = "Международное наименование";
    private static final String SQL_ID2 = "tradeName",               COLUMN_HEADING2 = "Торговое наименование";
    private static final String SQL_ID3 = "dosageForm",              COLUMN_HEADING3 = "Форма, дозировка, упаковка";
    private static final String SQL_ID4 = "owner",                   COLUMN_HEADING4 = "Владелец РУ/производитель/упаковщик/Выпускающий контроль";
    private static final String SQL_ID5 = "chemicalClassification",  COLUMN_HEADING5 = "Код АТХ";
    private static final String SQL_ID6 = "quantityInPackaging",     COLUMN_HEADING6 = "Количество в потреб. упаковке";
    private static final String SQL_ID7 = "maximumPrice",            COLUMN_HEADING7 = "Предельная цена руб. без НДС";
    private static final String SQL_ID8 = "pricePrimaryPackaging",   COLUMN_HEADING8 = "Цена указана для первич. упаковки";
    private static final String SQL_ID9 = "ruNumber",                COLUMN_HEADING9 = "№ РУ";
    private static final String SQL_ID10 = "priceRegistrationDate",  COLUMN_HEADING10 = "Дата регистрации цены (№ решения)";
    private static final String SQL_ID11 = "barcode",                COLUMN_HEADING11 = "Штрихкод (EAN13)";
    private static final String SQL_ID12 = "effectiveDate",          COLUMN_HEADING12 = "Дата вступления в силу";


    private HeadingsListAdapter headingsListAdapter;
    private RecyclerView recycleViewHeadings;
    private TextView textViewHeadingList;
    private EditText editTextInputSearch;
    private final ArrayList<ColumnHeadingItem> columnHeadingItems = new ArrayList<>();
    private Button myButtonBack;
    private TextView textViewNumberOfFound;
//    private TextView textViewQuotes;
    private TextView textViewHeadingSearch1;
    private TextView textViewHeadingSearch2;
    private TextView textViewNetworkStatus;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SearchResultAdapter searchResultAdapter;
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 500; // Задержка для поискового запроса в мс
    private DatabaseInfoViewModel viewModelInfoDB;
    private HeadingsListViewModel viewModelHeadings;
    private static boolean isHeadingsListAdapter = true; //Флаг адаптера заголовков таблицы






    private final String[] sqlIds = {
        SQL_ID1,
        SQL_ID2,
        SQL_ID3,
        SQL_ID4,
        SQL_ID5,
        SQL_ID6,
        SQL_ID7,
        SQL_ID8,
        SQL_ID9,
        SQL_ID10,
        SQL_ID11,
        SQL_ID12,
    };

    private final String[] textHeadings = {
        COLUMN_HEADING1,
        COLUMN_HEADING2,
        COLUMN_HEADING3,
        COLUMN_HEADING4,
        COLUMN_HEADING5,
        COLUMN_HEADING6,
        COLUMN_HEADING7,
        COLUMN_HEADING8,
        COLUMN_HEADING9,
        COLUMN_HEADING10,
        COLUMN_HEADING11,
        COLUMN_HEADING12,
    };

    private void initView() {
        recycleViewHeadings = findViewById(R.id.recycleViewHeadings);
        textViewHeadingList = findViewById(R.id.textViewHeadingList);
        editTextInputSearch = findViewById(R.id.editTextInputSearch);
        myButtonBack = findViewById(R.id.myButtonBack);
        textViewNumberOfFound = findViewById(R.id.textViewNumberOfFound);
//        textViewQuotes = findViewById(R.id.textViewQuotes);
        textViewHeadingSearch1 = findViewById(R.id.textViewHeadingSearch1);
        textViewHeadingSearch2 = findViewById(R.id.textViewHeadingSearch2);
        textViewNetworkStatus = findViewById(R.id.textViewNetworkStatus);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headings_list);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        //Создаем через ViewModelProvaider, чтобы вьюмодель не уничтожалась при уничтожениии активити
        viewModelInfoDB = new ViewModelProvider(this).get(DatabaseInfoViewModel.class);
        viewModelHeadings = new ViewModelProvider(this).get(HeadingsListViewModel.class);
        initView();
        //Запрашиваем разрешение для уведомлений Foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1); // 1 — код запроса
            }
        }
        searchResultAdapter= new SearchResultAdapter();
        headingsListAdapter = new HeadingsListAdapter();
        //Случайные цитаты (отключил)
//        QuotesCollection.loadQuotes(this);
        addTextToHeadings();
        //Необходимо было, когда файл базы данных обновлялся вручную при сборке apk
//        repairDatabase();
        Log.d("isHeadingsListAdapter", "isHeadingsListAdapter = " + isHeadingsListAdapter);
        if(isHeadingsListAdapter) {
            showHeadings();
        } else {
            //Один хнер этот элемент уже скрыт, создадим не null на всякий случай
            ColumnHeadingItem columnHeadingItem = new ColumnHeadingItem();
            showAfterClickToHeading(columnHeadingItem);
        }

        //Пока убрал. Разрешение запрашивается. Но по факту не сохраняется. Надо вручную разрешать в настройках андроида.
//        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//        intent.setData(Uri.parse("package:" + getPackageName()));
//        startActivity(intent);
        setTextView(viewModelInfoDB);


        //Подписка на корректность листа Excel
        viewModelHeadings.getIsCorrectSheetNameLD().observe(this, isCorrectSheetNameLD -> {
            Log.d("myLiveData4", "onChanged(Boolean isCorrectSheetName) = " + isCorrectSheetNameLD);
            if (!isCorrectSheetNameLD) {
                textViewNetworkStatus.setText(R.string.file_error);
                textViewNetworkStatus.setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
            }
        });


        viewModelInfoDB.getIsUpdateInProgressLD().observe(this, isUpdateInProgressLD -> {
            Log.d("myLiveData4", "onChanged(Boolean isCorrectSheetName) = " + isUpdateInProgressLD);
            if (isUpdateInProgressLD) {
                textViewNetworkStatus.setText(R.string.update_in_progress);
                textViewNetworkStatus.setBackgroundColor(getResources().getColor(R.color.light_sienna, getTheme()));
            } else setTextView(viewModelInfoDB);
        });

        viewModelInfoDB.isSuccessfulUpdateLD().observe(this, isSuccessfulUpdateLD -> {
            Log.d("myLiveData5", "onChanged(Boolean isCorrectSheetName) = " + isSuccessfulUpdateLD);
            if (isSuccessfulUpdateLD) {
                textViewNetworkStatus.setText(R.string.good_database);
                textViewNetworkStatus.setBackgroundColor(getResources().getColor(R.color.green, getTheme()));
            } else setTextView(viewModelInfoDB);
        });

        //Подписка на результаты поиска
        viewModelHeadings.getSearchResultItemsLD().observe(this, searchResultItemsLD -> {
            searchResultAdapter.setSearchResults(searchResultItemsLD);
            recycleViewHeadings.setAdapter(searchResultAdapter);
            isHeadingsListAdapter = false;
            int countSearch = searchResultItemsLD.size();
            textViewNumberOfFound.setText("Найдено: " + countSearch);
            textViewNumberOfFound.setVisibility(View.VISIBLE);
        });

        //Код, который выполняется при клике на заголовке столбца бла бла бла
        headingsListAdapter.setOnHeadingsClickListener(this::showAfterClickToHeading);

        //Код, который вызывается при клике на элементе поиска
        searchResultAdapter.setOnSearchResultClickListener(searchResultItem -> {
            int id = searchResultItem.getIdMedicinalProduct();
            Intent intent = InformationPanelActivity.newIntent(HeadingsList.this, id);
            startActivity(intent);
        });

        //Метод, который вызывается при клике на TextView "Статус базы данных"
        textViewNetworkStatus.setOnClickListener(new View.OnClickListener() {
            final AppPreferences appPreferences = AppPreferences.getInstance(HeadingsList.this);
            @Override
            public void onClick(View v) {
                appPreferences.setAutoUpdate(false);//Ручной вход на экран обновления
                Intent intent = DatabaseInfo.newIntent(HeadingsList.this);
                // Добавляем флаг, чтобы активити доставалась из стека, если уже создана, а не
                //создавалась новая
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    }


    // Метод для отображения заголовков
    private void showHeadings() {
        headingsListAdapter.setHeadingTables(columnHeadingItems);
        //Указываем RecycleView, какой адаптер использовать
        recycleViewHeadings.setAdapter(headingsListAdapter);
        isHeadingsListAdapter = true;
        //Сообщаем, каким образом отображать элементы (вертикально, гориз, сеткой), указали в макете
        recycleViewHeadings.setLayoutManager(new LinearLayoutManager(this));
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, HeadingsList.class);
    }


    //Метод для создания коллекции заголовков
    private void addTextToHeadings(){
        for (int i = 0; i < sqlIds.length; i++) {
            ColumnHeadingItem columnHeadingItem = new ColumnHeadingItem(textHeadings[i], sqlIds[i]);
            columnHeadingItems.add(columnHeadingItem);
        }
    }
//      Пока убрал. В данной реализации бесполезн, так как помимо InputView надо еще сохранять коллекцию
//    с результатами поиска, флаг подключенного адаптера перед переворотом экрана... Чтобы сохранить коллекцию результатов поиска,
//    нужно в SearhResultItem подключать интерфейс Parceble
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("editTextVisibility", editTextInputSearch.getVisibility());
        outState.putBoolean("isHeadingsListAdapter", isHeadingsListAdapter);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int visibility = savedInstanceState.getInt("editTextVisibility", View.GONE);
        editTextInputSearch.setVisibility(visibility);
        isHeadingsListAdapter = savedInstanceState.getBoolean("isHeadingsListAdapter", true);
    }


    //Метод для проверки доступности сервера, файла и актуальности базы данных
    private void setTextView(DatabaseInfoViewModel viewModel) {
        ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setPriority(Thread.MAX_PRIORITY); // Java-приоритет
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND); // Android-приоритет
            return thread;
        });
        ExportData exportData = ExportData.getInstance(getApplication());
        FileDownloader fileDownloader = FileDownloader.getInstance(this);
        AppPreferences appPreferences = AppPreferences.getInstance(this);
        executor.execute(() -> {
            viewModel.checkDatabaseRelevance();
            handler.post(() -> {
                Log.d("Флаги", "isDatabaseOutdated = " + appPreferences.isDatabaseOutdated() +
                        "\nisUpdateProgress = " + exportData.isUpdateProgress() + "\nisFileError = " + appPreferences.isFileError() +
                        "\nisCorrectSheetName = " + appPreferences.isCorrectSheetName());
                //Если установлен флаг недоступности сервера
                if (fileDownloader.getServerError()) {
                    textViewNetworkStatus.setText(R.string.server_error);
                    textViewNetworkStatus.setBackgroundColor(getResources().getColor(R.color.black, getTheme()));
                } else if (fileDownloader.getHtmlError()) {
                    textViewNetworkStatus.setText(R.string.html_error);
                    textViewNetworkStatus.setBackgroundColor(getResources().getColor(R.color.translucent_white_80, getTheme()));
                } else if (appPreferences.isDatabaseOutdated() && !exportData.isUpdateProgress()) {
                    textViewNetworkStatus.setText(R.string.database_out_date);
                    textViewNetworkStatus.setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
                    appPreferences.setAutoUpdate(true);//Устанавливаем флаг автоматического обновления базы
                    viewModel.setSuccessfulUpdateDB(false);//Сбрасываем флаг успешного обновления базы//
//
                    viewModel.startExportDataService(HeadingsList.this);//Запускаем обновление базы в фоне

                    //Переход на страницу обновления базы данных
//                             Intent intent = DatabaseInfo.newIntent(HeadingsList.this);
//                             startActivity(intent);
//                             finish();
                } else if (exportData.isUpdateProgress() && appPreferences.isCorrectSheetName()) {
                    textViewNetworkStatus.setText(R.string.update_in_progress);
                    textViewNetworkStatus.setBackgroundColor(getResources().getColor(R.color.light_sienna, getTheme()));
                } else if (appPreferences.isDatabaseOutdated() && (!appPreferences.isCorrectSheetName() || appPreferences.isFileError())) {
                    textViewNetworkStatus.setText(R.string.file_error);
                    textViewNetworkStatus.setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
                } else {
                    textViewNetworkStatus.setText(R.string.good_database);
                    textViewNetworkStatus.setBackgroundColor(getResources().getColor(R.color.green, getTheme()));
                }
            });
        });
    }

//     //Метод для форматирования базы данных, если файл .db обновился
//     private void repairDatabase () {
//         //Если файл базы данных изменился, заменяем в базе данных запятые точками, удаляем пробелы
//        AppPreferences appPreferences = AppPreferences.getInstance(context);
//         long currentModified;
//         //Если первый старт приложения, и файл базы данных еще не создавался
//
//         File dbFile = getDatabasePath("medicaments.db");
//         currentModified = dbFile.lastModified();
//         //Проверка флага первого запуска приложения
//         if(appPreferences.isFirstStart()) {
//             Log.d("ТОЧКА13", "Это первый старт приложения!");
//             //Сброс флага первого старта
//             appPreferences.resetFirstStart(false);
//             /*
//             При первом старте просто задаем дату последнего изменения файла базы данных
//             больше нуля, чтобы сработал метод по удалению пробелов и запятых (так как дата последнего
//             изменения базы данных в настройках приложения при первом запуске всегда будет равна нулю
//             Это избавляется нас от необходиомсти инициализировать базу. Без инициализации базы еще не будет
//             создана копия базы данных, с которой будет работать приложение. Следовательно, метод сравнения
//             даты изменения файла не сработает. А при следующих запусках уже будет создан файл рабочей
//             копии базы данных, и уже будут храниться параметры в файле настроек приложения. Поэтому
//             дальше будем форматировать базу при обновлении файлика базы.
//              */
//             currentModified = 1;
//         }
//         Log.d("ТОЧКА11", "Дата изменения файла базы данных: " + currentModified);
//         long lastModified = appPreferences.getLastModifiedDbFile();
//         Log.d("ТОЧКА12", "Дата последнего обновления базы данных: " + lastModified);
//         if (lastModified < currentModified){
//             long finalCurrentModified = currentModified;
//             Thread thread2 = new Thread(new Runnable() {
//                 @Override
//                 public void run() {
//                     //Сохраняем новое значение даты изменения файла в настройках приложения
//                     database.medicamentsDao().repairDatabase();
//                     appPreferences.setLastModifiedDbFile(finalCurrentModified);
//                 }
//             });
//             thread2.start();
//         }
//     }

//     protected void onResume() {
//        super.onResume();
//         Зачем-то, не помню, зачем...
//        checkDatabaseRelevance();
//     }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "Уведомления разрешены");
            } else {
                Log.d("Permissions", "Уведомления запрещены");
            }
        }
    }

    private void showAfterClickToHeading(ColumnHeadingItem columnHeadingItemOnClick){
        isHeadingsListAdapter = false;
        textViewNetworkStatus.setVisibility(View.GONE);
        textViewHeadingSearch1.setVisibility(View.VISIBLE);
        textViewHeadingSearch2.setVisibility(View.VISIBLE);
        editTextInputSearch.setVisibility(View.VISIBLE);
        editTextInputSearch.setBackgroundColor(Color.argb(100, 0, 0, 0));
        //Курсор в поле ввода поискового запроса
        editTextInputSearch.requestFocus();
        // Показать клавиатуру
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editTextInputSearch, InputMethodManager.SHOW_IMPLICIT);
        }
        textViewHeadingList.setVisibility(View.GONE);
        myButtonBack.setVisibility(View.VISIBLE);
        //Отключил по требованию пользователя
//                String quote = QuotesCollection.getRandomQuote();//Прилетает случайная цитата
//                textViewQuotes.setText(quote);
//                //Анимация плавного появления
//                Animation fadeIn = AnimationUtils.loadAnimation(HeadingsList.this, R.anim.fade_in);
//                textViewQuotes.startAnimation(fadeIn);
        if (Objects.equals(columnHeadingItemOnClick.getSqlId(), SQL_ID1) ||
                Objects.equals(columnHeadingItemOnClick.getSqlId(), SQL_ID2)) {
            editTextInputSearch.setHint("Наим. + Произв. через пробел");
        }
        editTextInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Этот метод вызывается перед изменением текста
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Этот метод вызывается во время изменения текста
                // Отменяем предыдущий Runnable, если он существует
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                // Создаем новый Runnable для выполнения поиска
                searchRunnable = () -> {
                    String input = s.toString().trim();
                    if (!input.isEmpty()) {
//                                    textViewQuotes.setVisibility(View.GONE);
                        String sqlId = columnHeadingItemOnClick.getSqlId();
                        //Убираем все пробелы в начале введенной строки
                        input = s.toString().replaceAll("^\\s+", "");
                        //Убираем каждый второй пробел между символами
                        input = input.replaceAll("\\s{2,}", " ");
                        viewModelHeadings.showResultsList(input, sqlId);
                    }
                };
                // Запланируем выполнение Runnable с задержкой, чтобы не запускалось слишком
                //много фоновых потоков, работающих с базой, что приводит к непредсказуемому
                //поведению программы при отправке получившейся коллекции результатов поиска
                //в адаптер
                handler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Этот метод вызывается после изменения текста
            }
        });
        //Метод, который вызывается при клике на кнопку "Назад"
        myButtonBack.setOnClickListener(v -> {
            finish();
            startActivity(newIntent(HeadingsList.this));
            isHeadingsListAdapter = true;
        });
    }

}




