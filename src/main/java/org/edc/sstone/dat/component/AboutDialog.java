/*
 * Copyright (c) 2012 EDC
 * 
 * This file is part of Stepping Stone.
 * 
 * Stepping Stone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Stepping Stone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Stepping Stone.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */
package org.edc.sstone.dat.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.dat.util.SwingUtil;

class AboutDialog extends JDialog {

    private static final long serialVersionUID = 278828804138021965L;

    private JPanel aboutPanel;

    AboutDialog(Frame owner) {
        super(owner, SAFUtil.getString("aboutDialog.title"), false);
        setName("aboutDialog");

        aboutPanel = new JPanel();
        aboutPanel.setName("aboutPanel");
        aboutPanel.setLayout(new BorderLayout(12, 12));
        aboutPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        populatePanel();

        setContentPane(aboutPanel);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void populatePanel() {
        aboutPanel.add(new JLabel(SAFUtil.getIcon("about.img")), BorderLayout.WEST);
        aboutPanel.add(getAboutTextLabel(), BorderLayout.CENTER);
        aboutPanel.add(getAboutLinksPanel(), BorderLayout.SOUTH);
        SAFUtil.injectComponents(aboutPanel);
    }

    private Container getAboutLinksPanel() {
        JPanel aboutLinkPanel = new JPanel(new GridLayout(1, 3));
        aboutLinkPanel.setName("aboutLinkPanel");

        String[] links = new String[] { "README", "NOTICES", "COPYING" };
        Color textColor = SAFUtil.getColor("aboutLinks.textColor");
        for (String s : links) {
            JLabel link = new JLabel(s);
            link.setForeground(textColor);
            link.setName(s);

            // link.setAlignmentX(0.5f);
            link.setHorizontalAlignment(JLabel.CENTER);
            link.addMouseListener(new ShowTextDialogListener());

            // link.setBackground(null);
            // link.setBorderPainted(false);
            aboutLinkPanel.add(link);
        }
        return aboutLinkPanel;
    }

    private static class ShowTextDialogListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Component c = e.getComponent();
            String title = c.getName();
            if (c instanceof JLabel) {
                title = ((JLabel) c).getText();
            }

            String resourcePath = "/" + c.getName() + ".gz";

            InputStream in = getClass().getResourceAsStream(resourcePath);
            BufferedReader br = null;
            try {
                GZIPInputStream zin = new GZIPInputStream(in);
                br = new BufferedReader(new InputStreamReader(zin));
                StringBuilder sb = new StringBuilder();
                String line = null;
                String sep = System.getProperty("line.separator");
                while ((line = br.readLine()) != null) {
                    sb.append(line).append(sep);
                }

                final JDialog dialog = new JDialog(JOptionPane.getRootFrame(), title, false);
                JTextArea textArea = new JTextArea(40, 95);
                textArea.setBorder(new EmptyBorder(12, 12, 12, 12));
                textArea.setEditable(false);
                textArea.setText(sb.toString());
                textArea.setCaretPosition(0); // position at top of text
                dialog.setContentPane(new JScrollPane(textArea));
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.pack();
                // SwingUtil.setMaxSizePercent(textArea, 0.6f, 0.6f);
                SwingUtil.center(dialog);
                dialog.setVisible(true);
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ignoreCloseFailed) {
                    }
                }
            }

            // System.err.println(nm);
            // TODO Auto-generated method stub
        }
    }

    private JLabel getAboutTextLabel() {
        JLabel aboutTextLabel = new JLabel();
        aboutTextLabel.setName("aboutText");
        aboutTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        aboutTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
        aboutTextLabel.setVerticalAlignment(SwingConstants.TOP);
        return aboutTextLabel;
    }
}
