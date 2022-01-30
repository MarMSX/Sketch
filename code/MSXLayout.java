/***************************************************************************
 *   Class MSXLayout                                                       *
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
 * MSXLayout - MSX screen 2 Graphos III Layout images                      *
 ***************************************************************************/

import java.util.Arrays;
import java.awt.image.BufferedImage;

public class MSXLayout {

	private int type;

	// Type 0, Graphos 1.1
	// Type 1, Graphos 1.2
	public MSXLayout(int t) {
		type = t;
	}

	//
	//  Read Layout and convert it to image
	//

	private byte [] uncompress(byte [] data) {
		byte [] tmp = new byte[6144];
		int R, C, p=0, p_data=7, count;

		while (p_data < data.length && p < tmp.length) {

			C = data[p_data] & 0xFF;

			if (type == 1)
				C = (C + 0x67) & 0xFF;

			if (C == 0xFF || C==0) {
				if (p_data >= data.length-1)
					return tmp;
				R = data[p_data+1] & 0xFF;
				if ((R+p >= tmp.length-1) || (C==0 && R==0))
					return tmp;

				for (int i=0; i<R; i++)
					tmp[p++] = (byte) C;
				p_data += 2;
			}
			else {
				while (C != 0 && C != 0xFF) {
					if (p_data >= (data.length-1) || p >= tmp.length)
						return tmp;
					tmp[p++] = (byte) C;
					C = data[++p_data] & 0xFF;
					if (type == 1)
						C = (C + 0x67) & 0xFF;
				}
			}
		}

		return tmp;
	}

	public BufferedImage renderLayout(byte data[]) {
		byte [] sc2pat;
		MSXScreen msx = new MSXScreen();
		sc2pat = uncompress(data);
		return msx.renderScr2(sc2pat, 256, 192);
	}


	//
	//  Convert image to layout
	//

	// Compress image data and add header
	private byte [] compress(byte [] data) {
		byte [] header = { (byte) 0xFE, (byte) 0x00, (byte) 0x90, (byte) 0x44, (byte) 0x91, (byte) 0x00, (byte) 0x90 };
		byte [] tmp = new byte[data.length*2];
		int p=7, p_data=0, count;
		int shf = (type==1)?0x99:0;
		int C;

		System.arraycopy(header, 0, tmp, 0, header.length);

		while (p_data < data.length) {
			C = data[p_data] & 0xFF;

			if (C==0 || C==0xFF) {
				count = 0;
				while (data[p_data] == (byte)C) {
					count++;
					p_data++;
					if  (p_data >= data.length || count == 0xFE)
						break;
				}
				tmp[p] = (byte) ((C+shf) & 0xFF);
				tmp[p+1] = (byte) count;
				p+=2;
			}
			else {
				while (data[p_data] != 0 && data[p_data] != (byte)0xFF) {
					tmp[p++] = (byte) ((data[p_data++] + shf) & 0xFF);
					if  (p_data >= data.length)
						break;
				}
			}
		}

		return Arrays.copyOfRange(tmp, 0, p);
	}

	public byte[] formatLayout(BufferedImage img) {
		byte data[], tmp[];
		MSXScreen msx = new MSXScreen();
		tmp = msx.formatScr2(msx.adjustImage(img, 256, 192));
		return compress(tmp);
	}

}
