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
package arme.core.control.marker;

/**
 * Interfaz para enlazar los nodos de jME con un marcador de ARToolkitPlus
 *
 * @author Jonny Alexander Velez Calle
 * @author Julian Alejandro Lamprea Lamprea
 *
 * @version 1.0
 */
public interface ARMarker {

	/**
	 * M&eacute;todo llamado en cada frame de renderizado siempre y cuando el marcador
	 * registrado a este nodo haya sido detectado.
	 *
	 * @param trans
	 *            Matriz de transformaci&oacute;n generada para este marcador. Si
	 *            calcPose() es false, esta matriz llegara como null.
	 */
	void update(float[] trans);

	/**
	 * Define si se debe o no calcular la matriz de transformaci&oacute;n (pose matrix)
	 * cuando este marcador sea detectado
	 *
	 * @return true para indicar que si se debe actualizar la matriz, false en caso contrario
	 */
	boolean calcPose();
}
