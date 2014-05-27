package com.javaapps.gdc.services;

import java.util.ArrayList;
import java.util.List;

import com.javaapps.gdc.model.BluetoothData;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.GenericDataUpload;
import com.javaapps.gdc.model.PostProcessedBluetoothData;
import com.javaapps.gdc.utils.ByteConverter;

public class SensorTagPostProcessor extends PostProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.javaapps.gdc.services.PostProcessor#postProcess(com.javaapps.gdc.
	 * model.GenericData)
	 */
	@Override
	public GenericData convertToPostProcessedData(GenericData genericData) {
		PostProcessedBluetoothData processedData = null;
		BluetoothData bluetoothData = (BluetoothData) genericData;
		if (bluetoothData.getServiceName().equalsIgnoreCase("humidity")) {
			processedData = convertToHumidity(bluetoothData);
		} else if (bluetoothData.getServiceName().equalsIgnoreCase("barometer")) {
			processedData = convertToBarometer(bluetoothData);
		}
		return processedData;
	}

	private PostProcessedBluetoothData convertToBarometer(
			BluetoothData bluetoothData) {
		int calibrationCoefficients[] = extractCalibrationCoefficients(bluetoothData
				.getCalibration());
		double barometer = this.extractBarometer(bluetoothData.getData(),
				calibrationCoefficients);
		PostProcessedBluetoothData postProcessedBluetoothData = new PostProcessedBluetoothData();
		postProcessedBluetoothData.setSampleDate(bluetoothData.getSampleDate());
		postProcessedBluetoothData.setValue(barometer);
		return postProcessedBluetoothData;
	}

	private PostProcessedBluetoothData convertToHumidity(
			BluetoothData bluetoothData) {
		double humidity = extractHumidity(bluetoothData.getData());
		PostProcessedBluetoothData postProcessedBluetoothData = new PostProcessedBluetoothData();
		postProcessedBluetoothData.setSampleDate(bluetoothData.getSampleDate());
		postProcessedBluetoothData.setValue(humidity);
		return postProcessedBluetoothData;
	}

	public static double extractAmbientTemperature(
			byte ambientTemperatureBytes[]) {
		int offset = 2;
		return ByteConverter.getUnsignedInteger(offset, 2,
				ambientTemperatureBytes) / 128.0;
	}

	public static double extractTargetTemperature(
			byte targetTemperatureBytes[], double ambient) {
		Integer twoByteValue = ByteConverter.getSignedInteger(0, 2,
				targetTemperatureBytes);

		double Vobj2 = twoByteValue.doubleValue();
		Vobj2 *= 0.00000015625;

		double Tdie = ambient + 273.15;

		double S0 = 5.593E-14; // Calibration factor
		double a1 = 1.75E-3;
		double a2 = -1.678E-5;
		double b0 = -2.94E-5;
		double b1 = -5.7E-7;
		double b2 = 4.63E-9;
		double c2 = 13.4;
		double Tref = 298.15;
		double S = S0
				* (1 + a1 * (Tdie - Tref) + a2 * Math.pow((Tdie - Tref), 2));
		double Vos = b0 + b1 * (Tdie - Tref) + b2 * Math.pow((Tdie - Tref), 2);
		double fObj = (Vobj2 - Vos) + c2 * Math.pow((Vobj2 - Vos), 2);
		double tObj = Math.pow(Math.pow(Tdie, 4) + (fObj / S), .25);

		return tObj - 273.15;
	}

	public static double extractHumAmbientTemperature(
			byte humidityAmbientTemperatureBytes[]) {
		int rawT = ByteConverter.getSignedInteger(0, 2,
				humidityAmbientTemperatureBytes);
		return -46.85 + 175.72 / 65536 * (double) rawT;
	}

	public static double extractHumidity(byte humidityBytes[]) {
		int a = ByteConverter.getUnsignedInteger(2, 2, humidityBytes)
				.intValue();
		a = a - (a % 4);
		return ((-6f) + 125f * (a / 65535f));
	}

	public static int[] extractCalibrationCoefficients(byte calibrationBytes[]) {
		int[] coefficients = new int[8];

		coefficients[0] = ByteConverter.getUnsignedInteger(0, 2,
				calibrationBytes).intValue();
		coefficients[1] = ByteConverter.getUnsignedInteger(2, 2,
				calibrationBytes).intValue();
		coefficients[2] = ByteConverter.getUnsignedInteger(4, 2,
				calibrationBytes).intValue();
		coefficients[3] = ByteConverter.getUnsignedInteger(6, 2,
				calibrationBytes).intValue();
		coefficients[4] = ByteConverter
				.getSignedInteger(8, 2, calibrationBytes);
		coefficients[5] = ByteConverter.getSignedInteger(10, 2,
				calibrationBytes);
		coefficients[6] = ByteConverter.getSignedInteger(12, 2,
				calibrationBytes);
		coefficients[7] = ByteConverter.getSignedInteger(14, 2,
				calibrationBytes);
		return coefficients;
	}

	public static double extractBarTemperature(
			final byte rawTemperatureBytes[],
			final int calibrationCoefficients[]) {
		// c holds the calibration coefficients

		int rawTemperatureFromSensor; // Temperature raw value from sensor
		double actualTemperatureInCentigrade; // Temperature actual value in
												// unit centi degrees celsius

		rawTemperatureFromSensor = ByteConverter.getSignedInteger(0, 2,
				rawTemperatureBytes);

		actualTemperatureInCentigrade = (100 * (calibrationCoefficients[0]
				* rawTemperatureFromSensor / Math.pow(2, 8) + calibrationCoefficients[1]
				* Math.pow(2, 6)))
				/ Math.pow(2, 16);

		return actualTemperatureInCentigrade / 100;
	}

	public static double extractBarometer(final byte barometerValues[],
			final int[] calibrationCoefficients) {
		// c holds the calibration coefficients

		int rawTemperatureFromSensor; // Temperature raw value from sensor
		int rawPressureFromSensor; // Pressure raw value from sensor
		double S; // Interim value in calculation
		double O; // Interim value in calculation
		double actualPressureInPascal; // Pressure actual value in unit Pascal.
		rawTemperatureFromSensor = ByteConverter.getSignedInteger(0, 2,
				barometerValues);
		rawPressureFromSensor = ByteConverter.getUnsignedInteger(2, 2,
				barometerValues).intValue();
		S = calibrationCoefficients[2]
				+ calibrationCoefficients[3]
				* rawTemperatureFromSensor
				/ Math.pow(2, 17)
				+ ((calibrationCoefficients[4] * rawTemperatureFromSensor / Math
						.pow(2, 15)) * rawTemperatureFromSensor)
				/ Math.pow(2, 19);
		O = calibrationCoefficients[5]
				* Math.pow(2, 14)
				+ calibrationCoefficients[6]
				* rawTemperatureFromSensor
				/ Math.pow(2, 3)
				+ ((calibrationCoefficients[7] * rawTemperatureFromSensor / Math
						.pow(2, 15)) * rawTemperatureFromSensor)
				/ Math.pow(2, 4);
		actualPressureInPascal = (S * rawPressureFromSensor + O)
				/ Math.pow(2, 14);

		// Convert pascal to in. Hg
		double pressureInHg = actualPressureInPascal * 0.000296;

		return pressureInHg;
	}

}
