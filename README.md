# Лабораторная работа - Проверка попадания точки в область

Приложение для проверки попадания точки в заданную область на координатной плоскости.

## Технологии

### Back-end
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security с JWT аутентификацией
- Oracle Database
- Java 17

### Front-end
- React 18.2.0
- Redux Toolkit
- PrimeReact
- Axios

## Структура проекта

```
.
├── src/main/java/org/example/          # Back-end код
│   ├── AreaCheckApplication.java       # Главный класс приложения
│   ├── config/                         # Конфигурация
│   ├── controller/                     # REST контроллеры
│   ├── dto/                            # Data Transfer Objects
│   ├── entity/                         # JPA сущности
│   ├── repository/                     # Spring Data репозитории
│   ├── security/                       # Spring Security конфигурация
│   ├── service/                        # Бизнес-логика
│   └── validator/                      # Валидаторы
├── frontend/                           # React приложение
│   ├── public/
│   └── src/
│       ├── api/                        # API клиент
│       ├── components/                  # React компоненты
│       ├── pages/                      # Страницы
│       └── store/                      # Redux store
└── build.gradle                        # Gradle конфигурация
```

## Настройка и запуск

### Требования
- Java 17 или выше
- Node.js 16 или выше
- Oracle Database
- Gradle 7.5 или выше

### Настройка базы данных

1. Создайте базу данных Oracle
2. Обновите настройки в `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. Обновите JWT секрет (рекомендуется использовать длинный случайный ключ):
   ```properties
   jwt.secret=your-secret-key-change-this-in-production-min-256-bits
   ```

### Запуск Back-end

```bash
# Из корневой директории проекта
./gradlew bootRun
```

Или используйте IDE для запуска `AreaCheckApplication`.

Back-end будет доступен по адресу: `http://localhost:8080`

### Запуск Front-end

```bash
# Перейдите в директорию frontend
cd frontend

# Установите зависимости
npm install

# Запустите приложение
npm start
```

Front-end будет доступен по адресу: `http://localhost:3000`

## Использование

1. Откройте браузер и перейдите на `http://localhost:3000`
2. Зарегистрируйте нового пользователя или войдите с существующими учетными данными
3. На основной странице:
   - Выберите координату X из списка: -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2
   - Введите координату Y (от -3 до 3)
   - Выберите радиус R из списка: -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2
   - Нажмите "Проверить" или кликните на графике для выбора координат
4. Результаты проверки отображаются в таблице ниже

## Адаптивный дизайн

Приложение поддерживает три режима отображения:

- **Десктопный** (≥ 1266px): Полная версия с двумя колонками
- **Планшетный** (714px - 1265px): Одноколоночная версия
- **Мобильный** (< 714px): Компактная версия для мобильных устройств

## API Endpoints

### Аутентификация
- `POST /api/auth/login` - Вход
- `POST /api/auth/register` - Регистрация

### Проверка точки
- `POST /api/area/check` - Проверить точку (требует аутентификации)
- `GET /api/area/results` - Получить результаты пользователя (требует аутентификации)

### Валидация
- `GET /api/validation/allowed-x` - Получить допустимые значения X
- `GET /api/validation/allowed-r` - Получить допустимые значения R
- `GET /api/validation/y-range` - Получить диапазон Y

## Область проверки

Область состоит из трех частей:
1. Треугольник в первом квадранте: x ≥ 0, y ≥ 0, x + y ≤ R
2. Прямоугольник во втором квадранте: x ≤ 0, y ≥ 0, x ≥ -R/2, y ≤ R
3. Четверть круга в четвертом квадранте: x ≥ 0, y ≤ 0, x² + y² ≤ (R/2)²
