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

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AdvancedTextArea extends JTextArea {

    private boolean modified;

    public AdvancedTextArea(String text, final ActionListener actionListener) {
        super(text);

        final AdvancedTextArea self = this;

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                modified = true;

                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume();
                    insert("    ", getCaretPosition());
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    try {
                        int lineNum = getLineOfOffset(getCaretPosition());
                        insert("\n", getCaretPosition());
                        String prevLine = getText().substring(getLineStartOffset(lineNum), getLineEndOffset(lineNum));
                        for (char c : prevLine.toCharArray()) {
                            if (c == ' ' || c == '\t') {
                                insert(Character.toString(c), getCaretPosition());
                            } else {
                                break;
                            }
                        }

                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (getCaretPosition() == 0) return;
                    try {
                        int lineNum = getLineOfOffset(getCaretPosition());
                        int backspaces = 1;
                        int spaces = 0;

                        if (getCaretPosition() >= 4) {
                            for (int i = getCaretPosition(); i >= getLineStartOffset(lineNum); i--) {
                                String c = getText(i, 1);
                                if (c.equals(" ")) {
                                    spaces++;
                                }
                            }

                            String tabString = getText(getCaretPosition() - 4, 4);
                            if (tabString.equals("    ") && spaces % 4 == 0) backspaces = 4;
                        }

                        replaceRange("", getCaretPosition() - backspaces, getCaretPosition());
                        e.consume();
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }

                } else if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
                    actionListener.actionPerformed(new ActionEvent(self, ActionEvent.ACTION_PERFORMED, "SAVE"));
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
