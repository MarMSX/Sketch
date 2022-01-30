/***************************************************************************
 *   Class PCShape                                                         *
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
 * PCShape - PC shapes - Print Master format                               *
 ***************************************************************************/

import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;

public class PCShape {

	// Shape
	// Header: Shape width_in_bytes, width, height , 00
	// Dimensions: from 8x1 to 255x255

	private byte [] sdr_data;

	//
	//  Try to load SDR file with descriptors
	//
	private String readSDR(File filename) {
		String name = filename.toString();
		String sdr_name = name.substring(0,name.length()-4) + ".SDR";

		File arq = new File(sdr_name);
		sdr_data = new byte[(int) arq.length()];
		try {
			InputStream is = new FileInputStream(arq);
			is.read(sdr_data);
			is.close();
		}
		catch(Exception e) {
			return "Bad file format";
		}

		return "";
	}

	private String getSDRAt(int pos) {
		String name = "";
		pos--;
		char c;

		for (int i=0; i<16; i++) {
			if (pos*16+i > sdr_data.length)
				return "";

			c = (char) sdr_data[pos*16+i];

			if (c != 0)
				name += c;
		}

		return name;
	}

	private boolean hasSDR() {
		return sdr_data.length > 0;
	}


	//
	//  Read PC shape and convert it to image
	//

	public String renderShape(byte data[], ImageStack img_stack, String name, File filename) {
		byte [] pcpat;
		PCScreen pc = new PCScreen();
		int p=0, id=1, width, height, size, wbyte;

		// Try to find SDR file
		String msg = readSDR(filename);
		if (msg != "")
			return msg;

		while (p < data.length) {
			if (p+3 >= data.length)
				return "";

			// Header
			wbyte = data[p] & 0xFF;
			width = data[p+2] & 0xFF;
			height = data[p+1] & 0xFF;
			size = width*height/8;
			p+=4;

			// Test width consistency (if error, probably is trash at the EOF)
			if (width != wbyte*8)
				return "";

			if (width < 8 || width > 255 || height < 1 || height > 255) {
				return "Bad size on shape id="+Integer.toString(id);
			}

			if (p+size >= data.length) {
				return "Data corrupt on shape id="+Integer.toString(id);
			}

			if (hasSDR())
				name = getSDRAt(id);

			pcpat = Arrays.copyOfRange(data, p, p+size);
			img_stack.insertImage(pc.render(pcpat, width, height), name);

			// Skip 00 between shapes
			p += size+1;

			id++;
		}

		return "";
	}

}
