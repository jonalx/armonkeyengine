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

import com.jme.math.FastMath;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

/**
 * Clase que permite la creaci&oacute;n de un Nodo de jme registrado a un marcador de
 * ARToolkitPlus y su renderizaci&oacute;n 3D
 *
 * @author Jonny Alexander Velez Calle
 * @author Julian Alejandro Lamprea Lamprea
 *
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class ARNode extends Node implements ARMarker {

	protected boolean visible;
	protected boolean keepTransform;
	private Matrix4f matrix4f;

	public ARNode(String name) {
		super(name);
		matrix4f = new Matrix4f();
	}

	public boolean calcPose() {
		return true;
	}

	public void update(float[] trans) {
		if (trans != null) {
			visible = true;

			setCullHint(CullHint.Dynamic);

			matrix4f.set(trans, false);

			// Convert opengl matrix to jme vector
			Vector3f translation = matrix4f.toTranslationVector();
			translation = translation.mult(-1);

			// Convert opengl matrix to jme matrix
			Quaternion rotation = matrix4f.toRotationQuat();
			Quaternion rotY180 = new Quaternion();
			rotY180.fromAngleAxis(FastMath.PI, new Vector3f(0, 1, 0));

			// Rotate the marker object as needed
			getLocalRotation().set(rotation.mult(rotY180));

			// Translate the marker object as needed
			setLocalTranslation(translation);

			// Update the display of our marker object
			updateWorldData(0);

		} else {
			visible = false;

			if (!keepTransform)
				setCullHint(CullHint.Always);
		}
	}

	/**
	 * Define si debe o no mantener el modelo 3D renderizado una vez haya
	 * desaparecido el marcador de la imagen
	 *
	 * @return true para mantener, false para esconder el modelo.
	 */
	public boolean isKeepTransform() {
		return keepTransform;
	}

	/**
	 * Establece la variable para definir la permanencia del modelo 3D
	 *
	 * @param keepTransform
	 */
	public void setKeepTransform(boolean keepTransform) {
		this.keepTransform = keepTransform;
	}

	/**
	 * Flag que indica si el patron est&aacute; visible.
	 *
	 * @return visible
	 */
	public boolean isVisible() {
		return visible;
	}

	public Matrix4f getMatrix4f() {
		return matrix4f;
	}
}
