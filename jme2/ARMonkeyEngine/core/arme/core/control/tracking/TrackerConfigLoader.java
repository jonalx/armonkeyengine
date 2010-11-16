/* ARMonkeyEngine
 * Copyright (c) 2009
 * Julian Lamprea (jalamprea@yahoo.com.co)
 * Jonny Velez (jonal_vc@yahoo.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package arme.core.control.tracking;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import arme.core.artkp.ARTKPException;
import arme.core.artkp.ARToolKitPlus;
import arme.core.artkp.Tracker;
import arme.core.artkp.TrackerMultiMarker;
import arme.core.artkp.TrackerSingleMarker;
import arme.core.artkp.ARToolKitPlus.IMAGE_PROC_MODE;
import arme.core.artkp.ARToolKitPlus.MARKER_MODE;
import arme.core.artkp.ARToolKitPlus.PIXEL_FORMAT;
import arme.core.artkp.ARToolKitPlus.POSE_ESTIMATOR;
import arme.core.artkp.ARToolKitPlus.UNDIST_MODE;
import arme.core.control.ARConfiguration;

/**
 * <p>
 * Clase que lee un archivo de propiedades para cargar la configuraci&oacute;n
 * de un Tracker. Permite cargar configuraciones por defecto o cargar una
 * configuraci&oacute;n definida por el usuario en un archivo personalizado.
 * </p>
 * <p>El archivo de propiedades debe definir los siguientes par&aacute;metros:
 * <ul>
 * <li>PIXEL_FORMAT=[ ABGR, BGRA, BGR, RGBA, RGB, RGB565, LUM ]</li>
 * <li>UNDIST_MODE=[ NONE, STD, LUT ]</li>
 * <li>IMAGE_PROC_MODE=[ HALF_RES, FULL_RES ]</li>
 * <li>MARKER_MODE=[ TEMPLATE, ID_SIMPLE, ID_BCH ]</li>
 * <li>POSE_ESTIMATOR=[ ORIGINAL, ORIGINAL_CONT, RPP ]</li>
 * <li>BORDER_WIDTH=[ 0.125, 0.250]</li>
 * <li>THRESHOLD=[ int values ]</li>
 * <li>AUTO_THRESHOLD=[ true, false ]</li>
 * </li>
 * </p>
 *
 * @author Jonny Alexander Velez Calle
 * @author Juli&aacute;n Alejandro Lamprea Lamprea
 *
 * @version 1.0
 */
public class TrackerConfigLoader {

	private static final String DEFAULT_CONFIG_ID_SIMPLE = "arme.core.control.tracking.trackerIDSimple";
	private static final String DEFAULT_CONFIG_ID_BCH = "arme.core.control.tracking.trackerIDBCH";
	private static final String DEFAULT_CONFIG_TEMPLATE = "arme.core.control.tracking.trackerTemplates";

	private final Properties prop;

	/**
	 * Este constructor carga la configuraci&oacute;n por defecto para el
	 * tracker especificado seg&uacute;n el tipo de marcador.
	 *
	 * @param markerMode
	 *            Tipo de marcador para escoger la configuraci&oacute;n
	 *            correspondiente
	 * @throws IOException
	 */
	public TrackerConfigLoader(MARKER_MODE markerMode) throws IOException {
		ResourceBundle resource = null;
		switch (markerMode) {
		case ID_BCH:
			resource = ResourceBundle.getBundle(DEFAULT_CONFIG_ID_BCH);
			break;

		case ID_SIMPLE:
			resource = ResourceBundle.getBundle(DEFAULT_CONFIG_ID_SIMPLE);
			break;

		case TEMPLATE:
			resource = ResourceBundle.getBundle(DEFAULT_CONFIG_TEMPLATE);
			break;
		}

		Set<String> keys = resource.keySet();
		if (keys.size() == 8) {
			prop = new Properties();
			for (String key : keys) {
				prop.setProperty(key, resource.getString(key));
			}
		} else
			throw new IOException("Fichero de propiedades no v&aacute;lido");

	}

	/**
	 * Constructor que recibe la ruta del archivo de propiedades que
	 * contendr&aacute; la configuraci&oacute;n para el Tracker.
	 *
	 * @param propertiesFileName
	 * @throws IOException
	 */
	public TrackerConfigLoader(String propertiesFileName) throws IOException {

		prop = new Properties();
		InputStream is = new FileInputStream(propertiesFileName);
		prop.load(is);
	}

	public String getProperty(String key) {
		return prop.getProperty(key);
	}

	public void configureTracker(Tracker tracker) throws ARTKPException {

		tracker.setPixelFormat(getPixelFormat());
		tracker.setBorderWidth(getBorderWidth());
		tracker.setThreshold(getThreshold());
		tracker.activateAutoThreshold(getAutoThreshold());
		tracker.setImageProcessingMode(getImageProcMode());
		tracker.setMarkerMode(getMarkerMode());
		tracker.setPoseEstimator(getPoseEstimator());

		tracker.setUndistortionMode(getUndistMode());
		// tracker.setLoadUndistLUT(getUndistMode() != 0 ? true : false);

	}

	public int getUndistMode() {
		ARToolKitPlus.UNDIST_MODE undistMode = UNDIST_MODE.valueOf(prop
				.getProperty("UNDIST_MODE"));
		return undistMode.ordinal();
	}

	public int getPoseEstimator() {
		ARToolKitPlus.POSE_ESTIMATOR poseEstimator = POSE_ESTIMATOR
				.valueOf(prop.getProperty("POSE_ESTIMATOR"));
		return poseEstimator.ordinal();
	}

	public int getPixelFormat() {
		ARToolKitPlus.PIXEL_FORMAT pixelFormat = PIXEL_FORMAT.valueOf(prop
				.getProperty("PIXEL_FORMAT"));
		return pixelFormat.ordinal();
	}

	public int getImageProcMode() {
		ARToolKitPlus.IMAGE_PROC_MODE imageProcMode = IMAGE_PROC_MODE
				.valueOf(prop.getProperty("IMAGE_PROC_MODE"));

		return imageProcMode.ordinal();
	}

	public int getMarkerMode() {
		ARToolKitPlus.MARKER_MODE markerMode = MARKER_MODE.valueOf(prop
				.getProperty("MARKER_MODE"));

		return markerMode.ordinal();
	}

	public int getThreshold() throws ARTKPException {
		try {
			return Integer.parseInt(prop.getProperty("THRESHOLD"));
		} catch (NumberFormatException e) {
			throw new ARTKPException("Threshold value is not valid");
		}
	}

	public boolean getAutoThreshold() {
		return Boolean.parseBoolean(prop.getProperty("AUTO_THRESHOLD"));
	}

	public float getBorderWidth() throws ARTKPException {
		try {
			return Float.parseFloat(prop.getProperty("BORDER_WIDTH"));

		} catch (NumberFormatException e) {
			throw new ARTKPException("Border width value is not valid");
		}
	}

	public boolean initMultiTracker(TrackerMultiMarker tracker,
			ARConfiguration config, String multiConfigFile)
			throws ARTKPException {

		if (tracker == null)
			throw new ARTKPException("Tracker no inicializado");

		boolean load = tracker.init(config.getCameraParametersFile(),
				multiConfigFile, config.getNearClip(), config.getFarClip());

		if (!load) {
			throw new ARTKPException(
					"Error al cargar los par&aacute;metros de la c&aacute;mara");
		} else
			return load;
	}

	public boolean initSingleTracker(TrackerSingleMarker tracker,
			ARConfiguration config) throws ARTKPException {

		boolean load = tracker.init(config.getCameraParametersFile(), config
				.getNearClip(), config.getFarClip());

		if (!load) {
			throw new ARTKPException(
					"Error al cargar los par&aacute;metros de la c&aacute;mara");
		} else
			return load;

	}
}
