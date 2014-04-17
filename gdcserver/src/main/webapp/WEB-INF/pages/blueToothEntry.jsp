<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@taglib
	prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<head>
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/ie.css"/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/style.css"/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/genericdatacollector.css"/>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="<c:url value="/js/jquery.js"/>"></script>
<script type="text/javascript" language="javascript"
	src="<c:url value="/js/jquery.dropdownPlain.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/bluetoothentry.js"/>"></script>

<title>Bluetooth Entry</title>
</head>
<body syle="margin:40px">
	<div id="serviceMap" style="margin:40px">Sensor Services </div>
	<div><input type="button" value="Save Sensor Meta Data"  onclick="saveSensorMetaData('<c:url value="/backend"/>')"/></div>
	<div
		style="border: 2px solid green; margin: 10px; padding: 10px 10px; width: 80%">
		<form id="sensorForm" onclick="return false;">
			Sensor Name: <input type="text" name="sensorName" length="50"  value="SensorTag"/>&nbsp;&nbsp;&nbsp;<input type="button"  value="Load Sensor Data" onclick="loadSensorData('<c:url value="/backend/getBlueToothMetaData"/>',this.form)"/>
		</form>
		<div
			style="border: 2px solid red; margin: 10px; padding: 10px 10px; width: 70%">
			<form id="serviceForm" onclick="return false;">
				Service Name <input type="text" name="serviceName" size="30" /><br />
				Service UUID <input type="text" name="serviceUUID" size="50" /><br />
				<input type="button" value="Add Service"
					onclick="addService(this.form)" /><br />
			</form>
			<div style="border: 2px solid blue; margin: 20px; padding: 10px 10px">
				<form id="charForm" >
					Characteristic Name <input type="text" name="characteristicName" size="30" /><br /> 
				    Characteristic UUID <input type="text" name="characteristicUUID" size="50" /><br />
				    Enable Characteristic UUID <input type="text" name="enableCharacteristicUUID" size="50" /><br />
				    Enable Value <input type="text" name="enableValue" size="30" /><br />
				    Disable Value <input type="text" name="disableValue" size="30" /><br />
				    Is Calibration Value?  <input type="checkbox"  name="calibration"   /><br />
					<input	type="button" value="Add Characteristic" onclick="addCharacteristic(this.form)" />
			</div>
		</div>
		</form>
	</div>
</body>
</html>