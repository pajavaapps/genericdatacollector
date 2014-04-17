	var SensorMetaData = function(name) {
		this.serviceMetaDataMap = {};
	};
	SensorMetaData.prototype = {
		addService : function(service) {
			this.serviceMetaDataMap[service.serviceUUID] = service;
		},
		deleteService : function(serviceUUID) {
			delete this.serviceMetaDataMap[serviceUUID];
		},
		getService:function(serviceUUID){
			return this.serviceMetaDataMap[serviceUUID]
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
							 ch.enableCharacteristicValue, ch.disableCharacteristicValue,ch.calibration)
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
			this.characteristicMap[characteristic.characteristicUUID] = characteristic;
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
			.append("<tr><td>Characteristic Name</td><td>UUID</td><td>Enable UUID</td><td>Enable Value</td><td>DisableValue</td><td>Is Calibration</td><td style='width:20%'>Actions</td></tr>");
			div.append(tableElement);
			 for(var key in this.characteristicMap)
			  {
				 var service=this.characteristicMap[key];
				 service.display(this.serviceName,tableElement);
			  }
			div.append("<br/>");
		}
	};

	var BluetoothCharacteristic = function(name, uuid,enableUuid,enableValue, disableValue,calibration) {
		this.characteristicName = name;
		this.characteristicUUID = uuid;
		this.enableCharacteristicUUID=enableUuid;
		this.enableCharacteristicValue=enableValue;
		this.disableCharacteristicValue=disableValue;
		this.calibration=calibration;
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
					+"<td>"+this.calibration+"</td>"
					+ "<td  style='width:20%'><input type='button' value='Delete'  onclick='deleteCharacteristic(\""+serviceName+"\",\""+this.characteristicName+"\")'/>"+
					"&nbsp;&nbsp;<input type='button' value='Edit'  onclick='editCharacteristic(\""+serviceName+"\",\""+this.characteristicName+"\")'/></td></tr>");

		}
	};

	var sensorMetaData = new SensorMetaData();
 
	function deleteCharacteristic(serviceUUID,characteristicUUID)
	{
		var serviceForm=document.getElementById("serviceForm");
		var service=sensorMetaData.getService(serviceUUID);
		service.deleteCharacteristic(characteristicUUID);
		sensorMetaData.display(serviceForm.serviceName.value,$("#serviceMap"));

	}
	
	function editCharacteristic(serviceUUID,characteristicUUID)
	{
		editService(serviceName);
		var charForm=document.getElementById("charForm");
		var service=sensorMetaData.getService(serviceUUID);
		var characteristic=service.getCharacteristic(characteristicUUID);
        charForm.characteristicName.value=characteristic.characteristicUUID;
        charForm.characteristicUUID.value=characteristic.characteristicUUID;
        charForm.enableCharacteristicUUID.value=characteristic.enableCharacteristicUUID;
        charForm.enableValue.value=characteristic.enableCharacteristicValue;
        charForm.disableValue.value=characteristic.disableCharacteristicValue;
	}

	
	function editService(serviceUUID)
	{
		var service=sensorMetaData.getService(serviceUUID);
		var serviceForm=document.getElementById("serviceForm");
		serviceForm.serviceName.value=service.serviceName;
		serviceForm.serviceUUID.value=service.serviceUUID;
	}
	
	function deleteService(serviceUUID)
	{
		var serviceForm=document.getElementById("serviceForm");
		sensorMetaData.deleteService(serviceUUID);
		sensorMetaData.display(serviceForm.serviceName.value,$("#serviceMap"));
	}

	function loadSensorData(contextPath,sensorDataForm)
	{
		 request = $.ajax({
		        url: contextPath+'?sensorKey='+sensorDataForm.sensorName.value,
		        type: "get",
		        async:false
		    }).done(function( msg ) {
		        sensorMetaData.loadSensorMetaData(msg);
		    });
		 sensorMetaData.display(sensorDataForm.sensorName.value,$("#serviceMap"));
	}
	
	function saveSensorMetaData(contextPath)
	{
		var sensorForm=document.getElementById("sensorForm");
		var data="sensorName="+sensorForm.sensorName.value+"&data="+JSON.stringify(sensorMetaData)
		//alert (data)
		request = $.ajax({
		        url: contextPath+'/saveBlueToothMetaData',
		        type: "post",
		        data: data,
		        async:false
		    }).done(function( msg ) {
		        alert( "Data Saved: " + msg );
		    });
	}
	 
	function addService(serviceForm) {
		var serviceName = serviceForm.serviceName.value;
		var serviceUUID = serviceForm.serviceUUID.value;
		var service =sensorMetaData.getService(serviceName);
		if (service == null) {
			var service = new BluetoothService(serviceName, serviceUUID);
			sensorMetaData.addService(serviceUUID)
		}else{
			service.serviceUUID=serviceUUID;
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
        var calibration=characteristicForm.calibration.checked;
		var serviceForm = document.getElementById("serviceForm");
		service = addService(serviceForm);
		var characteristic = new BluetoothCharacteristic(characteristicName,
					characteristicUUID,enableCharacteristicUUID,enableValue,disableValue,calibration,calibration);
		service.addCharacteristic(characteristicUUID);
		var sensorName=sensorForm.sensorName.value;
		sensorMetaData.display(	sensorName,$("#serviceMap"));
	}
