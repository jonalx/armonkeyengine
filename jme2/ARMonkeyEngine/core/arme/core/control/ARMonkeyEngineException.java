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

/**
 * Clase para representar las excepciones generadas por las operaciones
 * realizadas por el nucleo del framework. Lanzada cuando alguna de estas falla.
 * esta excepci&oacute;n encapsula las excepciones generadas internamente por los dem&aacute;s
 * m&oacute;dulos del framework.
 *
 * @author Jonny Alexander Velez Calle
 * @author Juli&aacute;n Alejandro Lamprea Lamprea
 *
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ARMonkeyEngineException extends Exception {

	public ARMonkeyEngineException() {
		super();
	}

	public ARMonkeyEngineException(String message) {
		super(message);
	}

	public ARMonkeyEngineException(Throwable cause) {
		super(cause);
	}

	public ARMonkeyEngineException(String message, Throwable cause) {
		super(message, cause);
	}

}
