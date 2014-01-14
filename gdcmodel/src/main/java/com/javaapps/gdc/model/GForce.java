package com.javaapps.gdc.model;

import java.io.Serializable;
import java.util.List;

import com.javaapps.gdc.interfaces.CsvWriter;


public class GForce extends GenericData  {

	private static final long serialVersionUID = 1L;
	private float x;
	private float y;
	private float z;
	private final static float VARIANCE = 0.4f;

	public double getGforce() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public GForce() {

	}

	public GForce(String csvString) {
      String props[]=csvString.split("\\,");
      if (props.length < 4){
    	  return;
      }
      this.sampleDate.setTime(Long.parseLong(props[0]));
      this.x=Float.parseFloat(props[1]);
      this.y=Float.parseFloat(props[2]);
      this.z=Float.parseFloat(props[3]);
	}

	public GForce(float x, float y, float z, long sampleDateInMillis) {
		this.sampleDate.setTime( sampleDateInMillis);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "GForceData [sampleDateInMillis=" + sampleDate.getTime() + ", x="
				+ x + ", y=" + y + ", z=" + z + "]";
	}

	public static GForce maximumData(List<GForce> gforceDataList) {
		GForce retGForceData = null;
		double maximumGForce = 0.0;
		if (gforceDataList != null && gforceDataList.size() > 0) {
			for (GForce gforceData : gforceDataList) {
				if (gforceData.getGforce() > maximumGForce) {
					maximumGForce = gforceData.getGforce();
					retGForceData = gforceData;
				}
			}
		}
		return retGForceData;
	}

	public static GForce averageData(List<GForce> gforceDataList) {
		GForce retGForceData = null;
		if (gforceDataList != null && gforceDataList.size() > 0) {
			float xTotal = 0;
			float yTotal = 0;
			float zTotal = 0;
			long minimumTime = gforceDataList.get(0).getSampleDateInMillis();
			long maximumTime = gforceDataList.get(0).getSampleDateInMillis();
			long listSize = gforceDataList.size();
			retGForceData = new GForce();
			for (GForce gforceData : gforceDataList) {
				xTotal += gforceData.x;
				yTotal += gforceData.y;
				zTotal += gforceData.z;
				minimumTime = Math.min(minimumTime,
						gforceData.getSampleDateInMillis());
				maximumTime = Math.max(maximumTime,
						gforceData.getSampleDateInMillis());
			}
			retGForceData.x = xTotal / listSize;
			retGForceData.y = yTotal / listSize;
			retGForceData.z = zTotal / listSize;
			retGForceData.sampleDate.setTime((minimumTime + maximumTime) / 2);
		}
		return retGForceData;
	}

	/* (non-Javadoc)
	 * @see com.javaapps.legaltracker.pojos.CsvWriter#toCSV()
	 */

	public String toCSV() {
		return sampleDate.getTime() + "," +x+ "," + y + "," + z + "\n";
	}

	public boolean isEqual(GForce lastGForceData) {
		if (lastGForceData == null) {
			return false;
		} else {
			return (Math.abs(this.x - lastGForceData.x) < VARIANCE
					&& Math.abs(this.y - lastGForceData.y) < VARIANCE && Math
					.abs(this.z - lastGForceData.z) < VARIANCE);
		}
	}

	
}
