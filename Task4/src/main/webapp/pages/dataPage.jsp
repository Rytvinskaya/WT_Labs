<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="data" type="epam.webtech.model.HorseRacingData" scope="request"/>

<html>
<head>
    <title>Horse Racing</title>
</head>
<body>
<h2>10X BET</h2>
<h1>HORSE RACING</h1>
<h3>Races</h3>
<c:forEach var="race" items="${data.races}">
    <tags:race currentRace="${race}"/>
</c:forEach>
</body>
</html>