/* Common interface for different image sources 
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
package arme.core.video;

/**
 * Esta interface describe a una fuente de im&aacute;genes, bien sea
 * est&aacute;ticas, o din&aacute;micas en el caso de una c&aacute;mara de
 * video.
 *
 * @author Jonny Alexander Velez
 * @author Juli&aacute;n Alejandro Lamprea
 *
 * @version 1.0.0
 */
public interface ImageSource {

	/**
	 * Esta funci&oacute;n abre la fuente de imagenes.
	 *
	 * @param config
	 *            Archivo o cadena de configuraci&oacute;n si este lo soporta.
	 * @throws ImageSourceException
	 *             Error generado por la fuente de im&aacute;genes.
	 */
	void open(String config) throws ImageSourceException;

	/**
	 * Obtiene el ancho de las imagenes devueltas por esta fuente.
	 *
	 * @return Ancho de la imagen
	 * @throws ImageSourceException
	 *             Error generado por la fuente de imagenes.
	 */
	int getWidth() throws ImageSourceException;

	/**
	 * Obtiene la altura de las imagenes devueltas por esta fuente.
	 *
	 * @return Altura de la imagen
	 * @throws ImageSourceException
	 *             Error generado por la fuente de imagenes.
	 */
	int getHeight() throws ImageSourceException;

	/**
	 * Esta funci&oacute;n obtiene la siguiete imagen del flujo de la fuente.
	 *
	 * @param buffer
	 *            Flujo de bytes de la imagen.
	 * @throws ImageSourceException
	 *             Error generado por la fuente de im&aacute;genes.
	 */
	void getImage(int[] buffer) throws ImageSourceException;

	/**
	 * Esta funci&oacute;n cierra la fuente de imagenes.
	 *
	 * @throws ImageSourceException
	 *             Error generado por la fuente de im&aacute;genes.
	 */
	void close() throws ImageSourceException;
}
