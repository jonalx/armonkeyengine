/* The abstract TrackerSingleMarker superclass.
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

/**
 * Implementación concreta para un tracker que marcadores independientes
 */
public class TrackerSingleMarker extends Tracker {

	static {
		System.loadLibrary("artkp_TrackerSingleMarker");
	}

	public TrackerSingleMarker(boolean ids) throws ARTKPException {
		super(_createTrackerHandle(ids));
	}

	public TrackerSingleMarker(int width, int height, boolean ids)
			throws ARTKPException {
		super(_createTrackerHandle(width, height, ids));
	}

	protected static native long _createTrackerHandle(boolean ids)
			throws ARTKPException;

	protected static native long _createTrackerHandle(int width, int height,
			boolean ids) throws ARTKPException;

	/**
	 * nCamParamFile is the name of the camera parameter file nNearClip &
	 * nFarClip are near and far clipping values for the OpenGL projection
	 * matrix
	 *
	 * @param nCamParamFile
	 * @param nNearClip
	 * @param nFarClip
	 * @return
	 * @throws ARTKPException
	 */
	public native boolean init(String nCamParamFile, float nNearClip,
			float nFarClip) throws ARTKPException;

	/**
	 * Calculates the transformation matrix of all patterns found.
	 *
	 * @param image
	 *            the camera image
	 * @return number of patterns found
	 */
	public int calc(byte[] image) throws ARTKPException {
		return calc(image, -1, true);
	}

	/**
	 * Calculates the transformation matrix of all patterns found.
	 *
	 * @param image
	 *            the camera image
	 * @return number of patterns found
	 */
	public native int calc(byte[] image, int nPattern, boolean updateMatrix)
			throws ARTKPException;

	/**
	 * pass the image as RGBX (32-bits) in 320x240 pixels.
	 */
	public int calc(int[] nImage) throws ARTKPException {
		return calc(nImage, -1, true);
	}

	/**
	 * pass the image as RGBX (32-bits) in 320x240 pixels.
	 */
	public native int calc(int[] nImage, int nPattern, boolean updateMatrix)
			throws ARTKPException;

	/**
	 * calculates the transformation matrix. pass the image as RGBX (32-bits) in
	 * 320x240 pixels. if nPattern is not -1 then only this pattern is accepted
	 * otherwise any found pattern will be used.
	 */

	// TODO: Implement this
	// public native int calc(const unsigned char* nImage, int nPattern=-1, bool
	// nUpdateMatrix=true, ARMarkerInfo** nMarker_info=NULL, int*
	// nNumMarkers=NULL);
	/**
	 * pass the patterns filename
	 *
	 * @param nFileName
	 * @return
	 */
	public native int addPattern(String nFileName);

	/**
	 * Returns the confidence value of the currently best detected marker.
	 *
	 * @return
	 */
	public native float getConfidence();

	/**
	 * Returns the number of detected markers used for single-marker tracking
	 *
	 * @return
	 * @throws ARTKPException
	 */
	public native int getNumDetectedMarkers() throws ARTKPException;

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
}