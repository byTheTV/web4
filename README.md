```bash
./gradlew bootRun
```

`http://localhost:8080`



```bash
docker compose -f docker-compose.keycloak.yml up -d

powershell -ExecutionPolicy Bypass -File "d:\.javaproj\web4\setup-user-profile.ps1"
```

- Админ-панель: `http://localhost:8081`
- Админ-учётка Keycloak: `admin` / `admin`


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
SELECT * FROM check_logs;  
```



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
  - Сохраняет результат в таблицу `results`
  - Логирует информацию о пользователе в таблицу `check_logs` для аудита
- `GET /api/area/results` - получение результатов проверок текущего пользователя

### Админ-панель (требуется роль ADMIN)
- `GET /api/admin/stats?date=YYYY-MM-DD` - статистика проверок по пользователям за день
  - Параметр `date` опционален, по умолчанию текущая дата
  - Возвращает: keycloak_id, username, количество проверок
  - Данные берутся из таблицы `check_logs` (аудит-лог всех проверок)

### Валидация
- `GET /api/validation/allowed-x`
- `GET /api/validation/allowed-r`
- `GET /api/validation/y-range`



