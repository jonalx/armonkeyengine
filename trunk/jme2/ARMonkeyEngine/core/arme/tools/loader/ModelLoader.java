/* Custom Model Loader
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
package arme.tools.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.jme.image.Texture;
import com.jme.scene.Node;
import com.jme.util.export.Savable;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.converters.AseToJme;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;
import com.jmex.model.converters.Md2ToJme;
import com.jmex.model.converters.Md3ToJme;
import com.jmex.model.converters.MilkToJme;
import com.jmex.model.converters.ObjToJme;

/**
 * Clase que permite cargar cualquier tipo de modelos 3D
 *
 * @author Juli&aacute;n Alejandro Lamprea
 * @version 1.0
 */
public class ModelLoader {

	/**
	 * Carga un modelo 3D de cualquier formato: (jme, 3ds, obj, ase, md2, md3,
	 * ms3d)<br>
	 * El modelo se carga con la escala por defecto (1.0).
	 *
	 * @param modelFile
	 *            Ruta del archivo del modelo 3D
	 * @return Nodo con el modelo 3D cargado
	 * @throws IOException
	 *             Si el archivo es null, no existe o no puede ser leido
	 */
	public static Node load(String modelFile) throws IOException {
		return load(modelFile, 1.0f);
	}

	/**
	 * Carga un modelo 3D de cualquier formato: (jme, 3ds, obj, ase, md2, md3,
	 * ms3d). El par&aacute;metro <code>scale</code> define el tama&ntilde;o a ser escalado
	 * este nodo.
	 *
	 * @param modelFile
	 * @param scale
	 * @return
	 * @throws IOException
	 */
	public static Node load(String modelFile, float scale) throws IOException {

		Node loadedModel = null;
		FormatConverter formatConverter = null;
		ByteArrayOutputStream BO = new ByteArrayOutputStream();
		String modelFormat = modelFile.substring(
				modelFile.lastIndexOf(".") + 1, modelFile.length());
		String modelBinary = modelFile.substring(0,
				modelFile.lastIndexOf(".") + 1)
				+ "jme";

		URL modelURL = ModelLoader.class.getClassLoader().getResource(modelBinary);

		// Verifica la presencia del archivo en formato binario (.jme)
		if (modelURL == null) {

			modelURL = ModelLoader.class.getClassLoader()
					.getResource(modelFile);

			// selecciona el tipo de formato
			if (modelFormat.equalsIgnoreCase("3ds")) {
				formatConverter = new MaxToJme();
			} else if (modelFormat.equals("md2")) {
				formatConverter = new Md2ToJme();
			} else if (modelFormat.equals("md3")) {
				formatConverter = new Md3ToJme();
			} else if (modelFormat.equals("ms3d")) {
				formatConverter = new MilkToJme();
			} else if (modelFormat.equalsIgnoreCase("ase")) {
				formatConverter = new AseToJme();
			} else if (modelFormat.equalsIgnoreCase("obj")) {
				formatConverter = new ObjToJme();
			}

			String path = modelFile.substring(0,
					modelFile.lastIndexOf("/") == -1 ? modelFile
							.lastIndexOf('\\') : modelFile.lastIndexOf('/'));
			URL modelFolder = ModelLoader.class.getClassLoader().getResource(
					path + "/");

			if (modelFolder == null)
				throw new NullPointerException("modelFolder can't be readed");

			try {
				SimpleResourceLocator sr2 = new SimpleResourceLocator(
						modelFolder);
				ResourceLocatorTool.addResourceLocator(
						ResourceLocatorTool.TYPE_TEXTURE, sr2);
			} catch (Exception e) {
				System.err.println("ERROR: " + e.getMessage());
			}
			Texture.DEFAULT_STORE_TEXTURE = false;
			formatConverter.setProperty("mtllib", modelFolder);
			formatConverter.setProperty("texdir", modelFolder);

			System.out.println(modelURL);
			formatConverter.convert(modelURL.openStream(), BO);
//			loadedModel = (Node) BinaryImporter.getInstance().load(
//					new ByteArrayInputStream(BO.toByteArray()));
			loadedModel = (Node) BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));

			// Guarda el modelo en formato binario de jme
			try {
				String file = modelURL.toURI().toString();
				file = file.substring(0,file.lastIndexOf("."));
				System.out.println(file+".jme");
				//new File(file+".jme")
				BinaryExporter.getInstance().save((Savable) loadedModel, new File(modelBinary));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// Carga de una vez el modelo binario de jme
			loadedModel = (Node) BinaryImporter.getInstance().load(modelURL.openStream());
			System.out.println("loaded bynary: "+loadedModel.getName());
		}

		if (scale != 1.0f)
			loadedModel.setLocalScale(scale);

		return loadedModel;
	}
}
