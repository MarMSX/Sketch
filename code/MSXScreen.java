/***************************************************************************
 *   Class MSXScreen                                                       *
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
 * MSXScreen - MSX Screens common services                                 *
 ***************************************************************************/

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

public class MSXScreen {

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
	// From MSX file
	//

	public BufferedImage renderScr2(byte [] data, int width, int height) {
		byte dado;
		int p=0;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int y=0; y<img.getHeight(); y+=8) {
			for (int x=0; x<img.getWidth(); x+=8) {
				for (int yy=0; yy<8; yy++) {
					dado = data[p];
					for (int b=0; b<8; b++)
						img.setRGB(x+(7-b), y+yy, (getPixelBit(dado,b))?0:0xFFFFFF);
					p++;
				}
			}
		}

		return img;
	}


	//
	// To MSX File
	//

	public byte[] formatScr2(BufferedImage img) {
		// Check if image width and height are multiple of 8
		if ((img.getWidth() % 8) != 0 || (img.getHeight() % 8) != 0)
			img = adjustImage(img);

		int p=0, size=img.getWidth()*img.getHeight()/8;
		byte [] data = new byte[size];

		for (int y=0; y<img.getHeight(); y+=8) {
			for (int x=0; x<img.getWidth(); x+=8) {
				for (int yy=0; yy<8; yy++) {
					for (int b=0; b<8; b++)
						data[p] = setBit(data[p], b, (img.getRGB(x+(7-b), y+yy) & 0xFF) < 128);
					p++;
				}
			}
		}

		return data;
	}


	public BufferedImage adjustImage(BufferedImage img) {
		return adjustImage(img, 0, 0);
	}

	public BufferedImage adjustImage(BufferedImage img, int width, int height) {
		if (width==0 || height==0) {
			width = img.getWidth();
			height = img.getHeight();
		}

		if (width % 8 != 0)
			width = (int) Math.floor(1+(double)(width-1)/8)*8;

		if (height % 8 != 0)
			height = (int) Math.floor(1+(double)(height-1)/8)*8;

		BufferedImage newimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = newimg.createGraphics();
		g2d.setColor(new Color(255,255,255));
		g2d.fillRect(0,0,width,height);
		if (img != null)
			g2d.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);

		return newimg;
	}
}
