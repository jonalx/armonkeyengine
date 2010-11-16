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
package arme.core.control.marker.events;

import java.util.ArrayList;

import arme.core.control.marker.ARControl;

public abstract class ARControlPublisher extends ARControl {

	private ArrayList<ARControlListener> observers;

	protected ARControlPublisher(boolean calcPose) {
		super(calcPose);
		observers = new ArrayList<ARControlListener>(1);
	}

	public void addControlListener(ARControlListener observer) {
		observers.add(observer);
	}

	public void fireControlEvent() {
		for (ARControlListener ob : observers) {
			ARControlEvent event = new ARControlEvent(this);
			ob.update(event);
		}
	}
}
