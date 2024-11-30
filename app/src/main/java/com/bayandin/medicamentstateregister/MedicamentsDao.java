package com.bayandin.medicamentstateregister;

/*
Интерфейс, позволяющий работать с базой данных через SQL запросы.
 */

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface MedicamentsDao {
    // Метод для выполнения динамического запроса (логика формирования SQL запроса в классе, в котором вызывается данный метод
    @RawQuery
    List<MedicinalProduct> searchByColumn(SupportSQLiteQuery query);


    @Query("SELECT * FROM medicamentsRegister WHERE id = :id")
    MedicinalProduct getMedicinalProductById(int id);

//    @Query("UPDATE medicamentsRegister " +
//            "SET " +
//            "barcode = REPLACE(barcode, ' ', ''), " +
//            "quantityInPackaging = REPLACE(quantityInPackaging, ' ', ''), " +
//            "maximumPrice = REPLACE(REPLACE(maximumPrice, ' ', ''), ',', '.'), " +
//            "pricePrimaryPackaging = REPLACE(REPLACE(pricePrimaryPackaging, ' ', ''), ',', '.')")
//    void repairDatabase();

//    // Используем объект MedicinalProduct для обновления
//    @Update
//    void setRow(MedicinalProduct medicinalProduct);

    // Метод для удаления всех данных
    @Query("DELETE FROM medicamentsRegister")
    int clearTable();  // Возвращаем количество удалённых записей

    // Метод для вставки всех данных
    @Insert
    void insertAll(List<MedicinalProduct> products);  // Простой insert
}





