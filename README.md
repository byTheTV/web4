# Common

```bash
C:\wildfly\standalone\deployments
D:\.javaproj\web3\build\libs

./gradlew clean war
java -jar hazelcast-5.3.6.jar
 C:\wildfly\bin\standalone.bat -b 0.0.0.0 
```

```sql
CREATE DATABASE web3db;
```

2. `src/main/resources/META-INF/persistence.xml`:
   - URL: `jdbc:postgresql://localhost:5432/web3db`
   - User: `postgres`
   - Password: `postgres`


# Я в роли SA xdxd

### Facelets шаблоны:
- `start.xhtml` - стартовая страница с часами и ссылкой на основную страницу
- `main.xhtml` - основная страница приложения с формой, графиком и таблицей результатов

### Managed Beans (Контроллеры):
- `StartPageBean` (`@RequestScoped`) - управляет данными стартовой страницы
- `AreaCheckBean` (`@ViewScoped`) - контроллер формы проверки точки (только связь с UI)
- `ResultsBean` (`@SessionScoped`) - контроллер для отображения результатов (только связь с UI)

### Service слой (Бизнес-логика):
- `AreaCalculationService` (`@ApplicationScoped`) - содержит логику расчета попадания точки в область
- `ResultService` (`@ApplicationScoped`) - координирует работу с результатами, конвертирует DTO ↔ Entity

### Repository слой (Работа с БД):
- `ResultRepository` (`@ApplicationScoped`) - отвечает только за работу с базой данных через JPA

### DTO слой (Data Transfer Object):
- `ResultDTO` - объект для передачи данных между слоями без привязки к JPA

### Entity слой (JPA):
- `ResultEntity` - JPA сущность для хранения результатов в PostgreSQL

### Mapper слой:
- `ResultMapper` - конвертирует между Entity (модель) и DTO (слой контроллера)

### Валидаторы:
- `XValidator` - валидация значения X (-5 до 3)
- `YValidator` - валидация значения Y (-3 до 3)
- `RValidator` - валидация значения R (0.1 до 3)

### Конфигурация:
- `faces-config.xml` - правила навигации между страницами
- `persistence.xml` - конфигурация EclipseLink для PostgreSQL
- `beans.xml` - конфигурация CDI
- `web.xml` - конфигурация JSF

## Архитектура приложения

Приложение использует многослойную архитектуру с четким разделением ответственности:

```
┌─────────────────────────────────────────────────────────┐
│  СЛОЙ ПРЕДСТАВЛЕНИЯ (View Layer)                        │
│  - main.xhtml, start.xhtml (JSF страницы)               │
└──────────────────┬──────────────────────────────────────┘
                   │ JSF Expression Language (#{bean})
                   ▼
┌─────────────────────────────────────────────────────────┐
│  СЛОЙ КОНТРОЛЛЕРА (Controller Layer)                    │
│  - AreaCheckBean, ResultsBean (Managed Beans)          │
│  Только связь с UI, делегирует работу в Service         │
└──────────────────┬──────────────────────────────────────┘
                   │ @Inject
                   ▼
┌─────────────────────────────────────────────────────────┐
│  СЛОЙ DTO (Data Transfer Object)                        │
│  - ResultDTO (чистые данные, без JPA аннотаций)         │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  СЛОЙ СЕРВИСА (Service Layer)                           │
│  - AreaCalculationService (бизнес-логика расчета)       │
│  - ResultService (координация, конвертация)             │
└──────────────────┬──────────────────────────────────────┘
                   │ @Inject
                   ▼
┌─────────────────────────────────────────────────────────┐
│  СЛОЙ МАППИНГА (Mapping Layer)                          │
│  - ResultMapper (конвертация Entity ↔ DTO)              │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  СЛОЙ РЕПОЗИТОРИЯ (Repository Layer)                   │
│  - ResultRepository (работа с EntityManager)            │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  СЛОЙ МОДЕЛИ (Model Layer)                              │
│  - ResultEntity (JPA Entity для работы с БД)            │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  СЛОЙ ПЕРСИСТЕНТНОСТИ (Persistence Layer)              │
│  - EntityManager, PostgreSQL                            │
└─────────────────────────────────────────────────────────┘
```

## Взаимодействие компонентов на бэке

### Сценарий 1: Добавление нового результата (UI → БД)

**Поток данных при сохранении:**


**Детальное описание:**

1. **AreaCheckBean** (контроллер):
   - Получает данные из формы (x, y, r)
   - Измеряет время выполнения
   - Делегирует расчет попадания в `AreaCalculationService`
   - Создает `ResultDTO` и передает в `ResultsBean`

2. **AreaCalculationService** (бизнес-логика):
   - Проверяет попадание точки в область по математическим формулам
   - Возвращает boolean результат

3. **ResultsBean** (контроллер):
   - Принимает `ResultDTO` от `AreaCheckBean`
   - Делегирует сохранение в `ResultService`
   - Обновляет свой кэш результатов после сохранения

4. **ResultService** (координация):
   - Конвертирует `ResultDTO` → `ResultEntity` через `ResultMapper`
   - Вызывает `ResultRepository` для сохранения

5. **ResultMapper** (конвертация):
   - Создает `ResultEntity` из `ResultDTO` без ID (ID генерируется БД)
   - Устанавливает timestamp автоматически

6. **ResultRepository** (работа с БД):
   - Управляет `EntityManager`
   - Выполняет транзакции (begin, commit, rollback)
   - Сохраняет `ResultEntity` через JPA

### Сценарий 2: Загрузка результатов (БД → UI)

**Поток данных при загрузке:**


**Детальное описание:**

1. **ResultsBean** (контроллер):
   - Хранит кэш `List<ResultDTO>` в памяти (SessionScoped)
   - При первом обращении загружает данные через `ResultService`
   - Возвращает DTO для отображения в UI

2. **ResultService** (координация):
   - Получает `List<ResultEntity>` из `ResultRepository`
   - Конвертирует каждый Entity в DTO через `ResultMapper.toDTO()`
   - Возвращает `List<ResultDTO>`

3. **ResultRepository** (работа с БД):
   - Выполняет JPQL запрос: `SELECT r FROM ResultEntity r ORDER BY r.timestamp DESC`
   - Возвращает список Entity из БД

4. **ResultMapper** (конвертация):
   - Создает `ResultDTO` из `ResultEntity`
   - Копирует все поля включая ID и timestamp

### Преимущества архитектуры

1. **Разделение ответственности:**
   - Managed Beans - только связь с UI
   - Service - бизнес-логика
   - Repository - работа с БД
   - Mapper - конвертация между слоями

2. **Независимость слоев:**
   - Контроллеры не зависят от JPA
   - Изменения в Entity не влияют на контроллеры
   - Легко тестировать каждый слой отдельно

3. **Переиспользование:**
   - Service можно использовать из других мест (REST API, другие beans)
   - Repository можно использовать для разных операций

4. **Масштабируемость:**
   - Легко добавить кэширование в Service
   - Легко добавить логирование
   - Легко добавить транзакции на уровне Service

## Hazelcast Cache Integration

Приложение использует Hazelcast для распределенного кеширования результатов проверки точек.

### Архитектура кеширования

**Паттерн: Write-Through с синхронизацией**
- **Запись**: одновременно в Hazelcast кеш и PostgreSQL БД
- **Чтение**: только из Hazelcast кеша
- **Инициализация**: при старте приложения загрузка всех данных из БД в кеш
- **Консистентность**: Hazelcast обеспечивает синхронизацию между инстансами через distributed cache

**Роль компонентов:**
- **Hazelcast**: распределенный кеш для быстрого доступа и синхронизации между инстансами
- **PostgreSQL**: персистентное хранилище для долгосрочного хранения и восстановления данных

### Компоненты кеширования

- `HazelcastConfig` - конфигурация Hazelcast клиента
- `CacheService` - сервис для работы с Hazelcast кешем
- `ResultService` - модифицирован для использования кеша (write-through паттерн)

### Порядок запуска

**Важно:** Приложение развертывается на WildFly, но требует отдельно запущенный Hazelcast сервер.

1. **Сначала запустить Hazelcast сервер** (standalone, отдельный процесс)
2. **Затем запустить WildFly** с развернутым приложением
3. Приложение на WildFly подключится к Hazelcast серверу как клиент

### Установка и запуск Hazelcast сервера

**Важно:** 
- Зависимости Hazelcast клиента уже добавлены в `build.gradle` и будут автоматически скачаны при сборке проекта
- Приложение развертывается на **WildFly** и подключается к Hazelcast серверу как клиент
- Hazelcast сервер должен быть запущен **отдельно** (standalone) до запуска WildFly

1. **Получить Hazelcast сервер JAR:**
   
   Скачать `hazelcast-5.3.6.jar` с официального сайта:
   ```bash
      https://repo1.maven.org/maven2/com/hazelcast/hazelcast/5.3.6/hazelcast-5.3.6.jar

   ```

2. **Запустить Hazelcast сервер (отдельно от WildFly):**
   ```bash
   # Запустить standalone Hazelcast сервер
   java -jar hazelcast-5.3.6.jar
   ```
   
   **Важно:** Hazelcast сервер должен быть запущен **до** развертывания приложения на WildFly.

3. **Проверить запуск Hazelcast сервера:**
   - Сервер должен запуститься на порту `5701` по умолчанию
   - В логах должно быть сообщение: `Members {size:1, ver:1} [...]`
   - Сервер должен продолжать работать в отдельном окне/процессе

### Конфигурация

Конфигурация Hazelcast клиента находится в:
- `src/main/resources/hazelcast-client.xml` - XML конфигурация
- `src/main/java/org/example/config/HazelcastConfig.java` - Java конфигурация

По умолчанию клиент подключается к `localhost:5701`.

### Тестирование нескольких инстансов

Для проверки синхронизации кеша между несколькими инстансами приложения на WildFly:

1. **Запустить Hazelcast сервер (один для всех инстансов):**
   ```bash
   java -jar hazelcast-5.3.6.jar
   ```
   Оставить сервер запущенным в отдельном окне/процессе.

2. **Собрать WAR файл:**
   ```bash
   ./gradlew clean war
   ```

3. **Запустить первый инстанс WildFly:**
   ```bash
   # Скопировать WAR в deployments первого WildFly
   cp build/libs/labwork2.war C:\wildfly1\standalone\deployments\
   
   # Запустить первый WildFly (если еще не запущен)
   # Приложение будет доступно на http://localhost:8080 (или другой порт)
   ```

4. **Запустить второй инстанс WildFly:**
   ```bash
   # Скопировать тот же WAR в deployments второго WildFly
   cp build/libs/labwork2.war C:\wildfly2\standalone\deployments\
   
   # Запустить второй WildFly на другом порту (например, 8081)
   # Нужно настроить offset в standalone.xml второго WildFly
   ```

5. **Проверить синхронизацию кеша:**
   - Открыть приложение в первом WildFly (например, `http://localhost:8080`)
   - Добавить несколько точек через форму
   - Открыть приложение во втором WildFly (например, `http://localhost:8081`)
   - Проверить, что все точки, добавленные в первом инстансе, видны во втором
   - Добавить точку во втором инстансе
   - Проверить, что новая точка видна в первом инстансе
   - Оба инстанса должны видеть одинаковые данные из общего Hazelcast кеша

### Обработка ошибок

- Если Hazelcast сервер недоступен, приложение продолжит работу с fallback на БД
- При записи: данные всегда сохраняются в БД (источник истины)
- При чтении: если кеш недоступен, данные загружаются из БД
- При инициализации: если кеш недоступен, приложение запустится, но кеш будет пустым

### Логирование

Все операции с кешем логируются в консоль:
- `CacheService initialized` - кеш инициализирован
- `Result cached with key: ...` - результат сохранен в кеш
- `Cache initialized with X results from database` - кеш загружен из БД
- `Cache cleared` - кеш очищен

### REST API для просмотра данных

Добавлены REST endpoints для просмотра данных из кэша и PostgreSQL:

#### Доступные endpoints:

1. **GET `/api/data/cache`** - получить все данные из кэша Hazelcast
   ```bash
   curl http://localhost:8080/labwork2/api/data/cache
   ```
   Ответ:
   ```json
   {
     "available": true,
     "size": 5,
     "data": [...]
   }
   ```

2. **GET `/api/data/database`** - получить все данные из PostgreSQL
   ```bash
   curl http://localhost:8080/labwork2/api/data/database
   ```
   Ответ:
   ```json
   {
     "size": 5,
     "data": [...]
   }
   ```

3. **GET `/api/data/stats`** - получить статистику и сравнение
   ```bash
   curl http://localhost:8080/labwork2/api/data/stats
   ```
   Ответ:
   ```json
   {
     "cache": {
       "available": true,
       "size": 5
     },
     "database": {
       "size": 5
     },
     "comparison": {
       "synchronized": true,
       "difference": 0
     }
   }
   ```

4. **GET `/api/data/all`** - получить все данные из обоих источников
   ```bash
   curl http://localhost:8080/labwork2/api/data/all
   ```
   Ответ содержит данные из кэша и БД для сравнения.

**Примечание:** Замените `labwork2` на имя вашего WAR файла, если оно отличается.

