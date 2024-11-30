package com.bayandin.medicamentstateregister;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class InformationPanelActivity extends AppCompatActivity {

    private static final String EXTRA_ID_MEDICINAL_PRODUCT = "id";
    private Database database;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private TextView textView9;
    private TextView textView10;
    private TextView textView11;
    private TextView textView12;
    private TextView textView13;
    private TextView textView14;
    private TextView textView15;
    private TextView textView16;
    private TextView textViewPercent;

    private Button buttonChangeMarkup;
    private Button buttonResetMarkup;
    private EditText editTextChangeMark;
    private ScrollView scrollViewFullInfo;

    private static Double price;  //Переменная для хранения стоимости препарата

    private MedicinalProduct medicinalProduct;
    private final Handler handler = new Handler(Looper.getMainLooper());


    private void initView() {
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);
        textView10 = findViewById(R.id.textView10);
        textView11 = findViewById(R.id.textView11);
        textView12 = findViewById(R.id.textView12);
        textView13 = findViewById(R.id.textView13);
        textView14 = findViewById(R.id.textView14);
        textView15 = findViewById(R.id.textView15);
        textView16 = findViewById(R.id.textView16);
        textViewPercent = findViewById(R.id.textViewPercent);

        buttonChangeMarkup = findViewById(R.id.buttonChangeMarkup);
        buttonResetMarkup = findViewById(R.id.buttonResetMarkup);
        editTextChangeMark = findViewById(R.id.editTextChangeMark);
        scrollViewFullInfo = findViewById(R.id.scrollViewFullInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_panel);
        initView();
        Intent intent = getIntent();
        int idMedicinalProduct = intent.getIntExtra(EXTRA_ID_MEDICINAL_PRODUCT, 0);
        database = Database.getInstance(getApplication());
        Thread thread = new Thread(() -> {
            //Достаем препарат из базы по его id
            medicinalProduct = database.medicamentsDao().getMedicinalProductById(idMedicinalProduct);
            price = medicinalProduct.getMaximumPrice();
            //Выводим информацию в TextView
            handler.post(this::setDisplayStandartMarkup);
        });
        thread.start();

        //Слушатель клика по кнопке "Изменить размер надбавки"
        buttonChangeMarkup.setOnClickListener(v -> {
            setDisplayClickChangeMark();
            // Показать клавиатуру
            editTextChangeMark.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editTextChangeMark, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        //Слушатель клика по кнопке "Вернуть надбавки по-умолчанию"
        buttonResetMarkup.setOnClickListener(v -> {
            setDisplayClickResetMark();
            // Скрыть клавиатуру
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editTextChangeMark.getWindowToken(), 0);
            }
        });
    }

    /*
    Метод для вывода информации в TextView с учетом локали (критично для андроида, так как в разных
    странах по разному отображаются разделители в числах). С переопределением в зависимости от типа
    данных
     */
    private void setTextViewLocale(int stringResId, TextView textView, Double text) {
        // Проверка на null для объекта Double
        String formattedText = (text != null)
                ? getString(stringResId, String.format(Locale.getDefault(),
                "<font color='#FFA500'>%.2f</font>", text))
                : getString(stringResId, "<font color='#FF0008'>" +
                getString(R.string.value_not_available) + "</font>");
        textView.setText(Html.fromHtml((formattedText), Html.FROM_HTML_MODE_LEGACY));
    }

//    private void setTextViewLocale(int stringResId, TextView textView, Long text) {
//        // Проверка на null для объекта Long
//        String formattedText = (text != null)
//                ? getString(stringResId, String.format(Locale.getDefault(),
//                "<font color='#C7EAF5'>%d</font>", text))
//                : getString(stringResId, "<font color='#FF0008'>" +
//                getString(R.string.value_not_available) + "</font>");
//        // Устанавливаем текст в TextView
//        textView.setText(Html.fromHtml((formattedText), Html.FROM_HTML_MODE_LEGACY));
//    }

//    private void setTextViewLocale(int stringResId, TextView textView, Integer text) {
//        // Проверка на null для объекта Long
//        String formattedText = (text != null)
//                ? getString(stringResId, String.format(Locale.getDefault(),
//                "<font color='#F6797D'>%d</font>", text))
//                : getString(stringResId, "<font color='#FF0008'>" +
//                getString(R.string.value_not_available) + "</font>");
//        // Устанавливаем текст в TextView
//        textView.setText(Html.fromHtml((formattedText), Html.FROM_HTML_MODE_LEGACY));
//    }

    private void setTextViewLocale(int stringResId, TextView textView, String text) {
        // Проверка на null или пустоту для объекта String
        String formattedText = (text != null && !text.isEmpty())
                ? getString(stringResId, String.format(Locale.getDefault(),
                "<font color='#C7F5D3'><i>%s</i></font>", text))
                : getString(stringResId, "<font color='#FF0008'>" +
                getString(R.string.value_not_available) + "</font>");
        textView.setText(Html.fromHtml((formattedText), Html.FROM_HTML_MODE_LEGACY));
    }

    //Конструктор для интента данной активти
    public static Intent newIntent(Context context, int idMedicinalProduct) {
        Intent intent = new Intent(context, InformationPanelActivity.class);
        intent.putExtra(EXTRA_ID_MEDICINAL_PRODUCT, idMedicinalProduct);
        return intent;
    }

    //Метод, изменяющий видимость эелементов после клика по кнопке "Инзменить размер надбавки"
    private void setDisplayClickChangeMark() {
        textView1.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView4.setVisibility(View.GONE);
        textView5.setVisibility(View.GONE);
        textView6.setVisibility(View.GONE);
        textView7.setVisibility(View.GONE);
        textView8.setVisibility(View.GONE);
        textView9.setVisibility(View.GONE);
        textView10.setVisibility(View.GONE);
        textView11.setVisibility(View.GONE);
        editTextChangeMark.setVisibility(View.VISIBLE);
        buttonChangeMarkup.setVisibility(View.INVISIBLE);
        buttonResetMarkup.setVisibility(View.VISIBLE);

        //Слушатель вводимых символов в поле editChangeMark
        editTextChangeMark.addTextChangedListener(new TextWatcher() {
            private boolean isToastShown = false;//флаг срабатывания тоста о недопустимой надбавке

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ничего не делаем до изменения текста
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ничего не делаем во время изменения текста
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                textViewPercent.setTextColor(getResources().getColor(R.color.translucent_white_50, getTheme()));
                String inputText = s.toString();
                double inputNumber;
                if (!inputText.isEmpty()) {
                    // Преобразуем строку в число типа Double
                    inputNumber = Double.parseDouble(inputText);
                    //Проверяем, не превышает ли процент введенной пользователем надбавки допустимый
                    if (inputNumber < 0 || inputNumber > (MathCalculations.getWholesaleMarkup(price) * 100)) {
                        // Если выше, откатываем изменение в EditText, чтобы оно оставалось прежним
//                        editTextChangeMark.setText("");  // Очищаем поле, можно использовать исходное значение
                        // Показываем тост, если он еще не был показан
                        setErrorDisplay();
                        textViewPercent.setText(inputText + "%");
                        textViewPercent.setTextColor(getResources().getColor(R.color.red, getTheme()));
                        if (!isToastShown) {
                            Toast.makeText(getApplicationContext(), "Надбавка выше допустимого значения!", Toast.LENGTH_SHORT).show();
                            isToastShown = true;
                        }
                    } else {
                        // Если значение допустимое, сбрасываем флаг
                        isToastShown = false;
                        //Выводим расчеты для заданной надбавки
                        setUserMarkupCalculation(inputNumber);
                        textViewPercent.setText(inputText + "%");
                        // Задержка перед началом анимации
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            // Прокрутка к самому низу
                            scrollViewFullInfo.fullScroll(ScrollView.FOCUS_DOWN);
                            // Задержка перед прокруткой обратно наверх
                            new Handler(Looper.getMainLooper()).postDelayed(() -> scrollViewFullInfo.fullScroll(ScrollView.FOCUS_UP), 500);
                        }, 500);  // Задержка перед началом анимации — 1 секунда
                    }
                    //Если пустая строка
                } else setDisplayStandartMarkup();

            }

        });
    }

    //Метод, изменяющий видимость эелементов после клика по кнопке "Вернуть надбавки по-умолчанию"
    private void setDisplayClickResetMark() {
        textView1.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.VISIBLE);
        textView4.setVisibility(View.VISIBLE);
        textView5.setVisibility(View.VISIBLE);
        textView6.setVisibility(View.VISIBLE);
        textView7.setVisibility(View.VISIBLE);
        textView8.setVisibility(View.VISIBLE);
        textView9.setVisibility(View.VISIBLE);
        textView10.setVisibility(View.VISIBLE);
        textView11.setVisibility(View.VISIBLE);
        editTextChangeMark.setVisibility(View.GONE);
        buttonChangeMarkup.setVisibility(View.VISIBLE);
        buttonResetMarkup.setVisibility(View.GONE);
        editTextChangeMark.setText("");  // Очищаем поле, можно использовать исходное значение
        setDisplayStandartMarkup();
    }

    //Метод, который выводит информацию на экран, если надбавка стандартная
    @SuppressLint("SetTextI18n")
    private void setDisplayStandartMarkup() {
        //Выводим информацию в TextView
        setTextViewLocale(R.string.international_name, textView1, medicinalProduct.getInternationalName());
        setTextViewLocale(R.string.trade_name, textView2, medicinalProduct.getTradeName());
        setTextViewLocale(R.string.owner, textView3, medicinalProduct.getOwner());
        setTextViewLocale(R.string.chemical_classification, textView4, medicinalProduct.getChemicalClassification());
        setTextViewLocale(R.string.dosage_form, textView5, medicinalProduct.getDosageForm());
        setTextViewLocale(R.string.rice_registration_date, textView6, medicinalProduct.getPriceRegistrationDate());
        setTextViewLocale(R.string.effective_date, textView7, medicinalProduct.getEffectiveDate());
        setTextViewLocale(R.string.quantity_in_packaging, textView8, medicinalProduct.getQuantityInPackaging());
        setTextViewLocale(R.string.ru_number, textView9, medicinalProduct.getRuNumber());
        setTextViewLocale(R.string.barcode, textView10, medicinalProduct.getBarcode());
        setTextViewLocale(R.string.price_primary_packaging, textView11, medicinalProduct.getPricePrimaryPackaging());
        setTextViewLocale(R.string.maximum_price, textView12, price);
        setTextViewLocale(R.string.markup_price, textView13, MathCalculations.getPriceWithMarkup(price));
        setTextViewLocale(R.string.price_tax, textView14, MathCalculations.getPriceWithWal(MathCalculations.getPriceWithMarkup(price)));
        setTextViewLocale(R.string.tax, textView15, MathCalculations.getWal(MathCalculations.getPriceWithMarkup(price)));
        setTextViewLocale(R.string.markup, textView16, MathCalculations.getMarkup(price, MathCalculations.getPriceWithMarkup(price)));
        //Получаем процент надбавки для цены препарата
        double wholesaleMarkup = MathCalculations.getWholesaleMarkup(price) * 100;
        //Округляем до десятых
        double roundedWholesaleMarkup = Math.round(wholesaleMarkup * 10.0) / 10.0;
        //Выводим в качестве подсказки большого размера
        textViewPercent.setText(roundedWholesaleMarkup + "%");
        textViewPercent.setTextColor(getResources().getColor(R.color.translucent_white_50, getTheme()));

        // Задержка перед началом анимации
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Прокрутка к самому низу
            scrollViewFullInfo.fullScroll(ScrollView.FOCUS_DOWN);

            // Задержка перед прокруткой обратно наверх
            new Handler(Looper.getMainLooper()).postDelayed(() -> scrollViewFullInfo.fullScroll(ScrollView.FOCUS_UP), 500);
        }, 500);  // Задержка перед началом анимации — 1 секунда

    }

    //Метод для вывода сообщений об ошибке в вычислениях
    private void setErrorDisplay() {
        setTextViewLocale(R.string.markup_price, textView13, "-----");
        setTextViewLocale(R.string.price_tax, textView14, "-----");
        setTextViewLocale(R.string.tax, textView15, "-----");
        setTextViewLocale(R.string.markup, textView16, "-----");
    }
    //Очищаем EditView при снятии фокуса с активити
    protected void onPause() {
        super.onPause();
        editTextChangeMark.setText("");  // Очищаем поле, можно использовать исходное значение
    }

    //Метод для вывода расчетов для заданной вручную надбавки
    private void setUserMarkupCalculation(Double inputNumber) {
        setTextViewLocale(R.string.markup_price, textView13, MathCalculations.getPriceWithUserMurkup(price, inputNumber));
        setTextViewLocale(R.string.price_tax, textView14, MathCalculations.getPriceWithWal(MathCalculations.getPriceWithUserMurkup(price, inputNumber)));
        setTextViewLocale(R.string.tax, textView15, MathCalculations.getWal(MathCalculations.getPriceWithUserMurkup(price, inputNumber)));
        setTextViewLocale(R.string.markup, textView16, MathCalculations.getMarkup(price, MathCalculations.getPriceWithUserMurkup(price, inputNumber)));
    }
}


