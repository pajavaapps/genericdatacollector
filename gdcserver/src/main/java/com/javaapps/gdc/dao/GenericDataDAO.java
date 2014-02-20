package com.javaapps.gdc.dao;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Repository;

import com.javaapps.gdc.entities.DeviceCheckinData;
import com.javaapps.gdc.model.GForce;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.GenericDataStore;
import com.javaapps.gdc.model.GenericDataUpload;
import com.javaapps.gdc.model.GenericWrapper;
import com.javaapps.gdc.types.DataType;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Repository(value="genericDataDAO")
public class GenericDataDAO {

	public final static int DATE_GRANULARITY = 1;
    private static Logger logger =Logger.getLogger(GenericDataDAO.class);
    
	//private MongoClient mongoClient;
    @Resource
	private DB mongoDb;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	private Pattern pattern = Pattern.compile("\\.");

	public GenericDataDAO() throws UnknownHostException {
		dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York")); 
		dateTimeFormat.setTimeZone(TimeZone.getTimeZone("America/New_York")); 
	}

	public DeviceCheckinData saveGenericData(String normalizedEmail,DataType dataType,String jsonStr) throws JsonParseException,
			JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		GenericDataUpload dataUpload =null;
			dataUpload = objectMapper.readValue(
				jsonStr.getBytes(), GenericDataUpload.class);
		return(saveGenericData(normalizedEmail,dataUpload));
	}

	public DeviceCheckinData saveGenericData(String normalizedEmail,GenericDataUpload totalDataUpload)
			throws JsonParseException, JsonMappingException, IOException {
		String deviceId=(totalDataUpload.getCustomIdentifier()!= null)?totalDataUpload.getCustomIdentifier():totalDataUpload.getDeviceId();
		logger.info("uploading data from device "+deviceId);
		DBCollection clientCollection = getClientDocument(normalizedEmail);
		DBCollection deviceCollection = getDeviceDocument(deviceId,clientCollection);
		DeviceCheckinData deviceCheckinData=new DeviceCheckinData(totalDataUpload.getDeviceId(),
				totalDataUpload.getCustomIdentifier(),"TODO",
				new Date(System.currentTimeMillis()),totalDataUpload.getVersion());
		if (totalDataUpload.getGenericDataList().size() == 0) {
			return deviceCheckinData;
		}
		logger.info("uploading generic data list from device "+deviceId);
		for (GenericDataUpload  genericDataUpload : splitListsByDay(totalDataUpload)) {
			Date uploadDate = genericDataUpload.getGenericDataList().get(0)
					.getSampleDate();
			DBCollection dateCollection = getDailyDocument(genericDataUpload.getDataType(),deviceCollection,
					dateFormat.format(uploadDate));
			DBCollection minuteCollection = getMinuteDocument(dateCollection,
					dateTimeFormat.format(uploadDate));
			ObjectMapper objectMapper = new ObjectMapper();
			String locationDataUploadStr = objectMapper
					.writeValueAsString(new GenericDataStore(genericDataUpload));
			Map<String, String> map = new HashMap<String, String>();
			map.put(String.valueOf(uploadDate.getTime()), locationDataUploadStr);
			DBObject doc = new BasicDBObject(map);
			minuteCollection.insert(doc);
		}
		logger.info("finished uploading data from device "+deviceId);
		return deviceCheckinData;
	}

	private List<GenericDataUpload> splitListsByDay(
			GenericDataUpload totalDataUpload) {
		List<GenericDataUpload> retList = new ArrayList<GenericDataUpload>();
		if (totalDataUpload.getGenericDataList().size() == 0) {
			return retList;
		}
		Date firstDate = totalDataUpload.getGenericDataList().get(0)
				.getSampleDate();
		Date lastDate = totalDataUpload.getGenericDataList()
				.get(totalDataUpload.getGenericDataList().size() - 1)
				.getSampleDate();
		if (firstDate.getDate() == lastDate.getDate()) {
			retList.add(totalDataUpload);
			return retList;
		}
		GenericDataUpload secondaryList = new GenericDataUpload();
		secondaryList.setUploadDate(totalDataUpload.getUploadDate());
		secondaryList.setDeviceId(totalDataUpload.getDeviceId());
		for (int ii = totalDataUpload.getGenericDataList().size() - 1; ii >= 0; ii--) {
			GenericData gforceData = totalDataUpload.getGenericDataList()
					.get(ii);
			if (gforceData.getSampleDate().getDate() != firstDate.getDate()) {
				totalDataUpload.getGenericDataList().remove(ii);
				secondaryList.getGenericDataList().add(0, gforceData);
			}
		}
		retList.add(totalDataUpload);
		return retList;
	}


	public List<GenericData> get(String normalizedEmail,DataType dataType,String deviceId, Date date, int timeGranularity)
			throws JsonParseException, JsonMappingException, IOException {
		List<GenericData> retList = new ArrayList<GenericData>();
		ObjectMapper objectMapper = new ObjectMapper();
		String dailyPrefix = normalizedEmail+"."+deviceId + "."+dataType.getPrefix()+"_" + dateFormat.format(date);
		for (String collectionName : mongoDb.getCollectionNames()) {
			if (collectionName.startsWith(dailyPrefix)) {
				DBCollection dbCollection = mongoDb.getCollection(collectionName);
				Iterator<DBObject> it = dbCollection.find().iterator();
				while (it.hasNext()) {
					DBObject dbObject = it.next();
					String jsonStr = null;
					Set<Entry> entrySet = dbObject.toMap().entrySet();
					for (Entry entry : entrySet) {
						if (!entry.getKey().toString().equals("_id")) {
							jsonStr = entry.getValue().toString();
						}
					}
					if (jsonStr != null) {
						GenericDataStore gforceDataStore = objectMapper
								.readValue(jsonStr, GenericDataStore.class);
						retList.addAll(gforceDataStore.hydrateGenericDataBlob(dataType));
					}
				}
			}
		}
		Collections.sort(retList);
		return retList;
	}

	public void deleteAll() {
		Set<String> collectionNames = mongoDb.getCollectionNames();
		for (String collectionName : collectionNames) {
			DBCollection collection = mongoDb.getCollection(collectionName);
			if (!collectionName.contains("system")) {
				logger.info("Dropping collection "+collection.getFullName());
				collection.drop();
			}
		}

	}

	public void deleteByWildcard(String wildcard) {
		Set<String> collectionNames = mongoDb.getCollectionNames();
		for (String collectionName : collectionNames) {
			DBCollection collection = mongoDb.getCollection(collectionName);
			if (!collectionName.contains("system")&&(collectionName.startsWith(wildcard))) {
				logger.info("Dropping collection "+collection.getFullName());
				collection.drop();
			}
		}
	}
	public void delete(String deviceId) {
		DBCollection coll = mongoDb.getCollection(deviceId);
		logger.info("Dropping collection "+coll.getFullName());
		coll.drop();
	}
	
	private DBCollection getClientDocument(String normalizedEmail) {
		DBCollection dbCollection = mongoDb.getCollection(normalizedEmail);
		return dbCollection;
	}

	private DBCollection getDeviceDocument(String deviceId,DBCollection clientCollection) {
		DBCollection dbCollection = clientCollection.getCollection(deviceId);
		return dbCollection;
	}

	private DBCollection getDailyDocument(DataType dataType,DBCollection deviceCollection,
			String dateString) {
		return deviceCollection.getCollection(dataType.getPrefix()+"_" + dateString);
	}

	private DBCollection getMinuteDocument(DBCollection dateCollection,
			String dateTimeString) {
		return dateCollection.getCollection(dateTimeString);
	}

	public List<GenericWrapper> getDeviceIds() {
		Set<String> deviceIdSet = new HashSet<String>();
		for (String collectionName : mongoDb.getCollectionNames()) {
			deviceIdSet.add(pattern.split(collectionName)[0]);
		}
		List<GenericWrapper> retList = new ArrayList<GenericWrapper>();
		for (String deviceId : deviceIdSet) {
			retList.add(new GenericWrapper(deviceId, deviceId));
		}
		return retList;
	}

	public List<GenericWrapper> getDates(DataType dataType,String deviceId) {
		Set<String> dateSet = new HashSet<String>();
		for (String collectionName : mongoDb.getCollectionNames()) {
			if (!collectionName.startsWith(deviceId)) {
				continue;
			}
			String parts[] = (pattern.split(collectionName));
			if (parts.length > 1) {
				String dateString = parts[1];
				String dataTypePrefix = dataType.getPrefix()+"_";
				if (dateString.startsWith(dataTypePrefix)) {
					dateSet.add(dateString.substring(dataTypePrefix.length()));
				}
			}
		}
		List<GenericWrapper> retList = new ArrayList<GenericWrapper>();
		for (String dateStr : dateSet) {
			retList.add(new GenericWrapper(dateStr, dateStr));
		}
		return retList;
	}

}
