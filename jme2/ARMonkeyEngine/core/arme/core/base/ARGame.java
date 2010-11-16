/* SimpleGame for Augmented Reality with ARME
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

import arme.core.control.ARMonkeyEngine;
import arme.core.control.ARMonkeyEngineException;

import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.JmeException;
import com.jme.util.Debug;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.geom.Debugger;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;
import com.jme.util.stat.graph.DefColorFadeController;
import com.jme.util.stat.graph.GraphFactory;
import com.jme.util.stat.graph.LineGrapher;
import com.jme.util.stat.graph.TabledLabelGrapher;

public abstract class ARGame extends ARBaseGame {

	/**
     * True if the renderer should display bounds.
     */
    protected boolean showBounds = false;

    /**
     * True if the renderer should display normals.
     */
    protected boolean showNormals = false;

    /**
     * True if the we should show the stats graphs.
     */
    protected boolean showGraphs = false;

    /**
     * True if the renderer should display the depth buffer.
     */
    protected boolean showDepth = false;


    /**
     * A wirestate to turn on and off for the rootNode
     */
    protected WireframeState wireState;


    /**
     * A lightstate to turn on and off for the rootNode
     */
    protected LightState lightState;


    /**
     * boolean for toggling the simpleUpdate and geometric update parts of the
     * game loop on and off.
     */
    protected boolean pause;


	/**
     * The root node for our stats graphs.
     */
    protected Node graphNode;

    private TabledLabelGrapher tgrapher;

//    private TimedAreaGrapher lgrapher;
    private LineGrapher lgrapher;

    private Quad lineGraph, labGraph;

    public ARGame() {
    	System.setProperty("jme.stats", "set");
	}

    @Override
    protected void initSystem() throws JmeException {
    	super.initSystem();

    	/** Assign key P to action "toggle_pause". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_pause",
                KeyInput.KEY_P );
        /** Assign key T to action "toggle_wire". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_wire",
                KeyInput.KEY_T );
        /** Assign key L to action "toggle_lights". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_lights",
                KeyInput.KEY_L );
        /** Assign key B to action "toggle_bounds". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_bounds",
                KeyInput.KEY_B );
        /** Assign key N to action "toggle_normals". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_normals",
                KeyInput.KEY_N );

        /** Assign key R to action "mem_report". */
        KeyBindingManager.getKeyBindingManager().set("mem_report",
                KeyInput.KEY_R);

        KeyBindingManager.getKeyBindingManager().set( "exit",
                KeyInput.KEY_ESCAPE );

        KeyBindingManager.getKeyBindingManager().set( "screen_shot",
                KeyInput.KEY_F1 );
        KeyBindingManager.getKeyBindingManager().set( "parallel_projection",
                KeyInput.KEY_F2 );
        KeyBindingManager.getKeyBindingManager().set( "toggle_depth",
                KeyInput.KEY_F3 );
        KeyBindingManager.getKeyBindingManager().set( "toggle_stats",
                KeyInput.KEY_F4 );
    }

    @Override
    protected void initGame() {
    	/** Create rootNode */
		rootNode = new Node("rootNode");

		/**
         * Create a wirestate to toggle on and off. Starts disabled with default
         * width of 1 pixel.
         */
        wireState = display.getRenderer().createWireframeState();
        wireState.setEnabled( false );
        rootNode.setRenderState( wireState );

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

		if (Debug.stats) {
            graphNode = new Node( "Graph node" );
            graphNode.setCullHint( Spatial.CullHint.Never );
            statNode.attachChild(graphNode);

            setupStatGraphs();
            setupStats();
        }


		// ---- LIGHTS
        /** Set up a basic, default light. */
        PointLight light = new PointLight();
        light.setDiffuse( new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ) );
        light.setAmbient( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ) );
        light.setLocation( new Vector3f( 100, 100, 100 ) );
        light.setEnabled( true );

        /** Attach the light to a lightState and the lightState to rootNode. */
        lightState = display.getRenderer().createLightState();
        lightState.setEnabled( true );
        lightState.attach( light );
        rootNode.setRenderState( lightState );

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


    @Override
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
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE).execute();

        /** If toggle_pause is a valid command (via key p), change pause. */
        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_pause", false ) ) {
            pause = !pause;
        }

        /** If step is a valid command (via key ADD), update scenegraph one unit. */
        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "step", true ) ) {
        	simpleUpdate();
        	rootNode.updateGeometricState(tpf, true);
        }

        /** If toggle_wire is a valid command (via key T), change wirestates. */
        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_wire", false ) ) {
            wireState.setEnabled( !wireState.isEnabled() );
            rootNode.updateRenderState();
        }
        /** If toggle_lights is a valid command (via key L), change lightstate. */
        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_lights", false ) ) {
            lightState.setEnabled( !lightState.isEnabled() );
            rootNode.updateRenderState();
        }
        /** If toggle_bounds is a valid command (via key B), change bounds. */
        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_bounds", false ) ) {
            showBounds = !showBounds;
        }

        /** If toggle_depth is a valid command (via key F3), change depth. */
        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_depth", false ) ) {
            showDepth = !showDepth;
        }

        if (Debug.stats) {
            /** handle toggle_stats command (key F4) */
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                    "toggle_stats", false ) ) {
                    showGraphs = !showGraphs;
                    Debug.updateGraphs = showGraphs;
                    labGraph.clearControllers();
                    lineGraph.clearControllers();
                    labGraph.addController(new DefColorFadeController(labGraph, showGraphs ? .6f : 0f, showGraphs ? .5f : -.5f));
                    lineGraph.addController(new DefColorFadeController(lineGraph, showGraphs ? .6f : 0f, showGraphs ? .5f : -.5f));
            }
        }

        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_normals", false ) ) {
            showNormals = !showNormals;
        }

        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "screen_shot", false ) ) {
            display.getRenderer().takeScreenShot( "ARGameScreenShot" );
        }

        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "parallel_projection", false ) ) {
            if ( cam.isParallelProjection() ) {
                cameraPerspective();
            }
            else {
                cameraParallel();
            }
        }

        if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
                "mem_report", false ) ) {
            long totMem = Runtime.getRuntime().totalMemory();
            long freeMem = Runtime.getRuntime().freeMemory();
            long maxMem = Runtime.getRuntime().maxMemory();

            logger.info("|*|*|  Memory Stats  |*|*|");
            logger.info("Total memory: "+(totMem>>10)+" kb");
            logger.info("Free memory: "+(freeMem>>10)+" kb");
            logger.info("Max memory: "+(maxMem>>10)+" kb");
        }

        if ( KeyBindingManager.getKeyBindingManager().isValidCommand( "exit",
                false ) ) {
            finish();
        }


        if ( !pause ) {

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
    }

    @Override
    protected void render(float interpolation) {
    	super.render(interpolation);
    	Renderer r = display.getRenderer();
    	doDebug(r);
    }

    private void doDebug(Renderer r) {
        /**
         * If showing bounds, draw rootNode's bounds, and the bounds of all its
         * children.
         */
        if ( showBounds ) {
            Debugger.drawBounds( rootNode, r, true );
        }

        if ( showNormals ) {
            Debugger.drawNormals( rootNode, r );
            Debugger.drawTangents( rootNode, r );
        }

        if (showDepth) {
            r.renderQueue();
            Debugger.drawBuffer(Texture.RenderToTextureType.Depth, Debugger.NORTHEAST, r);
        }
    }


	/**
     * Set up which stats to graph
     *
     */
    private void setupStats() {
        lgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.green);
        lgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Stipple.name(), 0XFF0F);
        lgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.cyan);
        lgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_QUAD_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.lightGray);
        lgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_LINE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.red);
        lgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_GEOM_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.gray);
        lgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        lgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.orange);
        lgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);

        tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Name.name(), "Frames/s:");
        tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tris:");
        tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Quads:");
        tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Lines:");
        tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Objs:");
        tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
        tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
        tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tex binds:");
        tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);

        // If you want to try out
//        lgrapher.addConfig(StatType.STAT_RENDER_TIMER,
//				TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.blue);
//		lgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER,
//				TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.white);
//		lgrapher.addConfig(StatType.STAT_STATES_TIMER,
//				TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.yellow);
//		lgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER,
//				TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.red);
//
//		tgrapher.addConfig(StatType.STAT_RENDER_TIMER,
//				TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//		tgrapher.addConfig(StatType.STAT_RENDER_TIMER,
//				TabledLabelGrapher.ConfigKeys.Name.name(), "Render:");
//		tgrapher.addConfig(StatType.STAT_RENDER_TIMER,
//				TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//		tgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER,
//				TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//		tgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER,
//				TabledLabelGrapher.ConfigKeys.Name.name(), "Other:");
//		tgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER,
//				TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//		tgrapher.addConfig(StatType.STAT_STATES_TIMER,
//				TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//		tgrapher.addConfig(StatType.STAT_STATES_TIMER,
//				TabledLabelGrapher.ConfigKeys.Name.name(), "States:");
//		tgrapher.addConfig(StatType.STAT_STATES_TIMER,
//				TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//		tgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER,
//				TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//		tgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER,
//				TabledLabelGrapher.ConfigKeys.Name.name(), "DisplaySwap:");
//		tgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER,
//				TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//
//		StatCollector.addTimedStat(StatType.STAT_RENDER_TIMER);
//		StatCollector.addTimedStat(StatType.STAT_STATES_TIMER);
//		StatCollector.addTimedStat(StatType.STAT_UNSPECIFIED_TIMER);
//		StatCollector.addTimedStat(StatType.STAT_DISPLAYSWAP_TIMER);
    }

    /**
     * Set up the graphers we will use and the quads we'll show the stats on.
     *
     */
    private void setupStatGraphs() {
        StatCollector.setSampleRate(1000L);
        StatCollector.setMaxSamples(40);

        lineGraph = new Quad("lineGraph", display.getWidth(), display.getHeight()*.75f) {
            private static final long serialVersionUID = 1L;
            @Override
            public void draw(Renderer r) {
                StatCollector.pause();
                super.draw(r);
                StatCollector.resume();
            }
        };
        lgrapher = GraphFactory.makeLineGraph((int)(lineGraph.getWidth()+.5f), (int)(lineGraph.getHeight()+.5f), lineGraph);
//      lgrapher = GraphFactory.makeTimedGraph((int)(lineGraph.getWidth()+.5f), (int)(lineGraph.getHeight()+.5f), lineGraph);
        lineGraph.setLocalTranslation((display.getWidth()*.5f), (display.getHeight()*.625f),0);
        lineGraph.setCullHint(CullHint.Always);
        lineGraph.getDefaultColor().a = 0;
        graphNode.attachChild(lineGraph);

        Text f4Hint = new Text("f4", "F4 - toggle stats") {
            private static final long serialVersionUID = 1L;
            @Override
            public void draw(Renderer r) {
                StatCollector.pause();
                super.draw(r);
                StatCollector.resume();
            }
        };
        f4Hint.setCullHint( Spatial.CullHint.Never );
        f4Hint.setRenderState( Text.getDefaultFontTextureState() );
        f4Hint.setRenderState( Text.getFontBlend() );
        f4Hint.setLocalScale(.8f);
        f4Hint.setTextColor(ColorRGBA.gray);
        f4Hint.setLocalTranslation(display.getRenderer().getWidth() - f4Hint.getWidth() - 15, display.getRenderer().getHeight() - f4Hint.getHeight() - 10, 0);
        graphNode.attachChild(f4Hint);

        labGraph = new Quad("labelGraph", display.getWidth(), display.getHeight()*.25f) {
            private static final long serialVersionUID = 1L;
            @Override
            public void draw(Renderer r) {
                StatCollector.pause();
                super.draw(r);
                StatCollector.resume();
            }
        };
        tgrapher = GraphFactory.makeTabledLabelGraph((int)(labGraph.getWidth()+.5f), (int)(labGraph.getHeight()+.5f), labGraph);
        tgrapher.setColumns(2);
        tgrapher.setMinimalBackground(false);
        tgrapher.linkTo(lgrapher);
        labGraph.setLocalTranslation((display.getWidth()*.5f), (display.getHeight()*.125f),0);
        labGraph.setCullHint(CullHint.Always);
        labGraph.getDefaultColor().a = 0;
        graphNode.attachChild(labGraph);

    }

}
