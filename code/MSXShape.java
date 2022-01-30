/***************************************************************************
 *   Class MSXShape                                                        *
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
 * MSXShape - MSX screen 2 Graphos III shapes                              *
 ***************************************************************************/

import java.util.Arrays;
import java.awt.image.BufferedImage;

public class MSXShape {

	// Shape
	// Header: Shape id, type, shape width, shape height
	// Dimensions: from 8x8 to 240x176

	private void printHeader(int id, int type, int width, int height) {
		System.out.println("ID: "+id);
		System.out.println("Type: "+type);
		System.out.println("Width: "+width);
		System.out.println("Height: "+height);
	}


	//
	//  Read shape file and convert it to image
	//

	public String renderShape(byte data[], ImageStack img_stack, String name) {
		byte [] sc2pat;
		MSXScreen msx = new MSXScreen();
		int p=0, id, type, width, height, size;

		while (p < data.length) {
			if (p+3 >= data.length)
				return "";

			// Header
			id = data[p] & 0xFF;
			type = data[p+1] & 0xFF;
			width = data[p+2] & 0xFF;
			height = 8*(data[p+3] & 0xFF);
			size = width*height/8;
			p+=4;

			if (id == 0xFF)
				return "";

			if (width < 8 || width > 240 || height < 8 || height > 176) {
				return "Bad size on shape id="+Integer.toString(id);
			}

			if (type < 0 || type > 4) {
				return "Bad type on shape id="+Integer.toString(id);
			}

			if (p+size > data.length) {
				return "Data corrupt on shape id="+Integer.toString(id);
			}

			sc2pat = Arrays.copyOfRange(data, p, p+size);
			img_stack.insertImage(msx.renderScr2(sc2pat, width, height), name);

			p += size;

			if (type == 2 || type == 3)
				p += size;
			if (type == 4)
				p += size;
		}

		return "";
	}


	//
	//  Convert image to shape file
	//

	private byte[] joinData(byte [] d1, byte [] d2) {
		byte [] data = new byte[d1.length+d2.length];

		System.arraycopy(d1, 0, data, 0, d1.length);
		System.arraycopy(d2, 0, data, d1.length, d2.length);
		
		return data;
	}

	// Ensure file size multiple of 128 bytes
	private byte [] checkFileSize(byte [] data) {
		if (data.length % 128 != 0)
			data = joinData(data, new byte[128 - (data.length % 128)]);
		return data;
	}

	// Multiple images
	public byte[] formatShapes(ImageStack img_stack) {
		byte [] data = new byte[0];
		byte [] tmp;		
		
		for (int i=0; i<img_stack.size(); i++) {
			tmp = formatShape(img_stack.getImage(i) , i+1, (i==img_stack.size()-1));
			data = joinData(data, tmp);
		}

		return checkFileSize(data);
	}

	// Single image
	public byte[] formatShape(BufferedImage img, int id) {
		byte [] data = formatShape(img, id, true);
		return checkFileSize(data);
	}

	private byte[] formatShape(BufferedImage img, int id, boolean addEOF) {
		byte [] header = new byte[4];
		byte [] data;
		byte [] eof = { (byte) 0xFF };

		// Adjust image size
		MSXScreen msx = new MSXScreen();
		img = msx.adjustImage(img);

		// Convert to screen 2 data
		data = msx.formatScr2(img);

		// Add header
		header[0] = (byte) (id & 0xFF);
		header[1] = 1;
		header[2] = (byte) (img.getWidth() & 0xFF);
		header[3] = (byte) ((img.getHeight()/8) & 0xFF);

		// Join
		data = joinData(header, data);

		// Add end-of-file byte 0xFF
		if (addEOF)
			data = joinData(data, eof);

		return data;
	}

}
