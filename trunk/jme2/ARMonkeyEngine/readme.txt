ARMonkeyEngine Framework
------------------------
[Augmented Reality framework for using with jMonkeyEngine 2.*]
@Copyright 2010 - Aureal Vision


DEPLOY
- Use build.xml ant file for generate distributable .jar files for this framework.

USAGE
- Configure:
  - Configure basic project with jMonkeyEngine 2.*
  - Add the arme-*.jar files to your classpath
  - Copy the native files to your project.
    Add native/artookitplus to native build path in arme-core.jar
    Add native/video to native build path in arme-jarvideo.jar (for usb webcams)
    Note: DSVL.dll and libARvideo.dll native librarys must be placed in the root of the project.
          if this native dll are into any subfolder, they don't work correctly. (use this uf you use a usb webcam)

- Implement:
  - SimpleGame:
    Use this example:
    public class Application extends ARGame {
    
        protected void simpleInitGame() {
            //Create a basic shape
            Vector3f min = new Vectgor3f(-20f, -20f,  0f);
            Vector3f max = new Vectgor3f( 20f,  20f, 40f);
            Box box = new Box("myBox", min, max);
            box.setRandomColors();
            
            //Create a Augmented Reality Node
            ARNode node = new ARNode("myARBox");
            node.attachChild(box);
            
            //Register arnode into ARME framework and using a template marker
            arEngine.registerMarker(node, patterns/patt.hiro");
            
            //add ARNode to rootNode
            rootNode.attachChild(node);
        }
        
        public ARConfiguration getARConfiguration() {
            return ARGame.getDefaultARConfiguration();
        }
	
        public ImageSource getImageSource() {
            return JARVideo.getInstance();
        }
    }
    

    
  - Advanced Game:
    Use this example:
    
    public class Application extends BaseGame implements ARApplication {
    
       .... configure all your jME Game ....
       /* ARMonkeyEngine Framework Controller */
       private ARMonkeyEngine arEngine;
       
       protected void initGame() {
           try {
               arEngine = new ARMonkeyEngine(this);
           } catch (ARMonkeyEngineException e) {
               e.printStackTrace();
               instance.finish();
           }
           ...
           simpleInitGame();          
       }
    
        protected void simpleInitGame() {
            //Create a basic shape
            Vector3f min = new Vectgor3f(-20f, -20f,  0f);
            Vector3f max = new Vectgor3f( 20f,  20f, 40f);
            Box box = new Box("myBox", min, max);
            box.setRandomColors();
            
            //Create a Augmented Reality Node
            ARNode node = new ARNode("myARBox");
            node.attachChild(box);
            
            //Register arnode into ARME framework and using a template marker
            arEngine.registerMarker(node, "data/patt.hiro");
            
            //add ARNode to rootNode
            rootNode.attachChild(node);
        }
        
        public ARConfiguration getARConfiguration() {
            ARConfiguration config = new ARConfiguration();
            config.setCameraConfigFile("data/WDM_camera_flipV.xml");
            config.setCameraParametersFile("data/camera_para.dat");
            config.setNearClip(1.0f);
            config.setFarClip(10000.0f);
            return config;           
        }
	
        public ImageSource getImageSource() {
            return JARVideo.getInstance();
        }
        
        protected final void update(float interpolation) {
            .....
            try {
                arEngine.update();
            } catch (ARMonkeyEngineException e) {
                e.printStackTrace();
            }
            simpleUdate();
        }
    }