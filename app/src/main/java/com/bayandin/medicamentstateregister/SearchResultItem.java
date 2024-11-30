package com.bayandin.medicamentstateregister;

import android.text.Spanned;

public class SearchResultItem {

    //Экземпляр результата поиска

    private final int idMedicinalProduct;
    private final Spanned text;

//    public SearchResultItem(Spanned text) {
//        this.text = text;
//    }

    public SearchResultItem(int idMedicinalProduct, Spanned text) {
        this.idMedicinalProduct = idMedicinalProduct;
        this.text = text;
    }

    public int getIdMedicinalProduct() {
        return idMedicinalProduct;
    }

    public Spanned getText() {
        return text;
    }
}
