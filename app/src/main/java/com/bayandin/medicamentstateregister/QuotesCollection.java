
/*
Класс для получения случайных цитат из интегрированного файла .txt
Отключено по требованиям пользователя
 */


//package com.bayandin.medicamentstateregister;
//
//import android.content.Context;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
//public class QuotesCollection {
//    private static final List<String> quotes = new ArrayList<>();
//
//    public static void loadQuotes(Context context) {
//        try (InputStream is = context.getAssets().open("quotes.txt");
//             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                quotes.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    // Метод для получения случайной цитаты
//    public static String getRandomQuote() {
//        if (quotes.isEmpty()) return "Нет доступных цитат.";
//        int randomIndex = (int) (Math.random() * quotes.size());
//        return quotes.get(randomIndex);
//    }
//}


