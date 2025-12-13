```bash
./gradlew bootRun
```

`http://localhost:8080`

### Запуск Keycloak через Docker

```bash
docker compose -f docker-compose.keycloak.yml up -d
```

- Админ-панель: `http://localhost:8081`
- Админ-учётка по умолчанию: `admin` / `admin`
- При старте автоматически импортируется realm `area-check` с публичным клиентом `area-check-frontend` и пользователем `demo/demo` (файл `keycloak/realm-export/area-check-realm.json`).
- Если поднимаете не на localhost, поменяйте в файле `keycloak/realm-export/area-check-realm.json` значения `redirectUris`/`webOrigins` под ваш хост и перезапустите `docker compose ... up -d`.
- При необходимости обновите переменные:
  - backend: `keycloak.auth-server-url` и `keycloak.realm` в `src/main/resources/application.properties`
  - frontend: `REACT_APP_KEYCLOAK_URL`, `REACT_APP_KEYCLOAK_REALM`, `REACT_APP_KEYCLOAK_CLIENT_ID`


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

## API Endpoints (после перехода на Keycloak)

### Проверка точки
- `POST /api/area/check`
- `GET /api/area/results`

### Валидация
- `GET /api/validation/allowed-x`
- `GET /api/validation/allowed-r`
- `GET /api/validation/y-range`

