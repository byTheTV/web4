# Common

```bash
C:\wildfly\standalone\deployments
D:\.javaproj\web3\build\libs

./gradlew clean war
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

