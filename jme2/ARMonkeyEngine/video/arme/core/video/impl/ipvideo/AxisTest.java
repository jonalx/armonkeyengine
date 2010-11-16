package arme.core.video.impl.ipvideo;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

public class AxisTest extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public boolean useMJPGStream = true;
	public String jpgURL = "http://190.15.29.156/axis-cgi/jpg/image.cgi?resolution=352x240";
//	public String mjpgURL = "http://190.15.29.156/axis-cgi/mjpg/video.cgi?resolution=352x240";
	public String mjpgURL = "http://190.15.29.156/mjpg/video.mjpg";
	
	DataInputStream dis;
	private Image image = null;
	public Dimension imageSize = null;
	public boolean connected = false;
	private boolean initCompleted = false;
	HttpURLConnection huc = null;
	Component parent;

	/* Creates a new instance of AxisCamera */
	public AxisTest(Component parent_) {
		parent = parent_;
	}

	public void connect() {
		try {
			System.setProperty("proxyHost", "fwingesis.uniquindio.edu.co");
			System.setProperty("proxyPort", "8080");
			
			URL u = new URL(useMJPGStream ? mjpgURL : jpgURL);
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
			e.printStackTrace();
			try {
				huc.disconnect();
				Thread.sleep(60);
			} catch (InterruptedException ie) {
				huc.disconnect();
				connect();
			}
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initDisplay() { // setup the display
		if (useMJPGStream)
			readMJPGStream();
		else {
			readJPG();
			disconnect();
		}
		imageSize = new Dimension(image.getWidth(this), image.getHeight(this));
		setPreferredSize(imageSize);
		parent.setSize(imageSize);
		parent.validate();
		initCompleted = true;
	}

	public void disconnect() {
		try {
			if (connected) {
				dis.close();
				connected = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void paint(Graphics g) { // used to set the image on the panel
		if (image != null)
			g.drawImage(image, 0, 0, this);
	}

	public void readStream() { // the basic method to continuously read the
								// stream
		try {
			if (useMJPGStream) {
				while (true) {
					readMJPGStream();
					parent.repaint();
				}
			} else {
				while (true) {
					connect();
					readJPG();
					parent.repaint();
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

	public void run() {
		connect();
		readStream();
	}

	public static void main(String[] args) {
		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		AxisTest axPanel = new AxisTest(jframe);
		new Thread(axPanel).start();
		jframe.getContentPane().add(axPanel);
		jframe.pack();
		jframe.setVisible(true);
	}

}