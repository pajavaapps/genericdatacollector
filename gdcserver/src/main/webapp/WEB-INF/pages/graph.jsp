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
<script type="text/javascript" language="javascript" src="<c:url value="/js/jquery.dropdownPlain.js"/>"></script>
<script type="text/javascript"
	src="http://maps.googleapis.com/maps/api/js?file=2.x&key=AIzaSyD00b69Lx-YTyGw4vhZehU3oSCh-lTAiws&sensor=false"></script>
<script type="text/javascript" src="<c:url value="/js/genericdatacollector.js"/>"></script>

<script type="text/javascript">
	$(document).ready(function() {
		var genericDataCollectorView = new GenericDataCollectorView();
		genericDataCollectorView.setDeviceIds();
		window.genericDataCollectorView=genericDataCollectorView;
	});
</script>
<title>Generic Data Collector</title>
</head>
<body>

<center>
        <div style="width:970px">
		<ul class="dropdown" >
			<li><a href="<c:url value="/backend/graph"/>">Monitor</a></li>
			<li><a href="<c:url value="/backend/admin"/>">Admin</a></li>
			<li><a href="<c:url value="/about.html"/>">About</a></li>
		</ul>
		</div>
		<br/>
		<h1>Generic Data Collector</h1>
	<form method="get" action="backend/getLocationData">
		Vehicle: <select id="deviceSelector"></select> Date:<select id="dateSelector"></select>
	
			Minimum GForce: <select
			name="gforce" id="gforceSelect">
			<option value="0.2">0.2</option>
			<option value="0.3">0.3</option>
			<option value="0.4">0.4</option>
			<option value="0.5"  selected="selected">0.5</option>
			<option value="0.6" >0.6</option>
			<option value="0.7"  >0.7</option>
			<option value="0.8">0.8</option>
			<option value="0.9">0.9</option>
			<option value="1.0">1.0</option>
			<option value="1.1" >1.1</option>
		</select>
		 <input
			type="button" value="Get Vehicle Data"  id="mergeDataButton" /> <img  id="progressBar" src="images/progress_bar.gif"  >
	</form>
	
	<div id="genericdatacollector_map"
		style="width: 900px; height: 500px; border: 1px; border-color: dark-blue">

	</div>
	
	<br/>
	<div id="divData" >
		<table id="dataTable" class="r-grid"  style=" width:900px">
		</table>
	</div>
	</center>
</body>
</html>