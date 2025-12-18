```bash
./gradlew bootRun
```

`http://localhost:8080`

### Запуск Keycloak через Docker

```bash
docker compose -f docker-compose.keycloak.yml up -d
```

- **Версия Keycloak**: 26.0.7
- Админ-панель: `http://localhost:8081`
- Админ-учётка Keycloak: `admin` / `admin`
- При старте автоматически:
  - Импортируется realm `area-check` с:
    - Ролями: `USER` и `ADMIN`
    - Публичным клиентом `area-check-frontend` с mapper для атрибута `maxRadius`
    - Автоматическим назначением роли `USER` новым зарегистрированным пользователям
    - Тестовыми пользователями:
      - `demo` / `demo` (роль: USER, maxRadius: 2.0)
      - `admin` / `admin` (роли: ADMIN, USER, maxRadius: 5.0)
      - `user1` / `user1` (роль: USER, maxRadius: 1.5)
  - **Автоматически настраивается User Profile** с полем `maxRadius` через инициализационный скрипт
  - При регистрации новых пользователей поле `maxRadius` будет обязательным
- Если поднимаете не на localhost, поменяйте в файле `keycloak/realm-export/area-check-realm.json` значения `redirectUris`/`webOrigins` под ваш хост и перезапустите `docker compose ... up -d`.
- При необходимости обновите переменные:
  - backend: `keycloak.auth-server-url` и `keycloak.realm` в `src/main/resources/application.properties`
  - frontend: `REACT_APP_KEYCLOAK_URL`, `REACT_APP_KEYCLOAK_REALM`, `REACT_APP_KEYCLOAK_CLIENT_ID`

#### Настройка maxRadius для новых пользователей

После запуска Keycloak нужно настроить User Profile вручную:

```powershell
# Windows PowerShell  
.\setup-user-profile.ps1
```

Это добавит обязательное поле `maxRadius` в форму регистрации.

#### Важно после обновления Keycloak

После изменения конфигурации Keycloak:
1. Перезапустите контейнер: `docker compose -f docker-compose.keycloak.yml restart`
2. Очистите кэш браузера или используйте режим инкогнито
3. Запустите скрипт настройки: `.\setup-user-profile.ps1`


```bash
cd frontend

npm install

npm start
```

`http://localhost:3000`






```sql
-- Создание табличного пространства
CREATE TABLESPACE area_check_ts
DATAFILE 'area_check_data.dbf' SIZE 100M
AUTOEXTEND ON NEXT 10M MAXSIZE 500M;

-- Создание пользователя
CREATE USER area_check_user IDENTIFIED BY zxc123
DEFAULT TABLESPACE area_check_ts
TEMPORARY TABLESPACE temp;

-- Выдача прав
GRANT CONNECT, RESOURCE TO area_check_user;
GRANT CREATE SESSION TO area_check_user;
GRANT UNLIMITED TABLESPACE TO area_check_user;
```

```sql

sqlplus area_check_user/zxc123@localhost:1521/XEPDB1

area_check_user

SELECT table_name FROM user_tables;
SELECT * FROM results;
SELECT * FROM users;  
```


### Back-end
- Spring Boot 3.2.0
- Oracle Database
- Java 17

### Front-end
- React 18.2.0
- Redux Toolkit
- PrimeReact
- Axios

### Настройка базы данных


`src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

## API Endpoints

### Проверка точки (требуется роль USER)
- `POST /api/area/check` - проверка попадания точки в область
  - Проверяет атрибут `maxRadius` из токена
  - Если R превышает maxRadius, возвращает 400
  - Логирует информацию о пользователе в таблицу `check_logs`
- `GET /api/area/results` - получение результатов проверок текущего пользователя

### Админ-панель (требуется роль ADMIN)
- `GET /api/admin/stats?date=YYYY-MM-DD` - статистика проверок по пользователям за день
  - Параметр `date` опционален, по умолчанию текущая дата

### Валидация
- `GET /api/validation/allowed-x`
- `GET /api/validation/allowed-r`
- `GET /api/validation/y-range`

## Роли и доступ

- **USER**: доступ к `/app` (панель пользователя для проверки точек)
- **ADMIN**: доступ к `/admin` (админ-панель со статистикой) + доступ к `/app`

