/***************************************************************************
 *   Class PCImage                                                         *
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
 * PCImage - deals with PC GIF images                                      *
 ***************************************************************************/

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;

public class PCImage {

	public String loadPCImages(File [] filename, ImageStack img_stack) {
		BufferedImage img;
		String name;

		img_stack.clear();
		img_stack.setType("gif");

		for (int i=0; i<filename.length; i++) {

			name = filename[i].getName();

			try {
				img = ImageIO.read(filename[i]);
			}
			catch(IOException e) {
				img_stack.clear();
				return "Error in: "+name;
			}

			if (img == null) {
				img_stack.clear();
				return "Invalid image: "+name;
			}

			if (img.getWidth() > 512 || img.getHeight() > 212) {
				img_stack.clear();
				return "Image too big: "+name;
			}

			img_stack.insertImage(img, name);
		}

		return "";
	}

	public String savePCImages(File path, ImageStack img_stack) {
		File nfname;
		String name, str_num="", msg;
		boolean isShape = (img_stack.getType() == "shape");

		for (int i=0; i<img_stack.size(); i++) {
			name = img_stack.getImageName(i);

			if (isShape)
				str_num = Integer.toString(i+1);

			if (name.length() < 4)
				name = name + str_num + ".gif";
			else
				name = name.substring(0, name.length()-4) + str_num + ".gif";

			nfname = new File(path, name);
			msg = savePCImage(nfname, img_stack, i);

			if (msg != "")
				return msg;
		}

		return "";
	}

	public String savePCImage(File filename, ImageStack img_stack, int img_no) {
		String name = filename.getName();

		if (img_no < 0 || img_no >= img_stack.size())
			return "Image not found";

		if (img_stack.size() < 1)
			return "There is no image to save.";

		// Check if filename is a directory
		if (filename.isDirectory())
			return "File "+name+" is a directory.";

		// Check if file exists
		if (filename.exists()) {
			if (JOptionPane.showConfirmDialog(null, "File "+name+" already exists. Overwite?", "Warning", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
				return "";
		}

		try {
			ImageIO.write(img_stack.getImage(img_no), "gif", filename);
		}
		catch(IOException e) {
			return "Error saving "+name;
		}

		return "";
	}

}
