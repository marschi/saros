package de.fu_berlin.inf.dpp.editor;

import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.activities.SPath;

/**
 * Empty abstract implementation of the ISharedEditorListener interface
 */
public abstract class AbstractSharedEditorListener implements
    ISharedEditorListener {

    public void activeEditorChanged(User user, SPath path) {
        // does nothing
    }

    public void editorRemoved(User user, SPath path) {
        // does nothing
    }

    public void driverEditorSaved(SPath path, boolean replicated) {
        // does nothing
    }

    public void followModeChanged(User user) {
        // does nothing
    }

    public void textEditRecieved(User user, SPath editor, String text,
        String replacedText, int offset) {
        // does nothing
    }
}