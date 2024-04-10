# Проект "User Management Service"

Проект представляет собой сервис управления пользователями, включая регистрацию, аутентификацию, подтверждение регистрации и сброс пароля.

## Технологии

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- BCryptPasswordEncoder
- JavaMailSender

## Установка

1. Клонировать репозиторий:

    ```bash
    git clone https://github.com/your-username/user-management-service.git
    ```

2. Перейти в директорию проекта:

    ```bash
    cd user-management-service
    ```

3. Собрать проект с помощью Maven:

    ```bash
    mvn clean install
    ```
   
## Настройка базы данных

Проект использует базу данных PostgreSQL. Для настройки подключения к базе данных отредактируйте файл `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase
spring.datasource.username=myusername
spring.datasource.password=mypassword
```
## Запуск
Вы можете запустить приложение из командной строки с помощью Maven:
```properties
mvn spring-boot:run
```
После запуска приложение будет доступно по адресу:
```properties
http://localhost:8080
```

## API Endpoints

```properties
POST /api/users/register - Регистрация нового пользователя
GET /api/users/confirm?code={token} - Подтверждение регистрации
POST /api/users/reset-password - Запрос на сброс пароля
GET /api/users/reset-password-confirm?token={token} - Подтверждение сброса пароля
```

## Функционал

- Регистрация пользователя: Пользователь может зарегистрироваться, указав свой email и пароль. После успешной регистрации на указанный email отправляется письмо с ссылкой для подтверждения регистрации.

- Подтверждение регистрации: Пользователь переходит по ссылке из письма и подтверждает свою регистрацию.

- Сброс пароля: Если пользователь забыл пароль, он может запросить сброс пароля. На указанный email отправляется письмо с ссылкой для сброса пароля.

## Использование Postman

Вы также можете тестировать API с помощью Postman.

1. Регистрация пользователя:
- URL: http://localhost:8080/api/users/register
- Method: POST
- Body (JSON)
```properties
{
"email": "user@example.com",
"password": "password",
"confirmPassword": "password"
}
```
2. Подтверждение регистрации:
- URL: http://localhost:8080/api/users/confirm?code={token}
- Method: GET
3. Запрос на сброс пароля:
- URL: http://localhost:8080/api/users/reset-password
- Method: POST
- Body (JSON)
```properties
{
"email": "user@example.com"
}
```
4. Подтверждение сброса пароля
- URL: http://localhost:8080/api/users/reset-password-confirm?token={token}
- Method: GET

## Автор

[P1cke](https://github.com/P1cke)