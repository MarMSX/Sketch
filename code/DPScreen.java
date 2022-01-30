/***************************************************************************
 *   Class DPScreen                                                        *
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
 * DPScreen - Dynamic Publisher render                                     *
 ***************************************************************************/

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

public class DPScreen {

	private boolean getPixelBit(byte pixel, int b) {
		return (boolean) (((pixel & (1 << b)) >> b) == 1);
	}

	public byte setBit(byte data, int b, boolean v) {
		int mask= 1 << b;

		if (v)
			return (byte) (data | mask);

		return (byte) (data & ~mask);
	}

	//
	// From DP file
	//

	// 4x1 pixels are not breaked by a new line
	private byte [] byte2Pixels(byte [] data) {
		byte [] tmp = new byte[data.length*4];
		byte [] P = new byte[4];

		for (int i=0; i<data.length; i++) {
			for (int b=1; b<8; b+=2)
				P[b/2] = (getPixelBit(data[i],7-b))?(byte)1:(byte)0;
			System.arraycopy(P, 0, tmp, i*4, P.length);
		}

		return tmp;
	}

	public BufferedImage render(byte [] data, int width, int height) {
		byte dado;
		int p=0;
		byte [] ndata = byte2Pixels(data);

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int y=0; y<img.getHeight(); y++) {
			for (int x=0; x<img.getWidth(); x++)
				img.setRGB(x, y, (ndata[p++]==1)?0:0xFFFFFF);
		}

		return img;
	}


	//
	// To DP File
	//

	private byte [] pixels2Byte(byte [] ndata, int w, int h) {
		int size = (int) (Math.ceil(w*h/4.0)), p=0;
		byte [] data = new byte[size];
		byte P;

		for (int i=0; i<ndata.length; i+=4) {
			P=0;
			for (int k=0; k<4; k++)
				P = setBit(P, 6-k*2, (ndata[i+k]==0));
			data[p++] = P;
		}

		return data;
	}

	public byte [] format(BufferedImage img) {
		int width = img.getWidth(), height = img.getHeight(), p=0;
		byte [] ndata = new byte[width*height];

		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++)
				ndata[p++] = ((img.getRGB(x, y) & 0xFF) < 128)?(byte)0:(byte)1;
		}

		return pixels2Byte(ndata, width, height);
	}
}
