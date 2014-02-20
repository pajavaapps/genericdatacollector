package com.javaapps.gdc.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.javaapps.gdc.dao.GenericDataDAO;
import com.javaapps.gdc.dao.orm.DeviceCheckinDAO;
import com.javaapps.gdc.entities.DeviceCheckinData;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.GenericWrapper;
import com.javaapps.gdc.probes.BlueToothLEMetaData;
import com.javaapps.gdc.probes.BlueToothLEMetaDataManager;
import com.javaapps.gdc.types.DataType;

@Controller
public class GenericDataController {

	private Date lastAPKUpload;

	private DeviceCheckinDAO deviceCheckinDAO;

	private int currentVersion = 1;

	private static String PREFIX = "javaapps";

	private static Logger logger = Logger
			.getLogger(GenericDataController.class);

	@Resource
	private GenericDataDAO genericDataDAO;

	@Resource
	public void setDeviceCheckinDAO(DeviceCheckinDAO deviceCheckinDAO) {
		this.deviceCheckinDAO = deviceCheckinDAO;
	}

	@RequestMapping(value = "/uploadGenericData", method = RequestMethod.POST)
	public @ResponseBody
	StatusReturn uploadGenericData(
			@RequestParam(value = "normalizedEmail", required = true) String normalizedEmail,
			@RequestParam(value = "dataType", required = true) DataType dataType,
			@RequestParam(value = "data", required = true) String data)
			throws JsonParseException, JsonMappingException, IOException {
		try {
			logger.info("logging data " + data);
			DeviceCheckinData deviceCheckinData = genericDataDAO
					.saveGenericData(normalizedEmail, dataType, data);
			// this.deviceCheckinDAO.save(deviceCheckinData);
			logger.info("logged data ");
		} catch (Throwable e) {
			logger.error("Unable to store checkin data on database because "
					+ e.getMessage());
		}
		return (new StatusReturn(1));
	}

	@RequestMapping(value = "/getCurrentVersion", method = RequestMethod.GET)
	public @ResponseBody
	String getCurrentVersion(HttpServletRequest request)
			throws FileNotFoundException {
		String realPath = request.getRealPath("versions");
		File file = new File(realPath + "/version.txt");
		if (file.exists()
				&& (lastAPKUpload == null || file.lastModified() > lastAPKUpload
						.getTime())) {
			Scanner scanner = new Scanner(file);
			currentVersion = scanner.nextInt();
			lastAPKUpload = new Date(file.lastModified());
		}
		return String.valueOf(currentVersion);
	}

	@RequestMapping(value = "/setCurrentVersion", method = RequestMethod.GET)
	public @ResponseBody
	String setCurrentVersion(
			HttpServletRequest request,
			@RequestParam(value = "currentVersion", required = true) String currentVersion)
			throws IOException {
		FileOutputStream fos = null;
		String realPath = request.getRealPath("versions");
		try {
			Integer.parseInt(currentVersion);
			File file = new File(realPath + "/version.txt");
			fos = new FileOutputStream(file);
			fos.write((currentVersion + "\n").getBytes());
		} catch (Exception ex) {
			return "FAILURE because " + ex.getMessage();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
		return "SUCCESS";
	}

	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	public @ResponseBody
	String uploadAPK(@RequestParam("uploadFile") MultipartFile uploadedFile,
			HttpServletRequest request) throws Exception {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = uploadedFile.getInputStream();
			String realPath = request.getRealPath("versions");
			File dir = new File(realPath);
			if (!dir.exists()) {
				if (!dir.mkdir()) {
					throw new Exception("Unable to create versions directory "
							+ realPath);
				}
			}
			File newFile = new File(dir, "gdcmobile.apk");
			outputStream = new FileOutputStream(newFile);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return "FAILURE because " + ex.getMessage();
		}
		return "SUCCESS";
	}

	@RequestMapping(value = "/getGenericData", method = RequestMethod.GET)
	public @ResponseBody
	String getGenericData(
			@RequestParam(value = "normalizedEmail", required = true) String normalizedEmail,
			@RequestParam(value = "dataType", required = true) DataType dataType,
			@RequestParam(required = true, value = "deviceId") String deviceId,
			@RequestParam(required = true) String dateStr) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		List<GenericData> genericDataList = genericDataDAO.get(normalizedEmail,
				dataType, deviceId, dateFormat.parse(dateStr),
				GenericDataDAO.DATE_GRANULARITY);
		for (GenericData genericData : genericDataList) {
			stringBuilder.append(genericData.toCSV()).append("<BR/>\n");
		}
		return stringBuilder.toString();
	}

	@RequestMapping(value = "/getDeviceCheckinData", method = RequestMethod.GET)
	public @ResponseBody
	List<DeviceCheckinData> getDeviceCheckinData() {
		logger.info("Getting device checkin data");
		try {
			List<DeviceCheckinData> list = new ArrayList<DeviceCheckinData>(
					deviceCheckinDAO.findAll());
			logger.info("Retrieved device checkin data");
			return list;
		} catch (Throwable ex) {
			logger.error(
					"Could not get device checkin list because "
							+ ex.getMessage(), ex);
			return null;
		}
	}

	@RequestMapping(value = "/getExternalIP", method = RequestMethod.GET)
	public @ResponseBody
	String getExternalIP(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder(PREFIX);
		String addr = request.getRemoteAddr();
		int port = request.getRemotePort();
		sb.append(",").append(addr).append(",").append(port);
		return sb.toString();
	}

	@RequestMapping(value = "/getDates", method = RequestMethod.GET)
	public @ResponseBody
	List<GenericWrapper> getDates(
			@RequestParam(value = "dataType", required = true) DataType dataType,
			@RequestParam(required = true, value = "deviceId") String deviceId) {
		return genericDataDAO.getDates(dataType, deviceId);
	}

	@RequestMapping(value = "/getBlueToothMetaData", method = RequestMethod.GET)
	public @ResponseBody
	BlueToothLEMetaData getBlueToothMetaData(
			@RequestParam(value = "sensorKey", required = true) String sensorKey) {
		BlueToothLEMetaData blueToothLEMetaData = null;
		try {
			blueToothLEMetaData = BlueToothLEMetaDataManager.get(sensorKey);
		} catch (Exception ex) {
			logger.error("Could not find bluetooth meta data for key "
					+ sensorKey + " because " + ex.getMessage(), ex);
		}
		if (blueToothLEMetaData == null) {
			blueToothLEMetaData = new BlueToothLEMetaData();
		}
		return blueToothLEMetaData;
	}

	@RequestMapping(value = "/saveBlueToothMetaData", method = RequestMethod.POST)
	public void saveBlueToothMetaData(
			@RequestParam(value = "blueToothMetaData", required = true) BlueToothLEMetaData blueToothMetaData) {
	}

	@RequestMapping(value = "/blueToothEntry", method = RequestMethod.GET)
	public String getBlueToothEntry() {
		return "blueToothEntry";
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String getAdmin() {
		return "admin";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String getUpload() {
		return "upload";
	}

	@RequestMapping(value = "/graph", method = RequestMethod.GET)
	public String getGraph() {
		return "graph";
	}

	public class StatusReturn {
		private int statusCode;

		public StatusReturn(int statusCode) {
			this.statusCode = statusCode;
		}

		public int getStatusCode() {
			return statusCode;
		}

	}

}
