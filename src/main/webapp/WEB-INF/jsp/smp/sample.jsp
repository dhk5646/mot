<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="/resources/js/cmm/lib/jquery/jquery-3.4.1.min.js"></script>
<script type="text/javascript" src="/resources/js/smp/sample.js"></script>

<h1>
	Hello world!  
</h1>

<P>  The time on the server is ${serverTime}. </P>

<H5>안녕하세요. Aks Company에 오신것을 환영합니다.</H5>
<H5>본 프로젝트는 My Office Terrior 입니다.</H5>

<h4>서버 URL :  <a href="http://118.36.196.100:7070/smp/sample.do" target="_blank">http://118.36.196.100:7070/smp/sample.do</a></h4>
<h4>mot git url : <a href="https://github.com/dhk5646/mot" target="_blank">https://github.com/dhk5646/mot</a> </h4>
<H4>진척사항</H4>

<H3>11월 18일 ~ 11월24일</H3>
1. java config 파일로 설정 파일 연동 완료 <br>
2. MVC 구성도 : SampleController.java -> SampleService.java -> SampleMappter.java -> SampleMappter.xml <br> 
3. MariaDB 연동 완료 <br>
4. git hub 연동 완료 <br>



<H3>11월 25일 ~ 12월1일</H3>
1. RestApi 형태로 구현 <br>
2. 트랜잭션 설정 <br>
3. 로그(logback) 설정 <br>
4. 인터셉터 설정 <br>
5. 공통 구조 잡기 <br>
6. resources 구조 잡기<br>

<br>

<img src="/resources/img/list.PNG" alt="My Image">

<H3>12월 28일</H3>
1. 계산기를 만들어봅시다. <br>

<input type="text" id="param1">
<select id="expression">
	<option value="add">+</option>
	<option value="sub">-</option>
	<option value="mul">*</option>
	<option value="div">%</option>
</select>
<input type="text" id="param2">
<button type="button" id="buttonCalculator" onclick="fnCalculator1();">계산하기</button>
결과 : <input type="text" id="result" >

<br>
<br>
<br>
<br>
<br>
<br>

