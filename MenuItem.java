class MenuItem {
    private int depth = 0;
    private String label;
    private Runnable action;

    MenuItem(int depth, String label, Runnable action) {
        this.depth = depth;
        this.label = label;
        this.action = action;
    }

    MenuItem(String label, Runnable action) {
        this.label = label;
        this.action = action;
    }

    int getDepth() {
        return depth;
    }

    String getLabel() {
        return label;
    }

    Runnable getAction() {
        return action;
    }

    void run() {
        action.run();
    }
}
