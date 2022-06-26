# Бэкенд для веб-сервиса сравнения цен (price_comparator)

## Описание сервиса
Данный сервис обрабатывает следующие типы запросов:

1) импорт / обновление товаров и категорий: /imports
2) удаление товара или категории (с учётом всех дочерних элементов) по идентификатору: /delete/{id}
3) получение информации об элементе по идентификатору: /nodes/{id}
4) получение информации об обновлённых элементах за период [date - 24h; date]: /sales
5) получение истории обновления элемента за период [dateStart; dateEnd): /node/{id}/statistic

Если в запросе поле не заполняется (price, parentId), то должно быть проставлено значение **null**.

В качестве основной базы данных сервиса используется PostgreSQL (развёрнута в docker контейнере).
Для запуска тестов используется встренная база данных H2.

Приложение развёрнуто в предоставленном контейнере (10.20.1.155.), для отправки запросов на него нужно использовать адрес: https://snake-2061.usr.yandex-academy.ru

В данном контейнере настроен автоматический перезапуск сервиса при перезагрузке в файле: /etc/systemd/system/price_comparator.service


## Описание работы приложения
История обновления элементов хранится в базе данных следующим образом (таблица SHOP_UNITS_STATISTICS):  

|      id         |    parentid     |    date    | type     | price |  
| --------------- | --------------- |----------- | -------- | ----- |   
| parent-category | null            | 01.01.2022 | CATEGORY | 100   |  
| child-offer     | parent-category | 01.01.2022 | OFFER    | 100   |  
| parent-category | null            | 21.01.2022 | CATEGORY | 200   |  
| child-offer     | child-offer     | 21.01.2022 | OFFER    | 200   |  

Если удаляется элемент child-offer, то в базе данных хранятся следующие записи:  

|     id          |    parentid     |    date    |    type  | price |  
| --------------- | --------------- | ---------- | -------- | ----- |  
| parent-category | null            | 01.01.2022 | CATEGORY | 100   |  
| parent-category | null            | 21.01.2022 | CATEGORY | 200   |  
| parent-category | null            | 21.01.2022 | CATEGORY | null  |  

Таким образом, вся история обновлений категории parent-category будет включать три элемента.


## Сборка проекта

1) Склонировать репозиторий на локальный диск:
https://github.com/salogubovay/price_comparator.git

2) Перейти в папку с репозиторием:
cd price_comparator

3) Для запуска тестов нужно выполнить команду:
mvn test

4) Для корректного запуска сборки контейнера с приложением на Ubuntu нужно выполнить команду:
chmod +x mvnw

5) Для запуска приложения нужно выполнить команду:
docker-compose up