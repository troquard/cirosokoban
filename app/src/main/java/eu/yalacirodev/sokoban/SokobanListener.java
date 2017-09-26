package eu.yalacirodev.sokoban;


public interface SokobanListener
{
    void sokobanOnMove (Sokoban.Direction dir);

    void sokobanOnReset();

    void sokobanOnInit();

    void sokobanOnGoToLevel();
}