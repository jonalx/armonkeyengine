/* XML Config interface
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

/**
 * Esta interface es el contrato de implementaci&oacute;n de un Analizador de
 * configuraci&oacute;n para el modulo de video. Las clases concretas pueden realizar
 * el analizis usando librerias como JAXB, SAX, DOM.
 *
 * @author Jonny Alexander V&eacute;lez
 * @author Julian Alejandro Lamprea
 *
 * @version 1.0.0
 */
public interface XMLConfigParser {

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
	void parseConfigFile(String config) throws XMLConfigParserException;

	/**
	 * Obtiene el Url de conexi&oacute;n de video IP
	 *
	 * @return URL de conexi&oacute;n
	 */
	String getURLValue();

	/**
	 * Obtiene el valor que determina si el video esta configurado en modo de
	 * flujo de datos o imagen a imagen.
	 *
	 * @return <code>true</code> si se especifico modo MJPG,
	 *         <code>false</code> de lo contrario
	 */
	boolean isMJPGMode();

	/**
	 * Obtiene valor que indica si a la entrada se le especifico una resoluci&oacute;n
	 * de video
	 *
	 * @return <code>true</code> si se especifico una resoluci&oacute;n,
	 *         <code>false</code> de lo contrario
	 */
	boolean hasInputResolution();

	/**
	 * Obtiene el ancho de la entrada de video
	 *
	 * @return Ancho definido en la resoluc&oacute;n de la entrada de video
	 */
	int getInputWidth();

	/**
	 * Obtiene la altura de la entrada de video
	 *
	 * @return Altura definida en la resoluci&oacute;n de la entrada de video
	 */
	int getInputHeight();
}
