if (GenericDataCollectorView == undefined) {
	var GenericDataCollectorView = function() {
		$("#progressBar").hide();
		this.overlay = new google.maps.OverlayView();
		$("#mergeDataButton").click(this.getMergedData);
		$("#deviceSelector").change(this.setDateList);
		this.initializeMap(40.0, -80);
		this.markers = [];
		this.polylines = [];
	};

}

$
		.extend(
				GenericDataCollectorView.prototype,
				{

					initializeMap : function(latitude, longitude) {
						var myOptions = {
							center : new google.maps.LatLng(latitude, longitude),
							zoom : 11,
							mapTypeId : google.maps.MapTypeId.ROADMAP
						};
						var markerOptions = {
							center : new google.maps.LatLng(latitude, longitude),
							zoom : 11,
							mapTypeId : google.maps.MapTypeId.ROADMAP
						};

						var divElem = document
								.getElementById("genericdatacollector_map");
						this.map = new google.maps.Map(divElem, myOptions);
						this.overlay.draw = function() {
						};
						this.overlay.setMap(this.map);
					},

					setDeviceIds : function() {
						$.getJSON('backend/getDeviceIds', function(data) {
							$.each(data, function(i, item) {
								if (item.value != "system") {
									$('#deviceSelector').append($('<option>', {
										value : item.value,
										text : item.key,
									}));
								}
							});
						});
						this.setDateList();
					},
					setDateList : function() {
						var deviceId = $('#deviceSelector').find(":selected")
								.text();
						$.getJSON('backend/getDates?deviceId=' + deviceId,
								function(data) {
									$('#dateSelector').empty();
									$.each(data, function(i, item) {
										if (item.value.match('^2')) {
											$('#dateSelector').append(
													$('<option>', {
														value : item.value,
														text : item.key
													}));
										}
									});
								});
					},
					getLocationData : function() {
						$("#divData").hide();
						$("#genericdatacollector_map").hide();
						var deviceId = $('#deviceSelector').find(":selected")
								.text();
						var dateStr = $('#dateSelector').find(":selected")
								.text();
						var urlStr = "backend/getLocationData?deviceId="
								+ deviceId + "&dateStr=" + dateStr;
						$("#dataTable").empty();
						$('#dataTable')
								.append(
										'<tr><td>Sample Date</td><td> Latitude</td><td>Longitude</td><td>Speed</td><td>Bearing</td></tr>');
						$.getJSON(urlStr, function(data) {
							$.each(data, function(i, item) {
								var latitude = item.latitude;
								var longitude = item.longitude;
								$('#dataTable').append(
										'<tr><td>' + new Date(item.sampleDate)
												+ '</td><td> ' + latitude
												+ '</td><td> ' + longitude
												+ '</td><td> ' + item.speed
												+ '</td><td> ' + item.bearing
												+ '</td></tr>');
							});
						});
						$("#divData").show();

					},
					getGForceData : function() {
						$("#divData").hide();
						$("#genericdatacollector_map").hide();
						var deviceId = $('#deviceSelector').find(":selected")
								.text();
						var dateStr = $('#dateSelector').find(":selected")
								.text();
						var urlStr = "backend/getGForceData?deviceId="
								+ deviceId + "&dateStr=" + dateStr;
						$("#dataTable").empty();
						$('#dataTable')
								.append(
										'<tr><td>Sample Date</td><td> X Component</td><td>YComponent</td><td>Z Component</td>></tr>');
						$
								.getJSON(
										urlStr,
										function(data) {
											$
													.each(
															data,
															function(i, item) {
																$('#dataTable')
																		.append(
																				'<tr><td>'
																						+ new Date(
																								item.sampleDateInMillis)
																						+ '</td><td> '
																						+ item.x
																						+ '</td><td> '
																						+ item.y
																						+ '</td><td> '
																						+ item.z
																						+ '</td></tr>');
															});

										});
						$("#divData").show();
					},
					getMergedData : function() {
						$("#progressBar").show();
						var deviceId = $('#deviceSelector').find(":selected")
								.text();
						var minimumGForce = parseFloat($("#gforceSelect").val());
						var dateStr = $('#dateSelector').find(":selected")
								.text();
						var urlStr = "backend/getMergedDataObjects?deviceId="
								+ deviceId + "&dateStr=" + dateStr;
						for (var ii = 0; ii < window.genericdatacollectorView.polylines.length; ii++) {
							window.genericdatacollectorView.polylines[ii].setMap(null);
						}
						var points = [];
						$("#divData").hide();
						$("#genericdatacollector_map").show();
						$("#dataTable").empty();
						$
								.ajax({
									type : "GET",
									url : urlStr,
									success : function(data) {
										if (data.length == 0) {
											$("#progressBar").hide();
											alert("No data found for that vehicle and date");
											return;
										}
										var minimumLongitude = 300;
										var minimumLatitude = 100;
										var maximumLongitude = -300;
										var maximumLatitude = -100;
										$.each(data, function(i, item) {
											var latitude = item.latitude;
											var longitude = item.longitude;
											if (latitude > maximumLatitude)
												maximumLatitude = latitude;
											if (latitude < minimumLatitude)
												minimumLatitude = latitude;
											if (longitude > maximumLongitude)
												maximumLongitude = longitude;
											if (longitude < minimumLongitude)
												minimumLongitude = longitude;

										});
										var averageLatitude = (minimumLatitude + maximumLatitude) / 2;
										var averageLongitude = (minimumLongitude + maximumLongitude) / 2;
										window.genericdatacollectorView.map
												.panTo(new google.maps.LatLng(
														averageLatitude,
														averageLongitude));
										for (var ii = 0; ii < window.genericdatacollectorView.markers.length; ii++) {
											window.genericdatacollectorView.markers[ii]
													.setMap(null);
										}
										var previousSampleDate = null;
										window.genericdatacollectorView.markers.length = 0;
							               var previousPoint=null;
										$
												.each(
														data,
														function(i, item) {
															if (previousSampleDate != null
																	&& (item.sampleDate - previousSampleDate) > 1000 * 60 * 3) {
																var polyline = new google.maps.Polyline(
																		{
																			path : points,
																			geodesic : true,
																			strokeColor : '#009900',
																			strokeOpacity : 1.0,
																			strokeWeight : 2
																		});
																polyline
																		.setMap(window.genericdatacollectorView.map);
																window.genericdatacollectorView.polylines
																		.push(polyline);
																points = [];
															}
  															if (i % 5 == 0) {
																var point=new google.maps.LatLng(item.latitude,item.longitude)
																if ( (previousPoint== null) ||(!(point.nb==previousPoint.nb&&point.ob==previousPoint.ob)))
																{	
																points.push(point);
																previousPoint=point
																}
															}
															if (item.gforce > minimumGForce) {
																var googleLatLng = new google.maps.LatLng(
																		item.latitude,
																		item.longitude);
																var marker = new google.maps.Marker(
																		{
																			position : googleLatLng,
																			icon : "images/red_dot.png",
																			title : "Date: "
																					+ new Date(
																							item.sampleDate)
																					+ " Latitude: "
																					+ item.latitude
																					+ " Longitude: "
																					+ item.longitude
																					+ " Speed: "
																					+ item.speed
																					+ " Bearing: "
																					+ item.bearing
																					+ " Gforce: "
																					+ item.gforce
																		});
																marker
																		.setMap(window.genericdatacollectorView.map);
																window.genericdatacollectorView.markers
																		.push(marker);
															}
															previousSampleDate = item.sampleDate;
														});

										var polyline = new google.maps.Polyline(
												{
													path : points,
													geodesic : true,
													strokeColor : '#009900',
													strokeOpacity : 1.0,
													strokeWeight : 2
												});
										polyline
												.setMap(window.genericdatacollectorView.map);
										window.genericdatacollectorView.polylines
												.push(polyline);
										$("#dataTable").empty();
										$('#dataTable')
												.append(
														'<tr class="r-grid"><th class="r-grid">Sample Date</th><th class="r-grid"> Latitude</th><th class="r-grid">Longitude</th><th class="r-grid">Speed</th><th class="r-grid">Bearing</th><th class="r-grid">X</th><th class="r-grid">Y</th><th class="r-grid">Z</th><th class="r-grid">GForce</th></tr>');
										$
												.each(
														data,
														function(i, item) {
															if (item.gforce >= minimumGForce){
																var latitude = item.latitude;
															var longitude = item.longitude;
															var trClassStr = "class='odd'"
															if (i % 2 == 1) {
																trClassStr = 'class="even"';
															}

															$('#dataTable')
																	.append(
																			'<tr '
																					+ trClassStr
																					+ '><td class="r-grid">'
																					+ new Date(
																							item.sampleDate)
																					+ '</td><td class="r-grid"> '
																					+ latitude
																					+ '</td><td class="r-grid"> '
																					+ longitude
																					+ '</td><td class="r-grid"> '
																					+ item.speed
																					+ '</td><td class="r-grid"> '
																					+ item.bearing
																					+ '</td><td class="r-grid"> '
																					+ item.x
																					+ '</td><td class="r-grid"> '
																					+ item.y
																					+ '</td><td class="r-grid"> '
																					+ item.z
																					+ '</td><td class="r-grid"> '
																					+ item.gforce
																					+ '</td></tr>');
															}
														});
										$("#progressBar").hide();
									}
								});
						$("#divData").show();
					}
				});

if (GenericDataCollectorAdminView == undefined) {
	var GenericDataCollectorAdminView = function() {
		var urlStr = "backend/getDeviceCheckinData";
		$("#deviceTable").empty();
		$('#deviceTable')
				.append(
						'<tr><td>Device Id</td><td>License Plate</td><td>External IP</td><td>Last Upload Date</td><td>Version</td></tr>');
		$.getJSON(urlStr, function(data) {
			$.each(data, function(i, item) {
				var trClassStr = "class='odd'"
					if (i % 2 == 1) {
						trClassStr = 'class="even"';
					}
				$('#deviceTable').append(
						'<tr '+ trClassStr+'><td>' + item.identifier + '</td><td> '+item.customIdentifier+'</td><td>'
						       +item.externalIP+'</td><td>'
								+ new Date(item.lastUploadDate) + '</td><td> '
								+ item.version + '</td></tr> ');

			});

		});
		$('#deviceTable').append('</table>');
	}
}