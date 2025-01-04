package eu.yalacirodev.sokoban;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class XSBReader {

    private static int width;
    private static int height;
    private static XSBFile file;

    // possible elements in a XSB well-formatted sokoban level
    private static final String components = " #$.+*@";


    // Reads the file filename in parameter and returns an XSBFile.
    public static XSBFile read(String filename, Context context) throws IOException {

        width = 0;
        height = 0;
        file = new XSBFile(filename);

        List linesList = fileFirstPassParsing(filename, context);

        if (linesList.size() == 0) {
            DevTools.DEBUG(filename + " : File not found.");
            throw new FileNotFoundException(filename + " : File not found.");
        }

        State state = new State(height, width);
        String line;

        int nbBox = 0;
        int nbTarget = 0;
        int nbSoko = 0;

        for (int y = 0; y < height; y++) {
            int index = 0;

            line = (String) linesList.get(y);

            for (; index < line.length(); index++) {
                char tmp = line.charAt(index);

                if (components.indexOf(tmp) == -1)
                    throw new IOException("Bad XSB File");

		/* keep count of boxes, targets and sokos */
                if (tmp == '$')
                    nbBox++;
                else if (tmp == '*') {
                    nbTarget++;
                    nbBox++;
                } else if (tmp == '.')
                    nbTarget++;
                else if (tmp == '+') {
                    nbTarget++;
                    nbSoko++;
                } else if (tmp == '@')
                    nbSoko++;


                state.setBlock(index, y, tmp);
            }
            // now we fill the end of the line with ' '
            for (; index < width; index++)
                state.setBlock(index, y, ' ');
        }

        if (nbBox != nbTarget || nbSoko != 1)
            throw new IOException("Bad XSB File");

        state.setPosition();
        file.setState(state);

        return file;
    }


    // First pass parsing. It instantiates the fields width, heigth,
    // and the fields authorName and title of the XSBFile file.
    private static List fileFirstPassParsing(String filename, Context context) {

        BufferedReader reader = null;
        String line = null;

        try {
            InputStream in = context.getApplicationContext().getAssets().open(filename);
            reader = new BufferedReader(new InputStreamReader(in));
            line = reader.readLine();
        } catch (IOException e) {
            System.err.println("Oups IOException " + e);
        }

        List<String> linesList = new ArrayList<>();
        int lg, index;

        while (reader != null && line != null) {
            if ((index = line.indexOf(":")) != -1) {
                if (line.contains("Author ") || line.contains("Author:"))
                    file.setAuthorName(line.substring(index + 1));
                else if (line.contains("Title ") || line.contains("Title:"))
                    file.setTitle(line.substring(index + 1));
                else if (line.contains("Best-steps ") || line.contains("Best-steps:"))
                    file.setBestStep(Integer.parseInt(line.substring(index + 1)));
            } else if (line.equals(""))
                continue;
            else {
                height++;
                if ((lg = line.length()) > width)
                    width = lg;
                linesList.add(line);
            }
            try {
                line = reader.readLine();
            } catch (IOException e) {
                System.err.println("Oups IOException " + e);
            }
        }
        return linesList;
    }

}
