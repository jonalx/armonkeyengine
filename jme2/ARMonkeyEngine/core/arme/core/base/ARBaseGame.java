/* ARBaseGame implementation, Base preconfigured implementation for use jME with ARME 
 *
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
package arme.core.base;

import java.util.logging.Level;
import java.util.logging.Logger;

import arme.core.control.ARApplication;
import arme.core.control.ARConfiguration;
import arme.core.control.ARMonkeyEngine;
import arme.core.control.ARMonkeyEngineException;

import com.jme.app.AbstractGame;
import com.jme.app.BaseGame;
import com.jme.app.BaseSimpleGame;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.joystick.JoystickInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Debug;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.stat.StatCollector;
import com.jmex.audio.AudioSystem;

public abstract class ARBaseGame extends BaseGame implements ARApplication {

	protected static final Logger logger = Logger
			.getLogger(BaseSimpleGame.class.getName());

	/**
	 * The ARMonkeyEngine
	 */
	protected ARMonkeyEngine arEngine;

	/**
	 * The camera that we see through.
	 */
	protected Camera cam;

	/**
	 * The root of our normal scene graph.
	 */
	protected Node rootNode;

	/**
	 * High resolution timer for jME.
	 */
	protected Timer timer;

	/**
	 * The root node for our stats and text.
	 */
	protected Node statNode;

	/**
	 * Alpha bits to use for the renderer. Any changes must be made prior to
	 * call of start().
	 */
	protected int alphaBits = 0;

	/**
	 * Depth bits to use for the renderer. Any changes must be made prior to
	 * call of start().
	 */
	protected int depthBits = 8;

	/**
	 * Stencil bits to use for the renderer. Any changes must be made prior to
	 * call of start().
	 */
	protected int stencilBits = 0;

	/**
	 * Number of samples to use for the multisample buffer. Any changes must be
	 * made prior to call of start().
	 */
	protected int samples = 0;

	/**
	 * Simply an easy way to get at timer.getTimePerFrame(). Also saves math
	 * cycles since you don't call getTimePerFrame more than once per frame.
	 */
	protected float tpf;


	/**
	 * Updates the timer, sets tpf, updates the input and updates the fps
	 * string. Also checks keys for toggling pause, bounds, normals, lights,
	 * wire etc.
	 *
	 * @param interpolation
	 *            unused in this implementation
	 * @see AbstractGame#update(float interpolation)
	 */
	protected void update(float interpolation) {
		/** Recalculate the framerate. */
		timer.update();
		/** Update tpf to time per frame according to the Timer. */
		tpf = timer.getTimePerFrame();

		/** Check for key/mouse updates. */
		updateInput();

		/** update stats, if enabled. */
		if (Debug.stats) {
			StatCollector.update();
		}

		// Execute updateQueue item
		GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE)
				.execute();

		try {
			arEngine.update();
		} catch (ARMonkeyEngineException e) {
			e.printStackTrace();
		}

		/** Call simpleUpdate in any derived classes of SimpleGame. */
		simpleUpdate();

		/** Update controllers/render states/transforms/bounds for rootNode. */
		rootNode.updateGeometricState(tpf, true);
		statNode.updateGeometricState(tpf, true);
	}

	/**
	 * Check for key/mouse updates. Allow overriding this method to skip update
	 * in subclasses.
	 */
	protected void updateInput() {
		// input.update( tpf );
	}

	/**
	 * Clears stats, the buffers and renders bounds and normals if on.
	 *
	 * @param interpolation
	 *            unused in this implementation
	 * @see AbstractGame#render(float interpolation)
	 */
	protected void render(float interpolation) {

		Renderer r = display.getRenderer();

		/** Clears the previously rendered information. */
		r.clearBuffers();

		// Execute renderQueue item
		GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
				.execute();

		/** Draw the rootNode and all its children. */
		r.draw(rootNode);

		/** Call simpleRender() in any derived classes. */
		simpleRender();

		/** Draw the stats node to show our stat charts. */
		r.draw(statNode);

	}

	/**
	 * Creates display, sets up camera, and binds keys. Called in
	 * BaseGame.start() directly after the dialog box.
	 *
	 * @see AbstractGame#initSystem()
	 */
	protected void initSystem() throws JmeException {
		logger.info(getVersion());
		try {
			/**
			 * Get a DisplaySystem acording to the renderer selected in the
			 * startup box.
			 */
			display = DisplaySystem.getDisplaySystem(settings.getRenderer());

			display.setMinDepthBits(depthBits);
			display.setMinStencilBits(stencilBits);
			display.setMinAlphaBits(alphaBits);
			display.setMinSamples(samples);

			/** Create a window with the startup box's information. */
			display.createWindow(settings.getWidth(), settings.getHeight(),
					settings.getDepth(), settings.getFrequency(), settings
							.isFullscreen());
			logger.info("Running on: " + display.getAdapter()
					+ "\nDriver version: " + display.getDriverVersion() + "\n"
					+ display.getDisplayVendor() + " - "
					+ display.getDisplayRenderer() + " - "
					+ display.getDisplayAPIVersion());

			/**
			 * Create a camera specific to the DisplaySystem that works with the
			 * display's width and height
			 */
			cam = display.getRenderer().createCamera(display.getWidth(),
					display.getHeight());

		} catch (JmeException e) {
			/**
			 * If the displaysystem can't be initialized correctly, exit
			 * instantly.
			 */
			logger.log(Level.SEVERE, "Could not create displaySystem", e);
			System.exit(1);
		}

		/** Set a black background. */
		display.getRenderer().setBackgroundColor(ColorRGBA.black.clone());

		/** Set up how our camera sees. */
		cameraPerspective();
		Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
		Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);

		/** Move our camera to a correct place and orientation. */
		cam.setFrame(loc, left, up, dir);

		/** Signal that we've changed our camera's location/frustum. */
		cam.update();

		/** Assign the camera to this renderer. */
		display.getRenderer().setCamera(cam);

		MouseInput.get().setCursorVisible(true);

		/** Get a high resolution timer for FPS updates. */
		timer = Timer.getTimer();

		/** Sets the title of our display. */
		String className = getClass().getName();
		if (className.lastIndexOf('.') > 0)
			className = className.substring(className.lastIndexOf('.') + 1);
		display.setTitle(className);

	}

	protected void cameraPerspective() {
		cam.setFrustumPerspective(45.0f, (float) display.getWidth()
				/ (float) display.getHeight(), 1, 1000);
		cam.setParallelProjection(false);
		cam.update();
	}

	protected void cameraParallel() {
		cam.setParallelProjection(true);
		float aspect = (float) display.getWidth() / display.getHeight();
		cam.setFrustum(-100, 1000, -50 * aspect, 50 * aspect, -50, 50);
		cam.update();
	}

	/**
	 * Creates rootNode, lighting, statistic text, and other basic render
	 * states. Called in BaseGame.start() after initSystem().
	 *
	 * @see AbstractGame#initGame()
	 */
	protected void initGame() {

		/** Create rootNode */
		rootNode = new Node("rootNode");

		/**
		 * Create a ZBuffer to display pixels closest to the camera above
		 * farther ones.
		 */
		ZBufferState buf = display.getRenderer().createZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		rootNode.setRenderState(buf);

		// -- STATS, text node
		// Finally, a stand alone node (not attached to root on purpose)
		statNode = new Node("Stats node");
		statNode.setCullHint(Spatial.CullHint.Never);
		statNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		try {
			arEngine = new ARMonkeyEngine(this);

		} catch (ARMonkeyEngineException e) {
			e.printStackTrace();
			this.finish();
		}

		/** Let derived classes initialize. */
		simpleInitGame();

		timer.reset();

		/**
		 * Update geometric and rendering information for both the rootNode and
		 * fpsNode.
		 */
		rootNode.updateGeometricState(0.0f, true);
		rootNode.updateRenderState();
		statNode.updateGeometricState(0.0f, true);
		statNode.updateRenderState();

		timer.reset();
	}

	/**
	 * Called near end of initGame(). Must be defined by derived classes.
	 */
	protected abstract void simpleInitGame();

	/**
	 * Can be defined in derived classes for custom updating. Called every frame
	 * in update.
	 */
	protected void simpleUpdate() {
		// do nothing
	}

	/**
	 * Can be defined in derived classes for custom rendering. Called every
	 * frame in render.
	 */
	protected void simpleRender() {
		// do nothing
	}

	/**
	 * unused
	 *
	 * @see AbstractGame#reinit()
	 */
	protected void reinit() {
		// do nothing
	}

	/**
	 * Cleans up the keyboard.
	 *
	 * @see AbstractGame#cleanup()
	 */
	protected void cleanup() {
		logger.info("Cleaning up resources.");

		TextureManager.doTextureCleanup();
		if (display != null && display.getRenderer() != null)
			display.getRenderer().cleanup();
		KeyInput.destroyIfInitalized();
		MouseInput.destroyIfInitalized();
		JoystickInput.destroyIfInitalized();
		if (AudioSystem.isCreated()) {
			AudioSystem.getSystem().cleanup();
		}
	}

	/**
	 * Calls the quit of BaseGame to clean up the display and then closes the
	 * JVM.
	 */
	protected void quit() {
		super.quit();
		System.exit(0);
	}

	public Camera getCamera() {
		return cam;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public static ARConfiguration getDefaultARConfiguration() {
		ARConfiguration config = new ARConfiguration();
		config.setCameraConfigFile("data/WDM_camera_flipV.xml");
		config.setCameraParametersFile("data/camera_para.dat");
		config.setNearClip(1.0f);
		config.setFarClip(1000000.0f);
		return config;
	}
}
