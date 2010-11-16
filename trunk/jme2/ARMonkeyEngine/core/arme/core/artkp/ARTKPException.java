/*
 * Copyright (c) 2006 Mathieu Guindon (mathieu@drone.ws)
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
package arme.core.artkp;

/**
 * Excepci&oacute;n lanzada cuando se genera un error en el procesamiento de frames,
 * por lo general causada por algun par&aacute;metro no configurado o definido, o por
 * alguna inicializaci&oacute;n incorrecta de alguno de los componentes de
 * ARToolKitPlus.
 *
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ARTKPException extends Exception {

	public ARTKPException() {
		super();
	}

	public ARTKPException(String arg0) {
		super(arg0);
	}

	public ARTKPException(Throwable arg0) {
		super(arg0);
	}

	public ARTKPException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
