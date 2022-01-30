/***************************************************************************
 *   Class DPShape                                                         *
 *                                                                         *
 *   Copyright (C) 2018 by Marcelo Silveira                                *
 *   MSX Sketch Tools: http://marmsx.msxall.com                            *
 *   Contact: flamar98@hotmail.com                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

/***************************************************************************
 * Class description:                                                      *
 * DPShape - Render Dynamic Publisher shapes                               *
 ***************************************************************************/

import java.util.Arrays;
import java.awt.image.BufferedImage;

public class DPShape {

	// Shape
	// Header: width, height
	// Dimensions: from 1x1 to 512x212

	private String msg;

	public String getLog() {
		return msg;
	}

	//
	//  Read Dynamic Publisher shape and convert it to image
	//

	public BufferedImage renderShape(byte data[]) {
		byte [] sc6pat;
		DPScreen dp = new DPScreen();
		int p=0, width, height, size;
		msg = "";

		if (p+3 >= data.length)
			return null;

		// Header
		width = (data[p+1] & 0xFF)*256 + (data[p] & 0xFF);
		height = (data[p+3] & 0xFF)*256 + (data[p+2] & 0xFF);
		size = width*height;
		size = (int) Math.ceil(size/4.0);
		p+=4;

		if (width < 8 || width > 512 || height < 1 || height > 212) {
			msg = "Bad size on shape: ";
			return null;
		}

		if (size > data.length-4) {
			msg = "Incomplete data: ";
			return null;
		}

		sc6pat = Arrays.copyOfRange(data, p, p+size);

		return dp.render(sc6pat, width, height);
	}


	//
	//  Convert image to Dynamic Publisher shape
	//

	private byte[] joinData(byte [] d1, byte [] d2) {
		byte [] data = new byte[d1.length+d2.length];

		System.arraycopy(d1, 0, data, 0, d1.length);
		System.arraycopy(d2, 0, data, d1.length, d2.length);
		
		return data;
	}

	public byte[] formatShape(BufferedImage img) {
		byte [] header = new byte[4];
		byte [] data;

		// Add header
		header[0] = (byte) (img.getWidth() & 0xFF);
		header[1] = (byte) ((img.getWidth() >> 8) & 0xFF);
		header[2] = (byte) (img.getHeight() & 0xFF);
		header[3] = (byte) ((img.getHeight() >> 8) & 0xFF);

		// Convert image to MSX file
		DPScreen dp = new DPScreen();
		data = dp.format(img);

		// Join
		data = joinData(header, data);

		return data;
	}

}
