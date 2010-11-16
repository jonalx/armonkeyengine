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
 * Clase que almacena los par&aacute;metros de configuraci&oacute;n de vista 3D y dispositivos
 * de video.
 *
 * @author Jonny Alexander Velez Calle
 * @author Juli&aacute;n Alejandro Lamprea Lamprea
 *
 * @version 1.0
 */
public class ARConfiguration {

	// ------- ATRIBUTOS -------//

	/**
	 * Ancho de la imagen entregada por el origen de video
	 */
	private int width;

	/**
	 * Alto de la imagen entregada por el origen de video
	 */
	private int height;

	/**
	 * Fichero XML de configuraci&oacute;n f&iacute;sica del dispositivo de video.
	 */
	private String cameraConfigFile;

	/**
	 * Fichero de configuraci&oacute;n de calibraci&oacute;n de c&aacute;mara (por defecto:
	 * camera_para.dat) el cual define la matriz de transformaci&oacute;n de la c&aacute;mara
	 * real respecto a la virtual.<br>
	 * De esta matriz depende la precisi&oacute;n de registro de los objetos virtuales
	 * sobre los reales.
	 */
	private String cameraParametersFile;

	/**
	 * Plano cercano para configuraci&oacute;n del frustum
	 */
	private float nearClip;

	/**
	 * Plano lejano para configuraci&oacute;n del frustum
	 */
	private float farClip;

	private boolean verticalFlipped;

	// ------- OPERACIONES -------//

	public String getCameraParametersFile() {
		return cameraParametersFile;
	}

	public void setCameraParametersFile(String cameraParametersFile) {
		this.cameraParametersFile = cameraParametersFile;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getCameraConfigFile() {
		return cameraConfigFile;
	}

	public void setCameraConfigFile(String cameraConfigFile) {
		this.cameraConfigFile = cameraConfigFile;
	}

	public float getNearClip() {
		return nearClip;
	}

	public void setNearClip(float nearClip) {
		this.nearClip = nearClip;
	}

	public float getFarClip() {
		return farClip;
	}

	public void setFarClip(float farClip) {
		this.farClip = farClip;
	}

	public boolean isVerticalFlipped() {
		return verticalFlipped;
	}

	public void setVerticalFlipped(boolean verticalFlipped) {
		this.verticalFlipped = verticalFlipped;
	}
}
