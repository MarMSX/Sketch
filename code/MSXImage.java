/***************************************************************************
 *   Class MSXImage                                                        *
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
 * MSXImage - deals with MSX images                                        *
 ***************************************************************************/

import java.io.*;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;

public class MSXImage {

	// Options - opt
	// Read              Write
	// 0 - GRP           0 - GRP
	// 1 - Layout 1.1    1 - Layout 1.1
	// 2 - Layout 1.2    2 - Layout 1.2
	// 3 - Shape         3 - Shape
	// 4 - PC Shape      4 - DP Shape
	// 5 - DP Shape

	private int opt;
	private BufferedImage img;

	public MSXImage(int o) {
		opt = o;
	}

	private String renderMSX(byte [] data) {
		String msg;

		if (opt==0) {
			MSXGrp grp = new MSXGrp();
			msg = grp.checkData(data);
			if (msg != "")
				return msg;
			img = grp.renderGRP(data);
		}

		if (opt==1 || opt==2) {
			MSXLayout lay = new MSXLayout(opt-1);
			img = lay.renderLayout(data);
		}

		if (opt == 5) {
			DPShape dp = new DPShape();
			img = dp.renderShape(data);
			msg = dp.getLog();
			if (msg != "")
				return msg;
		}

		return "";
	}


	//
	// Load MSX
	//

	public String loadMSXImages(File [] filename, ImageStack img_stack) {
		// Set img type
		img_stack.clear();
		String [] type = { "grp", "layout1", "layout2", "shape", "pc_shape", "dp_shape" };
		img_stack.setType(type[opt]);

		if (opt==3 || opt==4) {
			return loadShapeImages(filename, img_stack);
		}

		String fname;
		String name;
		File arq;
		byte data[];
		String msg;

		for (int i=0; i<filename.length; i++) {
			fname = filename[i].toString();
			name = filename[i].getName();

			// Load MSX file
			msg = "";
			arq = new File(fname);
			data = new byte[(int) arq.length()];
			try {
				InputStream is = new FileInputStream(arq);
				is.read(data);
				is.close();
			}
			catch(Exception e) {
				img_stack.clear();
				return "Error in: "+name;
			}

			// Render data
			msg = renderMSX(data);
			if (msg != "") {
				img_stack.clear();
				return msg+name;
			}

			// If no errors found, stack image
			if (img != null)
				img_stack.insertImage(img, name);
		}

		return "";
	}

	private String loadShapeImages(File [] filename, ImageStack img_stack) {
		File arq;
		byte data[];
		String msg;

		for (int i=0; i<filename.length; i++) {

			String name = filename[i].getName();
			String fname = filename[i].toString();

			// Load MSX file
			msg = "";
			arq = new File(fname);
			data = new byte[(int) arq.length()];
			try {
				InputStream is = new FileInputStream(arq);
				is.read(data);
				is.close();
			}
			catch(Exception e) {
				img_stack.clear();
				return "Error in: "+name;
			}

			// Render data
			if (opt==3) {
				MSXShape shape = new MSXShape();
				msg = shape.renderShape(data, img_stack, name);
			}
			else {
				PCShape shape = new PCShape();
				msg = shape.renderShape(data, img_stack, name, filename[i]);
			}
			if (msg != "") {
				return msg;
			}
		}

		return "";
	}


	//
	// Save MSX
	//

	private byte [] formatMSX(int img_no) {
		String msg;
		byte [] frm_data = null;

		if (opt == 0) {
			MSXGrp grp = new MSXGrp();
			frm_data = grp.formatGRP(img);
		}

		if (opt==1 || opt==2) {
			MSXLayout lay = new MSXLayout(opt-1);
			frm_data = lay.formatLayout(img);
		}

		if (opt == 3) {
			MSXShape shp = new MSXShape();
			frm_data = shp.formatShape(img, img_no);
		}

		if (opt == 4) {
			DPShape dp = new DPShape();
			frm_data = dp.formatShape(img);
		}

		return frm_data;
	}

	public String saveMSXImages(File path, ImageStack img_stack) {
		String f_ext[] = {".grp", ".lay", ".lay", ".shp", ".stp"};
		File nfname;
		String name, str_num="", msg;
		boolean isShape = (img_stack.getType() == "shape");

		for (int i=0; i<img_stack.size(); i++) {
			name = img_stack.getImageName(i);

			if (isShape)
				str_num = Integer.toString(i+1);

			if (name.length() < 4)
				name = name + str_num + f_ext[opt];
			else
				name = name.substring(0, name.length()-4) + str_num + f_ext[opt];

			nfname = new File(path, name);

			msg = saveMSXImage(nfname, img_stack, i, false);

			if (msg != "")
				return msg;
		}

		return "";
	}

	public String saveMSXImage(File filename, ImageStack img_stack, int img_no, boolean isMultipleShape) {
		String name = filename.getName();
		byte [] data;

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

		// Check if shape image has correct size
		img = img_stack.getImage(img_no);
		if (opt == 3 && (img.getWidth() > 240 || img.getHeight() > 176))
			return "Image no "+Integer.toString(img_no+1)+" is greater than 240x176.";

		if (opt == 3 && isMultipleShape) {
			MSXShape shp = new MSXShape();
			data = shp.formatShapes(img_stack);
		}
		else
			data = formatMSX(img_no+1);

		if (data == null)
			return "Error while converting "+name;

		// Check if shape file size is correct
		if (opt == 3) {
			if (data.length > 6912)
				return "Shape file size is greater than 6912.";
		}

		try {
			OutputStream os = new FileOutputStream(filename);
			os.write(data);
			os.close();
		}
		catch(Exception e) {
			return "Error in: "+name;
		}

		return "";
	}

}
