/* Video Capture using JARVideo original implementation of ARToolit
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
package arme.core.video.impl.jarvideo;

import arme.core.video.ImageSource;
import arme.core.video.ImageSourceException;

/**
 * Esta clase es una fuente de imagenes concreta, la cual toma el flujo de
 * imagenes a partir de la camara que se encuentra conectada al computador, esta
 * utiliza la implementaci&oacute;n de Video de ARToolkit, la cual se encuentra
 * disponible para varias plataformas, Win32, Linux, MacOS.
 *
 * @author Jonny Alexander V&eacute;lez
 * @author Juli&aacute;n Alejandro Lamprea
 *
 * @version 1.0.0
 */
public final class JARVideo implements ImageSource {

	// Se carga la libreria nativa de la implementaci&oacute;n del dispositivo de video
	static {
		System.loadLibrary("JARVideo");
	}

	/**
	 * Instancia &uacute;nica del dispositivo de video.
	 */
	private static JARVideo arVideo;

	/**
	 * Constructor privado, Singleton para definir una unica instancia del
	 * acceso al dispositivo de video.
	 */
	private JARVideo() {
	}

	/**
	 * Esta funci&oacute;n retorna una instancia de la fuente de im&aacute;genes. Se
	 * implementa el patron Singleton.
	 *
	 * @return Instancia de la fuente de im&aacute;genes.
	 */
	public static JARVideo getInstance() {
		if (arVideo == null)
			arVideo = new JARVideo();

		return arVideo;
	}

	/**
	 * Esta funci&oacute;n cierra la fuente de imagenes.
	 *
	 * @throws ImageSourceException
	 *             Error generado por la fuente de imagenes.
	 */
	public native void close() throws ImageSourceException;

	/**
	 * Obtiene la altura de las imagenes devueltas por esta fuente.
	 *
	 * @return Altura de la imagen
	 * @throws ImageSourceException
	 *             Error generado por la fuente de imagenes.
	 */
	public native int getHeight() throws ImageSourceException;

	/**
	 * Esta funci&oacute;n obtiene la siguiente imagen del flujo de la fuente.
	 *
	 * @param buffer
	 *            Flujo de bytes de la imagen.
	 * @throws ImageSourceException
	 *             Error generado por la fuente de im&aacute;genes.
	 */
	public native void getImage(int[] buffer) throws ImageSourceException;

	/**
	 * Obtiene el ancho de las im&aacute;genes devueltas por esta fuente.
	 *
	 * @return Ancho de la imagen
	 * @throws ImageSourceException
	 *             Error generado por la fuente de im&aacute;genes.
	 */
	public native int getWidth() throws ImageSourceException;

	/**
	 * Esta funci&oacute;n abre la fuente de im&aacute;genes.
	 *
	 * @param config
	 *            Archivo o cadena de configuraci&oacute;n si este lo soporta.
	 * @throws ImageSourceException
	 *             Error generado por la fuente de im&aacute;genes.
	 */
	public native void open(String config) throws ImageSourceException;
}
