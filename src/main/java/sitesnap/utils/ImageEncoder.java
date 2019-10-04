/*
 * Copyright (C) 2019 Nathan Crause <nathan@crause.name>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sitesnap.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * Given an image, this class can produce binary output of that image wrapped
 * up as either a JPG, or a PNG.
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class ImageEncoder {

	public ImageEncoder() {
	}
	
	public byte[] toJPG(BufferedImage image, int compression) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		MemoryCacheImageOutputStream stream = new MemoryCacheImageOutputStream(output);
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
		ImageWriteParam params = writer.getDefaultWriteParam();
		IIOImage source = new IIOImage(image, null, null);
		
		params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		params.setCompressionQuality((float) compression / 100f);
		
		writer.setOutput(stream);
		writer.write(null, source, params);
		writer.dispose();
		
		return output.toByteArray();
	}
	
	/**
	 * Returns a "web"-safe JPG (that is compression at 60%)
	 * 
	 * @param image the image to compress
	 * @return 
	 * @throws java.io.IOException 
	 */
	public byte[] toJPG(BufferedImage image) throws IOException {
		return toJPG(image, 60);
	}
	
}
