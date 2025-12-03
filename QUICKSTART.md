# Быстрый старт

## Предварительные требования

1. Java 17+
2. Node.js 16+
3. Oracle Database (или измените настройки на другую БД)

## Настройка

### 1. Настройка базы данных

Отредактируйте `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 2. Запуск Back-end

```bash
./gradlew bootRun
```

Back-end будет доступен на `http://localhost:8080`

### 3. Запуск Front-end

```bash
cd frontend
npm install
npm start
```

Front-end будет доступен на `http://localhost:3000`

## Использование

1. Откройте `http://localhost:3000`
2. Зарегистрируйте нового пользователя
3. Войдите в систему
4. Используйте форму или кликните на графике для проверки точек

## Примечания

- При первом запуске Spring Boot автоматически создаст таблицы в БД
- JWT токен хранится в localStorage браузера
- Для работы с Oracle убедитесь, что драйвер ojdbc11 доступен

