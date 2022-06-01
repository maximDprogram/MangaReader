# MangaReader
приложение для чтения манги и комиксов

![](https://github.com/maximDprogram/MangaReader/blob/master/assets/logo.jpg)

# Особенности:
* Имеет возможность подключения к внешнему FTP-серверу;
* Работает как в онлайн-режиме, так и в оффлайн.

# Внешний вид:
![](https://github.com/maximDprogram/MangaReader/blob/master/assets/main.gif)
![](https://github.com/maximDprogram/MangaReader/blob/master/assets/main2.gif)
![](https://github.com/maximDprogram/MangaReader/blob/master/assets/main1.gif)
![](https://github.com/maximDprogram/MangaReader/blob/master/assets/main3.gif)

# Настройка:
* Структура корневого каталога:

  ![](https://github.com/maximDprogram/MangaReader/blob/master/assets/structure.JPG)

* Структура json-файлов:

  ![](https://github.com/maximDprogram/MangaReader/blob/master/assets/json.JPG)

* Первая глава в каталоге всегда должна иметь название "ch 0 apter1.cbz";
* Названия всех остальных глав должны быть следующего вида "[любой_текст] [номер_главы] [любой_текст].cbz";
* Названия папок в папке "Cbz" должны соответствовать переменной "titleOrig" в json-файле;
* Названия изображений в папках "Pictures400" и "Pictures800" должны соответствовать переменной "img" в json-файле;
* Разрешение изображений должны быть 400х567 в папке "Pictures400" и 800x1133 в "Pictures800".

# Онлайн-режим:
* Запускаем FTP-сервер и вписываем данные на первом экране приложения.

# Оффлайн-режим:
* Файлы должны находиться по пути "/Android/data/com.mangareader/cache".

# Сторонние библиотеки:
* MuPDF (https://mupdf.com/);
* Lottie Animation (https://lottiefiles.com/);
* Apache Commons Net (https://commons.apache.org/proper/commons-net/).