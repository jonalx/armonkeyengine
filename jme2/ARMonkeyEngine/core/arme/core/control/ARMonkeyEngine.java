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

import java.io.IOException;

import arme.core.artkp.ARTKPException;
import arme.core.artkp.ARToolKitPlus.MARKER_MODE;
import arme.core.control.marker.ARMarker;
import arme.core.control.tracking.TrackerConfigLoader;

import com.jme.math.Matrix4f;
import com.jme.renderer.Camera;

/**
 * Esta clase controla todo el acceso a las funcionalidades principales de este
 * framework. Inicializa los controladores de Video y Tracking, define la
 * configuraci&oacute;n de vista e inicializa el primer frame de video.
 * Adem&aacute;s permite el registro de los distintos tipos de marcadores
 * soportados:
 * <ul>
 * <li>template</li>
 * <li>ID simple</li>
 * <li>ID BCH</li>
 * </ul>
 * Marcadores que pueden ser registrados en cualquier modo: Single o Multi.<br>
 * Por cada tipo de marcador, se usa una instancia de Tracker (Single o Multi
 * seg&uacute;n el caso), la cual debe ser configurada según los
 * par&aacute;metros que define el sistema de tracking (ARToolkitPlus). Estos
 * par&aacute;metros pueden ser personalizados por medio de un archivo de
 * propiedades o dejar al framework cargar sus par&aacute;metros por defecto.
 *
 * @see TrackerConfigLoader
 *
 * @author Jonny Alexander Velez Calle
 * @author Juli&aacute;n Alejandro Lamprea Lamprea
 *
 * @version 1.0
 */
public class ARMonkeyEngine {

	private VideoManager videoManager;
	private TrackerManager trackerManager;
	private int[] imageArray;

	/**
	 * Constructor por defecto que recibe una instancia de ARApplication para la
	 * comunicaci&oacute;n con la aplicaci&oacute;n jMonkeyEngine desde la cual
	 * se instancie esta clase. Realiza tambi&eacute;n el registro de la
	 * c&aacute;mara virtual con la c&aacute;mara real.
	 *
	 * @param app
	 *            ARApplication (por lo general: this) Clase desde la que se
	 *            instancia ARMonekyEngine
	 * @throws ARMonkeyEngineException
	 *             Excepci&oacute;n generada si hay errores en la
	 *             inicializaci&oacute;n de los controladores de video y
	 *             tracking
	 */
	public ARMonkeyEngine(ARApplication app) throws ARMonkeyEngineException {
		ARConfiguration config = app.getARConfiguration();

		videoManager = new VideoManager(app, config);
		trackerManager = new TrackerManager(app, config);

		Camera cam = app.getCamera();

		Matrix4f matrix4f = new Matrix4f(trackerManager
				.getCameraProjectionMatrix());

		cam.setLocation(matrix4f.toTranslationVector());

		float[] projmatrix = trackerManager.getCameraProjectionMatrix();

		// convert openGl to jME matrix
		projmatrix[11] = -1 * projmatrix[11];
		projmatrix[5] = -1 * projmatrix[5];
		Matrix4f projectionmatrix = new Matrix4f(projmatrix);

		// fovy -> Vertical Field of View
		// http://en.wikipedia.org/wiki/Angle_of_view
		// http://books.google.com.co/books?id=bfcLeqRUsm8C&pg=PA112&lpg=PA112&dq=computer+graphics,+field+of+view&source=bl&ots=FpTri6qXjz&sig=s3kC001y0seKqWSvLxE5vsUbWhs&hl=es&ei=YRw1SsqwN4uMtgfut5j5Dg&sa=X&oi=book_result&ct=result&resnum=3#PPA114,M1
		float fovy = (float) (Math.toDegrees(Math
				.atan(1 / projectionmatrix.m11) * 2));

		float aspect = projectionmatrix.m11 / projectionmatrix.m00;
		// float aspect = config.getWidth() / config.getHeight();

		cam.setFrustumPerspective(fovy, aspect, 0.1f, 100000f);

		// Inicializa el primer frame
		update();
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
		trackerManager.registerMarker(marker, id, isBch);
	}

	/**
	 * Registra un marcador por plantilla
	 *
	 * @param marker
	 *            Nodo al cual registrar el marcador
	 * @param templateFileName
	 *            Ruta del fichero que contiene la plantilla del marcador (Ej:
	 *            data/hiro.patt)
	 * @throws ARMonkeyEngineException
	 *             Excepci&oacute;n si no es posible instanciar el multiTracker
	 *             o el fichero de configuraci&oacute;n no es v&aacute;lido
	 */
	public void registerMarker(ARMarker marker, String templateFileName)
			throws ARMonkeyEngineException {
		trackerManager.registerMarker(marker, templateFileName);
	}

	/**
	 * Registra un tracker m&uacute;ltiple usando la configuraci&oacute;n por
	 * defecto seg&uacute;n el tipo de marcadores a detectar por este tracker
	 *
	 * @param marker
	 *            Nodo al cual se registra el multiMarker
	 * @param multiConfigFile
	 *            Fichero de configuraci&oacute;n del multimarcador
	 * @param markerMode
	 * @throws ARMonkeyEngineException
	 *             Excepci&oacute;n si no es posible instanciar el multiTracker
	 *             por alg&uacute;n error en el archivo de configuraci&oacute;n
	 *             de marcadores m&uacute;ltiples.
	 */
	public void registerMultiMarker(ARMarker marker, String multiConfigFile,
			MARKER_MODE markerMode) throws ARMonkeyEngineException {
		trackerManager.registerMultiMarker(marker, multiConfigFile, markerMode);
	}

	/**
	 *
	 * Registra un tracker m&uacute;ltiple y lo configura con los
	 * par&aacute;metos definidos en el archivo de configuraci&oacute;n dado por
	 * el usuario
	 *
	 * @param marker
	 *            Nodo jME asociado al marcador a detectar
	 * @param multiConfigFile
	 *            Archivo de configuraci&oacute;n de la matriz de marcadores
	 *            m&uacute;ltiples
	 * @param trackerConfigFile
	 *            Archivo de configuraci&oacute;n del tracker seg&uacute;n el
	 *            tipo de marcadores a detectar.
	 * @throws ARMonkeyEngineException
	 *             Excepci&oacute;n en caso de asignar archivos no
	 *             v&aacute;lidos o incorrectos, o en caso de no poder
	 *             inicializar el marcador por errores en los parámetros de
	 *             configuraci&oacute;n
	 */
	public void registerMultiMarker(ARMarker marker, String multiConfigFile,
			String trackerConfigFile) throws ARMonkeyEngineException {
		trackerManager.registerMultiMarker(marker, multiConfigFile,
				trackerConfigFile);
	}

	/**
	 * M&eacute;todo que actualiza los controladores de Video (capturar
	 * siguiente frame) y de Tracking (procesar frame y generar matrices de
	 * marcadores encontrados)
	 *
	 * @throws ARMonkeyEngineException
	 */
	public void update() throws ARMonkeyEngineException {
		imageArray = videoManager.update();
		trackerManager.update(imageArray);
	}

	/**
	 * Permite tener acceso a la matriz de pixeles entregada por el ImageSource
	 * actual. Esta matriz corresponde al &uacute;ltimo frame capturado.
	 *
	 * @return array de int[] con los pixeles de la imagen
	 */
	public int[] getBackgroundImage() {
		return imageArray;
	}

	// --------- SETS DE LOS ARCHIVOS PERSONALIZADOS DE CONFIGURACION ---------

	/**
	 * Establece la ruta del archivo de configuraci&oacute;n que se usar&aacute;
	 * para el tracker de marcadores <b>ID-Simple</b><br>
	 * En caso de ya haber sido inicializado el respectivo tracker, este vuelve
	 * a ser reconfigurado.
	 *
	 * @param configTrackerIDSimple
	 *            Ruta del archivo properties
	 * @throws ARMonkeyEngineException
	 */
	public void setConfigTrackerIDSimple(String configTrackerIDSimple)
			throws ARMonkeyEngineException {
		try {
			trackerManager.setConfigTrackerIDSimple(configTrackerIDSimple);
		} catch (IOException e) {
			throw new ARMonkeyEngineException(e);
		} catch (ARTKPException e) {
			throw new ARMonkeyEngineException(e);
		}
	}

	/**
	 * Establece la ruta del archivo de configuraci&oacute;n que se usar&aacute;
	 * para el tracker de marcadores <b>ID-BCH</b><br>
	 * En caso de ya haber sido inicializado el respectivo tracker, este vuelve
	 * a ser reconfigurado.
	 *
	 * @param configTrackerIDBCH
	 *            Ruta del archivo properties
	 * @throws ARMonkeyEngineException
	 */
	public void setConfigTrackerIDBCH(String configTrackerIDBCH)
			throws ARMonkeyEngineException {
		try {
			trackerManager.setConfigTrackerIDBCH(configTrackerIDBCH);
		} catch (IOException e) {
			throw new ARMonkeyEngineException(e);
		} catch (ARTKPException e) {
			throw new ARMonkeyEngineException(e);
		}
	}

	/**
	 * Establece la ruta del archivo de configuraci&oacute;n que se usar&aacute;
	 * para el tracker de marcadores <b>Template</b><br>
	 * En caso de ya haber sido inicializado el respectivo tracker, este vuelve
	 * a ser reconfigurado.
	 *
	 * @param configTrackerTemplate
	 *            Ruta del archivo properties
	 * @throws ARMonkeyEngineException
	 */
	public void setConfigTrackerTemplate(String configTrackerTemplate)
			throws ARMonkeyEngineException {
		try {
			trackerManager.setConfigTrackerTemplate(configTrackerTemplate);
		} catch (IOException e) {
			throw new ARMonkeyEngineException(e);
		} catch (ARTKPException e) {
			throw new ARMonkeyEngineException(e);
		}
	}

}
