/*
Copyright 2016 Seth Traverse

This file is part of SecureFiles.

SecureFiles is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SecureFiles is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SecureFiles.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.viaware.securefiles.editor;

import ca.viaware.api.gui.base.*;
import ca.viaware.api.logging.Log;
import ca.viaware.securefiles.model.entry.SecureString;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;

public class StringEditor implements ActionListener {

    private SecureString entry;
    private AdvancedTextArea textArea;

    public StringEditor(SecureString entry) {
        this.entry = entry;
        initGui();
    }

    private void initGui() {
        final VFrame frame = new VFrame("String Editor - " + entry.getTitle());

        VPanel panel = frame.getMainPanel();
        panel.setLayout(new BorderLayout());
        textArea = new AdvancedTextArea(entry.getString(), this);
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (textArea.isModified()) {
                    int result = JOptionPane.showConfirmDialog(frame, "You have unsaved changes, save them now?", "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.CANCEL_OPTION) {
                        return;
                    } else if (result == JOptionPane.YES_OPTION) {
                        save();
                    } else {
                        //Log.info("Discarding changes to %0", entry.getTitle());
                    }
                }
                frame.dispose();
            }
        });

        VMenuBar menuBar = new VMenuBar();
        VMenu menu = new VMenu("File");
        menu.add(new VMenuItem("Save", this, "SAVE"));
        menuBar.add(menu);
        frame.add(menuBar, BorderLayout.PAGE_START);

        frame.setSize(800, 600);
        frame.setMinimumSize(new Dimension(400, 400));
        frame.setVisible(true);

        frame.setAlwaysOnTop(true);
        frame.setAlwaysOnTop(false);
        frame.requestFocus();
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("SAVE")) {
            save();
        }
    }

    private void save() {
        entry.setString(textArea.getText());
        textArea.setModified(false);
    }
}
