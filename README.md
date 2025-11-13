### Facelets шаблоны:
- `start.xhtml` - стартовая страница с часами и ссылкой на основную страницу
- `main.xhtml` - основная страница приложения с формой, графиком и таблицей результатов

### Managed Beans:
- `StartPageBean` (@RequestScoped) - управляет данными стартовой страницы
- `AreaCheckBean` (@ViewScoped) - управляет формой проверки точки
- `ResultsBean` (@SessionScoped) - управляет списком результатов и доступом к БД

### Entity:
- `ResultEntity` - JPA сущность для хранения результатов в PostgreSQL

### Валидаторы:
- `YValidator` - валидация значения Y (-3 до 3)
- `RValidator` - валидация значения R (0.1 до 3)

### Конфигурация:
- `faces-config.xml` - правила навигации между страницами
- `persistence.xml` - конфигурация EclipseLink для PostgreSQL
- `beans.xml` - конфигурация CDI
- `web.xml` - конфигурация JSF

## Настройка базы данных


```sql
CREATE DATABASE web3db;
```

2. `src/main/resources/META-INF/persistence.xml`:
   - URL: `jdbc:postgresql://localhost:5432/web3db`
   - User: `postgres`
   - Password: `postgres`

## Сборка и развертывание

```bash

./gradlew clean war

```

## Примечания

- Используется только PrimeFaces для компонента спиннера (p:spinner)


┌─────────────────┐
│   main.xhtml     │ (JSF View)
└────────┬────────┘
         │ JSF EL
         ▼
┌─────────────────┐
│  AreaCheckBean  │ ← Managed Bean (контроллер)
│  @ViewScoped    │
└────────┬────────┘
         │ @Inject
         ▼
┌─────────────────────────┐
│  AreaCalculationService  │ ← Service (бизнес-логика)
│  @ApplicationScoped      │
└─────────────────────────┘
         │
         ▼
┌─────────────────┐
│  ResultService  │ ← Service (координация)
│  @ApplicationScoped│
└────────┬────────┘
         │ @Inject
         ▼
┌─────────────────┐
│ ResultRepository│ ← Repository (БД)
│ @ApplicationScoped│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ EntityManager   │
└─────────────────┘