
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="currentRace" type="epam.webtech.model.race.Race" required="true" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div>
    <h3>Race</h3>
    <span>Date: ${currentRace.date}</span>
    <span>Horses:</span>
    <c:forEach var="horse" items="${currentRace.horsesNames}">
        <span>${horse}</span>
    </c:forEach>
    <span>Status: ${currentRace.status}</span>
    <c:if test="${currentRace.status.priority eq 2}">
        <span>Winner: ${currentRace.winnerHorseName}</span>
    </c:if>
</div>
