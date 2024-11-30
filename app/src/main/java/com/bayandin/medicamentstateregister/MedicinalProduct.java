package com.bayandin.medicamentstateregister;
/*
"Класс, экземплярами которого являются лекарственные препараты.
 */


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicamentsRegister")
public class MedicinalProduct {

          @PrimaryKey(autoGenerate = true)
          @ColumnInfo(name = "id")
    private Integer id;//Уникальный идентификатор с автогенерацией
          @ColumnInfo(name = "internationalName")
    private String internationalName; // МНН (Международное непатентованное наименование)
          @ColumnInfo(name = "tradeName")
    private String tradeName; // Торговое наименование лекарственного препарата
          @ColumnInfo(name = "dosageForm")
    private String dosageForm;// Лекарственная форма, дозировка, упаковка (полная)
          @ColumnInfo(name = "owner")
    private String owner; // Владелец РУ/производитель/упаковщик/Выпускающий контроль
          @ColumnInfo(name = "chemicalClassification")
    private String chemicalClassification; //Код АТХ
          @ColumnInfo(name = "quantityInPackaging")
    private Double quantityInPackaging; // Количество в потреб. упаковке
          @ColumnInfo(name = "maximumPrice")
    private Double maximumPrice; // Предельная цена руб. без НДС
          @ColumnInfo(name = "pricePrimaryPackaging")
    private Double pricePrimaryPackaging; // Цена указана для первич. упаковки
          @ColumnInfo(name = "ruNumber")
    private String ruNumber; // № РУ
          @ColumnInfo(name = "priceRegistrationDate")
    private String priceRegistrationDate; // Дата регистрации цены (№ решения)
          @ColumnInfo(name = "barcode")
    private String barcode; //Штрих-код (EAN13)
          @ColumnInfo(name = "effectiveDate")
    private String effectiveDate; //Дата вступления в силу

    public MedicinalProduct(Integer id, String internationalName, String tradeName,
                            String dosageForm, String owner, String chemicalClassification,
                            Double quantityInPackaging, Double maximumPrice,
                            Double pricePrimaryPackaging, String ruNumber,
                            String priceRegistrationDate, String barcode, String effectiveDate) {
        this.id = id;
        this.internationalName = internationalName;
        this.tradeName = tradeName;
        this.dosageForm = dosageForm;
        this.owner = owner;
        this.chemicalClassification = chemicalClassification;
        this.quantityInPackaging = quantityInPackaging;
        this.maximumPrice = maximumPrice;
        this.pricePrimaryPackaging = pricePrimaryPackaging;
        this.ruNumber = ruNumber;
        this.priceRegistrationDate = priceRegistrationDate;
        this.barcode = barcode;
        this.effectiveDate = effectiveDate;
    }

    public Integer getId() {
        return id;
    }

    public String getInternationalName() {
        return internationalName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public String getOwner() {
        return owner;
    }

    public String getChemicalClassification() {
        return chemicalClassification;
    }

    public Double getQuantityInPackaging() {
        return quantityInPackaging;
    }

    public Double getMaximumPrice() {
        return maximumPrice;
    }

    public Double getPricePrimaryPackaging() {
        return pricePrimaryPackaging;
    }

    public String getRuNumber() {
        return ruNumber;
    }

    public String getPriceRegistrationDate() {
        return priceRegistrationDate;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }
}

