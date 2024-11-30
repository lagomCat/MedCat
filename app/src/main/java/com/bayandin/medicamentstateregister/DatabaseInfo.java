package com.bayandin.medicamentstateregister;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.LinkedList;

public class DatabaseInfo extends AppCompatActivity {

    private TextView textViewOldGuid;
    private TextView textViewNewGuid;
    private TextView textViewNewRows;
    private TextView textViewOldRows;
    private TextView textViewOtherInformation;
    private Button buttonTest;
    private Button buttonSettings;
    private static boolean UPDATE_PROGRESS = true; //Флаг процесса обновления базы


    //Переменные для вывода сообщений в консоль приложения
    private TextView consoleTextView;
    private TextView textViewDateOfUpdate;
    private TextView textViewFileError;
    private final LinkedList<String> logMessages = new LinkedList<>();
    private static final int MAX_LINES = 1;

    private void initView() {
        textViewOldGuid = findViewById(R.id.textViewOldGuid);
        textViewNewGuid = findViewById(R.id.textViewNewGuid);
        textViewDateOfUpdate = findViewById(R.id.textViewDateOfUpdate);
        buttonTest = findViewById(R.id.buttonTest);
        consoleTextView = findViewById(R.id.consoleTextView);
        textViewFileError = findViewById(R.id.textViewFileError);
        textViewNewRows = findViewById(R.id.textViewNewRows);
        textViewOldRows = findViewById(R.id.textViewOldRows);
        textViewOtherInformation = findViewById(R.id.textViewOtherInformation);
        buttonSettings = findViewById(R.id.buttonSettings);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_database_info);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        AppPreferences appPreferences = AppPreferences.getInstance(this);
        DatabaseInfoViewModel viewModel = new ViewModelProvider(this).get(DatabaseInfoViewModel.class);
        Log.d("Точка11", "Автоматическое обновление базы: " + appPreferences.isAutoUpdate());
        initView();



        //Предлагаем пользователю разрешить работу в фоне (для автоматического обновления по планировщику
        buttonSettings.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);
        });

        setTextViews(appPreferences);
        showLogData(viewModel);

        // Создаем обработчик для кнопки "Назад", чтобы вью с процессом обновления не уничтожалась по кнопке "Назад"
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("Точка12", "Кнопка Назад была нажата, но активность не будет закрыта.");
                //Переход на главную страницу
                Intent intent = HeadingsList.newIntent(DatabaseInfo.this);
                startActivity(intent);
                finish();
            }
        };
        // Добавляем обработчик в диспетчер
        getOnBackPressedDispatcher().addCallback(this, callback);

        //Выводим сообщения консоли (после пересоздания активити)
        consoleTextView.setText(TextUtils.join("\n", logMessages));

        //Подписываемся на изменение флага статуса процесса обновления
        viewModel.getIsUpdateInProgressLD().observe(this, updateProgress -> {
            Log.d("myLiveData1", "onChanged(Boolean updateProgress) = " + updateProgress);
            UPDATE_PROGRESS = updateProgress;
            setTextViews(appPreferences);
            if (!updateProgress) {
                logToConsole("Обновление завершено.");
//                    textViewUpdateMessage.setVisibility(View.GONE);
            } //else textViewUpdateMessage.setVisibility(View.VISIBLE);
        });

        //Подписка на флаг не найденного имени целевого листа в таблице Excel
        viewModel.getIsCorrectSheetNameLD().observe(this, isCorrectSheetName -> {
            Log.d("myLiveData2", "onChanged(Boolean isCorrectSheetName) = " + isCorrectSheetName);
            if (!isCorrectSheetName) {
                textViewOtherInformation.setVisibility(View.VISIBLE);
                textViewOtherInformation.setText(R.string.incorrect_sheet);
                consoleTextView.setVisibility(View.GONE);
            } else {
                textViewOtherInformation.setVisibility(View.GONE);
                consoleTextView.setVisibility(View.VISIBLE);
            }
        });

        //Подписка на isFileErrorLD
        viewModel.getIsFileErrorLD().observe(this, isFileErrorLD -> {
            Log.d("myLiveData3", "onChanged(Boolean isFileErrorLD) = " + isFileErrorLD);
            if (isFileErrorLD) {
                textViewFileError.setVisibility(View.VISIBLE);
                consoleTextView.setVisibility(View.GONE);
            } else {
                textViewFileError.setVisibility(View.GONE);
                consoleTextView.setVisibility(View.VISIBLE);
            }
        });

        //Подписка на успешное обновление
        viewModel.isSuccessfulUpdateLD().observe(this, isSuccessfulUpdateLD -> {
            if (isSuccessfulUpdateLD) {
//                    textViewUpdateMessage.setVisibility(View.GONE);
            }
        });


        if (!UPDATE_PROGRESS && appPreferences.isDatabaseOutdated()) {
                    viewModel.startExportDataService(DatabaseInfo.this);//Запускаем обновление базы в фоне
                    logToConsole("Загрузка файла обновления...");
                    Log.d("Точка13", "viewModel.startExportDataService(this);");
        }


        if (appPreferences.isAutoUpdate()) {
                    Log.d("Точка16", "Автоматическое обновление базы: " + appPreferences.isAutoUpdate());
                    //Переход на главную страницу, если это автоматическое обновление
                    Intent intent = HeadingsList.newIntent(DatabaseInfo.this);
                    startActivity(intent);
        }





        //Клик по кнопке "Вернуться на главную страницу"
        buttonTest.setOnClickListener(v -> {
        //Переход на главную страницу
            Intent intent = HeadingsList.newIntent(DatabaseInfo.this);
            startActivity(intent);
            finish();
        });
    }

    //Метод для вывода информации в консоль
    public void logToConsole(String message) {
        runOnUiThread(() -> {
            if (!logMessages.isEmpty()) {
                logMessages.removeFirst();
            }
            logMessages.add(message);
            Log.d("Точка17", "Получаем сообщение в лог: " + message);
            consoleTextView.setText(TextUtils.join("\n", logMessages));
        });
    }

    //Конструктор для интента данной активти
    public static Intent newIntent(Context context) {
        return new Intent(context, DatabaseInfo.class);
    }

    //Метод для вывода различных данных в консоль
    private void showLogData (DatabaseInfoViewModel viewModel){
        Log.d("Точка18", "Вызов метода showLogData");
        viewModel.getRowsPercentagesCount().observe(this, rowCount -> {
            Log.d("ТОчка19", "Сработка подписки на rowCount");
            logToConsole(rowCount);
        });

        viewModel.getOldRows().observe(this, this::logToConsole);

        viewModel.getNewRows().observe(this, this::logToConsole);

        viewModel.getCountRows().observe(this, this::logToConsole);

    }

    //Метод для установки значений в виджеты
    private void setTextViews(AppPreferences appPreferences) {
        textViewOldGuid.setText(getString(R.string.old_guid, appPreferences.getCurrentGuid()));
        textViewNewGuid.setText(getString(R.string.new_guid, appPreferences.getNewGuid()));
        textViewNewRows.setText(getString(R.string.new_rows, appPreferences.getRowsCurrentDB()));
        textViewOldRows.setText(getString(R.string.old_rows, appPreferences.getRowsOldDB()));
        textViewDateOfUpdate.setText(getString(R.string.date_of_update, appPreferences.getGetDateOfUpdate()));
    }

}