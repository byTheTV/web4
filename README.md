```bash
./gradlew bootRun
```

`http://localhost:8080`


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

### Аутентификация
- `POST /api/auth/login` 
- `POST /api/auth/register`

### Проверка точки
- `POST /api/area/check` 
- `GET /api/area/results` 

### Валидация
- `GET /api/validation/allowed-x` 
- `GET /api/validation/allowed-r` 
- `GET /api/validation/y-range`

