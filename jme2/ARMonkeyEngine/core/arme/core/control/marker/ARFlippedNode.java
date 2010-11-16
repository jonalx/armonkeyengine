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

import arme.core.control.marker.ARNode;

import com.jme.math.FastMath;
import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

/**
 * Clase que permite la creaci&oacute;n de un Nodo de jme registrado a un
 * marcador de ARToolkitPlus y su renderizaci&oacute;n 3D. A diferencia del
 * padre ARNode, este nodo, invierte la aplicaci&oacute;n de las matrices de
 * transformaci&oacute;n para acomodarlas acorde al video cuando este se encuentra
 * invertido.
 *
 * @author Jonny Alexander Velez Calle
 * @author Juli&aacute;n Alejandro Lamprea Lamprea
 *
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class ARFlippedNode extends ARNode {

	public ARFlippedNode(String name) {
		super(name);
	}

	@Override
	public void update(float[] trans) {
		if (trans != null) {
			visible = true;

			setCullHint(CullHint.Dynamic);

			Matrix4f matrix4f = new Matrix4f(trans);

			Quaternion rotation = matrix4f.toRotationQuat();
			// Convert opengl matrix to jme matrix
			rotation.z *= -1.0f;
			rotation.x *= -1.0f;

			Vector3f translation = matrix4f.toTranslationVector();
			// Convert opengl matrix to jme vector
			translation.x *= -1.0f;
			translation.z *= -1.0f;

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
}
