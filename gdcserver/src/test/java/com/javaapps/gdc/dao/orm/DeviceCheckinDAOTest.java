package com.javaapps.gdc.dao.orm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.javaapps.gdc.dao.orm.DeviceCheckinDAO;
import com.javaapps.gdc.entities.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/unittest.xml" })
public class DeviceCheckinDAOTest {

	private static final String TEST_IP = "1.0.0.27";

	@Resource
	DeviceCheckinDAO deviceDataCheckinDAO;

	@Test
	public void testDeviceCheckins() {
		try {
			/*assertNotNull(deviceDataCheckinDAO);
			DeviceCheckinData deviceCheckinData = new DeviceCheckinData(
					"unittest", "license",TEST_IP, new Date(), 1);
			deviceDataCheckinDAO.save(deviceCheckinData);
			assertEquals(1, deviceDataCheckinDAO.findAll().size());
			deviceCheckinData = new DeviceCheckinData("unittest", "license",TEST_IP,
					new Date(), 2);
			deviceDataCheckinDAO.save(deviceCheckinData);
			List<DeviceCheckinData>checkinList=new ArrayList(deviceDataCheckinDAO.findAll());
			assertEquals(1,checkinList.size());
			assertEquals("license",checkinList.get(0).getCustomIdentifier());
			assertEquals(TEST_IP,checkinList.get(0).getExternalIP());
			for ( int ii =0;ii<50;ii++)
			{
				checkinList=new ArrayList(deviceDataCheckinDAO.findAll());
				assertEquals(1,checkinList.size());
			}*/
		} catch (Exception ex) {
			fail("test failed because " + ex.getMessage());
		}
	}

}
