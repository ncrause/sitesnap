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
import java.net.URL;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nathan Crause <nathan@crause.name>
 */
public class ImageEncoderTest {
	
	public ImageEncoderTest() {
	}

	@Test
	public void testToJPG() throws Exception {
		URL url = new URL("https://www.google.ca");
		PhotographRequest request = new PhotographRequest(url);
		
		request.setMaximumBounds(new Dimension(640, 384));
		
		Photographer photographer = new Photographer();
		BufferedImage image = photographer.click(request);
		ImageEncoder encoder = new ImageEncoder();
		byte[] jpg = encoder.toJPG(image);
		byte[] expected = new byte[] {
			(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, (byte) 0x00,
			(byte) 0x10, (byte) 0x4A, (byte) 0x46, (byte) 0x49, (byte) 0x46,
			(byte) 0x00, (byte) 0x01
		};
		byte[] actual = Arrays.copyOf(jpg, 12);
		
		assertArrayEquals(expected, actual);
	}
	
}
