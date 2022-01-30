/***************************************************************************
 *   Class ImageStack                                                      *
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
 * ImageStack - Stores images and file names                               *
 ***************************************************************************/

import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class ImageStack {

	private ArrayList<BufferedImage> img_list = new ArrayList<BufferedImage>();
	private ArrayList<String> img_name_list = new ArrayList<String>();
	private int current_img;
	private String type;

	//
	// General
	//

	public void clear() {
		img_list.clear();
		img_name_list.clear();
		type = "";
		current_img = 0;
	}

	public int size() {
		return img_list.size();
	}

	public void setType(String t) {
		type = t;
	}

	public String getType() {
		return type;
	}

	public String getCurrentImageSize() {
		if (size() < 1)
			return "0x0";

		return Integer.toString(img_list.get(current_img).getWidth())+"x"+Integer.toString(img_list.get(current_img).getHeight());
	}


	//
	// Image I/O
	//

	public void insertImage(BufferedImage img, String filename) {
		img_list.add(img);
		img_name_list.add(filename);
	}

	public BufferedImage getImage(int pos) {
		if (pos < 0 || pos > img_list.size())
			return null;

		return img_list.get(pos);
	}

	public String getImageName(int pos) {
		if (pos < 0 || pos >= img_name_list.size())
			return "No file";

		return img_name_list.get(pos);
	}


	//
	// Image pointer (current image)
	//

	public int getCurrentImage() {
		return current_img;
	}

	public void setNext() {
		current_img++;
		if (current_img > size()-1)
			current_img = size()-1;
	}

	public void setPrevious() {
		current_img--;
		if (current_img < 0)
			current_img = 0;
	}

	public void setLast() {
		current_img = size()-1;
	}

	public void setFirst() {
		current_img = 0;
	}
}
