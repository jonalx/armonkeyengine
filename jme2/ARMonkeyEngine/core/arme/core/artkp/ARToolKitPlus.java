package arme.core.artkp;

/* Static class containing the constants used by ARToolKitPlus.
 *
 * Copyright (c) 2006 Jean-Sebastien Senecal (js@drone.ws)
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

/**
 * Contiene los enumeradores de los par&aacute;metros de configuraci&oacute;n de un Tracker
 */
public class ARToolKitPlus {

	/**
	 * Enumerador de Formatos de pixel soportados por ARToolkitPlus. (Siempre se
	 * deber&iacute;a usar BGRA)
	 */
	public enum PIXEL_FORMAT {
		NONE, ABGR, BGRA, BGR, RGBA, RGB, RGB565, LUM
	}

	/**
	 * Modo de C&aacute;lculo de Distorsi&oacute;n
	 */
	public enum UNDIST_MODE {
		NONE, STD, LUT
	}

	/**
	 * Modo de procesamiento de imagen al realizar la normalizaci&oacute;n
	 */
	public enum IMAGE_PROC_MODE {
		HALF_RES, FULL_RES
	}

	/**
	 * Tipo de Marcadores a ser detectados por el tracker
	 */
	public enum MARKER_MODE {
		TEMPLATE, ID_SIMPLE, ID_BCH
	}

	/**
	 * Algoritmo de estimaci&oacute;n de Postura
	 */
	public enum POSE_ESTIMATOR {
		ORIGINAL, ORIGINAL_CONT, RPP
	}
}
