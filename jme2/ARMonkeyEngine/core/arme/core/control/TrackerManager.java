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
package arme.core.control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import arme.core.artkp.ARTKPException;
import arme.core.artkp.ARToolKitPlus;
import arme.core.artkp.TrackerMultiMarker;
import arme.core.artkp.TrackerSingleMarker;
import arme.core.artkp.ARToolKitPlus.MARKER_MODE;
import arme.core.control.marker.ARMarker;
import arme.core.control.tracking.TrackerConfigLoader;

/**
 * Controlador de Tracking para registrar los marcadores, realizar el
 * procesamiento en cada frame y actualizar las matrices de
 * transformaci&oacute;n.<br>
 * Este controlador utiliza HashMaps para registrar cada uno de los diferentes
 * tipos de marcadores que soporta ARToolKitPlus, cada marcador es registrado en
 * un HashMap seg&uacute;n el tipo y por cada HashMap se crea una instancia de
 * un TrackerSingleMarker de ARToolKitPlus.
 *
 * @author Jonny Alexander Velez Calle
 * @author Juli&aacute;n Alejandro Lamprea Lamprea
 *
 * @version 1.0
 */
final class TrackerManager {

	private TrackerSingleMarker templateTracker;
	private TrackerSingleMarker idSimpleTracker;
	private TrackerSingleMarker idBchTracker;
	private ArrayList<TrackerMultiMarker> multiTrackers;

	private HashMap<Integer, ARMarker> idSimpleMarkers;
	private HashMap<Integer, ARMarker> idBchMarkers;
	private HashMap<Integer, ARMarker> templateMarkers;
	private HashMap<TrackerMultiMarker, ARMarker> multiMarkers;

	private String configTrackerIDSimple;
	private String configTrackerIDBCH;
	private String configTrackerTemplate;

	private ARConfiguration config;

	private float[] cameraProjectionMatrix;

	/**
	 * Constructor de este controlador
	 *
	 * @param app
	 * @param config
	 * @throws ARMonkeyEngineException
	 */
	public TrackerManager(ARApplication app, ARConfiguration config)
			throws ARMonkeyEngineException {
		this.config = config;

		idSimpleMarkers = new HashMap<Integer, ARMarker>();
		idBchMarkers = new HashMap<Integer, ARMarker>();
		templateMarkers = new HashMap<Integer, ARMarker>();
		multiMarkers = new HashMap<TrackerMultiMarker, ARMarker>();

		multiTrackers = new ArrayList<TrackerMultiMarker>();

		try {
			idSimpleTracker = new TrackerSingleMarker(config.getWidth(), config
					.getHeight(), true);

			configureSingleTracker(config, idSimpleTracker,
					MARKER_MODE.ID_SIMPLE);

			cameraProjectionMatrix = idSimpleTracker.getProjectionMatrix();

		} catch (Exception e) {
			throw new ARMonkeyEngineException(e);
		}
	}

	/**
	 * M&eacute;todo para saber si un ID de un marcador se encuentra dentro del arreglo
	 * de ids detectador por el tracker. Es caso de encontrarlo, retorna la
	 * posici&oacute;n de dicho ID
	 *
	 * @param id
	 *            ID a buscar
	 * @param ids
	 *            arreglo de IDs detectados
	 * @return posici&oacute;n del ID encontrado en el arreglo
	 */
	private int containsId(int id, int[] ids) {
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] == id)
				return i;
		}

		return -1;
	}

	/**
	 * Retorna la matriz de proyecci&oacute;n de la c&aacute;mara. Esta matriz
	 * es obtenida a trav&eacute;s de la inicializaci&oacute;n del tracker para IDs
	 * Simples
	 *
	 * @return
	 */
	public float[] getCameraProjectionMatrix() {
		return cameraProjectionMatrix;
	}

	// -------------------- REGISTRO DE MARCADORES --------------------

	/**
	 * Registra un marcador por plantilla
	 *
	 * @param marker
	 *            Nodo al cual registrar el marcador
	 * @param templateFileName
	 *            Ruta del fichero que contiene la plantilla del marcador (Ej:
	 *            hiro.patt)
	 * @throws ARMonkeyEngineException
	 */
	public void registerMarker(ARMarker marker, String templateFileName)
			throws ARMonkeyEngineException {
		try {
			if (templateTracker == null) {
				templateTracker = new TrackerSingleMarker(config.getWidth(),
						config.getHeight(), false);

				configureSingleTracker(config, templateTracker,
						MARKER_MODE.TEMPLATE);

			}

			if (new File(templateFileName).exists() == false)
				throw new ARMonkeyEngineException(
						"Fichero de marcador no encontrado");

			int markerId = templateTracker.addPattern(templateFileName);
			templateMarkers.put(markerId, marker);
		} catch (ARTKPException e) {
			throw new ARMonkeyEngineException(e);
		} catch (IOException e) {
			throw new ARMonkeyEngineException(e);
		}

	}

	/**
	 * Registra un marcador por ID (simple o bch)
	 *
	 * @param marker
	 *            Nodo al cual registrar el marcador
	 * @param id
	 *            Numero ID del marcador que se quiere registrar
	 * @param isBch
	 *            true si es BCH o false para marcadores simples
	 * @throws ARMonkeyEngineException
	 */
	public void registerMarker(ARMarker marker, int id, boolean isBch)
			throws ARMonkeyEngineException {
		try {
			if (isBch) {
				if (idBchTracker == null) {

					idBchTracker = new TrackerSingleMarker(config.getWidth(),
							config.getHeight(), true);

					configureSingleTracker(config, idBchTracker,
							MARKER_MODE.ID_BCH);
				}

				idBchMarkers.put(id, marker);
			} else {
				idSimpleMarkers.put(id, marker);
			}
		} catch (ARTKPException e) {
			throw new ARMonkeyEngineException(e);
		} catch (IOException e) {
			throw new ARMonkeyEngineException(e);
		}
	}

	/**
	 * Registra un tracker multiple
	 *
	 * @param marker
	 *            Nodo al cual se registra el multiMarker
	 * @param multiConfigFile
	 *            Fichero de configuraci&oacute;n del multipatr&oacute;n
	 * @param ids
	 * @throws ARMonkeyEngineException
	 *             Excepci&oacute;n si no es posible instanciar el multiTracker
	 *             o el fichero de configuraci&oacute;n no es v&aacute;lido
	 */
	public void registerMultiMarker(ARMarker marker, String multiConfigFile,
			MARKER_MODE markerMode) throws ARMonkeyEngineException {
		try {

			TrackerMultiMarker tracker = new TrackerMultiMarker(config
					.getWidth(), config.getHeight(),
					markerMode != ARToolKitPlus.MARKER_MODE.TEMPLATE);

			TrackerConfigLoader trackerConfig = new TrackerConfigLoader(
					markerMode);
			trackerConfig.configureTracker(tracker);
			trackerConfig.initMultiTracker(tracker, config, multiConfigFile);

			multiTrackers.add(tracker);
			multiMarkers.put(tracker, marker);
		} catch (ARTKPException e) {
			throw new ARMonkeyEngineException(e);
		} catch (IOException e) {
			throw new ARMonkeyEngineException(e);
		}
	}

	public void registerMultiMarker(ARMarker marker, String multiConfigFile,
			String trackerConfigFile) throws ARMonkeyEngineException {
		try {

			TrackerConfigLoader trackerConfig = new TrackerConfigLoader(
					trackerConfigFile);

			TrackerMultiMarker tracker = new TrackerMultiMarker(config
					.getWidth(), config.getHeight(), trackerConfig
					.getMarkerMode() != MARKER_MODE.TEMPLATE.ordinal());

			trackerConfig.configureTracker(tracker);
			trackerConfig.initMultiTracker(tracker, config, multiConfigFile);

			multiTrackers.add(tracker);
			multiMarkers.put(tracker, marker);
		} catch (ARTKPException e) {
			throw new ARMonkeyEngineException(e);
		} catch (IOException e) {
			throw new ARMonkeyEngineException(e);
		}
	}

	// -------------------- ACTUALIZACION DE TRACKERS --------------------


	/**
	 * M&eacute;todo llamado desde la ARApplication dentro del loop principal de la
	 * aplicaci&oacute;n. Este metodo invoca la actualizaci&oacute;n de todos
	 * los marcadores registrados.
	 *
	 * @param image
	 *            Matriz de pixeles de la imagen a procesar
	 * @throws ARMonkeyEngineException
	 */
	public void update(int[] image) throws ARMonkeyEngineException {
		calcSingleMarkers(image, templateTracker, templateMarkers);
		calcSingleMarkers(image, idSimpleTracker, idSimpleMarkers);
		calcSingleMarkers(image, idBchTracker, idBchMarkers);
		calcMultiConfigMarkers(image);
	}

	/**
	 * Calcula la matriz de transformaci&oacute;n de cada uno de los marcadores
	 * ID Simple registrados en el respectivo tracker
	 *
	 * @param image
	 *            Matriz de pixeles de la imagen a procesar
	 * @throws ARMonkeyEngineException
	 */
	private void calcSingleMarkers(int[] image, TrackerSingleMarker tracker,
			HashMap<Integer, ARMarker> markers) throws ARMonkeyEngineException {
		try {
			if (tracker != null) {
				int found = tracker.calc(image);

				found = tracker.getNumDetectedMarkers();

				int idIndex;
				ARMarker marker;

				int[] ids = new int[found];
				float[] trans = new float[16];

				tracker.getDetectedMarkers(ids);

				for (int id : markers.keySet()) {
					marker = markers.get(id);

					if ((idIndex = containsId(id, ids)) != -1) {
						if (marker.calcPose())
							tracker.calcOpenGLMatrixFromMarker(tracker
									.getDetectedMarker(idIndex), 0, 0, 80,
									trans);

						marker.update(trans);
					} else
						marker.update(null);
				}
			}
		} catch (ARTKPException e) {
			throw new ARMonkeyEngineException(e);
		}
	}

	/**
	 * Calcula la matriz de transformaci&oacute;n de cada uno de los
	 * TrackerMultiMarker registrados, e invoca la actualizaci&oacute;n del nodo
	 * que tiene registrado cada MultiMarker.
	 *
	 * @param image
	 *            Matriz de pixeles de la imagen a procesar
	 * @throws ARMonkeyEngineException
	 */
	private void calcMultiConfigMarkers(int[] image)
			throws ARMonkeyEngineException {
		try {
			if (!multiTrackers.isEmpty()) {
				int found;
				ARMarker marker;

				float[] trans = new float[16];

				for (TrackerMultiMarker tracker : multiTrackers) {
					found = tracker.calc(image);
					marker = multiMarkers.get(tracker);

					if (found > 0) {
						if (marker.calcPose())
							trans = tracker.getModelViewMatrix();

						marker.update(trans);
					} else
						marker.update(null);
				}
			}
		} catch (ARTKPException e) {
			throw new ARMonkeyEngineException(e);
		}
	}


	// -------------------- CONFIGURACION DE TRACKERS --------------------

	/**
	 * Establece la ruta del archivo de configuraci&oacute;n que se usar&aacute; para el
	 * tracker de marcadores <b>ID-Simple</b><br />
	 * Si el tracker ya habia sido inicializado, vuelve a reconfigurar y
	 * ejecutar el init.
	 *
	 * @param configTrackerIDSimple
	 *            Ruta del archivo properties
	 * @throws IOException
	 * @throws ARTKPException
	 */
	public void setConfigTrackerIDSimple(String configTrackerIDSimple)
			throws IOException, ARTKPException {
		this.configTrackerIDSimple = configTrackerIDSimple;
		configureSingleTracker(config, idSimpleTracker, MARKER_MODE.ID_SIMPLE);
	}

	/**
	 * Establece la ruta del archivo de configuraci&oacute;n que se usar&aacute; para el
	 * tracker de marcadores <b>ID-BCH</b><br />
	 * Si el tracker ya habia sido inicializado, vuelve a reconfigurar y
	 * ejecutar el init.
	 *
	 * @param configTrackerIDBCH
	 *            Ruta del archivo properties
	 * @throws ARTKPException
	 * @throws IOException
	 */
	public void setConfigTrackerIDBCH(String configTrackerIDBCH)
			throws IOException, ARTKPException {
		this.configTrackerIDBCH = configTrackerIDBCH;

		if (idBchTracker != null)
			configureSingleTracker(config, idBchTracker, MARKER_MODE.ID_BCH);
	}

	/**
	 * Establece la ruta del archivo de configuraci&oacute;n que se usar&aacute; para el
	 * tracker de marcadores <b>ID-BCH</b><br />
	 * Si el tracker ya habia sido inicializado, vuelve a reconfigurar y
	 * ejecutar el init.
	 *
	 * @param configTrackerTemplate
	 *            Ruta del archivo properties
	 * @throws ARTKPException
	 * @throws IOException
	 */
	public void setConfigTrackerTemplate(String configTrackerTemplate)
			throws IOException, ARTKPException {
		this.configTrackerTemplate = configTrackerTemplate;

		if (templateTracker != null)
			configureSingleTracker(config, templateTracker,
					MARKER_MODE.TEMPLATE);
	}

	/**
	 * Inicializa y Configura un tracker seg&uacute;n el fichero de configuraci&oacute;n
	 * definido, si no existe, se carga el que provee el framework por defecto
	 * seg&uacute;n el tipo de marcador
	 *
	 * @param config
	 *            Sistema de configuraci&oacute;n de la aplicaci&oacute;n
	 * @param tracker
	 *            Tracker a ser configurado
	 * @param markerMode
	 *            Tipo de Marcador que se asignar&aacute; al tracker
	 * @throws IOException
	 *             Excepci&oacute;n lanzada si se detectan errores en el archivo de
	 *             configuraci&oacute;n
	 * @throws ARTKPException
	 *             Excepci&oacute;n generada si no es posible configurar el tracker con
	 *             los par&aacute;metros establecidos
	 */
	private void configureSingleTracker(ARConfiguration config,
			TrackerSingleMarker tracker, MARKER_MODE markerMode)
			throws IOException, ARTKPException {

		// Declara la instancia del sistema de configuraci&oacute;n de trackers
		TrackerConfigLoader configLoader = null;

		// Ruta del archivo de propiedades
		String configurationFile = null;

		switch (markerMode) {
		case ID_BCH:
			configurationFile = configTrackerIDBCH;
			break;

		case ID_SIMPLE:
			configurationFile = configTrackerIDSimple;
			break;

		case TEMPLATE:
			configurationFile = configTrackerTemplate;
			break;
		}

		if (configurationFile != null)
			// Si la ruta existe, se instancia la configuraci&oacute;n del tracker con
			// este archivo
			configLoader = new TrackerConfigLoader(configurationFile);
		else
			// Si no existe, se carga la configuracion por defecto seg&uacute;n el tipo
			// de marcador a detectar con este tracker
			configLoader = new TrackerConfigLoader(markerMode);

		// se asignana los parametros al tracker
		configLoader.configureTracker(tracker);

		// se inicializa el tracker
		configLoader.initSingleTracker(tracker, config);
	}
}
