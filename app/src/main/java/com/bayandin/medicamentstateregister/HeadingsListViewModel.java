package com.bayandin.medicamentstateregister;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.ArrayList;
import java.util.List;

public class HeadingsListViewModel extends AndroidViewModel {

    private static final String SQL_ID1 = "internationalName";
    private static final String SQL_ID2 = "tradeName";
    private static final String SQL_ID3 = "dosageForm";
    private static final String SQL_ID4 = "owner";
    private static final String SQL_ID5 = "chemicalClassification";
    private static final String SQL_ID6 = "quantityInPackaging";
    private static final String SQL_ID7 = "maximumPrice";
    private static final String SQL_ID8 = "pricePrimaryPackaging";
    private static final String SQL_ID9 = "ruNumber";
    private static final String SQL_ID10 = "priceRegistrationDate";
    private static final String SQL_ID11 = "barcode";
    private static final String SQL_ID12 = "effectiveDate";

    private List<MedicinalProduct> medicinalProducts = new ArrayList<>();
    private final ArrayList<SearchResultItem> searchResultItems = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    Database database = Database.getInstance(getApplication());

    AppPreferences appPreferences = AppPreferences.getInstance(getApplication());


    private final MutableLiveData<ArrayList<SearchResultItem>> searchResultItemsLD = new MutableLiveData<>();

    public HeadingsListViewModel(@NonNull Application application) {
        super(application);
    }

    // Метод, который вызывается при каждом изменении текста
    @SuppressLint("SetTextI18n")
    public void showResultsList(String input, String sqlId) {
        // Отправляем работу с поиском в базе данных в фоновый поток
        Thread thread = new Thread(() -> {
            String[] twoPartsSearch = input.split(" "); // Разделяем строку по пробелу
            if (twoPartsSearch.length < 2) {
                medicinalProducts = searchMedicinalProducts(twoPartsSearch[0], sqlId);
            } else {
                medicinalProducts = searchMedicinalProducts(twoPartsSearch[0], twoPartsSearch[1], sqlId);
            }
            searchResultItems.clear();
            for (MedicinalProduct medicinalProduct : medicinalProducts){
                switch (sqlId) {
                    case SQL_ID1: {
            /*
            Будем выводить результаты поиска из трех атрибутов объекта medicinalProduct.
            В searchResultItem будет конкатенация этих атрибутов и перевод в HTML формат
            в методе stringToHTML, чтобы выводить текст в разном цвете
            */
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getInternationalName(),                                                                       //color1
                                        medicinalProduct.getOwner(),                                                                                        //color2
                                        medicinalProduct.getMaximumPrice().toString(),                                                                      //color3
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",  //color4
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));                         //color5

                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID2: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getTradeName(),
                                        medicinalProduct.getOwner(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID3: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getDosageForm(),
                                        medicinalProduct.getInternationalName(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID4: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getOwner(),
                                        medicinalProduct.getInternationalName(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID5: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getChemicalClassification(),
                                        medicinalProduct.getOwner(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID6: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getInternationalName(),
                                        medicinalProduct.getOwner(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID7: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getInternationalName(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID8: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getPricePrimaryPackaging().toString(),
                                        medicinalProduct.getOwner(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID9: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getRuNumber(),
                                        medicinalProduct.getInternationalName(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID10: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getPriceRegistrationDate(),
                                        medicinalProduct.getInternationalName(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID11: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getBarcode(),
                                        medicinalProduct.getInternationalName(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                    case SQL_ID12: {
                        SearchResultItem searchResultItem = new SearchResultItem(
                                medicinalProduct.getId(),
                                stringToHTML(medicinalProduct.getEffectiveDate(),
                                        medicinalProduct.getInternationalName(),
                                        medicinalProduct.getMaximumPrice().toString(),
                                        medicinalProduct.getQuantityInPackaging() != null ? medicinalProduct.getQuantityInPackaging().toString() : "N/A",
                                        medicinalProduct.getBarcode() != null ? medicinalProduct.getBarcode() : "N/A"));
                        searchResultItems.add(searchResultItem);
                        break;
                    }
                }
            }
            //Отправляем в Handler код, который необходимо выполнить после окончания поиска в базе данных
            handler.post(new Runnable() {
                @Override
                public void run() {
                    searchResultItemsLD.postValue(searchResultItems);
//                        searchResultAdapter.setSearchResults(searchResultItems);
//                        recycleViewHeadings.setAdapter(searchResultAdapter);
//                        int countSearch = searchResultItems.size();
//                        textViewNumberOfFound.setText("Найдено: " + countSearch);
//                        textViewNumberOfFound.setVisibility(View.VISIBLE);
                }
            });
        });
        thread.start();
    }

    private List<MedicinalProduct> searchMedicinalProducts(String input1, String columnName1) {
        // Проверка на валидность имени столбца
        if (columnName1 == null || columnName1.isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        // Формирование SQL-запроса
        String sql = "SELECT * FROM medicamentsRegister WHERE " + columnName1 + " LIKE ? LIMIT 100";
        // Создание объекта SupportSQLiteQuery
        SupportSQLiteQuery query = new SimpleSQLiteQuery(sql, new Object[]{"%" + input1 + "%"});
        medicinalProducts = database.medicamentsDao().searchByColumn(query);
        // Вызов метода DAO с созданным запросом
        return medicinalProducts;
    }

    private List<MedicinalProduct> searchMedicinalProducts(String input1, String input2, String columnName1) {
        // Проверка на валидность имен столбцов
        if (columnName1 == null || columnName1.isEmpty()) {
            throw new IllegalArgumentException("Column names cannot be null or empty");
        }
        // Формирование SQL-запроса
        String sql = "SELECT * FROM medicamentsRegister WHERE "
                + columnName1 + " LIKE ? AND "
                + "owner" + " LIKE ? LIMIT 100";
        // Создание объекта SupportSQLiteQuery с параметрами
        SupportSQLiteQuery query = new SimpleSQLiteQuery(sql, new Object[]{"%" + input1 + "%", "%" + input2 + "%"});
        medicinalProducts = database.medicamentsDao().searchByColumn(query);
        // Вызов метода DAO с созданным запросом
        return medicinalProducts;
    }

    // Используем Html.fromHtml для интерпретации HTML-тегов
    private Spanned stringToHTML (String textColor1, String textColor2, String textColor3,
                                  String textColor4, String textColor5) {
        String formattedText = textColor1 +
                "<font color='#44C1C1'><br>" + textColor2 + "</font>" +
                "<font color='#C4C327'><br>" + textColor3 + " руб.</font>" +
                "<font color='#F08114'><br>Упаковка " + textColor4 + " шт.</font>" +
                "<font color='#A497E7'><br>Штрихкод (EAN13): " + textColor5 + "</font>";
        return Html.fromHtml(formattedText, Html.FROM_HTML_MODE_LEGACY);
    }

    public MutableLiveData<ArrayList<SearchResultItem>> getSearchResultItemsLD() {
        return searchResultItemsLD;
    }

    public LiveData<Boolean> getIsCorrectSheetNameLD() {
        return appPreferences.getIsCorrectSheetNameLD();
    }

}
