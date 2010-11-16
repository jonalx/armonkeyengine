/* XMLCOnfigParser Implementation using XAS
 *
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
package arme.core.video.impl.ipvideo;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * Clase utilizada para analizar el archivo de configuraci&oacute;n del modulo de
 * captura de video ip.
 *
 * @author Jonny Alexander V&eacute;lez
 * @author Julian Alejandro Lamprea
 *
 * @version 1.0.0
 */
public final class XMLDefaultConfigParser implements XMLConfigParser {

	/* Claves que componen cada elemento del archivo XML de configuraci&oacute;n */
	private final static String ROOT_ELEMENT = "ipvideo";
	private final static String INPUT_ELEMENT = "input";
	private final static String INPUT_ATTRIBUTE_URL = "url";
	private static final String INPUT_ATTRIBUTE_MJPG = "mjpg";
	private static final String RESOLUTION_ELEMENT = "resolution";
	private static final String RESOLUTION_ATTRIBUTE_WIDTH = "width";
	private static final String RESOLUTION_ATTRIBUTE_HEIGHT = "height";

	/* Mensajes de error del analizador */
	private final static String NO_CONFIG = "Configuracion de video no especificada";
	private final static String CONFIG_FILE_NOT_FOUND = "No se encontro el archivo de configuracion";
	private final static String CONFIG_FILE_READ_ERROR = "Error leyendo el archivo de configuracion";
	private final static String CONFIG_FILE_SINTAX_ERROR = "Error en la sintaxis XML";

	/** URL del Stream de video */
	private String url;

	/** Indica si se lee un flujo continuo de imagenes */
	private boolean mjpgMode;

	/** Indica si se especifico una resoluci&oacute;n para la entrada de video */
	private boolean resolutionDefined;

	/** Ancho de la resoluci&oacute;n especificada */
	private int width;

	/** Altura de la resoluci&oacute;n especificada */
	private int height;

	/**
	 * Funci&oacute;n encargada de realizar el analisis de la configuraci&oacute;n para el
	 * video. Si el archivo tiene una mala sintaxis, la configuraci&oacute;n no ser&aacute;
	 * cargada.
	 *
	 * @param config
	 *            Ruta al archivo, o cadena de configuraci&oacute;n
	 * @throws XMLConfigParserException
	 *             Si ocurre un error en la lectura del archivo o la
	 *             configuraci&oacute;n tiene problemas de sintaxis.
	 */
	public void parseConfigFile(String config) throws XMLConfigParserException {
		InputStream in = null;

		// Se debe especificar una configuraci&oacute;n
		if (config == null)
			throw new XMLConfigParserException(NO_CONFIG);

		/*
		 * Se inicializa el flujo de entrada para el analizador, seg&uacute;n sea la
		 * fuente, un archivo o una cadena de configuraci&oacute;n.
		 */
		if (config.startsWith("<?xml"))
			in = new ByteArrayInputStream(config.getBytes());
		else
			try {
				in = new FileInputStream(config);
			} catch (FileNotFoundException e) {
				throw new XMLConfigParserException(CONFIG_FILE_NOT_FOUND);
			}

		try {
			DOMParser xmlParser = new DOMParser();
			xmlParser.parse(new InputSource(in));

			Document document = xmlParser.getDocument();

			/*
			 * Se analiza el documento XML y se extraen los atributos requeridos
			 * para la configuraci&oacute;n
			 */
			Element rootElement = (Element) document.getElementsByTagName(
					ROOT_ELEMENT).item(0);

			Element inputElement = (Element) rootElement.getElementsByTagName(
					INPUT_ELEMENT).item(0);

			url = inputElement.getAttribute(INPUT_ATTRIBUTE_URL);
			mjpgMode = Boolean.parseBoolean(inputElement
					.getAttribute(INPUT_ATTRIBUTE_MJPG));

			resolutionDefined = false;

			NodeList inputNodesList = inputElement
					.getElementsByTagName(RESOLUTION_ELEMENT);

			// Se verifica si se especifico una resoluci&oacute;n (no es obligatoria)
			if (inputNodesList.getLength() == 0)
				return;

			resolutionDefined = true;

			Element resolutionElement = (Element) inputNodesList.item(0);

			width = Integer.parseInt(resolutionElement
					.getAttribute(RESOLUTION_ATTRIBUTE_WIDTH));
			height = Integer.parseInt(resolutionElement
					.getAttribute(RESOLUTION_ATTRIBUTE_HEIGHT));
		} catch (IOException e) {
			throw new XMLConfigParserException(CONFIG_FILE_READ_ERROR);
		} catch (Exception e) {
			throw new XMLConfigParserException(CONFIG_FILE_SINTAX_ERROR);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
	}

	/**
	 * Obtiene el valor del atributo.
	 *
	 * @return URL de conexi&oacute;n
	 */
	public String getURLValue() {
		return url;
	}

	/**
	 * Obtiene el valor del atributo.
	 *
	 * @return <code>true</code> si se especifico modo MJPG,
	 *         <code>false</code> de lo contrario
	 */
	public boolean isMJPGMode() {
		return mjpgMode;
	}

	/**
	 * Obtiene el valor del atributo
	 *
	 * @return <code>true</code> si se especifico una resoluci&oacute;n,
	 *         <code>false</code> de lo contrario
	 */
	public boolean hasInputResolution() {
		return resolutionDefined;
	}

	/**
	 * Obtiene el valor del atributo
	 *
	 * @return Ancho definido en la resoluci&oacute;n de la entrada de video
	 */
	public int getInputWidth() {
		return width;
	}

	/**
	 * Obtiene el valor del atributo
	 *
	 * @return Altura definida en la resoluci&oacute;n de la entrada de video
	 */
	public int getInputHeight() {
		return height;
	}
}
