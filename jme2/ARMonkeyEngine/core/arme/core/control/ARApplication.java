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

import arme.core.video.ImageSource;

import com.jme.renderer.Camera;
import com.jme.scene.Node;

/**
 * Interfaz para establecer la comunicaci&oacute;n entre los distintos tipos de
 * Aplicaciones de jMonkeyEngine y el controlador ARMonkeyEngine.<br>
 * Esta clase permite la separaci&oacute;n entre la implementaci&oacute;n del motor gr&aacute;fico y
 * el framework ARMonkeyEngine.
 *
 * @author Jonny Alexander Velez Calle
 * @author Juli&aacute;n Alejandro Lamprea Lamprea
 * @version 1.0
 */
public interface ARApplication {

	/**
	 * Retorna la instancia de la c&aacute;mara creada por la aplicacion jMonkeyEngine.<br>
	 * Este m&eacute;todo es usado para permitirle al framework configurar la vista de
	 * la c&aacute;mara (frustum Perspective) de acuerdo al sistema de tracking.
	 *
	 * @return Camara de jME
	 */
	Camera getCamera();

	/**
	 * Retorna la instancia del objeto que implemente un ImageSource para
	 * registrarla al controlador de video y acceder a cada frame para
	 * renderizarla por el controlador de la aplicaci&oacute;n y procesarla por el
	 * controlador de tracking
	 *
	 * @return Instancia concreta de la implementaci&oacute;n de ImageSource
	 */
	ImageSource getImageSource();

	/**
	 * Retorna la configuraci&oacute;n definida por el usuario para el acceso a los
	 * dispositivos de video.
	 *
	 * @return configuraci&oacute;n de la aplicaci&oacute;n
	 * @see ARConfiguration
	 */
	ARConfiguration getARConfiguration();

	/**
	 * Retorna la instancia del nodo que tendr&aacute; la imagen de fondo
	 * obtenida de la fuente de im&aacute;genes (ImageSource)
	 *
	 * @return rootNode
	 *
	 */
	Node getRootNode();
}
