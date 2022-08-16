SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode, 'ONLY_FULL_GROUP_BY', ''));

CREATE TABLE IF NOT EXISTS sweater_message
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    filename VARCHAR(255),
    tag VARCHAR(255),
    text VARCHAR(3000) NOT NULL,
    user_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sweater_roles
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sweater_user
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    activation_code VARCHAR(255),
    active BIT NOT NULL,
    email VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    username varchar(255) NOT NULL,
    image_filename VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sweater_user_role
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS sweater_user_subscriptions
(
    subscriber_id BIGINT NOT NULL,
    channel_id BIGINT NOT NULL,
    PRIMARY KEY (subscriber_id, channel_id)
);

CREATE TABLE IF NOT EXISTS message_likes
(
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (message_id) REFERENCES sweater_message (id),
    FOREIGN KEY (user_id) REFERENCES sweater_user (id),
    PRIMARY KEY (message_id, user_id)
);

ALTER TABLE sweater_message
    ADD CONSTRAINT message_user_fk
        FOREIGN KEY (user_id) REFERENCES sweater_user (id);

ALTER TABLE sweater_user_role
    ADD CONSTRAINT user_role_role_fk
        FOREIGN KEY (role_id) REFERENCES sweater_roles (id);

ALTER TABLE sweater_user_role
    ADD CONSTRAINT user_role_user_fk
        FOREIGN KEY (user_id) REFERENCES sweater_user (id);

ALTER TABLE sweater_user_subscriptions
    ADD CONSTRAINT user_role_subscriptions_channel_fk
        FOREIGN KEY (channel_id) REFERENCES sweater_user (id);

ALTER TABLE sweater_user_subscriptions
    ADD CONSTRAINT user_role_subscriptions_subscriber_fk
        FOREIGN KEY (subscriber_id) REFERENCES sweater_user (id);


INSERT INTO sweater_user (id, active, password, username, email)
VALUES (1, TRUE, '$2a$12$StHXG5uhtPOK5CswFuvZNu4JqTBhFQSL/fthsMIJketj/klNuX04m', 'admin', 'administrator@mail.ru'),
       (2, TRUE, '$2a$12$145m9lMUWTl/k7mMs4t5c.rjI50hiaxqU2CN4FTLv5Vp.OGannJam', 'user', 'user@mail.ru');

INSERT INTO sweater_roles (id, name)
VALUES (1, 'ADMIN'), (2, 'USER');

INSERT INTO sweater_user_role (user_id, role_id)
VALUES (1, 1), (1, 2), (2, 2);


INSERT INTO sweater_message (id, filename, tag, text, user_id)
VALUES (1, NULL, '#programming',
        'Java — строго типизированный объектно-ориентированный язык программирования общего назначения, разработанный компанией Sun Microsystems. Разработка ведётся сообществом, организованным через Java Community Process; язык и основные реализующие его технологии распространяются по лицензии GPL.', 1),
       (2, 'wave.png', '#subscribe',
        'Hello, subscribe on me', 2),
       (3, NULL, '#programming',
        'Java Platform, Standard Edition, сокращённо Java SE — стандартная версия платформы Java 2, предназначенная для создания и исполнения апплетов и приложений, рассчитанных на индивидуальное пользование или на использование в масштабах малого предприятия.', 1),
       (4, NULL, '#programming',
        'Jakarta EE. В 2018 Eclipse Foundation переименовала Java EE в Jakarta EE — набор спецификаций и соответствующей документации для языка Java, описывающей архитектуру серверной платформы для задач средних и крупных предприятий.', 1),
       (5, 'spring.png', '#programming',
        'Spring Framework — универсальный фреймворк с открытым исходным кодом для Java-платформы. Также существует форк для платформы .NET Framework, названный Spring.NET.', 1),
       (6, NULL, '#programming',
        'Фреймворк Spring MVC обеспечивает архитектуру паттерна Model — View — Controller (Модель — Отображение (далее — Вид) — Контроллер) при помощи слабо связанных готовых компонентов. Паттерн MVC разделяет аспекты приложения (логику ввода, бизнес-логику и логику UI), обеспечивая при этом свободную связь между ними.', 1),
       (7, NULL, '#programming',
        'Spring Data — дополнительный удобный механизм для взаимодействия с сущностями базы данных, организации их в репозитории, извлечение данных, изменение, в каких то случаях для этого будет достаточно объявить интерфейс и метод в нем, без имплементации.', 1),
       (8, 'spring-security.png', '#programming',
        'Spring Security это Java/Java EE фреймворк, предоставляющий механизмы построения систем аутентификации и авторизации, а также другие возможности обеспечения безопасности для промышленных приложений, созданных с помощью Spring Framework.', 1),
       (9, NULL, '#programming',
        'Spring Boot — это полезный проект, целью которого является упрощение создания приложений на основе Spring. Он позволяет наиболее простым способом создать web-приложение, требуя от разработчиков минимум усилий по его настройке и написанию кода.', 1),
       (10, NULL, '#programming',
        'Spring Cloud — это модуль Spring, в котором есть много всего разного для разработки микросервисной архитектуры. Там есть куски инфраструктуры и другие полезные плюшки. Одна из основных вещей, которые там есть — это Service Discovery.', 1),
       (11, NULL, '#programming',
        'Apache Maven — фреймворк для автоматизации сборки проектов на основе описания их структуры в файлах на языке POM, являющемся подмножеством XML. Проект Maven издаётся сообществом Apache Software Foundation, где формально является частью Jakarta Project.', 1),
       (12, 'jdbc.png', '#programming',
        'JDBC — платформенно независимый промышленный стандарт взаимодействия Java-приложений с различными СУБД, реализованный в виде пакета java.sql, входящего в состав Java SE. JDBC основан на концепции так называемых драйверов, позволяющих получать соединение с базой данных по специально описанному URL.', 1),
       (13, NULL, '#programming',
        'Java Persistence API — спецификация API Java EE, предоставляет возможность сохранять в удобном виде Java-объекты в базе данных. Существует несколько реализаций этого интерфейса, одна из самых популярных использует для этого Hibernate. JPA реализует концепцию ORM.', 1),
       (14, 'hibernate.png', '#programming',
        'Hibernate — библиотека для языка программирования Java, предназначенная для решения задач объектно-реляционного отображения, самая популярная реализация спецификации JPA. Распространяется свободно на условиях GNU Lesser General Public License.', 1),
       (15, 'mysql.png', '#programming',
        'MySQL — свободная реляционная система управления базами данных. Разработку и поддержку MySQL осуществляет корпорация Oracle, получившая права на торговую марку вместе с поглощённой Sun Microsystems, которая ранее приобрела шведскую компанию MySQL AB.', 1),
       (16, NULL, '#programming',
        'SQL — декларативный язык программирования, применяемый для создания, модификации и управления данными в реляционной базе данных, управляемой соответствующей системой управления базами данных.', 2),
       (17, NULL, '#programming',
        'NoSQL — обозначение широкого класса разнородных систем управления базами данных, появившихся в конце 2000-х — начале 2010-х годов и существенно отличающихся от традиционных реляционных СУБД с доступом к данным средствами языка SQL.', 2),
       (18, 'docker.png', '#programming',
        'Docker — программное обеспечение для автоматизации развёртывания и управления приложениями в средах с поддержкой контейнеризации, контейнеризатор приложений.', 2),
       (19, NULL, '#programming',
        'HTML — стандартизированный язык гипертекстовой разметки документов для просмотра веб-страниц в браузере. Веб-браузеры получают HTML документ от сервера по протоколам HTTP/HTTPS или открывают с локального диска, далее интерпретируют код в интерфейс, который будет отображаться на экране монитора.', 2),
       (20, NULL, '#programming',
        'CSS — формальный язык описания внешнего вида документа, написанного с использованием языка разметки. Также может применяться к любым XML-документам, например, к SVG или XUL.', 2),
       (21, NULL, '#programming',
        'JavaScript — мультипарадигменный язык программирования. Поддерживает объектно-ориентированный, императивный и функциональный стили. Является реализацией спецификации ECMAScript. JavaScript обычно используется как встраиваемый язык для программного доступа к объектам приложений.', 2),
       (22, NULL, '#programming',
        'Thymeleaf - это шаблонизатор Java XML / XHTML / HTML5, который может работать как в веб-средах, так и не в веб-средах. Он лучше подходит для обслуживания XHTML / HTML5 на уровне представления веб-приложений на основе MVC, но он может обрабатывать любой XML-файл даже в автономных средах.', 1),
       (23, NULL, '#programming',
        'Flyway - это инструмент миграции базы данных с открытым исходным кодом.', 1),
       (24, NULL, '#programming',
        'Bootstrap — свободный набор инструментов для создания сайтов и веб-приложений. Включает в себя HTML- и CSS-шаблоны оформления для типографики, веб-форм, кнопок, меток, блоков навигации и прочих компонентов веб-интерфейса, включая JavaScript-расширения.', 2),
       (25, NULL, '#programming',
        'GitHub — крупнейший веб-сервис для хостинга IT-проектов и их совместной разработки. Веб-сервис основан на системе контроля версий Git и разработан на Ruby on Rails и Erlang компанией GitHub, Inc.', 2),
       (26, NULL, '#programming',
        'JUnit — фреймворк для модульного тестирования программного обеспечения на языке Java. Созданный Кентом Беком и Эриком Гаммой, JUnit принадлежит семье фреймворков xUnit для разных языков программирования, берущей начало в SUnit Кента Бека для Smalltalk.', 1),
       (27, NULL, '#programming',
        'Mockito - это среда тестирования с открытым исходным кодом для Java, выпущенная по лицензии MIT. Инфраструктура позволяет создавать тестовые двойные объекты в автоматических модульных тестах с целью разработки, управляемой тестами, или разработки, основанной на поведении.', 1),
       (28, 'tomcat.png', '#programming',
        'Tomcat — контейнер сервлетов с открытым исходным кодом, разрабатываемый Apache Software Foundation. Реализует спецификацию сервлетов, спецификацию JavaServer Pages и JavaServer Faces. Написан на языке Java. Tomcat позволяет запускать веб-приложения и содержит ряд программ для самоконфигурирования.', 1),
       (29, NULL, '#programming',
        'Spring Mock-MVC предоставляет отличные методы тестирования Spring Boot REST API. Mock-MVC позволяет нам тестировать обработку запросов Spring-MVC без запуска реального сервера.', 1);

