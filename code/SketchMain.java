/***************************************************************************
 *   Class SketchMain                                                      *
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
 * SketchMain  - Interface controller                                      *
 ***************************************************************************/

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;


public class SketchMain {

	private SketchMainUI frame;
	private SketchManager manager;
	private File current_dir;

	public SketchMain(SketchManager new_manager) {
		manager = new_manager;

		try {
			frame = new SketchMainUI(this);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		update();
	}

	public void update() {
		frame.updateImage(manager.getCurrentImage());
		frame.setCurrentImg(manager.getCurrentImageIndex());
		frame.setImgName(manager.getCurrentImageName());
		frame.setImageSize(manager.getImageSize());
	}

	private File fixFileExtension(File f, String ext) {

		String fname = f.getName();
		String pname = f.toString();
		int plen = pname.length();

		if (fname.length() < 4)
			return new File(pname+ext);

		String fext = pname.substring(plen-4, plen).toLowerCase();

		if (!fext.equals(ext)) {
			if (fext.substring(0,1).equals("."))
				pname = pname.substring(0, plen-4) + ext;
			else
				pname = pname + ext;
		}

		return new File(pname);
	}


	//
	// MSX
	//

	public void onLoadMSXClicked(int opt) {
		String f_desc[] = {"Screen 2 (*.grp)", "Layout (*.lay)", "Layout (*.lay)", "Shapes (*.shp)", "PC Shapes (*.shp)", "DP Shapes (*.stp)"};
		String f_ext[] = {"grp", "lay", "lay", "shp", "shp", "stp"};
		JFileChooser fc_load = new JFileChooser();
		fc_load.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc_load.setMultiSelectionEnabled(true);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(f_desc[opt],f_ext[opt]);
		fc_load.addChoosableFileFilter(filter);
		fc_load.setCurrentDirectory(current_dir);
		fc_load.setFileFilter(filter);

		int result = fc_load.showOpenDialog(frame);

		if (result == JFileChooser.CANCEL_OPTION)
			return;

		// From version 1.3, shapes also may load multiple files
		File filename[] = new File[1];
		filename = fc_load.getSelectedFiles();

		current_dir = fc_load.getCurrentDirectory();

		// Load MSX File
		String return_msg = manager.loadMSXImages(filename,opt);

		if (!return_msg.isEmpty()) {
			String msg = manager.getErrorMessage();
			JOptionPane.showMessageDialog(frame, return_msg, "Load error", JOptionPane.ERROR_MESSAGE);
		}

		frame.setTotalImgs(manager.getTotalImages());

		update();
	}

	public void onSaveMSXClicked(int opt) {
		// Test if image stack is empty
		if (manager.getTotalImages() < 1) {
			JOptionPane.showMessageDialog(frame, "There is no image to save.", "Save MSX Image", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Test if target images are the same from the source
		String [] types = { "grp", "layout1", "layout2", "shape", "sp_shape" };
		if (manager.getImageType() == types[opt]) {
			JOptionPane.showMessageDialog(frame, "Target images are of the same type.", "Save MSX Image", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Current or All
		int opt_file=0, opt_shape=0;
		boolean isMultipleShapes=false;
		if (manager.getTotalImages() > 1) {
			Object [] opts_file = { "Current", "All" };
			Object [] opts_shape = { "Single File", "Multiple Files" };
			opt_file = JOptionPane.showOptionDialog(frame, "Which images do you want to save?", "Save MSX Image", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts_file, opts_file[0]);
			if (opt_file == -1)
				return;

			if (opt_file == 1 && opt == 3) {
				opt_shape = JOptionPane.showOptionDialog(frame, "How do you want to save shape images?", "Save MSX Image", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts_shape, opts_shape[0]);
				if (opt_shape == -1)
					return;
				opt_file = opt_shape;
				isMultipleShapes = (opt_shape==0);
			}
		}

		// Save dialog
		String f_desc[] = {"Screen 2 (*.grp)", "Layout (*.lay)", "Layout (*.lay)", "Shapes (*.shp)", "DP Shapes (*.stp)"};
		String f_ext[] = {"grp", "lay", "lay", "shp", "stp"};
		JFileChooser fc_save = new JFileChooser();
		fc_save.setAcceptAllFileFilterUsed(false);
		fc_save.setCurrentDirectory(current_dir);

		// Current
		if (opt_file == 0) {
			fc_save.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc_save.addChoosableFileFilter(new FileNameExtensionFilter(f_desc[opt],f_ext[opt]));
		}
		else
			fc_save.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = fc_save.showSaveDialog(frame);

		if (result == JFileChooser.CANCEL_OPTION) 
			return;

		File filename = fc_save.getSelectedFile();
		current_dir = fc_save.getCurrentDirectory();

		if (opt_file == 0)
			filename = fixFileExtension(filename, "."+f_ext[opt]);

		// Save MSX File
		String return_msg = manager.saveMSXImages(filename, opt, (opt_file==1), isMultipleShapes);

		if (!return_msg.isEmpty()) {
			String msg = manager.getErrorMessage();
			JOptionPane.showMessageDialog(frame, return_msg, "Save error", JOptionPane.ERROR_MESSAGE);
		}

	}


	//
	// PC
	//

	public void onLoadPCClicked() {
		JFileChooser fc_load = new JFileChooser();
		fc_load.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc_load.setMultiSelectionEnabled(true);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PC Images (*.gif)","gif");
		fc_load.setCurrentDirectory(current_dir);
		fc_load.setFileFilter(filter);

		int result = fc_load.showOpenDialog(frame);

		if (result == JFileChooser.CANCEL_OPTION)
			return;

		File filename[] = fc_load.getSelectedFiles();
		current_dir = fc_load.getCurrentDirectory();

		// Load GIF
		String return_msg = manager.loadPCImages(filename);

		if (!return_msg.isEmpty()) {
			String msg = manager.getErrorMessage();
			JOptionPane.showMessageDialog(frame, return_msg, "Load error", JOptionPane.ERROR_MESSAGE);
		}

		frame.setTotalImgs(manager.getTotalImages());

		update();
	}

	public void onSavePCClicked() {
		// Test if image stack is empty
		if (manager.getTotalImages() < 1) {
			JOptionPane.showMessageDialog(frame, "There is no image to save.", "Save as GIF", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Test if images are already GIF
		if (manager.getImageType() == "gif") {
			JOptionPane.showMessageDialog(frame, "Images are already GIF.", "Save Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Current or All
		int opt_file=0;
		if (manager.getTotalImages() > 1) {
			Object [] opts_file = { "Current", "All" };
			opt_file = JOptionPane.showOptionDialog(frame, "Which images do you want to save?", "Save as GIF", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts_file, opts_file[0]);
			if (opt_file == -1)
				return;
		}

		// Save dialog
		JFileChooser fc_save = new JFileChooser();
		fc_save.setAcceptAllFileFilterUsed(false);
		fc_save.setCurrentDirectory(current_dir);

		// Current
		if (opt_file == 0) {
			fc_save.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc_save.addChoosableFileFilter(new FileNameExtensionFilter("Images GIF (*.gif)","gif"));
		}
		else
			fc_save.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = fc_save.showSaveDialog(frame);

		if (result == JFileChooser.CANCEL_OPTION) 
			return;

		File filename = fc_save.getSelectedFile();
		current_dir = fc_save.getCurrentDirectory();

		if (opt_file == 0)
			filename = fixFileExtension(filename, ".gif");

		// Save GIF
		String return_msg = manager.savePCImages(filename, (opt_file==1));

		if (!return_msg.isEmpty()) {
			String msg = manager.getErrorMessage();
			JOptionPane.showMessageDialog(frame, return_msg, "Save error", JOptionPane.ERROR_MESSAGE);
		}
	}


	//
	// Image options
	//

	public void onShowGridChanged(boolean chg) {
		manager.setHasGrid(chg);
		update();
	}

	public void onShowLimitsChanged(boolean chg) {
		manager.setHasLimits(chg);
		update();
	}


	//
	// Image navigation
	//

	public void onFirstClicked() {
		manager.setFirst();
		update();
	}

	public void onLastClicked() {
		manager.setLast();
		update();
	}

	public void onPrevClicked() {
		manager.setPrevious();
		update();
	}

	public void onNextClicked() {
		manager.setNext();
		update();
	}

}
