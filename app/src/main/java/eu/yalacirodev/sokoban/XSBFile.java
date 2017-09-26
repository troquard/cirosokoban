package eu.yalacirodev.sokoban;

public class XSBFile {

    private final int BEST_STEP_MAX_UNKNOWN = 10000000;
    private final String AUTHOR_UNKNOWN = "unknown";

    private State initial;
    private String title;
    private String authorName;
    private int bestSteps;

    public XSBFile(String name) {
        title = name;
        authorName = AUTHOR_UNKNOWN;
        bestSteps = BEST_STEP_MAX_UNKNOWN;
    }

    public State getState() {
        return initial;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public int getBestStep() {
        return bestSteps;
    }

    public void setState(State state) {
        this.initial = state;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthorName(String name) {
        this.authorName = name;
    }

    public void setBestStep(int num) {
        this.bestSteps = num;
    }
}
