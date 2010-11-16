package arme.core.video.impl.ipvideo;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import arme.core.video.ImageSource;
import arme.core.video.ImageSourceException;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

public class AxisCamera implements ImageSource {
	
	/* Mensajes de Error del Video IP */
	private static final String CONFIG_LOAD_ERROR = "Error cargando la configuracion de video";
	
	/**
	 * URL de conexi&oacute;n a la fuente de video
	 */
	private String url;

	/**
	 * Variable de estado que determina si el flujo de video es de tipo MJPG
	 */
	private boolean mjpgMode;

	/**
	 * Variable de estado que determina si la entrada de video debe ser
	 * reescalada
	 */
	private boolean scaleInput;

	/**
	 * Ancho de las im&aacute;genes del flujo de video IP
	 */
	private int width;

	/**
	 * Altura de las im&aacute;genes del flujo de video IP
	 */
	private int height;
	
	public String jpgURL = "http://190.15.29.156/axis-cgi/jpg/image.cgi?resolution=640x480";
	public String mjpgURL = "http://190.15.29.156/axis-cgi/mjpg/video.cgi?resolution=640x480";
	DataInputStream dis;
	private BufferedImage image = null;

	public boolean connected = false;
	private boolean initCompleted = false;
	HttpURLConnection huc = null;


	public void connect() {
		try {
			System.setProperty("proxyHost", "fwingesis.uniquindio.edu.co");
			System.setProperty("proxyPort", "8080");
			
			URL u = new URL(mjpgMode ? url : jpgURL);
			huc = (HttpURLConnection) u.openConnection();
			// System.out.println(huc.getContentType());
			InputStream is = huc.getInputStream();
			connected = true;
			BufferedInputStream bis = new BufferedInputStream(is);
			dis = new DataInputStream(bis);
			if (!initCompleted)
				initDisplay();
		} catch (IOException e) { // incase no connection exists wait and try
			// again, instead of printing the error
			try {
				huc.disconnect();
				Thread.sleep(60);
			} catch (InterruptedException ie) {
				huc.disconnect();
				connect();
			}
			connect();
		} catch (Exception e) {
			;
		}
	}

	public void initDisplay() { // setup the display
		if (mjpgMode)
			readMJPGStream();
		else {
			readJPG();
			disconnect();
		}
		// imageSize = new Dimension(image.getWidth(this),
		// image.getHeight(this));
//		// setPreferredSize(imageSize);
//		parent.setSize(imageSize);
//		parent.validate();
		initCompleted = true;
	}

	public void disconnect() {
		try {
			if (connected) {
				dis.close();
				connected = false;
			}
		} catch (Exception e) {
			;
		}
	}

	public void readStream() { // the basic method to continuously read the
		// stream
		try {
			if (mjpgMode) {
//				while (true) {
				readMJPGStream();
//				}
			} else {
				while (true) {
					connect();
					readJPG();
					disconnect();

				}
			}

		} catch (Exception e) {
			;
		}
	}

	public void readMJPGStream() { // preprocess the mjpg stream to remove the
		// mjpg encapsulation
		readLine(4, dis); // discard the first 3 lines
		readJPG();
		readLine(1, dis); // discard the last two lines
	}

	public void readJPG() { // read the embedded jpeg image
		try {
			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(dis);
			image = decoder.decodeAsBufferedImage();
		} catch (Exception e) {
			e.printStackTrace();
			disconnect();
		}
	}

	public void readLine(int n, DataInputStream dis) { // used to strip out the
		// header lines
		for (int i = 0; i < n; i++) {
			readLine(dis);
		}
	}

	public void readLine(DataInputStream dis) {
		try {
			boolean end = false;
			String lineEnd = "\n"; // assumes that the end of the line is marked
			// with this
			byte[] lineEndBytes = lineEnd.getBytes();
			byte[] byteBuf = new byte[lineEndBytes.length];

			while (!end) {
				dis.read(byteBuf, 0, lineEndBytes.length);
				String t = new String(byteBuf);
				// System.out.print(t); //uncomment if you want to see what the
				// lines actually look like
				if (t.equals(lineEnd))
					end = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws ImageSourceException {
		disconnect();
	}

	@Override
	public int getHeight() throws ImageSourceException {
		return height;
	}

	@Override
	public void getImage(int[] buffer) throws ImageSourceException {
		readStream();

		if (scaleInput)
			image.getGraphics().drawImage(image, 0, 0, width, height, null);

		image.getData().getDataElements(0, 0, width, height, buffer);
	}

	@Override
	public int getWidth() throws ImageSourceException {
		return width;
	}

	@Override
	public void open(String config) throws ImageSourceException {
		loadConfiguration(config);
		connect();
		
		if (scaleInput == false) {
			readStream();

			width = image.getWidth(null);
			height = image.getHeight(null);
		}
	}
	
	private void loadConfiguration(String config) throws ImageSourceException {
		XMLConfigParser xmlConfig = new XMLDefaultConfigParser();

		try {
			xmlConfig.parseConfigFile(config);
		} catch (XMLConfigParserException e) {
			throw new ImageSourceException(CONFIG_LOAD_ERROR, e);
		}

		scaleInput = false;
		url = xmlConfig.getURLValue();
		mjpgMode = xmlConfig.isMJPGMode();

		if (xmlConfig.hasInputResolution()) {
			scaleInput = true;
			width = xmlConfig.getInputWidth();
			height = xmlConfig.getInputHeight();
		}
	}
}
