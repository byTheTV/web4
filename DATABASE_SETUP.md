sys as sysdba

# Настройка базы данных Oracle

## Вариант 1: Oracle Database Express Edition (XE) - Рекомендуется для разработки

### Шаг 1: Скачать Oracle Database XE

1. Перейдите на официальный сайт Oracle:
   - https://www.oracle.com/database/technologies/xe-downloads.html
   - Выберите версию для вашей ОС (Windows/Linux)

2. Скачайте Oracle Database 21c Express Edition или 23c Free

### Шаг 2: Установка Oracle XE

**Для Windows:**
1. Запустите установщик
2. Следуйте инструкциям мастера установки
3. Запомните пароль для пользователя SYS (системный администратор)
4. После установки база данных запустится автоматически

**Для Linux:**
```bash
# Распакуйте архив
unzip oracle-database-xe-21c-1.0-1.ol8.x86_64.rpm.zip

# Установите
sudo yum localinstall -y Disk1/oracle-database-xe-21c-1.0-1.ol8.x86_64.rpm

# Настройте базу данных
sudo /etc/init.d/oracle-xe-21c configure
```

### Шаг 3: Создание пользователя для приложения

Подключитесь к базе данных как системный администратор:

```sql
-- Подключение через SQL*Plus
sqlplus sys/zxc123@localhost:1521/XE as sysdba

-- Или через SQL Developer
```

Создайте пользователя и табличное пространство:

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

### Шаг 4: Настройка application.properties

Отредактируйте `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=area_check_user
spring.datasource.password=your_password
```

## Вариант 2: Docker (Самый простой способ)

### Шаг 1: Установите Docker

Скачайте и установите Docker Desktop:
- https://www.docker.com/products/docker-desktop

### Шаг 2: Запустите Oracle в Docker

```bash
docker run -d --name oracle-xe \
  -p 1521:1521 \
  -p 5500:5500 \
  -e ORACLE_PWD=your_password \
  container-registry.oracle.com/database/express:21.3.0-xe
```

Или используйте готовый образ:

```bash
docker run -d --name oracle-xe \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=your_password \
  gvenzl/oracle-xe:21-slim
```

### Шаг 3: Создание пользователя

Подключитесь к контейнеру:

```bash
docker exec -it oracle-xe bash
sqlplus sys/your_password@localhost:1521/XE as sysdba
```

Затем выполните SQL из Варианта 1, Шаг 3.

### Шаг 4: Настройка application.properties

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=area_check_user
spring.datasource.password=your_password
```

## Вариант 3: Использование другой БД (PostgreSQL/MySQL)

Если Oracle недоступна, можно временно использовать другую БД:

### PostgreSQL

1. Установите PostgreSQL: https://www.postgresql.org/download/

2. Создайте базу данных:
```sql
CREATE DATABASE area_check;
CREATE USER area_check_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE area_check TO area_check_user;
```

3. Измените `build.gradle`:
```gradle
// Замените Oracle драйвер на PostgreSQL
implementation 'org.postgresql:postgresql:42.7.1'
```

4. Измените `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/area_check
spring.datasource.username=area_check_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### MySQL

1. Установите MySQL: https://dev.mysql.com/downloads/

2. Создайте базу данных:
```sql
CREATE DATABASE area_check;
CREATE USER 'area_check_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON area_check.* TO 'area_check_user'@'localhost';
FLUSH PRIVILEGES;
```

3. Измените `build.gradle`:
```gradle
implementation 'com.mysql:mysql-connector-j:8.2.0'
```

4. Измените `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/area_check
spring.datasource.username=area_check_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

## Проверка подключения

После настройки запустите приложение:

```bash
./gradlew bootRun
```

Если подключение успешно, вы увидите в логах:
- Создание таблиц (если `spring.jpa.hibernate.ddl-auto=update`)
- SQL запросы (если `spring.jpa.show-sql=true`)

## Полезные команды

### Проверка подключения через SQL*Plus:
```bash
sqlplus area_check_user/your_password@localhost:1521/XE
```

### Проверка через JDBC:
```bash
# Используйте любой JDBC клиент или SQL Developer
```

### Просмотр таблиц:
```sql
SELECT table_name FROM user_tables;
```

## Решение проблем

### Ошибка: "ORA-12505: TNS:listener does not currently know of SID"
- Проверьте, что база данных запущена
- Убедитесь, что SID правильный (обычно XE для Express Edition)

### Ошибка: "ORA-01017: invalid username/password"
- Проверьте правильность имени пользователя и пароля
- Убедитесь, что пользователь создан и имеет необходимые права

### Ошибка: "Connection refused"
- Проверьте, что Oracle слушает на порту 1521
- Проверьте файрвол

### Ошибка драйвера
- Убедитесь, что зависимость `ojdbc11` добавлена в `build.gradle`
- Выполните `./gradlew build --refresh-dependencies`

