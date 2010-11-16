/* ImageSource Exception
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
 * Esta clase representa una excepci&oacute;n generada en el m&oacute;dulo de
 * video.
 *
 * @author Jonny Alexander Velez
 * @author Juli&aacute;n Alejandro Lamprea
 *
 * @version 1.0.0
 */
@SuppressWarnings("serial")
public class ImageSourceException extends Exception {

	/**
	 * Crea una instancia de la excepci&oacute;n.
	 */
	public ImageSourceException() {
		super();
	}

	/**
	 * Crea una instancia de la excepci&oacute;n con un mensaje.
	 *
	 * @param message
	 *            Mensaje de la excepci&oacute;n
	 */
	public ImageSourceException(String message) {
		super(message);
	}

	/**
	 * Crea una instancia de la excepci&oacute;n con su causa.
	 *
	 * @param cause
	 *            Causa de la excepci&oacute;n
	 */
	public ImageSourceException(Throwable cause) {
		super(cause);
	}

	/**
	 * Crea una instancia de la excepci&oacute;n con un mensaje y su causa.
	 *
	 * @param message
	 *            Mensaje de la excepci&oacute;n
	 * @param cause
	 *            Causa de la excepci&oacute;n
	 */
	public ImageSourceException(String message, Throwable cause) {
		super(message, cause);
	}
}