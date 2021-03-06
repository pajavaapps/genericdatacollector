<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<link rel="stylesheet" type="text/css" href="<c:url value="/css/ie.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/css/genericdatacollector.css"/>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="<c:url value="/js/jquery.js"/>"></script>
<script type="text/javascript" language="javascript"
	src="<c:url value="/js/jquery.dropdownPlain.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/genericdatacollector.js"/>"></script>

<script type="text/javascript">
	$(document).ready(function() {
		var genericDataCollectorAdminView = new GenericDataCollectorAdminView();
		window.genericDataCollectorAdminView = genericDataCollectorAdminView;
	});
</script>
<title>Generic Data Collector Admin</title>
</head>
<body>
	<center>
		<div  style="width:970px">
			<ul class="dropdown">
			<li><a href="<c:url value="/backend/graph"/>">Monitor</a></li>
			<li><a href="<c:url value="/backend/admin"/>">Admin</a></li>
			<li><a href="<c:url value="/about.html"/>">About</a></li>
			</ul>
			<br />
		</div>
		<h1>Generic Data Collector Admin</h1>


		<br />
		<table id="deviceTable" class="r-grid" style="width: 600px">
		</table>
	</center>
</body>
</html>