<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<nav class="navbar navbar-expand-lg navbar-light bg-light fixed-top">
    <div class="container">
        <a class="navbar-brand" href="#">WORDS</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarResponsive">
            <ul class="navbar-nav ml-auto">




<li class="nav-item">
          <a class="nav-link active" href="showNewWord">Добавить слово</a>
        
        </li>
         <li class="nav-item">
         
        <a class="nav-link active" href="showListWords">Словарь</a>
        </li>
        <li class="nav-item">
         
        <a class="nav-link active" href="showLearning">Обучение</a>
        
        </li>
         <li class="nav-item">
         
        <a class="nav-link active" href="showLearnedWords">Выученные слова</a>
        
        </li>
         <li class="nav-item">
         
        <a class="nav-link active" href="showCheckingWords">Проверить Себя</a>
        
        </li>
         <li class="nav-item">
         
        <a class="nav-link active" href="showPageStatistic">Статистика</a>
        
        </li>
                <li class="nav-item ">
                    <a class="nav-link active" id="logout" href="logout">Выйти</a><br>
                </li>
            </ul>
        </div>
    </div>
</nav><br><br><br><br>
<div class="container">
    <div class="row">
        <c:if test="${info ne null}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            ${info}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        </c:if>
        
    </div>
    
    <h2 class="white">Словарь - статистика</h2>
    <form action="showStatistic" method="POST">
    
    
    <hr>
    <br>
    
    <div class="row">
        
        
            <div class="col-md-12  center">


<c:forEach var="entry" items="${listHistories}" varStatus="count">
    <p class="white"> ${entry.word.word} - ${entry.word.translation} правильный ответ <strong>${entry.rating}</strong> раз из <strong>${entry.count} </strong> показов - 
<c:set var = "balance" value = "${(entry.rating/entry.count)}" />
<fmt:formatNumber type = "percent" maxIntegerDigits="3" value = "${balance}" /> </p>
        </c:forEach>  
                <input type="submit" class="btn btn-primary" name="popWords" value="Показать статистику">
                
 
                
                 </div>
        
     </form>
    </div>
       <br> 
    <hr>
</div>


  


            
          
            
            
            
            
            
            
            
            
            
            
            
            
    













