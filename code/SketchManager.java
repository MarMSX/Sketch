/***************************************************************************
 *   Class SketchManager                                                   *
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
 * SketchManager - manages the program                                     *
 ***************************************************************************/

import java.io.File;
import java.awt.image.BufferedImage;

public class SketchManager {

	private ImageStack img_stack;
	private SketchMain sm;
	private String error_msg;
	private boolean hasGrid, hasLimits;


	public SketchManager() {
		img_stack = new ImageStack();
		sm = new SketchMain(this);
		hasGrid = true;
		hasLimits = true;
	}

	public String getErrorMessage() {
		return error_msg;
	}

	public void setHasGrid(boolean hg) {
		hasGrid = hg;
	}

	public void setHasLimits(boolean hl) {
		hasLimits = hl;
	}


	//
	// Image stack interface
	//

	public int getCurrentImageIndex() {
		return img_stack.getCurrentImage();
	}

	public BufferedImage getCurrentImage() {
		if (img_stack.size() == 0)
			return null;
		Screen scr = new Screen();
		return scr.renderImage(img_stack.getImage(img_stack.getCurrentImage()), hasGrid, hasLimits);
	}

	public String getCurrentImageName() {
		return img_stack.getImageName(img_stack.getCurrentImage());
	}

	public int getTotalImages() {
		return img_stack.size();
	}

	public void setNext() {
		img_stack.setNext();
	}

	public void setPrevious() {
		img_stack.setPrevious();
	}

	public void setLast() {
		img_stack.setLast();
	}

	public void setFirst() {
		img_stack.setFirst();
	}

	public String getImageType() {
		return img_stack.getType();
	}

	public String getImageSize() {
		return img_stack.getCurrentImageSize();
	}


	//
	// MSX Images
	//

	public String loadMSXImages(File [] filename, int opt) {
		MSXImage msx = new MSXImage(opt);
		return msx.loadMSXImages(filename, img_stack);
	}

	public String saveMSXImages(File filename, int opt, boolean isMultipleFiles, boolean isMultipleShapes) {

		MSXImage msx = new MSXImage(opt);

		if (isMultipleFiles)
			return msx.saveMSXImages(filename, img_stack);

		return msx.saveMSXImage(filename, img_stack, img_stack.getCurrentImage(), isMultipleShapes);
	}


	//
	// PC Images
	//

	public String loadPCImages(File [] filename) {
		PCImage pc = new PCImage();
		return pc.loadPCImages(filename, img_stack);
	}

	public String savePCImages(File filename, boolean isMultipleFiles) {
		PCImage pc = new PCImage();

		if (isMultipleFiles)
			return pc.savePCImages(filename, img_stack);

		return pc.savePCImage(filename, img_stack, img_stack.getCurrentImage());
	}
}
