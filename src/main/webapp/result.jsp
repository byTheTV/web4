<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Результат</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/styles/reset.css" />
    <link rel="stylesheet" href="<%= request.getContextPath() %>/styles/main.css" />
</head>
<body>
<main>
    <h2>Параметры</h2>
    <table border="1" cellpadding="4" cellspacing="0">
        <tr><th>X</th><td><%= request.getAttribute("x") %></td></tr>
        <tr><th>Y</th><td><%= request.getAttribute("y") %></td></tr>
        <tr><th>R</th><td><%= request.getAttribute("r") %></td></tr>
        <tr><th>Попадание</th><td><%= Boolean.TRUE.equals(request.getAttribute("hit")) ? "Да" : "Нет" %></td></tr>
    </table>
    <p><a href="<%= request.getContextPath() %>/index.jsp">Новый запрос</a></p>
</main>
</body>
</html>


