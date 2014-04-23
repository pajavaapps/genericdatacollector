package com.javaapps.gdc.uploader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.javaapps.gdc.factories.GenericDataFactory;
import com.javaapps.gdc.io.DataFile;
import com.javaapps.gdc.model.FileResultMap;
import com.javaapps.gdc.model.FileResultMapsWrapper;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.pojos.Config;
import com.javaapps.gdc.pojos.DeviceMetaData;
import com.javaapps.gdc.types.DataType;
import com.javaapps.gdc.utils.MockHttpClientFactory;
import com.javaapps.gdc.utils.WifiConnectionTester;

@RunWith(RobolectricTestRunner.class)
public class DataUploaderTest {

	private List<GenericData> locationDataList = new ArrayList<GenericData>();
	private static File testFileDir = new File("unitTestDir");
	private static long systemTimeInMillis = System.currentTimeMillis();
	private static ProtocolVersion protocolVersion;

	@BeforeClass
	public static void setupBeforeClass() {
		try {
			if (!testFileDir.exists()) {
				testFileDir.mkdir();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("LocationDataUploaderHandlerTest setup failed because "
					+ ex.getMessage());
		}
	}

	@Before
	public void setupBeforeTest() {
		protocolVersion = new ProtocolVersion("HTTP", 1, 2);
		WifiConnectionTester.testMode = true;
		for (File file : testFileDir.listFiles()) {
			file.delete();
		}
		DeviceMetaData deviceMetaData = new DeviceMetaData();
		deviceMetaData.setDataEndpoint("http://boguswebsite.go");
		Config.setConfigInstance(deviceMetaData);
		Config.getInstance().setFilesDir(testFileDir);
		FileResultMapsWrapper.getInstance().getFileResultMaps().clear();
	}

	private void createLocationCSVFile(FileResult fileResult,
			int numberOfSamples, int timeDelta) throws FileNotFoundException,
			IOException {
		if (fileResult.file.exists()) {
			fileResult.file.delete();
		}
		FileOutputStream fos = new FileOutputStream(fileResult.file);
		for (int ii = 0; ii < numberOfSamples; ii = ii + timeDelta) {
			GenericData location = GenericDataFactory.createGenericData(
					DataType.GPS, "123,40.0,-80.0, 10., 20.0, 10.0,"
							+ systemTimeInMillis + ii);
			fos.write(location.toCSV().getBytes());
		}
		fos.flush();
		fos.close();
	}

	@Before
	public void setup() {
		try {
		} catch (Exception ex) {
			fail("Unable to setup test because " + ex.getMessage());
		}
	}

	private void testResults(FileResult fileResult) {
		Map<Integer, Integer> resultMap = getResultMap(fileResult);
		assertTrue("resultMap is empty", resultMap.size() > 0);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		System.out.println(resultMap);
		for (Entry<Integer, Integer> entry : resultMap.entrySet()) {
			assertTrue("expecting " + fileResult.result
					+ " status code but was " + entry.getValue(),
					entry.getValue() == fileResult.result);
		}
		if (fileResult.fileShouldBeDeleted) {
			assertFalse(fileResult.file.exists());
		}
	}

	private Map<Integer, Integer> getResultMap(FileResult fileResult) {
		System.out.println("Get result map for fileResults " + fileResult);
		System.out.println("Get result map for file "
				+ fileResult.file.getAbsolutePath());
		FileResultMapsWrapper wrapper = FileResultMapsWrapper.getInstance();
		assertNotNull("wrapper is null", wrapper);
		Map<String, FileResultMap> resultMaps = wrapper.getFileResultMaps();
		assertNotNull("Cant get resultMaps " + resultMaps + " for file "
				+ fileResult.file.getAbsolutePath(), resultMaps);
		assertTrue("resultMaps contains no maps " + wrapper + " for file "
				+ fileResult.file.getAbsolutePath(), resultMaps.size() > 0);
		FileResultMap fileResultMap = resultMaps.get(fileResult.file
				.getAbsolutePath());
		assertNotNull("Cant get file result map " + resultMaps, fileResultMap);
		Map<Integer, Integer> resultMap = fileResultMap.getResultMap();
		assertNotNull("Cant get result map " + fileResultMap, resultMap);
		return resultMap;
	}

	@Test
	public void uploadDataResultMapSizeTest() throws ClientProtocolException,
			IOException {
		try {
			FileResult fileResult = new FileResult("gps", -1, false);
			createLocationCSVFile(fileResult, 100, 1);
			DataUploader dataUploader = new DataUploader();
			dataUploader.setHttpClientFactory(new MockHttpClientFactory(
					protocolVersion, new int[] { 400 }, "URL not found"));
			Config.getInstance().setUploadBatchSize(13);
			dataUploader.uploadData();
			Map<Integer, Integer> resultMap = getResultMap(fileResult);
			assertTrue("expecting 8 but was " + resultMap.size(),
					resultMap.size() == 8);
			Config.getInstance().setUploadBatchSize(10);
			dataUploader.uploadData();
			assertTrue("expecting 10 but was " + resultMap.size(),
					resultMap.size() == 10);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("uploadDataResultMapSizeTest failed because "
					+ ex.getMessage());
		}
	}

	@Test
	public void uploadDataResultMapWithBadStatusTest()
			throws ClientProtocolException, IOException {
		try {
			FileResult fileResult = new FileResult("gps", 400, false);
			createLocationCSVFile(fileResult, 100, 1);
			DataUploader dataUploader = new DataUploader();
			dataUploader.setHttpClientFactory(new MockHttpClientFactory(
					protocolVersion, new int[] { 400 }, "URL not found"));
			Config.getInstance().setUploadBatchSize(10);
			dataUploader.uploadData();
			testResults(fileResult);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("uploadDataResultMapWithBadStatusTest because "
					+ ex.getMessage());
		}
	}

	@Test
	public void uploadDataResultMapWithBadStatusThanGoodStatsTest()
			throws ClientProtocolException, IOException {
		try {
			List<FileResult> fileResultList = new ArrayList<FileResult>();
			fileResultList.add(new FileResult("gps", 400, true));
			fileResultList.add(new FileResult("gps", 200, true));
			DataUploader dataUploader = new DataUploader();
			for (FileResult fileResult : fileResultList) {
				createLocationCSVFile(fileResult, 100, 1);
			}

			for (FileResult fileResult : fileResultList) {
				dataUploader.setHttpClientFactory(new MockHttpClientFactory(
						protocolVersion, new int[] { fileResult.result },
						"URL not found"));
				Config.getInstance().setUploadBatchSize(10);
				dataUploader.uploadData();
			}

			for (FileResult fileResult : fileResultList) {
				fileResult.result = 200;
				testResults(fileResult);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("uploadDataResultMapWithBadStatusThanGoodStatsTest failed because "
					+ ex.getMessage());
		}
	}

	/**
	 * This test must be run last because it deletes the test file
	 */
	@Test
	public void uploadDataResultMapWithGoodStatusLastTest()
			throws ClientProtocolException, IOException {
		try {
			FileResult fileResult = new FileResult("gps", 200, true);
			createLocationCSVFile(fileResult, 100, 1);
			DataUploader dataUploader = new DataUploader();
			dataUploader.setHttpClientFactory(new MockHttpClientFactory(
					protocolVersion, new int[] { 200 }, "OK"));
			Config.getInstance().setUploadBatchSize(10);
			dataUploader.uploadData();
			testResults(fileResult);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("uploadDataResultMapWithBadStatusThanGoodStatsTest failed because "
					+ ex.getMessage());
		}

	}

	class FileResult {
		String fileName;
		int result;
		File file;
		boolean fileShouldBeDeleted;

		public FileResult(String fileName, int result,
				boolean fileShouldBeDeleted) {
			super();
			this.fileName = fileName;
			this.fileShouldBeDeleted = fileShouldBeDeleted;
			this.result = result;
			file = new File(testFileDir.getPath() + "/" + fileName
					+ DataFile.ARCHIVE_STRING + ".obj");
		}

	}
}
