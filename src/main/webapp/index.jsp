<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%! String[] header() { return new String[]{"Тарасов Владислав Павлович", "P3219", "Вариант 6633"}; } %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Lab 2 - Check area</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/styles/reset.css" />
    <link rel="stylesheet" href="<%= request.getContextPath() %>/styles/main.css" />
    <script defer src="<%= request.getContextPath() %>/scripts/form.js"></script>
    <script defer src="<%= request.getContextPath() %>/scripts/area.js"></script>
    
</head>
<body>
<header>
    <div>
        <strong><%= header()[0] %></strong> — группа <%= header()[1] %> — вариант <%= header()[2] %>
    </div>
</header>
<main>
    <jsp:useBean id="resultsBean" class="org.example.models.ResultsBean" scope="session" />
    <section class="card"><div>
        <h2 class="section-title">Проверка попадания точки</h2>
        <form id="form" action="<%= request.getContextPath() %>/app/area-check" method="post" onsubmit="return validate()">
            <div>
                <label class="muted">X</label>
                <div class="options">
                <% int[] xs = {-5,-4,-3,-2,-1,0,1,2,3}; for (int xv: xs) { %>
                <label class="pill"><input type="radio" name="x" value="<%=xv%>"><span><%=xv%></span></label>
                <% } %>
                </div>
                <div id="x-error" class="muted" style="color:#b00"></div>
            </div>
            <div>
                <label for="y" class="muted">Y</label>
                <input id="y" name="y" type="text" placeholder="[-5..5]" />
                <div id="y-error" class="muted" style="color:#b00"></div>
            </div>
            <div>
                <label class="muted">R</label>
                <div class="options">
                    <% int[] rs = {1,2,3,4,5}; for (int rv: rs) { %>
                    <label class="pill"><input type="radio" id="r" name="r" value="<%=rv%>"><span><%=rv%></span></label>
                    <% } %>
                </div>
                <div id="r-error" class="muted" style="color:#b00"></div>
            </div>
            <button type="submit">Проверить</button>
            <% if (request.getAttribute("error") != null) { %>
            <div style="color:#b00"><%= request.getAttribute("error") %></div>
            <% } %>
        </form>
    </div></section>
    <section class="card"><div>
        <h2 class="section-title">Область</h2>
        <div style="position:relative; display:inline-block;">
            <canvas id="area" style="max-width:400px; cursor:crosshair; display:block;"></canvas>
        </div>
        <div class="muted">Перемещайте курсор по графику для выбора X и Y. Клик для отправки формы.</div>
    </div></section>
    <section class="card"><div>
        <h2 class="section-title">История</h2>
        <table class="zebra" border="1" cellpadding="4" cellspacing="0">
            <thead><tr><th>Время выполнения</th><th>X</th><th>Y</th><th>R</th><th>Попадание</th></tr></thead>
            <tbody>
            <c:forEach var="row" items="${resultsBean.results}">
                <tr>
                    <td>${row.time}</td>
                    <td>${row.x}</td>
                    <td>${row.y}</td>
                    <td>${row.r}</td>
                    <td><c:out value='${row.hit ? "Да" : "Нет"}'/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div></section>
</main>
</body>
</html>


