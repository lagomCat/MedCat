# Это моё первое приложение

Код написан на начальном этапе изучения базового уровня Java. Не применяются библиотеки RxJava и т.п.

Приложение для удобной оффлайн работы с Государственным реестром предельных отпускных цен на лекарственные препараты. Реализованы:
- Проверка обновления файла на сайте при запуске главного экрана приложения.
- Загрузка обновленного файла `.xlsx`.
- Проверка таблицы на отсутствие критичных изменений.
- Парсинг данных в базу Room.
- Попытка реализации автоматического обновления базы по планировщику Worker (работает только на Samsung, остальные - нестабильно).

## Скриншоты

### Главный экран
![Главный экран](screenshots/Screenshot_2024-11-30-16-10-16-79_8ae7b337a6af061581b893e8a0541c92.jpg)

### Экран результатов поиска
![Экран результатов поиска](screenshots/Screenshot_2024-11-30-16-10-30-71_8ae7b337a6af061581b893e8a0541c92.jpg)

### Экран расчета цен
![Экран расчета цен](screenshots/Screenshot_2024-11-30-16-11-12-29_8ae7b337a6af061581b893e8a0541c92.jpg)

### Экран информации о базе данных
![Экран информации о базе данных](screenshots/Screenshot_2024-11-30-16-11-31-68_8ae7b337a6af061581b893e8a0541c92.jpg)
