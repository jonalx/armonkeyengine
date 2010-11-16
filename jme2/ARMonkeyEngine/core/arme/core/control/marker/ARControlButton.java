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

import arme.core.control.marker.events.ARControlPublisher;

/**
 * ARControl que envia eventos cuando es presionado u ocultado durante un
 * pequeño rango de tiempo para simular el clic de un boton.
 *
 * @version 1.0
 */
public class ARControlButton extends ARControlPublisher {

	private long timeHidden = 0;
	private boolean deactivated;

	private static int MIN_TIME = 80;
	private static int MAX_TIME = 450;

	public ARControlButton(boolean calcPose) {
		super(calcPose);
	}

	@Override
	protected void activated() {
		if (deactivated) {
			deactivated = false;
			timeHidden = System.currentTimeMillis() - timeHidden;
			if (timeHidden > MIN_TIME && timeHidden < MAX_TIME) {
				fireControlEvent();
			} else
				System.out.println(timeHidden);
			timeHidden = 0;
		}
	}

	@Override
	protected void deactivated() {
		if (!deactivated) {
			timeHidden = System.currentTimeMillis();
			deactivated = true;
		}
	}

	public static int getMinTime() {
		return MIN_TIME;
	}

	public static void setMinTime(int minTime) {
		MIN_TIME = minTime;
	}

	public static int getMaxTime() {
		return MAX_TIME;
	}

	public static void setMaxTime(int maxTime) {
		MAX_TIME = maxTime;
	}
	
	
}