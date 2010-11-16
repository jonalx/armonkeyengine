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
 * Clase que permite la implementaci&oacute;n de un marcador de control, es decir,
 * aquellos que se necesitan detectar pero no renderizan ningun objeto 3D.<br>
 * Esta clase es abstracta y debe ser implementada por el usuario para definir
 * el comportamiento que debe tener cuando el marcador es o no detectado.
 *
 * @author Jonny Alexander Velez Calle
 * @author Juli&aacute;n Alejandro Lamprea Lamprea
 *
 * @version 1.0
 */
public abstract class ARControl implements ARMarker {

	private boolean visible;
	private boolean calcPose;
	private float[] trans;

	/**
	 * Constructor que inicializa este marcador y define si debe o no calcular
	 * si matriz de transformaci&oacute;n.
	 *
	 * @param calcPose
	 */
	protected ARControl(boolean calcPose) {
		this.calcPose = calcPose;
	}

	public boolean calcPose() {
		return calcPose;
	}

	public void update(float[] trans) {
		if (calcPose)
			this.trans = trans;

		if (trans != null) {
			visible = true;
			activated();
		}
		else {
			visible = false;
			deactivated();
		}
	}

	public boolean isVisible() {
		return visible;
	}

	/**
	 * Retorna la matriz de postura en caso de haber activado dicho flag
	 *
	 * @return pose matrix
	 */
	protected float[] getTransformation() {
		return trans;
	}

	/**
	 * Metodo llamado cuando el marcador ha sido detectado
	 */
	protected abstract void activated();

	/**
	 * Metodo llamado cuando el marcador ha desaparecido
	 */
	protected abstract void deactivated();
}
