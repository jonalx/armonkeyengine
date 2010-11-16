/* The abstract TrackerMultiMarker superclass.
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
package arme.core.artkp;

public class TrackerMultiMarker extends Tracker {

	static {
		System.loadLibrary("artkp_TrackerMultiMarker");
	}

	public TrackerMultiMarker(boolean ids) throws ARTKPException {
		super(_createTrackerHandle(ids));
	}

	public TrackerMultiMarker(int width, int height, boolean ids) throws ARTKPException {
		super(_createTrackerHandle(width, height, ids));
	}

	protected static native long _createTrackerHandle(boolean ids) throws ARTKPException;

	protected static native long _createTrackerHandle(int width, int height, boolean ids)
			throws ARTKPException;

	/**
	 * nCamParamFile is the name of the camera parameter file<br>
	 * nMultiFile is the name of the MultiMarker configuration file<br>
	 * nNearClip & nFarClip are near and far clipping values for the OpenGL
	 * projection matrix
	 *
	 * @param nCamParamFile
	 * @param nMultiFile
	 * @param nNearClip
	 * @param nFarClip
	 * @return
	 * @throws ARTKPException
	 */
	public native boolean init(String nCamParamFile, String nMultiFile,
			float nNearClip, float nFarClip) throws ARTKPException;

	/**
	 * pass the image as RGBX (32-bits) in 320x240 pixels.
	 *
	 * @param nImage
	 *            imagen en formato BYTE
	 * @return
	 * @throws ARTKPException
	 */
	public native int calc(byte[] nImage) throws ARTKPException;

	/**
	 * calculates the transformation matrix<br>
	 * pass the image as RGBX (32-bits) in 320x240 pixels.
	 *
	 *
	 * @param nImage
	 *            imagen en formato INT
	 * @return
	 * @throws ARTKPException
	 */
	public native int calc(int[] nImage) throws ARTKPException;

	/**
	 * Returns the number of detected markers used for multi-marker tracking
	 *
	 * @return
	 * @throws ARTKPException
	 */
	public native int getNumDetectedMarkers() throws ARTKPException;

	/**
	 * Enables usage of arDetectMarkerLite. Otherwise arDetectMarker is used In
	 * general arDetectMarker is more powerful since it keeps history about
	 * markers. In some cases such as very low camera refresh rates it is
	 * advantegous to change this. Using the non-lite version treats each image
	 * independent.
	 *
	 * @param nEnable
	 * @throws ARTKPException
	 */
	public native void setUseDetectLite(boolean nEnable) throws ARTKPException;

	/**
	 * Returns array of detected marker IDs<br>
	 * Only access the first getNumDetectedMarkers() markers
	 *
	 * @param nMarkerIDs
	 * @throws ARTKPException
	 */
	public native void getDetectedMarkers(int[] nMarkerIDs)
			throws ARTKPException;

	// / Returns the ARMarkerInfo object for a found marker
	public native long getDetectedMarker(int nWhich) throws ARTKPException;

	// / Calculates the OpenGL transformation matrix for the specified detected
	// markers.
	// public native float calcOpenGLMatrixFromDetectedMarker(int nWhich,
	// float nPatternCenterX, float nPatternCenterY, float nPatternSize,
	// float[] nOpenGLMatrix) throws ARTKPException;

	// / Returns the loaded ARMultiMarkerInfoT object
	/**
	 * If loading the multi-marker config file failed then this method returns
	 * NULL.
	 */
	// public native const ARMultiMarkerInfoT* getMultiMarkerConfig() const = 0;
	// / Provides access to ARToolKit' internal version of the transformation
	// matrix
	/**
	 * This method is primarily for compatibility issues with code previously
	 * using ARToolKit rather than ARToolKitPlus. This is the original
	 * transformation matrix ARToolKit calculates rather than the OpenGL style
	 * version of this matrix that can be retrieved via getModelViewMatrix().
	 */
	// public native void getARMatrix(ARFloat nMatrix[3][4]) const = 0;
}
