/***************************************************************************
 *   Class MSXGrp                                                          *
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
 * MSXGrp - MSX screen 2 GRP images                                        *
 ***************************************************************************/

import java.util.Arrays;
import java.awt.image.BufferedImage;

public class MSXGrp {

	//
	//  Read GRP file and convert to image
	//

	public String checkData(byte data[]) {
		if (data.length < 14343 || data.length > 17000)
			return "Incorrect file size: ";
		return "";
	}


	// Check for monochromatic color table inverted: F1 instead of 1F
	private void fixMonoImg(byte [] data) {
		for (int i=7; i<6151; i++) {
			if (((data[i+0x2000] >> 4) & 0xF) == 0xF || (data[i+0x2000] & 0xF) == 1)
				data[i] = (byte) ~data[i];
		}
	}
	
	public BufferedImage renderGRP(byte data[]) {
		MSXScreen msx = new MSXScreen();
		fixMonoImg(data);
		return msx.renderScr2(Arrays.copyOfRange(data, 7, data.length-1), 256, 192);
	}


	//
	//  Convert image to GRP
	//

	// Header, data between pattern and color tables and color table
	private void fillSc2Data(byte [] data) {
		// Header
		byte [] header = { (byte) 0xFE, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x37 };
		System.arraycopy(header, 0, data, 0, header.length);
		

		// Between: from 1800H to 1FFFH
		byte c=0;
		int p;

		// 1800 - 1AFF
		p = 0x1807;
		for (int i = p; i < p+256*3; i++)
			data[i] = c++;

		// 1B00 - 1B7F
		c=0;
		p = 0x1B07;
		for (int i = p; i < p+0x80; i+=4) {
			data[i] = (byte) 0xD1;
			data[i+2] = c++;
			data[i+3] = 1;
		}

		// 1B80 - 1B9FH
		byte [] tmp = {(byte) 0x11, (byte) 0x06, (byte) 0x33, (byte) 0x07, (byte) 0x17, (byte) 0x01, (byte) 0x27, (byte) 0x03, (byte) 0x51, (byte) 0x01, (byte) 0x27, (byte) 0x06, (byte) 0x71, (byte) 0x01, (byte) 0x73, (byte) 0x03, (byte) 0x61, (byte) 0x06, (byte) 0x64, (byte) 0x06, (byte) 0x11, (byte) 0x04, (byte) 0x65, (byte) 0x02, (byte) 0x55, (byte) 0x05, (byte) 0x77, (byte) 0x07};
		System.arraycopy(tmp, 0, data, 0x1B87, tmp.length);


		// Fill color table - 2000H - 37FFH - Pattern: 1F
		for (int i=0x2007; i<0x3807; i++)
			data[i] = (byte) 0x1F;
	}

	public byte[] formatGRP(BufferedImage img) {
		byte data[] = new byte[14343], tmp[];
		MSXScreen msx = new MSXScreen();
		tmp = msx.formatScr2(msx.adjustImage(img, 256, 192));
		System.arraycopy(tmp, 0, data, 7, tmp.length);
		fillSc2Data(data);
		return data;
	}

}
