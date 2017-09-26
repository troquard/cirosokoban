package eu.yalacirodev.sokoban;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;



public class SokobanSolution
{
    private Sokoban sokoban;
    private Sokoban originalSokoban;
    private State initial;
    private Collection<Sokoban.Movement> history;
    private StateCompact targetS4S;

    private HashSet<StateCompact> s4sVisited;
    private LinkedList<String> prefixPaths = new LinkedList<>();
    private LinkedList<StateCompact> pPathsLastS4S = new LinkedList<>();

    private int solutionRank; // size of solutions currently investigated
    private boolean solving;

    private LinkedList<SokobanSolutionListener> listenerSet;


    private static final char[] MOVE_LETTERS = {'u', 'd', 'l', 'r'};

    private int NUM_STEPS_HINT;


    public SokobanSolution(Sokoban sokoban, int numStepHint) {
        originalSokoban = sokoban;
        NUM_STEPS_HINT = numStepHint;
        listenerSet = new LinkedList<>();
    }

    private void init() {
        //sokoban.init(); // if one wants a solution from the initial state
        sokoban = new MySokoban(originalSokoban);
        initial = sokoban.getCurrentState();
        history = new ArrayList<>(sokoban.getHistory());

        DevTools.DEBUG("Initial " + initial.toString());
        s4sVisited = new HashSet<>();
        s4sVisited.add(new StateCompact(initial));

        prefixPaths = new LinkedList<String>();
        pPathsLastS4S = new LinkedList<StateCompact>();

        targetS4S = new StateCompact(getTargetState());
        DevTools.DEBUG("Objective " + targetS4S.toString());
        solutionRank = 0;
    }


    public void cancel() {
        solving = false;
        notifySolvingStateChange();
    }

    public void run() {
        init();
        solving = true;
        notifySolvingStateChange();

        String sol = generateSolutionIfExists(); // given as a string of MOVE_LETTERS

        int length = sol.length();

        DevTools.DEBUG("Length solution " + length);

        //sokoban.init(); // if one has opted out for a solution from the inital state
        originalSokoban.setHistory(history);

        for(int i = 0 ; i < Math.min(NUM_STEPS_HINT, length) ; i++)
            originalSokoban.move( charToDirection(sol.charAt(i)) );
        for(int i = 0 ; i < Math.min(NUM_STEPS_HINT, length) ; i++)
            originalSokoban.undo();
    }

    private String generateSolutionIfExists() {
        prefixPaths.add("");
        solutionRank = 0;

        pPathsLastS4S.add(new StateCompact(sokoban.getCurrentState()));

        while( isSolving() && prefixPaths.size() != 0 ) {

            assert prefixPaths.size() == pPathsLastS4S.size();

            solutionRank++;
            notifySolutionRankChange();
            DevTools.DEBUG("Investigating solution paths of size " + solutionRank);

            int nbPartialPaths = prefixPaths.size();

            for (int i = 0 ; i < nbPartialPaths && isSolving(); i++) {
                String strDad = (String) prefixPaths.get(0);
                StateCompact s4sDad = (StateCompact) pPathsLastS4S.get(0);

                prefixPaths.remove(strDad);
                pPathsLastS4S.remove(s4sDad);

                for (int j = 0 ; j < 4 ; j++) {
                    String child = strDad + MOVE_LETTERS[j];
                    if (winningPath(child, s4sDad)) {
                        DevTools.DEBUG("Found winning path of size " + solutionRank);
                        return child;
                    }
                }
            }
        }

        s4sVisited = null;
        prefixPaths = null;
        pPathsLastS4S = null;
        return "";
    }


    private boolean winningPath(String ch, StateCompact s4s) {
        sokoban.setCurrentState(getState(s4s));
        sokoban.move(charToDirection(ch.charAt(solutionRank - 1)));

        StateCompact s = new StateCompact(sokoban.getCurrentState());

        if (s.winning())
            return true;

        if (!(s4sVisited.contains(s) || s.losing())) {
            prefixPaths.add(ch);
            pPathsLastS4S.add(s);
            s4sVisited.add(s);
        }

        sokoban.setCurrentState(initial);

        return false;
    }


    public boolean isSolving() {
        return solving;
    }
    public int currentSolutionRank() {
        return solutionRank;
    }


    private static Sokoban.Direction charToDirection (char c)
    {
        Sokoban.Direction dir = new Sokoban.Direction();;

        switch(c)
        {
            case 'u' : {
                dir = new Sokoban.Direction(Sokoban.Direction.UP);
                break; }
            case 'd' : {
                dir = new Sokoban.Direction(Sokoban.Direction.DOWN);
                break; }
            case 'r' : {
                dir = new Sokoban.Direction(Sokoban.Direction.RIGHT);
                break; }
            case 'l' : {
                dir = new Sokoban.Direction(Sokoban.Direction.LEFT);
                break; }
        }

        return dir;
    }


    private static char directionToChar (Sokoban.Direction dir)
    {
        char c = ' ';

        switch(dir.getDirection())
        {
            case Sokoban.Direction.UP : {
                c = 'u';
                break;}
            case Sokoban.Direction.DOWN : {
                c = 'd';
                break;}
            case Sokoban.Direction.RIGHT : {
                c = 'r';
                break;}
            case Sokoban.Direction.LEFT : {
                c = 'l';
                break;}
        }

        return c;
    }


    /*
       Convert a StateCompact into a State.
     */
    private State getState(StateCompact s) {
        State res = new State(initial);
        ArrayList posList = ((StateCompact)s).positions;
        char block ;
        for (int y = 0; y < res.getHeight(); y++)
            for (int x = 0; x < res.getWidth(); x++){
                block = res.getBlock(x, y);
                if (block == '$' || res.getBlock(x, y) == '@')
                    res.setBlock(x, y, ' ');
                if (block == '*' || res.getBlock(x, y) == '+')
                    res.setBlock(x, y, '.');
            }
        Coordinate sokoPos = (Coordinate) posList.get(0);
        int x = (int) sokoPos.getX();
        int y = (int) sokoPos.getY();
        block = res.getBlock(x, y);
        if ( block == ' ')
            res.setBlock(x, y, '@');
        if (block == '.')
            res.setBlock(x, y, '+');

        int size = posList.size();
        for (int i = 1; i < size ; i++){
            Coordinate boxPos = (Coordinate) posList.get(i);
            x = (int) boxPos.getX();
            y = (int) boxPos.getY();
            block = res.getBlock(x, y);
            if (block == ' ')
                res.setBlock(x, y, '$');
            if (block  == '.')
                res.setBlock(x, y, '*');
        }

        return res;
    }

    /*
        Determines the StateCompact representation of an effectively winning state.
     */
    private StateCompact getTargetState() {
        StateCompact res = new StateCompact();
        res.positions.add(new Coordinate(-17,-17)); // we don't care about the position of the soko
        for (int y = 0; y < initial.getHeight(); y++)
            for (int x = 0; x < initial.getWidth(); x++){
                char c  = initial.getBlock(x, y);
                if (c == '.' || c == '*' || c == '+') // we want a box on every target
                    res.positions.add(new Coordinate(x, y));
            }
        return res;
    }




    /*
     Class to manage sokoban states in a compact way.

     A State with matrix :
     ########
     ##  $. #
     #  ##. #
     # @  $ #
     ####   #
        #####

     becomes :
     (2,3)(4.1)(5.3),
     where the first couple represents the position of the soko,
     and the remaining couples are the boxes positions.

     The method getState is useful to convert back a StateCompact into a State.
     */

    public class StateCompact {

        ArrayList<Coordinate> positions; // record of the soko position and boxes positions


        public StateCompact() {
            this.positions = new ArrayList<Coordinate>();
        }


        public StateCompact(StateCompact s4s){
            this.positions = new ArrayList<Coordinate>(s4s.positions);
        }

        public StateCompact(State s) {
            positions = new ArrayList<Coordinate>();
            positions.add(new Coordinate(s.getPosX(), s.getPosY()));
            for (int y = 0; y < s.getHeight(); y++)
                for (int x = 0; x < s.getWidth(); x++)
                    if (s.getBlock(x,y) == '$' || s.getBlock(x,y) == '*')
                        positions.add(new Coordinate(x, y));
        }

        public int hashCode() {
            return toString().hashCode();
        }

        public boolean equals(Object o) {
            return o instanceof StateCompact && (((StateCompact) o).positions).equals(positions);
        }

        // same as equals but does not care about where the soko is
        private boolean equalsWSoko(Object o) {
            if ( o instanceof StateCompact){

                int lg = positions.size();
                assert (lg == ((StateCompact)o).positions.size());

                //etats toujours dans le meme ordre
                for (int i = 1; i < lg; i++)
                    if  ( ! ((Coordinate) positions.get(i)).equals( (Coordinate) ((StateCompact)o).positions.get(i) ) )
                        return false;

                return true;
            }
            return false;
        }

        public boolean winning() {
            return this.equalsWSoko(targetS4S);
        }

        private boolean isWallOrBox(char block) {
            return block == '#' || block == '$' || block == '*';
        }

        /**
         Checks whether the state is already losing because a box is blocked. E.g.,

         NN  # #  NN   $# $N   #$ N$
         N$ #$ $# $N   #  NN    # NN

         with N : $, *, #

         It does not find all losing states!
         */
        public boolean losing () {
            State maybeLosing = getState(this);
            int h = maybeLosing.getHeight() ;
            int w = maybeLosing.getWidth();
            int taille = positions.size();

            for (int i = 1 ; i < taille ; i++) {
                int x = (int) ((Coordinate) positions.get(i)).getX();
                int y = (int) ((Coordinate) positions.get(i)).getY();

                char block = maybeLosing.getBlock(x, y);

                if (block == '$') {
                    if (maybeLosing.getBlock(x - 1, y) == '#' && maybeLosing.getBlock(x, y - 1) == '#')
                        return true; /*  #
					                    #$*/

                    if (isWallOrBox(maybeLosing.getBlock(x - 1, y))
                            && isWallOrBox(maybeLosing.getBlock(x, y - 1)) && isWallOrBox(maybeLosing.getBlock(x - 1, y - 1)))
                        return true; /*NN
				                       N$*/


                    if (maybeLosing.getBlock(x, y - 1) == '#' && maybeLosing.getBlock(x + 1, y) == '#')
                        return true;

                    if (isWallOrBox(maybeLosing.getBlock(x + 1, y))
                            && isWallOrBox(maybeLosing.getBlock(x, y - 1)) && isWallOrBox(maybeLosing.getBlock(x + 1, y - 1)))
                        return true; /*NN
				                       $N*/

                    if (maybeLosing.getBlock(x + 1, y) == '#' && maybeLosing.getBlock(x, y + 1) == '#') {
                        //DevTools.DEBUG("Serious problem with box at angle " + x + "," + y + " in " + maybeLosing.toString());
                        return true;
                    }

                    if (isWallOrBox(maybeLosing.getBlock(x + 1, y))
                            && isWallOrBox(maybeLosing.getBlock(x, y + 1)) && isWallOrBox(maybeLosing.getBlock(x + 1, y + 1)))
                        return true;/*$N
				                      NN*/

                    if (maybeLosing.getBlock(x - 1, y) == '#' && maybeLosing.getBlock(x, y + 1) == '#') {
                        //DevTools.DEBUG("Serious problem with box at angle " + x + "," + y + " in " + maybeLosing.toString());
                        return true;
                    }

                    if (isWallOrBox(maybeLosing.getBlock(x - 1, y))
                            && isWallOrBox(maybeLosing.getBlock(x, y + 1)) && isWallOrBox(maybeLosing.getBlock(x - 1, y + 1))) {
                        //DevTools.DEBUG("Serious problem with box at angle " + x + "," + y + " in " + maybeLosing.toString());
                        return true; /*N$
				                       NN*/
                    }
                }


                // box along an horizontal wall segment
                // #################
                // #   #  . $  $   #
                if (maybeLosing.getBlock(x,y-1) == '#') {
                    int nbBoxesAlongTheWall = 1;
                    int nbTargetsAlongTheWall;
                    if (maybeLosing.getBlock(x,y) == '*')
                        nbTargetsAlongTheWall = 1;
                    else
                        nbTargetsAlongTheWall = 0;
                    boolean seriousProblem = true;
                    int k = x - 1;
                    while (k > 0) {
                        if (maybeLosing.getBlock(k, y - 1) != '#') {
                            seriousProblem = false;
                            break;
                        }
                        char blockk = maybeLosing.getBlock(k, y);
                        if (blockk == '#')
                            break;
                        if (blockk == '$') {
                            nbBoxesAlongTheWall++;
                        } else if (blockk == '*') {
                            nbBoxesAlongTheWall++;
                            nbTargetsAlongTheWall++;
                        } else if (blockk == '+' || blockk == '.') {
                            nbTargetsAlongTheWall++;
                        }
                        k--;
                    }
                    k = x + 1;
                    while (seriousProblem && k < w) {
                        if(maybeLosing.getBlock(k, y - 1) != '#') {
                            seriousProblem = false;
                            break;
                        }
                        char blockk = maybeLosing.getBlock(k, y);
                        if (blockk == '#')
                            break;
                        if (blockk == '$') {
                            nbBoxesAlongTheWall++;
                        } else if (blockk == '*') {
                            nbBoxesAlongTheWall++;
                            nbTargetsAlongTheWall++;
                        } else if (blockk == '+' || blockk == '.') {
                            nbTargetsAlongTheWall++;
                        }
                        k++;
                    }
                    if (seriousProblem && nbBoxesAlongTheWall > nbTargetsAlongTheWall) {
                        //DevTools.DEBUG("Serious horizontal1 problem with box at " + x + "," + y + " in " + maybeLosing.toString());
                        return true;
                    }
                }


                // box along an horizontal wall segment
                // #   #  . $  $   #
                // #################
                if (maybeLosing.getBlock(x,y+1) == '#') {
                    int nbBoxesAlongTheWall = 1;
                    int nbTargetsAlongTheWall;
                    if (maybeLosing.getBlock(x,y) == '*')
                        nbTargetsAlongTheWall = 1;
                    else
                        nbTargetsAlongTheWall = 0;
                    boolean seriousProblem = true;
                    int k = x - 1;
                    while (k > 0) {
                        if (maybeLosing.getBlock(k, y + 1) != '#') {
                            seriousProblem = false;
                            break;
                        }
                        char blockk = maybeLosing.getBlock(k, y);
                        if (blockk == '#')
                            break;
                        if (blockk == '$') {
                            nbBoxesAlongTheWall++;
                        } else if (blockk == '*') {
                            nbBoxesAlongTheWall++;
                            nbTargetsAlongTheWall++;
                        } else if (blockk == '+' || blockk == '.') {
                            nbTargetsAlongTheWall++;
                        }
                        k--;
                    }
                    k = x + 1;
                    while (seriousProblem && k < w) {
                        if(maybeLosing.getBlock(k, y + 1) != '#') {
                            seriousProblem = false;
                            break;
                        }
                        char blockk = maybeLosing.getBlock(k, y);
                        if (blockk == '#')
                            break;
                        if (blockk == '$') {
                            nbBoxesAlongTheWall++;
                        } else if (blockk == '*') {
                            nbBoxesAlongTheWall++;
                            nbTargetsAlongTheWall++;
                        } else if (blockk == '+' || blockk == '.') {
                            nbTargetsAlongTheWall++;
                        }
                        k++;
                    }
                    if (seriousProblem && nbBoxesAlongTheWall > nbTargetsAlongTheWall) {
                        //DevTools.DEBUG("Serious horizontal2 problem with box at " + x + "," + y + " in " + maybeLosing.toString());
                        return true;
                    }
                }

                // box along a vertical wall segment
                // ##
                // #
                // ##
                // #
                // #$
                // #
                // #*
                // ##
                if (maybeLosing.getBlock(x-1,y) == '#') {
                    int nbBoxesAlongTheWall = 1;
                    int nbTargetsAlongTheWall;
                    if (maybeLosing.getBlock(x,y) == '*')
                        nbTargetsAlongTheWall = 1;
                    else
                        nbTargetsAlongTheWall = 0;
                    boolean seriousProblem = true;
                    int k = y - 1;
                    while (k > 0) {
                        if (maybeLosing.getBlock(x-1, k) != '#') {
                            seriousProblem = false;
                            break;
                        }
                        char blockk = maybeLosing.getBlock(x, k);
                        if (blockk == '#')
                            break;
                        if (blockk == '$') {
                            nbBoxesAlongTheWall++;
                        } else if (blockk == '*') {
                            nbBoxesAlongTheWall++;
                            nbTargetsAlongTheWall++;
                        } else if (blockk == '+' || blockk == '.') {
                            nbTargetsAlongTheWall++;
                        }
                        k--;
                    }
                    k = y + 1;
                    while (seriousProblem && k < h) {
                        if(maybeLosing.getBlock(x-1, k) != '#') {
                            seriousProblem = false;
                            break;
                        }
                        char blockk = maybeLosing.getBlock(x, k);
                        if (blockk == '#')
                            break;
                        if (blockk == '$') {
                            nbBoxesAlongTheWall++;
                        } else if (blockk == '*') {
                            nbBoxesAlongTheWall++;
                            nbTargetsAlongTheWall++;
                        } else if (blockk == '+' || blockk == '.') {
                            nbTargetsAlongTheWall++;
                        }
                        k++;
                    }
                    if (seriousProblem && nbBoxesAlongTheWall > nbTargetsAlongTheWall) {
                        //DevTools.DEBUG("Serious vertical1 problem with box at " + x + "," + y + " in " + maybeLosing.toString());
                        return true;
                    }
                }

                // box along a vertical wall segment
                // ##
                //  #
                // ##
                //  #
                // $#
                //  #
                // *#
                // ##
                if (maybeLosing.getBlock(x+1,y) == '#') {
                    int nbBoxesAlongTheWall = 1;
                    int nbTargetsAlongTheWall;
                    if (maybeLosing.getBlock(x,y) == '*')
                        nbTargetsAlongTheWall = 1;
                    else
                        nbTargetsAlongTheWall = 0;
                    boolean seriousProblem = true;
                    int k = y - 1;
                    while (k > 0) {
                        if (maybeLosing.getBlock(x+1, k) != '#') {
                            seriousProblem = false;
                            break;
                        }
                        char blockk = maybeLosing.getBlock(x, k);
                        if (blockk == '#')
                            break;
                        if (blockk == '$') {
                            nbBoxesAlongTheWall++;
                        } else if (blockk == '*') {
                            nbBoxesAlongTheWall++;
                            nbTargetsAlongTheWall++;
                        } else if (blockk == '+' || blockk == '.') {
                            nbTargetsAlongTheWall++;
                        }
                        k--;
                    }
                    k = y + 1;
                    while (seriousProblem && k < h) {
                        if(maybeLosing.getBlock(x+1, k) != '#') {
                            seriousProblem = false;
                            break;
                        }
                        char blockk = maybeLosing.getBlock(x, k);
                        if (blockk == '#')
                            break;
                        if (blockk == '$') {
                            nbBoxesAlongTheWall++;
                        } else if (blockk == '*') {
                            nbBoxesAlongTheWall++;
                            nbTargetsAlongTheWall++;
                        } else if (blockk == '+' || blockk == '.') {
                            nbTargetsAlongTheWall++;
                        }
                        k++;
                    }
                    if (seriousProblem && nbBoxesAlongTheWall > nbTargetsAlongTheWall) {
                        //DevTools.DEBUG("Serious vertical2 problem with box at " + x + "," + y + " in " + maybeLosing.toString());
                        return true;
                    }
                }



            }
            return false;
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < positions.size(); i++)
                s.append((positions.get(i)).toString());
            s.append("\n");
            return s.toString();
        }

    } // end StateCompact



    class Coordinate {
        private int posX;
        private int posY;

        public Coordinate(int x, int y) {
            posX = x; posY = y;
        }

        public int getX() {
            return posX;
        }

        public int getY() {
            return posY;
        }

        public String toString() {
            return "(" +posX +"."+posY+")";
        }

        public boolean equals(Object o) {
            return (o instanceof Coordinate) && posX == ((Coordinate) o).getX() && posY == ((Coordinate) o).getY();
        }
    } // end class Coordinate


    /*
    LISTENER
     */

    public interface SokobanSolutionListener {
        void solvingStateChange();
        void solvingSolutionRankChange();
    }

    public void addSokobanSolutionListener(SokobanSolutionListener listener)
    {
        listenerSet.add(listener);
    }

    public void removeSokobanSolutionListener(SokobanSolutionListener listener)
    {
        listenerSet.remove(listener);
    }

    private void notifySolvingStateChange()
    {
        Iterator i = listenerSet.listIterator(0);
        while (i.hasNext()) {
            SokobanSolutionListener listener = (SokobanSolutionListener) i.next();
            listener.solvingStateChange();
        }
    }

    private void notifySolutionRankChange()
    {
        Iterator i = listenerSet.listIterator(0);
        while (i.hasNext()) {
            SokobanSolutionListener listener = (SokobanSolutionListener) i.next();
            listener.solvingSolutionRankChange();
        }
    }
}// end of SokobanSolution