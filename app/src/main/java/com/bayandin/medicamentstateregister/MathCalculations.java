package com.bayandin.medicamentstateregister;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathCalculations {

    private static final double wholesaleMarkup100 = 0.17; //Размер оптовой наценки (цена до 100 руб включительно)
    private static final double wholesaleMarkup500 = 0.16; //Размер оптовой наценки (цена от 100 до 500 руб включительно)
    private static final double wholesaleMarkupMin = 0.14; //Размер оптовой наценки (свыше 500 руб)
    private static final Float wat = 0.1f; //Размер НДС (%/100)
    private static Double priceWithMarkup; //Размер стоимости с надбавкой

    //Метод, возвращающий применяемую надбавку в зависимости от цены препарата в процентах
    public static double getWholesaleMarkup (Double price) {
        double wholesaleMarkup = 0;
        if (price <= 100) {
            wholesaleMarkup = wholesaleMarkup100;
        } else if(price <= 500) {
            wholesaleMarkup = wholesaleMarkup500;
        } else if(price > 500) {
            wholesaleMarkup = wholesaleMarkupMin;
        }
        return wholesaleMarkup;
    }


    //Метод для отбрасывания лишних знаков после запятой до десятых без округления
    private static Double getOneDecimalPlacePrice(Double price) {
        BigDecimal bigDecimalPrice = BigDecimal.valueOf(price);
        BigDecimal oneDecimalPlacePrice = bigDecimalPrice.setScale(1, RoundingMode.DOWN);
        return oneDecimalPlacePrice.doubleValue();
    }

    //Метод, возвращающий предельный размер цены со стандартной оптовой надбавкой в зависимости от размера цены
    public static Double getPriceWithMarkup(Double price) {
        double wholesaleMarkup = getWholesaleMarkup(price);
        double markup;
        double priceDecimal = getOneDecimalPlacePrice(price);
        markup = priceDecimal * (1 + wholesaleMarkup);
        priceWithMarkup = getOneDecimalPlacePrice(markup);
        return priceWithMarkup;
    }

    //Метод, возвращающий предельный размер цены с произвольной оптовой надбавкой
    public static Double getPriceWithUserMurkup (Double price, Double userMarkup) {
        double markup;
        Double priceDecimal = getOneDecimalPlacePrice(price);
        markup = priceDecimal * (1 + userMarkup / 100);
        priceWithMarkup = getOneDecimalPlacePrice(markup);
        return priceWithMarkup;
    }

    //Метод, возвращающий цену с НДС
    public static Double getPriceWithWal(Double priceWithMarkup) {
        return priceWithMarkup * (1 + wat);
    }

    //Метод, возвращающий сумму НДС
    public static Double getWal(Double priceWithMarkup) {
        return priceWithMarkup * (1 + wat) - priceWithMarkup;
    }

    //Метод, возвращающий размер надбавки
    public static Double getMarkup(Double price, Double priceWithMarkup) {
        return priceWithMarkup - price;
    }
}
