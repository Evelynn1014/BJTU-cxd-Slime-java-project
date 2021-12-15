package client.event;

import client.bean.Slimeball;

public class SlimeAdhesionEvent {
    private Slimeball root;

    public Slimeball getRoot() {
        return root;
    }

    public void setRoot(Slimeball root) {
        this.root = root;
    }

    public SlimeAdhesionEvent(Slimeball root) {
        this.root = root;
    }
}
