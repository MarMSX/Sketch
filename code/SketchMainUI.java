/***************************************************************************
 *   Class SketchMainUI                                                    *
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
 * SketchMainUI - Main program interface                                   *
 ***************************************************************************/

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class SketchMainUI extends JFrame {

	private JPanel contentPane;
	private JComboBox comboBox1;
	private JComboBox comboBox2;
	private SketchMain sm;
	private JLabel lblScreen;
	private JLabel lblImgNum;
	private JLabel lblFile;
	private JLabel lblSize;
	private int total;
	private JButton buttonFirst;
	private JButton buttonPrev;
	private JButton buttonNext;
	private JButton buttonLast;
	private JCheckBox checkGrid;
	private JCheckBox checkLimits;

	public void updateImage(Image img) {
		if (img != null)
			lblScreen.setIcon(new ImageIcon(img));
		else
			lblScreen.setIcon(null);
	}

	public void setTotalImgs(int t) {
		total = t;
	}

	public void setImgName(String name) {
		lblFile.setText(name);
	}

	public void setCurrentImg(int pos) {
		if (total > 0) {
			block_controls(false);
			lblImgNum.setText(Integer.toString(pos+1)+"/"+Integer.toString(total));
		}
		else {
			block_controls(true);
			lblImgNum.setText("1/1");
		}
	}

	private void block_controls(boolean block) {
		buttonFirst.setEnabled(!block);
		buttonLast.setEnabled(!block);
		buttonPrev.setEnabled(!block);
		buttonNext.setEnabled(!block);
	}

	public void setImageSize(String str_size) {
		lblSize.setText("Image size: "+str_size);
	}

	public SketchMainUI(SketchMain new_sm) {

		// Links this module to SketckMain
		sm = new_sm;
		total=0;

		setTitle("MSX Sketch Tools 1.3 - MarMSX 2020");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 590);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panelImg = new JPanel();
		panelImg.setBounds(10, 10, 514, 426);
		panelImg.setBorder(new LineBorder(new Color(92, 92, 92)));
		panelImg.setLayout(null);
		contentPane.add(panelImg);

		lblScreen = new JLabel("");
		lblScreen = new JLabel("");
		lblScreen.setBackground(new Color(255, 255, 255));
		lblScreen.setOpaque(true);
		lblScreen.setBounds(1, 1, 512, 424);
		panelImg.add(lblScreen);

		// Logo
		JPanel panelLogo = new JPanel();
		panelLogo.setBounds(534, 10, 142, 142);
		panelLogo.setBorder(new LineBorder(new Color(92, 92, 92)));
		panelLogo.setLayout(null);
		contentPane.add(panelLogo);
		
		JLabel lblLogo = new JLabel("");
		lblLogo.setBounds(1, 1, 140, 140);
		Image img = new ImageIcon(this.getClass().getResource("imgs/sketch.png")).getImage();
		lblLogo.setIcon(new ImageIcon(img));
		panelLogo.add(lblLogo);

		// MarMSX
		JPanel panelMarMSX = new JPanel();
		panelMarMSX.setBounds(534, 495, 144, 40);
		panelMarMSX.setBorder(new LineBorder(new Color(92, 92, 92)));
		panelMarMSX.setLayout(null);
		contentPane.add(panelMarMSX);
		
		JLabel lblMarMSX = new JLabel("");
		lblMarMSX.setBounds(1, 1, 142, 38);
		img = new ImageIcon(this.getClass().getResource("imgs/marmsx.png")).getImage();
		lblMarMSX.setIcon(new ImageIcon(img));
		panelMarMSX.add(lblMarMSX);


		//
		// Painel do MSX
		//
		
		JPanel panelMSX = new JPanel();
		panelMSX.setBorder(new TitledBorder(null, "MSX", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelMSX.setBounds(536, 265, 141, 175);
		panelMSX.setLayout(null);
		contentPane.add(panelMSX);
		
		JButton btnMSXLoad = new JButton("Load");
		btnMSXLoad.setToolTipText("Load MSX image");
		btnMSXLoad.setBounds(15, 18, 110, 30);
		img = new ImageIcon(this.getClass().getResource("imgs/fileopen.png")).getImage();
		btnMSXLoad.setIcon(new ImageIcon(img));
		panelMSX.add(btnMSXLoad);
		btnMSXLoad.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				sm.onLoadMSXClicked(comboBox1.getSelectedIndex());
			}
		});
		
		JButton btnMSXSave = new JButton("Save");
		btnMSXSave.setToolTipText("Save as MSX image");
		btnMSXSave.setBounds(15, 103, 110, 30);
		img = new ImageIcon(this.getClass().getResource("imgs/msxdisk.png")).getImage();
		btnMSXSave.setIcon(new ImageIcon(img));
		panelMSX.add(btnMSXSave);
		btnMSXSave.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				sm.onSaveMSXClicked(comboBox2.getSelectedIndex());
			}
		});
		
		comboBox1 = new JComboBox();
		comboBox1.setModel(new DefaultComboBoxModel(new String[] {"B/W GRP", "Layout 1.1", "Layout 1.2", "Shape", "PC Shape", "DP Shape"}));
		comboBox1.setBounds(15, 55, 110, 24);
		panelMSX.add(comboBox1);
		
		comboBox2 = new JComboBox();
		comboBox2.setModel(new DefaultComboBoxModel(new String[] {"B/W GRP", "Layout 1.1", "Layout 1.2", "Shape", "DP Shape"}));
		comboBox2.setBounds(15, 139, 110, 24);
		panelMSX.add(comboBox2);


		//
		// Painel do PC
		//
		
		JPanel panelPC = new JPanel();
		panelPC.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "PC - GIF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPC.setBounds(536, 170, 141, 75);
		panelPC.setLayout(null);
		contentPane.add(panelPC);
		
		JButton btnPCLoad = new JButton("");
		btnPCLoad.setToolTipText("Load PC GIF image");
		btnPCLoad.setBounds(15, 20, 50, 35);
		img = new ImageIcon(this.getClass().getResource("imgs/fileopen.png")).getImage();
		btnPCLoad.setIcon(new ImageIcon(img));
		panelPC.add(btnPCLoad);
		btnPCLoad.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				sm.onLoadPCClicked();
			}
		});
		
		JButton btnPCSave = new JButton("");
		btnPCSave.setToolTipText("Save as PC GIF image");
		btnPCSave.setBounds(75, 20, 50, 35);
		img = new ImageIcon(this.getClass().getResource("imgs/msxdisk.png")).getImage();
		btnPCSave.setIcon(new ImageIcon(img));
		panelPC.add(btnPCSave);
		btnPCSave.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				sm.onSavePCClicked();
			}
		});


		//
		// Image options
		//

		JPanel panelImgOpt = new JPanel();
		panelImgOpt.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelImgOpt.setBounds(10, 505, 512, 30);
		panelImgOpt.setLayout(null);
		contentPane.add(panelImgOpt);

		checkGrid = new JCheckBox("Show 8x8 grid");
		checkGrid.setFont(new Font("Dialog", Font.BOLD, 14));
		checkGrid.setBounds(10, 5, 150, 20);
		checkGrid.setSelected(true);
		panelImgOpt.add(checkGrid);
		checkGrid.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				sm.onShowGridChanged(checkGrid.isSelected());
			}
		});

		checkLimits = new JCheckBox("Show image limits");
		checkLimits.setFont(new Font("Dialog", Font.BOLD, 14));
		checkLimits.setBounds(250, 5, 180, 20);
		checkLimits.setSelected(true);
		panelImgOpt.add(checkLimits);
		checkLimits.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				sm.onShowLimitsChanged(checkLimits.isSelected());
			}
		});


		//
		// Image navigation
		//

		lblFile = new JLabel("No file");
		lblFile.setFont(new Font("Dialog", Font.BOLD, 16));
		lblFile.setBounds(10, 445, 247, 20);
		lblFile.setBackground(new Color(200, 200, 200));
		lblFile.setOpaque(true);
		contentPane.add(lblFile);

		lblSize = new JLabel("Image size: 0x0");
		lblSize.setFont(new Font("Dialog", Font.BOLD, 16));
		lblSize.setBackground(new Color(220, 220, 220));
		lblSize.setOpaque(true);
		lblSize.setBounds(10, 470, 247, 20);
		contentPane.add(lblSize);

		lblImgNum = new JLabel("1/1");
		lblImgNum.setFont(new Font("Dialog", Font.BOLD, 15));
		lblImgNum.setHorizontalAlignment(SwingConstants.CENTER);
		lblImgNum.setBounds(363, 460, 70, 20);
		contentPane.add(lblImgNum);
		
		buttonFirst = new JButton("");
		buttonFirst.setToolTipText("First image");
		img = new ImageIcon(this.getClass().getResource("imgs/first.png")).getImage();
		buttonFirst.setIcon(new ImageIcon(img));
		buttonFirst.setBounds(275, 453, 35, 30);
		contentPane.add(buttonFirst);
		buttonFirst.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				sm.onFirstClicked();
			}
		});
		
		buttonPrev = new JButton("");
		buttonPrev.setToolTipText("Previous image");
		img = new ImageIcon(this.getClass().getResource("imgs/left.png")).getImage();
		buttonPrev.setIcon(new ImageIcon(img));
		buttonPrev.setBounds(320, 453, 35, 30);
		contentPane.add(buttonPrev);
		buttonPrev.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				sm.onPrevClicked();
			}
		});
		
		buttonNext = new JButton("");
		buttonNext.setToolTipText("Next image");
		img = new ImageIcon(this.getClass().getResource("imgs/right.png")).getImage();
		buttonNext.setIcon(new ImageIcon(img));
		buttonNext.setBounds(440, 453, 35, 30);
		contentPane.add(buttonNext);
		buttonNext.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				sm.onNextClicked();
			}
		});
		
		buttonLast = new JButton("");
		buttonLast.setToolTipText("Last image");
		img = new ImageIcon(this.getClass().getResource("imgs/last.png")).getImage();
		buttonLast.setIcon(new ImageIcon(img));
		buttonLast.setBounds(485, 453, 35, 30);
		contentPane.add(buttonLast);
		buttonLast.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				sm.onLastClicked();
			}
		});

	}
}
