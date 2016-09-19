package ca.viaware.securefiles.editor;

import ca.viaware.securefiles.model.SecureEntry;

abstract class Editor<T extends SecureEntry> {

    private T entry;

    public Editor(T entry) {
        this.entry = entry;
    }

    protected T getEntry() {
        return entry;
    }

}
