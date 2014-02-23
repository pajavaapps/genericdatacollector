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
<script type="text/javascript">
	var SensorMetaData = function(name) {
		this.name = name;
		this.serviceMetaDataMap = {};
	};
	SensorMetaData.prototype = {
		addService : function(service) {
			this.serviceMetaDataMap[service.serviceName] = service;
		},
		deleteService : function(serviceName) {
			delete this.serviceMetaDataMap[serviceName];
		},
		getService:function(serviceName){
			return this.serviceMetaDataMap[serviceName]
		},
		loadSensorMetaData:function(sensorObject){
			 for(var key in sensorObject.serviceMetaDataMap)
			  {
				 var service=sensorObject.serviceMetaDataMap[key];
				 var bluetoothService=new BluetoothService(service.serviceName,service.serviceUUID);
				 this.addService(bluetoothService);
				 for(var key2 in service.characteristicMap)
				  {
					 var ch=service.characteristicMap[key2];
					 var bluetoothCharacterisitic=new BluetoothCharacteristic (ch.characteristicName,
							 ch.characteristicUUID,ch.enableCharacteristicUUID,
							 ch.enableCharacteristicValue, ch.disableCharacteristicValue)
					 bluetoothService.addCharacteristic(bluetoothCharacterisitic);
				  }
			  }
		},
		display : function(sensorName,div) {
			 div.html("Sensor Services for "+sensorName);
			 for(var key in this.serviceMetaDataMap)
			  {
				 var service=this.serviceMetaDataMap[key];
				 service.display(div);
			  }
		}

	}
	var BluetoothService = function(name, uuid) {
		this.serviceName = name;
		this.serviceUUID = uuid;
		this.characteristicMap = {};
	};
	BluetoothService.prototype = {
		addCharacteristic : function(characteristic) {
			this.characteristicMap[characteristic.characteristicName] = characteristic;
		},
		deleteCharacteristic:function(characteristicName){
			delete this.characteristicMap[characteristicName];
		},
		getCharacteristic:function(characteristicName){
			return this.characteristicMap[characteristicName];
		},display:function(div)
		{
			tableElement = $(document.createElement('table'));
			tableElement.attr("style", "border:2px solid;width:80%");
			tableElement
					.append("<tr><td style='width:20%'>Sensor "
							+ this.serviceName
							+ "</td><td>"
							+ this.serviceUUID
							+ "</td><td></td><td></td><td></td><td  style='width:20%'>"+
							"<input type='button' value='Delete' onclick='deleteService(\""+this.serviceName+"\")'/> &nbsp;&nbsp;"+
							"<input type='button' value='Edit' onclick='editService(\""+this.serviceName+"\")'/></td></tr>");
			tableElement
			.append("<tr><td>Characteristic Name</td><td>UUID</td><td>Enable UUID</td><td>Enable Value</td><td>DisableValue</td><td style='width:20%'>Actions</td></tr>");
			div.append(tableElement);
			 for(var key in this.characteristicMap)
			  {
				 var service=this.characteristicMap[key];
				 service.display(this.serviceName,tableElement);
			  }
			div.append("<br/>");
		}
	};

	var BluetoothCharacteristic = function(name, uuid,enableUuid,enableValue, disableValue) {
		this.characteristicName = name;
		this.characteristicUUID = uuid;
		this.enableCharacteristicUUID=enableUuid;
		this.enableCharacteristicValue=enableValue;
		this.disableCharacteristicValue=disableValue;
	};
	BluetoothCharacteristic.prototype = {
		display:function(serviceName,tableElement){
			tableElement
			.append("<tr><td style='width:20%'>Characteristic "
					+ this.characteristicName
					+ "</td>"
					+"<td>"+this.characteristicUUID+"</td>"
					+"<td>"+this.enableCharacteristicUUID+"</td>"
					+"<td>"+this.enableCharacteristicValue+"</td>"
					+"<td>"+this.disableCharacteristicValue+"</td>"
					+ "<td  style='width:20%'><input type='button' value='Delete'  onclick='deleteCharacteristic(\""+serviceName+"\",\""+this.characteristicName+"\")'/>"+
					"&nbsp;&nbsp;<input type='button' value='Edit'  onclick='editCharacteristic(\""+serviceName+"\",\""+this.characteristicName+"\")'/></td></tr>");

		}
	};

	var sensorMetaData = new SensorMetaData();
 
	function deleteCharacteristic(serviceName,characteristicName)
	{
		var serviceForm=document.getElementById("serviceForm");
		var service=sensorMetaData.getService(serviceName);
		service.deleteCharacteristic(characteristicName);
		sensorMetaData.display(serviceForm.serviceName.value,$("#serviceMap"));

	}
	
	function editCharacteristic(serviceName,characteristicName)
	{
		editService(serviceName);
		var charForm=document.getElementById("charForm");
		var service=sensorMetaData.getService(serviceName);
		var characteristic=service.getCharacteristic(characteristicName);
        charForm.characteristicName.value=characteristic.characteristicName;
        charForm.characteristicUUID.value=characteristic.characteristicUUID;
        charForm.enableCharacteristicUUID.value=characteristic.enableCharacteristicUUID;
        charForm.enableValue.value=characteristic.enableCharacteristicValue;
        charForm.disableValue.value=characteristic.disableCharacteristicValue;
	}

	
	function editService(serviceName)
	{
		var service=sensorMetaData.getService(serviceName);
		var serviceForm=document.getElementById("serviceForm");
		serviceForm.serviceName.value=service.serviceName;
		serviceForm.serviceUUID.value=service.serviceUUID;
	}
	
	function deleteService(serviceName)
	{
		var serviceForm=document.getElementById("serviceForm");
		sensorMetaData.deleteService(serviceName);
		sensorMetaData.display(serviceForm.serviceName.value,$("#serviceMap"));
	}

	function loadSensorData(sensorDataForm)
	{
		 request = $.ajax({
		        url: '<c:url value="/backend/getBlueToothMetaData"/>?sensorKey='+sensorDataForm.sensorName.value,
		        type: "get",
		        async:false
		    }).done(function( msg ) {
		        sensorMetaData.loadSensorMetaData(msg);
		    });
		 sensorMetaData.display(sensorDataForm.sensorName.value,$("#serviceMap"));
	}
	
	function saveSensorMetaData()
	{
		var data="sensorName="+sensorMetaData.name+"&data="+JSON.stringify(sensorMetaData)
		 request = $.ajax({
		        url: '<c:url value="/backend/saveBlueToothMetaData"/>',
		        type: "post",
		        data: data,
		        async:false
		    }).done(function( msg ) {
		        alert( "Data Saved: " + msg );
		    });;
	}
	 
	function addService(serviceForm) {
		var serviceName = serviceForm.serviceName.value;
		var serviceUUID = serviceForm.serviceUUID.value;
		var service =sensorMetaData.getService(serviceName);
		if (service == null) {
			var service = new BluetoothService(serviceName, serviceUUID);
			sensorMetaData.addService(service)
		}else{
			service.uuid=serviceUUID;
		}
		var sensorName=sensorForm.sensorName.value;
		sensorMetaData.display(sensorName,$("#serviceMap"));
		return service;
	}

	function addCharacteristic(characteristicForm) {
		var characteristicName = characteristicForm.characteristicName.value;
		var characteristicUUID = characteristicForm.characteristicUUID.value;
		var enableCharacteristicUUID = characteristicForm.enableCharacteristicUUID.value;
        var enableValue= characteristicForm.enableValue.value;
        var disableValue= characteristicForm.disableValue.value;
		var serviceForm = document.getElementById("serviceForm");
		service = addService(serviceForm);
		var characteristic = new BluetoothCharacteristic(characteristicName,
					characteristicUUID,enableCharacteristicUUID,enableValue,disableValue);
		service.addCharacteristic(characteristic);
		var sensorName=sensorForm.sensorName.value;
		sensorMetaData.display(	sensorName,$("#serviceMap"));
	}
</script>

<title>Bluetooth Entry</title>
</head>
<body syle="margin:40px">
	<div id="serviceMap" style="margin:40px">Sensor Services </div>
	<div><input type="button" value="Save Sensor Meta Data"  onclick="saveSensorMetaData()"/></div>
	<div
		style="border: 2px solid green; margin: 10px; padding: 10px 10px; width: 80%">
		<form id="sensorForm" onclick="return false;">
			Sensor Name: <input type="text" name="sensorName" length="50"  value="SensorTag"/>&nbsp;&nbsp;&nbsp;<input type="button"  value="Load Sensor Data" onclick="loadSensorData(this.form)"/>
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
				<form id="charForm" onclick="return false;">
					Characteristic Name <input type="text" name="characteristicName" size="30" /><br /> 
				    Characteristic UUID <input type="text" name="characteristicUUID" size="50" /><br />
				    Enable Characteristic UUID <input type="text" name="enableCharacteristicUUID" size="50" /><br />
				    Enable Value <input type="text" name="enableValue" size="30" /><br />
				    Disable Value <input type="text" name="disableValue" size="30" /><br />
					<input	type="button" value="Add Characteristic" onclick="addCharacteristic(this.form)" />
			</div>
		</div>
		</form>
	</div>
</body>
</html>