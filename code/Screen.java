/***************************************************************************
 *   Class Screen                                                          *
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
 * Screen - render interface screen                                        *
 ***************************************************************************/

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;


public class Screen {

	/************************************************
	 * Render display image                         *
	 ************************************************/

	public BufferedImage renderImage(BufferedImage msx_img, boolean hasGrid, boolean hasLimits) {

		int xi=0, dx=16, xf=512, scr=2;

		if (msx_img.getWidth() > 256 || msx_img.getHeight() > 192)
			scr=6;

		int wf=(scr==2)?2:1;
		int max_y = (scr==2)?384:424;
		int x_f = (scr==2)?2:1;
		BufferedImage img = new BufferedImage(512, 424, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();

		g2d.setColor(new Color(255,255,255));
		g2d.fillRect(0,0,512,max_y);

		if (msx_img != null)
			g2d.drawImage(msx_img, xi, 0, msx_img.getWidth()*wf, msx_img.getHeight()*2, null);

		// Draw grid
		if (scr==2 && hasGrid) {
			g2d.setColor(new Color(192,192,255));
			for (int y=0; y<385; y+=16) {
				g2d.drawLine(xi, y, xf-1, y);
			for (int x=xi; x<xf; x+=dx)
				g2d.drawLine(x, 0, x, 383);
			}
		}

		// Draw image limits
		if (msx_img != null && hasLimits) {
			g2d.setColor(new Color(255,0,0));
			g2d.drawRect(xi, 0, msx_img.getWidth()*wf, msx_img.getHeight()*2);
		}

		return img;
	}

}
