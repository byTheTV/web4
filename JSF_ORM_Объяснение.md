# JavaServer Faces (JSF) и ORM технологии - Подробное объяснение

## 1. Технология JavaServer Faces (JSF)

### Что такое JSF?

**JavaServer Faces (JSF)** — это Java-спецификация для построения пользовательских интерфейсов веб-приложений на основе компонентной архитектуры. JSF является частью платформы Jakarta EE (ранее Java EE).

### Особенности JSF

1. **Компонентная архитектура**: JSF предоставляет набор готовых UI-компонентов (кнопки, формы, таблицы и т.д.)
2. **Управляемые бины (Managed Beans)**: Связывание компонентов с бизнес-логикой через специальные Java-классы
3. **Жизненный цикл запроса**: Четко определенные фазы обработки HTTP-запроса
4. **Навигация**: Декларативная навигация между страницами
5. **Валидация и конвертация**: Встроенные механизмы проверки и преобразования данных
6. **AJAX-поддержка**: Встроенная поддержка асинхронных запросов

### Отличия от сервлетов и JSP

#### JSF vs Сервлеты

| Аспект | Сервлеты | JSF |
|--------|----------|-----|
| **Уровень абстракции** | Низкий (работа с HTTP напрямую) | Высокий (компонентная модель) |
| **Обработка запросов** | Ручная обработка request/response | Автоматическая через жизненный цикл |
| **UI-компоненты** | Отсутствуют | Богатый набор компонентов |
| **Связывание данных** | Ручное извлечение параметров | Автоматическое через EL-выражения |
| **Валидация** | Ручная проверка | Встроенные валидаторы |

**Пример сервлета:**
```java
@WebServlet("/check")
public class CheckServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String x = request.getParameter("x");  // Ручное извлечение
        String y = request.getParameter("y");
        // Ручная валидация, обработка, генерация HTML...
    }
}
```

**Пример JSF (из вашего проекта):**
```java
@Named("areaCheckBean")
@ViewScoped
public class AreaCheckBean {
    private Integer x;  // Автоматическое связывание
    private Double y;
    
    public String checkPoint() {  // Простой метод действия
        // Логика обработки
        return null;
    }
}
```

#### JSF vs JSP

| Аспект | JSP | JSF |
|--------|-----|-----|
| **Назначение** | Шаблонизация (генерация HTML) | Компонентный фреймворк |
| **Жизненный цикл** | Простая компиляция в сервлет | Сложный жизненный цикл с фазами |
| **Состояние компонентов** | Stateless | Stateful (сохранение состояния) |
| **События** | Отсутствуют | Модель событий (action, valueChange и т.д.) |
| **Валидация** | Ручная или через теги | Встроенная система валидаторов |
| **Навигация** | Ручная через sendRedirect/forward | Декларативная навигация |

**JSP подход:**
```jsp
<form method="post" action="check.jsp">
    <input type="text" name="x" value="<%= request.getParameter("x") %>"/>
    <input type="submit"/>
</form>
```

**JSF подход (Facelets):**
```xhtml
<h:form>
    <h:inputText value="#{areaCheckBean.x}"/>
    <h:commandButton action="#{areaCheckBean.checkPoint()}"/>
</h:form>
```

### Преимущества JSF

1. **Быстрая разработка**: Готовые компоненты ускоряют создание UI
2. **Разделение ответственности**: Четкое разделение между представлением и логикой
3. **Переиспользование**: Компоненты можно использовать многократно
4. **Валидация**: Встроенная система валидации и конвертации
5. **AJAX**: Простая интеграция AJAX через `<f:ajax>`
6. **Стандартизация**: Часть Jakarta EE, широко поддерживается
7. **Библиотеки компонентов**: PrimeFaces, RichFaces, ICEfaces и др.

### Недостатки JSF

1. **Кривая обучения**: Сложнее для новичков, чем простые сервлеты
2. **Производительность**: Больше накладных расходов, чем у сервлетов
3. **Размер**: Больше зависимостей и библиотек
4. **Отладка**: Сложнее отлаживать из-за жизненного цикла
5. **SEO**: Менее дружелюбен для поисковых систем (хотя можно настроить)
6. **Гибкость**: Меньше контроля над генерируемым HTML

### Структура JSF-приложения

```
webapp/
├── WEB-INF/
│   ├── web.xml              # Конфигурация сервлета и JSF
│   ├── faces-config.xml     # Конфигурация JSF (навигация, бины и т.д.)
│   └── beans.xml            # Конфигурация CDI (для управляемых бинов)
├── *.xhtml                  # Facelets страницы
├── resources/               # Статические ресурсы (CSS, JS, изображения)
└── scripts/                 # JavaScript файлы
```

**Пример из вашего проекта:**
- `web.xml` - настройка FacesServlet и параметров JSF
- `faces-config.xml` - правила навигации
- `main.xhtml` - Facelets страница с JSF-компонентами
- `AreaCheckBean.java` - управляемый бин

---

## 2. Использование JSP-страниц и Facelets-шаблонов в JSF

### JSP в JSF (устаревший подход)

В ранних версиях JSF использовались JSP-страницы, но это создавало проблемы:
- Конфликты жизненных циклов JSP и JSF
- Ограниченная поддержка компонентов
- Сложности с AJAX

**Пример JSP с JSF (устаревший):**
```jsp
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:view>
    <h:form>
        <h:inputText value="#{bean.value}"/>
    </h:form>
</f:view>
```

### Facelets - современный стандарт

**Facelets** — это система шаблонов, специально разработанная для JSF. Начиная с JSF 2.0, Facelets стал стандартом по умолчанию.

#### Преимущества Facelets

1. **Нативная интеграция**: Создан специально для JSF
2. **Шаблонизация**: Поддержка композиции и включения шаблонов
3. **Производительность**: Быстрее, чем JSP
4. **XHTML-валидация**: Строгая валидация XML
5. **Переиспользование**: Композиционные компоненты

#### Структура Facelets-страницы

**Пример из вашего `main.xhtml`:**

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:p="http://primefaces.org/ui">
    
    <!-- Объявления пространств имен:
         h: - стандартные HTML-компоненты JSF
         f: - основные компоненты (валидаторы, конвертеры, AJAX)
         p: - компоненты PrimeFaces
    -->
    
    <h:head>
        <title>Заголовок страницы</title>
        <h:outputStylesheet name="styles/main.css"/>
    </h:head>
    
    <body>
        <h:form id="mainForm">
            <!-- JSF компоненты -->
            <h:inputText value="#{areaCheckBean.x}"/>
            <h:commandButton action="#{areaCheckBean.checkPoint()}">
                <f:ajax execute="@form" render="resultsTable"/>
            </h:commandButton>
        </h:form>
    </body>
</html>
```

#### Основные теги Facelets

1. **Композиция компонентов** (`<ui:composition>`):
```xhtml
<ui:composition template="/templates/layout.xhtml">
    <ui:define name="content">
        <!-- Содержимое страницы -->
    </ui:define>
</ui:composition>
```

2. **Включение фрагментов** (`<ui:include>`):
```xhtml
<ui:include src="/fragments/header.xhtml"/>
```

3. **Параметры** (`<ui:param>`):
```xhtml
<ui:include src="/fragments/header.xhtml">
    <ui:param name="title" value="Главная страница"/>
</ui:include>
```

4. **Условный рендеринг** (`<ui:fragment rendered="...">`):
```xhtml
<ui:fragment rendered="#{bean.showContent}">
    <!-- Контент -->
</ui:fragment>
```

#### EL-выражения (Expression Language)

JSF использует Unified EL для связывания компонентов с бинами:

- **Value выражения** (`#{}`): Двустороннее связывание
  ```xhtml
  <h:inputText value="#{areaCheckBean.x}"/>
  ```

- **Method выражения**: Вызов методов
  ```xhtml
  <h:commandButton action="#{areaCheckBean.checkPoint()}"/>
  ```

- **Immediate выражения** (`${}`): Только чтение (устаревший синтаксис)

---

## 3. JSF-компоненты

### Особенности реализации

JSF-компоненты реализованы на основе паттерна **Composite** и **Component Tree**:

1. **Компонентное дерево**: Все компоненты образуют иерархическую структуру
2. **Жизненный цикл**: Каждый компонент проходит через фазы жизненного цикла
3. **Рендеринг**: Компоненты генерируют HTML через рендереры
4. **Состояние**: Комponents сохраняют свое состояние между запросами

### Иерархия классов компонентов

```
UIComponent (абстрактный базовый класс)
│
├── UIOutput (для вывода данных)
│   ├── UIOutputLabel
│   ├── UIOutputText
│   └── UIOutputLink
│
├── UIInput (для ввода данных)
│   ├── UIInputText
│   ├── UIInputSecret
│   ├── UIInputTextarea
│   └── UIInputHidden
│
├── UICommand (для действий)
│   ├── UICommandButton
│   └── UICommandLink
│
├── UIData (для таблиц)
│   └── UIDataTable
│
├── UIForm (форма)
│
└── UIPanel (контейнер)
    ├── UIPanelGroup
    └── UIPanelGrid
```

### Основные компоненты JSF

#### 1. Форма (`<h:form>`)
```xhtml
<h:form id="mainForm">
    <!-- Компоненты формы -->
</h:form>
```

#### 2. Ввод данных (`<h:inputText>`, `<h:inputHidden>`)
```xhtml
<!-- Из вашего проекта -->
<h:inputText id="yValue" value="#{areaCheckBean.y}" 
            required="true"
            converter="jakarta.faces.Double">
    <f:validateDoubleRange minimum="-3.0" maximum="3.0"/>
</h:inputText>

<h:inputHidden id="xValue" value="#{areaCheckBean.x}">
    <f:convertNumber integerOnly="true"/>
    <f:validator validatorId="xValidator"/>
</h:inputHidden>
```

#### 3. Кнопки (`<h:commandButton>`, `<h:commandLink>`)
```xhtml
<h:commandButton value="Проверить" action="#{areaCheckBean.checkPoint()}">
    <f:ajax execute="@form" render="resultsTable"/>
</h:commandButton>
```

#### 4. Вывод данных (`<h:outputText>`, `<h:outputLabel>`)
```xhtml
<h:outputLabel value="X:"/>
<h:outputText value="#{result.x}"/>
```

#### 5. Таблицы (`<h:dataTable>`)
```xhtml
<!-- Из вашего проекта -->
<h:dataTable value="#{resultsBean.results}" var="result">
    <h:column>
        <f:facet name="header">X</f:facet>
        <h:outputText value="#{result.x}"/>
    </h:column>
</h:dataTable>
```

#### 6. Сообщения (`<h:message>`, `<h:messages>`)
```xhtml
<h:message for="xValue" styleClass="error-message"/>
```

### Дополнительные библиотеки компонентов

#### PrimeFaces

**PrimeFaces** — популярная библиотека расширенных компонентов для JSF.

**Пример из вашего проекта:**
```xhtml
<!-- Spinner компонент PrimeFaces -->
<p:spinner id="rValue" value="#{areaCheckBean.r}" 
          min="0.1" max="3.0" stepFactor="0.1"
          decimalPlaces="1">
    <f:validateDoubleRange minimum="0.1" maximum="3.0"/>
</p:spinner>
```

**Преимущества PrimeFaces:**
- Богатый набор компонентов (календари, диалоги, графики)
- Современный дизайн
- Встроенная AJAX-поддержка
- Темы оформления
- Хорошая документация

**Другие библиотеки:**
- **RichFaces** (устаревшая)
- **ICEfaces**
- **OmniFaces** (утилиты)
- **BootsFaces** (интеграция с Bootstrap)

### Модель обработки событий в JSF

JSF поддерживает событийно-ориентированную модель программирования.

#### Типы событий

1. **Action Events** (`ActionEvent`):
   - Генерируются кнопками и ссылками
   - Обрабатываются методами действия

```xhtml
<h:commandButton action="#{bean.handleAction()}"/>
```

```java
public String handleAction() {
    // Обработка действия
    return "outcome";  // Результат навигации
}
```

2. **Value Change Events** (`ValueChangeEvent`):
   - Генерируются при изменении значения компонента
   - Обрабатываются через `valueChangeListener`

```xhtml
<h:inputText value="#{bean.value}" 
            valueChangeListener="#{bean.handleValueChange}"/>
```

```java
public void handleValueChange(ValueChangeEvent event) {
    Object oldValue = event.getOldValue();
    Object newValue = event.getNewValue();
    // Обработка изменения
}
```

3. **Phase Events**:
   - События жизненного цикла JSF
   - Можно перехватывать на разных фазах

#### AJAX-события

JSF поддерживает AJAX через `<f:ajax>`:

```xhtml
<!-- Из вашего проекта -->
<h:commandButton action="#{areaCheckBean.checkPoint()}">
    <f:ajax execute="@form" 
            render="mainForm:resultsTable mainForm:yValueMessage"
            oncomplete="drawAll();"/>
</h:commandButton>
```

**Атрибуты `<f:ajax>`:**
- `execute` - какие компоненты обрабатывать
- `render` - какие компоненты обновлять
- `event` - тип события (по умолчанию "click")
- `oncomplete` - JavaScript после завершения
- `onerror` - JavaScript при ошибке

---

## 4. Конвертеры и валидаторы данных

### Конвертеры (Converters)

**Конвертеры** преобразуют строковые значения (из HTTP-запросов) в объекты Java и обратно.

#### Встроенные конвертеры JSF

1. **Number Converter** (`<f:convertNumber>`):
```xhtml
<h:inputText value="#{bean.number}">
    <f:convertNumber type="number" minFractionDigits="2"/>
</h:inputText>
```

2. **DateTime Converter** (`<f:convertDateTime>`):
```xhtml
<h:inputText value="#{bean.date}">
    <f:convertDateTime pattern="dd.MM.yyyy"/>
</h:inputText>
```

3. **Стандартные конвертеры**:
```xhtml
<!-- Конвертер для Double -->
<h:inputText value="#{areaCheckBean.y}" 
            converter="jakarta.faces.Double"/>

<!-- Конвертер для Integer -->
<h:inputText value="#{bean.x}">
    <f:convertNumber integerOnly="true"/>
</h:inputText>
```

#### Создание собственного конвертера

```java
@FacesConverter("customConverter")
public class CustomConverter implements Converter {
    
    @Override
    public Object getAsObject(FacesContext context, 
                             UIComponent component, 
                             String value) {
        // Преобразование строки в объект
        if (value == null || value.isEmpty()) {
            return null;
        }
        return new CustomObject(value);
    }
    
    @Override
    public String getAsString(FacesContext context, 
                              UIComponent component, 
                              Object value) {
        // Преобразование объекта в строку
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
```

**Использование:**
```xhtml
<h:inputText value="#{bean.customValue}">
    <f:converter converterId="customConverter"/>
</h:inputText>
```

### Валидаторы (Validators)

**Валидаторы** проверяют корректность введенных данных.

#### Встроенные валидаторы JSF

1. **DoubleRangeValidator** (`<f:validateDoubleRange>`):
```xhtml
<!-- Из вашего проекта -->
<h:inputText value="#{areaCheckBean.y}">
    <f:validateDoubleRange minimum="-3.0" maximum="3.0"
                          minimumMessage="Y должен быть не менее -3.0"
                          maximumMessage="Y должен быть не более 3.0"/>
</h:inputText>
```

2. **LongRangeValidator** (`<f:validateLongRange>`):
```xhtml
<h:inputText value="#{bean.age}">
    <f:validateLongRange minimum="18" maximum="100"/>
</h:inputText>
```

3. **LengthValidator** (`<f:validateLength>`):
```xhtml
<h:inputText value="#{bean.name}">
    <f:validateLength minimum="3" maximum="50"/>
</h:inputText>
```

4. **RequiredValidator** (атрибут `required`):
```xhtml
<h:inputText value="#{bean.value}" required="true"
            requiredMessage="Поле обязательно для заполнения"/>
```

#### Создание собственного валидатора

**Пример из вашего проекта (`XValidator.java`):**

```java
@FacesValidator("xValidator")
public class XValidator implements Validator {
    
    @Override
    public void validate(FacesContext context, 
                        UIComponent component, 
                        Object value) throws ValidatorException {
        
        if (value == null) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации", 
                    "Значение X обязательно для ввода"));
        }
        
        Integer intValue = (Integer) value;
        
        if (!Config.getAllowedX().contains(intValue)) {
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка валидации", 
                    "X должен быть одним из допустимых значений"));
        }
    }
}
```

**Использование:**
```xhtml
<h:inputHidden id="xValue" value="#{areaCheckBean.x}">
    <f:validator validatorId="xValidator"/>
</h:inputHidden>
<h:message for="xValue" styleClass="error-message"/>
```

#### Bean Validation (JSR 303/380)

Можно использовать аннотации Bean Validation:

```java
public class AreaCheckBean {
    @NotNull(message = "X обязателен")
    @Min(value = -5, message = "X должен быть >= -5")
    @Max(value = 3, message = "X должен быть <= 3")
    private Integer x;
    
    @NotNull
    @DecimalMin(value = "-3.0", message = "Y должен быть >= -3.0")
    @DecimalMax(value = "3.0", message = "Y должен быть <= 3.0")
    private Double y;
}
```

```xhtml
<h:inputText value="#{areaCheckBean.x}"/>
<h:message for="x"/>
```

### Порядок обработки

1. **Конвертация** (Convert) - строка → объект
2. **Валидация** (Validate) - проверка объекта
3. **Обновление модели** (Update Model) - установка значения в бин
4. **Вызов действия** (Invoke Application) - выполнение action-метода

---

## 5. Представление страницы JSF на стороне сервера. Класс UIViewRoot

### Компонентное дерево

Каждая JSF-страница представлена на сервере в виде **компонентного дерева** (Component Tree), корнем которого является `UIViewRoot`.

### Класс UIViewRoot

`UIViewRoot` — это специальный компонент, который:
- Является корнем компонентного дерева
- Представляет всю страницу JSF
- Хранит информацию о представлении (view)
- Управляет жизненным циклом компонентов

#### Получение UIViewRoot

```java
FacesContext facesContext = FacesContext.getCurrentInstance();
UIViewRoot viewRoot = facesContext.getViewRoot();
String viewId = viewRoot.getViewId();  // Например, "/main.xhtml"
```

#### Работа с компонентами

```java
// Поиск компонента по ID
UIComponent component = viewRoot.findComponent("mainForm:xValue");

// Получение всех компонентов формы
UIForm form = (UIForm) viewRoot.findComponent("mainForm");

// Программное обновление компонента
FacesContext.getCurrentInstance()
    .getPartialViewContext()
    .getRenderIds()
    .add("mainForm:resultsTable");
```

### Жизненный цикл компонентного дерева

1. **Restore View**: Восстановление дерева из состояния (если это postback)
2. **Apply Request Values**: Установка значений из запроса
3. **Process Validations**: Валидация и конвертация
4. **Update Model Values**: Обновление управляемых бинов
5. **Invoke Application**: Выполнение действий
6. **Render Response**: Генерация HTML

### Сохранение состояния

JSF сохраняет состояние компонентного дерева между запросами:

- **Client-side state saving**: Состояние в скрытом поле формы
- **Server-side state saving**: Состояние на сервере (в сессии)

**Настройка в `web.xml`:**
```xml
<context-param>
    <param-name>jakarta.faces.STATE_SAVING_METHOD</param-name>
    <param-value>server</param-value>  <!-- или "client" -->
</context-param>
```

---

## 6. Управляемые бины (Managed Beans)

### Назначение

**Управляемые бины** — это Java-классы, которые:
- Связаны с JSF-компонентами через EL-выражения
- Управляют бизнес-логикой приложения
- Управляются контейнером (жизненный цикл, внедрение зависимостей)

### Способы конфигурации

#### 1. Аннотации CDI (современный подход)

**Пример из вашего проекта:**

```java
@Named("areaCheckBean")  // Имя бина для EL
@ViewScoped              // Область видимости
public class AreaCheckBean implements Serializable {
    
    @Inject              // Внедрение зависимостей
    private AreaCalculationService calculationService;
    
    @Inject
    private ResultsBean resultsBean;
    
    private Integer x;
    private Double y;
    
    public String checkPoint() {
        // Логика обработки
        return null;
    }
    
    // Геттеры и сеттеры
    public Integer getX() { return x; }
    public void setX(Integer x) { this.x = x; }
}
```

**Аннотации CDI:**
- `@Named` - регистрация бина (имя по умолчанию = имя класса с маленькой буквы)
- `@Inject` - внедрение зависимостей
- `@RequestScoped` - область видимости запроса
- `@ViewScoped` - область видимости представления
- `@SessionScoped` - область видимости сессии
- `@ApplicationScoped` - область видимости приложения

#### 2. Конфигурация в faces-config.xml (устаревший подход)

```xml
<faces-config>
    <managed-bean>
        <managed-bean-name>areaCheckBean</managed-bean-name>
        <managed-bean-class>org.example.beans.AreaCheckBean</managed-bean-class>
        <managed-bean-scope>view</managed-bean-scope>
    </managed-bean>
</faces-config>
```

### Контекст управляемых бинов (Scope)

#### 1. RequestScoped
- Живет в течение одного HTTP-запроса
- Создается заново при каждом запросе
- Используется для временных данных

```java
@RequestScoped
@Named
public class RequestBean {
    // Данные теряются после завершения запроса
}
```

#### 2. ViewScoped
- Живет в течение жизненного цикла представления
- Сохраняется при AJAX-запросах на той же странице
- Уничтожается при переходе на другую страницу
- **Идеален для форм с AJAX**

```java
@ViewScoped
@Named
public class AreaCheckBean implements Serializable {
    // Сохраняется при AJAX-обновлениях на той же странице
}
```

#### 3. SessionScoped
- Живет в течение HTTP-сессии пользователя
- Сохраняется между запросами
- Используется для данных пользователя

```java
@SessionScoped
@Named
public class UserBean implements Serializable {
    private String username;
    // Данные сохраняются в течение сессии
}
```

#### 4. ApplicationScoped
- Живет в течение жизни приложения
- Один экземпляр на все приложение
- Используется для глобальных данных

```java
@ApplicationScoped
@Named
public class ConfigBean {
    private String appName = "MyApp";
    // Общие данные для всех пользователей
}
```

#### 5. FlowScoped (JSF 2.2+)
- Живет в течение потока навигации
- Используется для многошаговых процессов

### Внедрение зависимостей

**CDI Injection:**
```java
@Named
@ViewScoped
public class AreaCheckBean {
    
    @Inject
    private AreaCalculationService service;  // Внедрение сервиса
    
    @Inject
    private ResultsBean resultsBean;         // Внедрение другого бина
}
```

**Qualifiers для выбора реализации:**
```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface DatabaseService {}

@DatabaseService
@Named
public class DatabaseCalculationService implements CalculationService {
    // Реализация
}

@Inject
@DatabaseService
private CalculationService service;
```

### EL-выражения и бины

```xhtml
<!-- Доступ к свойству -->
<h:outputText value="#{areaCheckBean.x}"/>

<!-- Вызов метода -->
<h:commandButton action="#{areaCheckBean.checkPoint()}"/>

<!-- Условный рендеринг -->
<h:panelGroup rendered="#{areaCheckBean.x != null}">
    <!-- Контент -->
</h:panelGroup>

<!-- Итерация -->
<h:dataTable value="#{resultsBean.results}" var="result">
    <h:column>#{result.x}</h:column>
</h:dataTable>
```

---

## 7. Конфигурация JSF-приложений

### Файл faces-config.xml

**Назначение:** Конфигурация навигации, управляемых бинов, конвертеров, валидаторов и других аспектов JSF.

**Пример из вашего проекта:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<faces-config xmlns="https://jakarta.ee/xml/ns/jakartaee"
              version="4.0">
    
    <!-- Правила навигации -->
    <navigation-rule>
        <from-view-id>/start.xhtml</from-view-id>
        <navigation-case>
            <from-outcome>main</from-outcome>
            <to-view-id>/main.xhtml</to-view-id>
        </navigation-case>
    </navigation-rule>
    
    <navigation-rule>
        <from-view-id>/main.xhtml</from-view-id>
        <navigation-case>
            <from-outcome>start</from-outcome>
            <to-view-id>/start.xhtml</to-view-id>
        </navigation-case>
        <navigation-case>
            <from-outcome>main</from-outcome>
            <to-view-id>/main.xhtml</to-view-id>
            <redirect/>  <!-- Редирект вместо forward -->
        </navigation-case>
    </navigation-rule>
</faces-config>
```

#### Элементы faces-config.xml

1. **Навигация** (`<navigation-rule>`):
```xml
<navigation-rule>
    <from-view-id>/page1.xhtml</from-view-id>
    <navigation-case>
        <from-outcome>success</from-outcome>
        <to-view-id>/page2.xhtml</to-view-id>
        <redirect/>  <!-- Опционально -->
    </navigation-case>
</navigation-rule>
```

2. **Управляемые бины** (устаревший способ):
```xml
<managed-bean>
    <managed-bean-name>myBean</managed-bean-name>
    <managed-bean-class>com.example.MyBean</managed-bean-class>
    <managed-bean-scope>request</managed-bean-scope>
</managed-bean>
```

3. **Конвертеры**:
```xml
<converter>
    <converter-id>customConverter</converter-id>
    <converter-class>com.example.CustomConverter</converter-class>
</converter>
```

4. **Валидаторы**:
```xml
<validator>
    <validator-id>customValidator</validator-id>
    <validator-class>com.example.CustomValidator</validator-class>
</validator>
```

### Файл web.xml

**Конфигурация JSF в `web.xml`:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         version="6.0">
    
    <!-- Параметры JSF -->
    <context-param>
        <param-name>jakarta.faces.PROJECT_STAGE</param-name>
        <param-value>Development</param-value>
        <!-- Возможные значения: Development, Production, UnitTest, SystemTest -->
    </context-param>
    
    <context-param>
        <param-name>jakarta.faces.CONFIG_FILES</param-name>
        <param-value>/WEB-INF/faces-config.xml</param-value>
    </context-param>
    
    <context-param>
        <param-name>jakarta.faces.DEFAULT_SUFFIX</param-name>
        <param-value>.xhtml</param-value>
    </context-param>
    
    <context-param>
        <param-name>jakarta.faces.FACELETS_SKIP_COMMENTS</param-name>
        <param-value>true</param-value>
    </context-param>
    
    <context-param>
        <param-name>jakarta.faces.ENCODING</param-name>
        <param-value>UTF-8</param-value>
    </context-param>
    
    <!-- FacesServlet -->
    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>jakarta.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.xhtml</url-pattern>
    </servlet-mapping>
    
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>/faces/*</url-pattern>
    </servlet-mapping>
</web-app>
```

#### Важные параметры JSF

- `jakarta.faces.PROJECT_STAGE` - стадия разработки (Development/Production)
- `jakarta.faces.STATE_SAVING_METHOD` - способ сохранения состояния (client/server)
- `jakarta.faces.DEFAULT_SUFFIX` - расширение Facelets-файлов
- `jakarta.faces.CONFIG_FILES` - пути к конфигурационным файлам
- `jakarta.faces.ENCODING` - кодировка по умолчанию

### Класс FacesServlet

**FacesServlet** — это центральный сервлет JSF, который:
- Обрабатывает все запросы к JSF-страницам
- Управляет жизненным циклом JSF
- Координирует работу компонентов

#### Настройка FacesServlet

```xml
<servlet>
    <servlet-name>Faces Servlet</servlet-name>
    <servlet-class>jakarta.faces.webapp.FacesServlet</servlet-class>
    <load-on-startup>1</load-on-startup>  <!-- Загрузка при старте -->
</servlet>

<servlet-mapping>
    <servlet-name>Faces Servlet</servlet-name>
    <url-pattern>*.xhtml</url-pattern>    <!-- Обработка .xhtml файлов -->
</servlet-mapping>
```

#### Жизненный цикл FacesServlet

1. **Получение запроса** → создание `FacesContext`
2. **Восстановление представления** → создание/восстановление компонентного дерева
3. **Применение значений запроса** → установка значений в компоненты
4. **Обработка валидаций** → валидация и конвертация
5. **Обновление модели** → обновление управляемых бинов
6. **Вызов приложения** → выполнение action-методов
7. **Рендеринг ответа** → генерация HTML

---

## 8. Навигация в JSF-приложениях

### Типы навигации

#### 1. Декларативная навигация (faces-config.xml)

**Пример из вашего проекта:**

```xml
<navigation-rule>
    <from-view-id>/start.xhtml</from-view-id>
    <navigation-case>
        <from-outcome>main</from-outcome>
        <to-view-id>/main.xhtml</to-view-id>
    </navigation-case>
</navigation-rule>
```

**Использование:**
```java
public String navigate() {
    return "main";  // Возвращает outcome, который сопоставляется с правилом
}
```

#### 2. Программная навигация

**Через метод действия:**
```java
@Named
@ViewScoped
public class NavigationBean {
    
    public String goToMain() {
        return "main";  // Outcome для навигации
    }
    
    public String goToStart() {
        return "start";
    }
    
    public String stayOnPage() {
        return null;  // Остаться на текущей странице
    }
}
```

**Использование:**
```xhtml
<h:commandButton value="Перейти" action="#{navigationBean.goToMain()}"/>
<h:link outcome="main" value="Ссылка"/>
```

#### 3. Условная навигация

```xml
<navigation-rule>
    <from-view-id>/login.xhtml</from-view-id>
    <navigation-case>
        <from-outcome>success</from-outcome>
        <if>#{userBean.role == 'admin'}</if>
        <to-view-id>/admin.xhtml</to-view-id>
    </navigation-case>
    <navigation-case>
        <from-outcome>success</from-outcome>
        <to-view-id>/user.xhtml</to-view-id>
    </navigation-case>
</navigation-rule>
```

#### 4. Навигация с параметрами

```java
public String navigateWithParams() {
    FacesContext.getCurrentInstance()
        .getExternalContext()
        .getFlash()
        .put("message", "Данные сохранены");
    return "result?faces-redirect=true";
}
```

**Или через навигационное правило:**
```xml
<navigation-case>
    <from-outcome>result</from-outcome>
    <to-view-id>/result.xhtml</to-view-id>
    <redirect>
        <view-param>
            <name>id</name>
            <value>#{bean.id}</value>
        </view-param>
    </redirect>
</navigation-case>
```

### Redirect vs Forward

**Forward (по умолчанию):**
- URL в браузере не меняется
- Данные сохраняются в запросе
- Быстрее

```java
return "page";  // Forward
```

**Redirect:**
- URL в браузере меняется
- Новый запрос
- Можно использовать для предотвращения повторной отправки формы

```java
return "page?faces-redirect=true";  // Redirect
```

**Или в faces-config.xml:**
```xml
<navigation-case>
    <from-outcome>page</from-outcome>
    <to-view-id>/page.xhtml</to-view-id>
    <redirect/>  <!-- Принудительный redirect -->
</navigation-case>
```

### Навигация через ссылки

```xhtml
<!-- Навигационная ссылка -->
<h:link outcome="main" value="Главная страница"/>

<!-- С параметрами -->
<h:link outcome="details">
    <f:param name="id" value="#{bean.id}"/>
</h:link>

<!-- Обычная ссылка -->
<h:outputLink value="/page.xhtml">
    <h:outputText value="Ссылка"/>
</h:outputLink>
```

---

## 9. Доступ к БД из Java-приложений. JDBC

### Протокол JDBC

**JDBC (Java Database Connectivity)** — стандартный API для доступа к реляционным базам данных из Java.

#### Основные компоненты JDBC

1. **JDBC API** - интерфейсы и классы для работы с БД
2. **JDBC Driver** - драйвер для конкретной СУБД
3. **Connection** - соединение с БД
4. **Statement/PreparedStatement** - выполнение SQL-запросов
5. **ResultSet** - результат выполнения запроса

### Формирование запросов

#### 1. Statement (простой запрос)

```java
Connection conn = DriverManager.getConnection(url, user, password);
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = 1");

while (rs.next()) {
    String name = rs.getString("name");
    int age = rs.getInt("age");
}
```

#### 2. PreparedStatement (рекомендуется)

```java
String sql = "SELECT * FROM users WHERE id = ? AND name = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setInt(1, userId);
pstmt.setString(2, userName);
ResultSet rs = pstmt.executeQuery();
```

**Преимущества PreparedStatement:**
- Защита от SQL-инъекций
- Лучшая производительность (компиляция запроса)
- Типобезопасность

#### 3. CallableStatement (хранимые процедуры)

```java
CallableStatement cstmt = conn.prepareCall("{call get_user(?, ?)}");
cstmt.setInt(1, userId);
cstmt.registerOutParameter(2, Types.VARCHAR);
cstmt.execute();
String result = cstmt.getString(2);
```

### Работа с драйверами СУБД

#### Загрузка драйвера

**Способ 1 (устаревший):**
```java
Class.forName("org.postgresql.Driver");
```

**Способ 2 (современный, автоматический):**
```java
// JDBC 4.0+ автоматически загружает драйвер из classpath
Connection conn = DriverManager.getConnection(
    "jdbc:postgresql://localhost:5432/mydb",
    "user",
    "password"
);
```

#### Подключение к БД

```java
// PostgreSQL (из вашего проекта)
String url = "jdbc:postgresql://localhost:5432/web3db";
String user = "postgres";
String password = "postgres";

Connection conn = DriverManager.getConnection(url, user, password);
```

**URL-форматы для разных СУБД:**
- PostgreSQL: `jdbc:postgresql://host:port/database`
- MySQL: `jdbc:mysql://host:port/database`
- Oracle: `jdbc:oracle:thin:@host:port:database`
- H2: `jdbc:h2:mem:testdb`

#### Управление транзакциями

```java
Connection conn = null;
try {
    conn = DriverManager.getConnection(url, user, password);
    conn.setAutoCommit(false);  // Отключение автокоммита
    
    // Выполнение операций
    PreparedStatement pstmt = conn.prepareStatement(
        "INSERT INTO results (x, y, r) VALUES (?, ?, ?)");
    pstmt.setInt(1, x);
    pstmt.setDouble(2, y);
    pstmt.setDouble(3, r);
    pstmt.executeUpdate();
    
    conn.commit();  // Подтверждение транзакции
} catch (SQLException e) {
    if (conn != null) {
        conn.rollback();  // Откат при ошибке
    }
} finally {
    if (conn != null) {
        conn.close();
    }
}
```

#### Connection Pool

Для production-приложений используется пул соединений:

```java
// Пример с HikariCP
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:5432/web3db");
config.setUsername("postgres");
config.setPassword("postgres");
config.setMaximumPoolSize(10);

HikariDataSource ds = new HikariDataSource(config);
Connection conn = ds.getConnection();
```

### Недостатки JDBC

1. **Много шаблонного кода** (Connection, Statement, ResultSet)
2. **Ручное маппинг** объектов на таблицы
3. **Нет кэширования**
4. **Нет управления отношениями** между объектами
5. **SQL-зависимость** (привязка к конкретной СУБД)

Эти проблемы решает **ORM**.

---

## 10. Концепция ORM

### Что такое ORM?

**ORM (Object-Relational Mapping)** — технология программирования, которая связывает объектную модель приложения с реляционной базой данных.

### Принципы ORM

1. **Маппинг классов на таблицы**: Класс Java → таблица БД
2. **Маппинг полей на колонки**: Поле класса → колонка таблицы
3. **Маппинг объектов на строки**: Экземпляр класса → строка таблицы
4. **Маппинг отношений**: Связи между классами → внешние ключи

### Преимущества ORM

1. **Меньше кода**: Не нужно писать SQL вручную
2. **Типобезопасность**: Работа с объектами вместо строк
3. **Переносимость**: Абстракция от конкретной СУБД
4. **Кэширование**: Автоматическое кэширование объектов
5. **Lazy Loading**: Ленивая загрузка связанных объектов
6. **Управление транзакциями**: Автоматическое управление

### Недостатки ORM

1. **Производительность**: Может быть медленнее нативных SQL-запросов
2. **Сложность**: Кривая обучения
3. **Ограничения**: Сложные запросы могут быть неудобными
4. **Отладка**: Сложнее отлаживать сгенерированные запросы

### Библиотеки ORM в Java

1. **Hibernate** - самая популярная
2. **EclipseLink** - референсная реализация JPA
3. **Apache OpenJPA**
4. **DataNucleus**
5. **MyBatis** - SQL-маппер (не полноценный ORM)

### Основные API

#### 1. JPA (Java Persistence API)

**JPA** — стандартный API для ORM в Java. Это спецификация, а не реализация.

**Основные интерфейсы:**
- `EntityManager` - основной интерфейс для работы с сущностями
- `EntityManagerFactory` - фабрика для создания EntityManager
- `EntityTransaction` - управление транзакциями
- `Query` / `TypedQuery` - выполнение запросов

#### 2. Hibernate API

Hibernate предоставляет собственный API (помимо JPA):
- `Session` - аналог EntityManager
- `SessionFactory` - аналог EntityManagerFactory
- `Criteria API` - построение запросов

#### 3. Spring Data JPA

Надстройка над JPA для упрощения работы:
- Репозитории с автоматической реализацией методов
- Query methods
- Specifications

### Интеграция ORM-провайдеров с JDBC

ORM-провайдеры используют JDBC под капотом:

```
Приложение
    ↓
ORM API (JPA/Hibernate)
    ↓
ORM Провайдер (Hibernate/EclipseLink)
    ↓
JDBC Driver
    ↓
База данных
```

**Пример конфигурации (из вашего проекта):**

```xml
<!-- persistence.xml -->
<persistence-unit name="web3PU">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <class>org.example.entities.ResultEntity</class>
    <properties>
        <!-- JDBC настройки -->
        <property name="jakarta.persistence.jdbc.driver" 
                  value="org.postgresql.Driver"/>
        <property name="jakarta.persistence.jdbc.url" 
                  value="jdbc:postgresql://localhost:5432/web3db"/>
        <property name="jakarta.persistence.jdbc.user" value="postgres"/>
        <property name="jakarta.persistence.jdbc.password" value="postgres"/>
    </properties>
</persistence-unit>
```

ORM-провайдер:
1. Использует JDBC драйвер для подключения к БД
2. Генерирует SQL-запросы на основе маппинга
3. Выполняет запросы через JDBC
4. Маппит результаты обратно в объекты

---

## 11. Библиотеки ORM: Hibernate и EclipseLink

### Hibernate

#### Особенности

1. **Самая популярная** ORM-библиотека
2. **Богатый функционал**: Кэширование, валидация, поиск
3. **Гибкость**: Поддержка различных стратегий загрузки
4. **Производительность**: Оптимизированные запросы
5. **Сообщество**: Большая база знаний и поддержка

#### API Hibernate

**Нативный API:**
```java
SessionFactory sessionFactory = new Configuration()
    .configure()
    .buildSessionFactory();

Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();

ResultEntity entity = new ResultEntity(x, y, r, hit, time);
session.save(entity);

tx.commit();
session.close();
```

**JPA API (через Hibernate):**
```java
EntityManagerFactory emf = Persistence
    .createEntityManagerFactory("web3PU");
EntityManager em = emf.createEntityManager();

EntityTransaction tx = em.getTransaction();
tx.begin();

ResultEntity entity = new ResultEntity(x, y, r, hit, time);
em.persist(entity);

tx.commit();
em.close();
```

#### Конфигурация Hibernate

**hibernate.cfg.xml:**
```xml
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">
            org.postgresql.Driver
        </property>
        <property name="hibernate.connection.url">
            jdbc:postgresql://localhost:5432/web3db
        </property>
        <property name="hibernate.connection.username">postgres</property>
        <property name="hibernate.connection.password">postgres</property>
        <property name="hibernate.dialect">
            org.hibernate.dialect.PostgreSQLDialect
        </property>
        <mapping class="org.example.entities.ResultEntity"/>
    </session-factory>
</hibernate-configuration>
```

### EclipseLink

#### Особенности

1. **Референсная реализация JPA** (для JPA 2.0+)
2. **Разработан Eclipse Foundation**
3. **Поддержка NoSQL**: MongoDB, XML, JSON
4. **Кэширование**: Встроенная система кэширования
5. **Производительность**: Оптимизирован для больших объемов данных

#### API EclipseLink

**JPA API (стандартный):**
```java
EntityManagerFactory emf = Persistence
    .createEntityManagerFactory("web3PU");
EntityManager em = emf.createEntityManager();

EntityTransaction tx = em.getTransaction();
tx.begin();

ResultEntity entity = new ResultEntity(x, y, r, hit, time);
em.persist(entity);

tx.commit();
em.close();
```

**Пример из вашего проекта:**

```java
// ResultRepository.java (предположительно)
@ApplicationScoped
public class ResultRepository {
    
    @PersistenceContext(unitName = "web3PU")
    private EntityManager em;
    
    public void save(ResultEntity entity) {
        em.persist(entity);
    }
    
    public List<ResultEntity> findAll() {
        return em.createQuery(
            "SELECT r FROM ResultEntity r", 
            ResultEntity.class
        ).getResultList();
    }
}
```

#### Конфигурация EclipseLink

**persistence.xml (из вашего проекта):**
```xml
<persistence-unit name="web3PU" transaction-type="RESOURCE_LOCAL">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <class>org.example.entities.ResultEntity</class>
    <properties>
        <property name="jakarta.persistence.jdbc.driver" 
                  value="org.postgresql.Driver"/>
        <property name="jakarta.persistence.jdbc.url" 
                  value="jdbc:postgresql://localhost:5432/web3db"/>
        <property name="jakarta.persistence.jdbc.user" value="postgres"/>
        <property name="jakarta.persistence.jdbc.password" value="postgres"/>
        
        <!-- Специфичные для EclipseLink -->
        <property name="eclipselink.ddl-generation" 
                  value="create-or-extend-tables"/>
        <property name="eclipselink.logging.level" value="FINE"/>
    </properties>
</persistence-unit>
```

### Сходства и отличия

#### Сходства

| Аспект | Hibernate | EclipseLink |
|--------|-----------|-------------|
| **JPA-совместимость** | ✅ Да | ✅ Да (референсная) |
| **Аннотации JPA** | ✅ Поддерживает | ✅ Поддерживает |
| **Кэширование** | ✅ Есть | ✅ Есть |
| **Lazy Loading** | ✅ Есть | ✅ Есть |
| **Транзакции** | ✅ Поддерживает | ✅ Поддерживает |

#### Отличия

| Аспект | Hibernate | EclipseLink |
|--------|-----------|-------------|
| **Популярность** | Очень высокая | Средняя |
| **Производительность** | Оптимизирована | Очень оптимизирована |
| **NoSQL поддержка** | Ограниченная | Хорошая |
| **Сложность** | Средняя | Средняя |
| **Документация** | Отличная | Хорошая |
| **Сообщество** | Очень большое | Среднее |
| **Специфичные фичи** | Много | Меньше (фокус на стандарте) |

#### Когда использовать что?

**Hibernate:**
- Нужна максимальная популярность и поддержка
- Требуется много специфичных фич
- Большое сообщество важно

**EclipseLink:**
- Нужна референсная реализация JPA
- Требуется поддержка NoSQL
- Важна максимальная производительность
- Используется в GlassFish/Jakarta EE

---

## 12. Технология JPA

### Что такое JPA?

**JPA (Java Persistence API)** — стандартная спецификация для ORM в Java, часть Jakarta EE (ранее Java EE).

### Особенности JPA

1. **Стандартизация**: Единый API для всех провайдеров
2. **Аннотации**: Декларативный маппинг через аннотации
3. **JPQL**: Язык запросов, независимый от СУБД
4. **Кэширование**: Встроенная поддержка кэширования
5. **Транзакции**: Управление через EntityTransaction

### API JPA

#### Основные интерфейсы

1. **EntityManagerFactory**
   - Создает EntityManager
   - Кэширует метаданные
   - Обычно один на приложение

```java
EntityManagerFactory emf = Persistence
    .createEntityManagerFactory("web3PU");
```

2. **EntityManager**
   - Основной интерфейс для работы с сущностями
   - Управляет жизненным циклом сущностей
   - Выполняет запросы

```java
EntityManager em = emf.createEntityManager();
```

3. **EntityTransaction**
   - Управление транзакциями

```java
EntityTransaction tx = em.getTransaction();
tx.begin();
// Операции
tx.commit();
```

#### Основные операции

**CRUD операции:**

```java
// Create (Persist)
ResultEntity entity = new ResultEntity(x, y, r, hit, time);
em.persist(entity);

// Read (Find)
ResultEntity found = em.find(ResultEntity.class, id);

// Update (Merge)
entity.setHit(true);
em.merge(entity);

// Delete (Remove)
em.remove(entity);
```

#### Жизненный цикл сущностей

1. **New (Transient)** - новый объект, не связан с EntityManager
2. **Managed (Persistent)** - управляется EntityManager
3. **Detached** - был управляемым, но больше не связан
4. **Removed** - помечен на удаление

### Пример из вашего проекта

**Сущность (`ResultEntity.java`):**

```java
@Entity
@Table(name = "results")
public class ResultEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer x;
    
    @Column(nullable = false)
    private Double y;
    
    @Column(nullable = false)
    private Double r;
    
    @Column(nullable = false)
    private Boolean hit;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    // Конструкторы, геттеры, сеттеры
}
```

**Аннотации JPA:**

- `@Entity` - помечает класс как сущность
- `@Table` - указывает имя таблицы
- `@Id` - первичный ключ
- `@GeneratedValue` - стратегия генерации ID
- `@Column` - маппинг колонки
- `@OneToMany`, `@ManyToOne`, `@ManyToMany` - отношения

### JPQL (Java Persistence Query Language)

**JPQL** — объектно-ориентированный язык запросов:

```java
// Простой запрос
TypedQuery<ResultEntity> query = em.createQuery(
    "SELECT r FROM ResultEntity r WHERE r.hit = true",
    ResultEntity.class
);
List<ResultEntity> results = query.getResultList();

// С параметрами
TypedQuery<ResultEntity> query = em.createQuery(
    "SELECT r FROM ResultEntity r WHERE r.x = :x AND r.y = :y",
    ResultEntity.class
);
query.setParameter("x", 5);
query.setParameter("y", 2.5);
List<ResultEntity> results = query.getResultList();

// Нативный SQL (если нужен)
Query nativeQuery = em.createNativeQuery(
    "SELECT * FROM results WHERE x = ?",
    ResultEntity.class
);
nativeQuery.setParameter(1, 5);
```

### Интеграция с ORM-провайдерами

JPA — это **спецификация**, а не реализация. Провайдеры реализуют эту спецификацию:

**Провайдеры JPA:**
1. **EclipseLink** - референсная реализация
2. **Hibernate** - самая популярная
3. **Apache OpenJPA**
4. **DataNucleus**

**Выбор провайдера в persistence.xml:**

```xml
<persistence-unit name="web3PU">
    <!-- EclipseLink -->
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    
    <!-- Или Hibernate -->
    <!-- <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider> -->
</persistence-unit>
```

**Преимущества использования JPA:**
- **Переносимость**: Можно сменить провайдера без изменения кода
- **Стандартизация**: Единый API
- **Интеграция**: Хорошо интегрируется с Jakarta EE

### Конфигурация JPA

**persistence.xml (из вашего проекта):**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             version="3.0">
    
    <persistence-unit name="web3PU" transaction-type="RESOURCE_LOCAL">
        <!-- Провайдер -->
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
        
        <!-- Классы сущностей -->
        <class>org.example.entities.ResultEntity</class>
        
        <!-- Свойства -->
        <properties>
            <!-- JDBC настройки -->
            <property name="jakarta.persistence.jdbc.driver" 
                      value="org.postgresql.Driver"/>
            <property name="jakarta.persistence.jdbc.url" 
                      value="jdbc:postgresql://localhost:5432/web3db"/>
            <property name="jakarta.persistence.jdbc.user" value="postgres"/>
            <property name="jakarta.persistence.jdbc.password" value="postgres"/>
            
            <!-- Специфичные для EclipseLink -->
            <property name="eclipselink.ddl-generation" 
                      value="create-or-extend-tables"/>
            <property name="eclipselink.logging.level" value="FINE"/>
        </properties>
    </persistence-unit>
</persistence>
```

**Типы транзакций:**
- `RESOURCE_LOCAL` - локальные транзакции (для standalone приложений)
- `JTA` - транзакции через JTA (для Jakarta EE приложений)

---

## Заключение

Этот документ охватывает основные аспекты JSF и ORM технологий. Ваш проект использует:

- **JSF 4.0** с Facelets
- **CDI** для управляемых бинов
- **JPA** с **EclipseLink** провайдером
- **PrimeFaces** для расширенных компонентов
- **PostgreSQL** как базу данных

Все эти технологии работают вместе, обеспечивая современную архитектуру веб-приложения на Java.

