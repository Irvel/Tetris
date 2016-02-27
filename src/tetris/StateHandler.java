package tetris;

import java.io.*;

/**
 * StateHandler
 * Utility class to save a given state of the game and load a previous state
 * of the game.
 *
 * @author Irvel
 */
public class StateHandler {

    /**
     * Saves the current game member variables to a binary file.
     * @param tetGame The current instance of the game.
     */
    public static void saveGame(Tetris tetGame) {
        try {
            /*
			 * Save a serialized version of the individual member variables
			 * in the received Tetris instance
			 */
            ObjectOutputStream objOut = new ObjectOutputStream(
                    new FileOutputStream("saveGame.bin"));
            writeVariables(tetGame, objOut);
            objOut.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished writing stuff");
    }

    /**
     * Loads a previously saved game state by loading the game variables
     * from a binary file to the current instance member variables.
     * @param tetGame The current instance of the game.
     */
    public static void loadGame(Tetris tetGame) {
        /*
	     * Load a serialized version of a previous game state from
	     * a binary file, and set each member variable in the received Tetris
	     * instance
		 */
        try {
            ObjectInputStream objIn = new ObjectInputStream(
                    new FileInputStream("saveGame.bin"));
            readVariables(tetGame, objIn);
            objIn.close();
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load the previous game state");
            e.printStackTrace();
        }
    }

    /**
     * Saves the required member variables of Tetris for capturing the game's
     * state.
     * @param tetGame The current instance of the game.
     * @param objOut The file to write the current state.
     */
    private static void writeVariables(Tetris tetGame, ObjectOutputStream objOut) throws
                                                                                  IOException {
        objOut.writeObject(tetGame.isNewGame());
        objOut.writeObject(tetGame.isGameOver());
        objOut.writeObject(tetGame.isPaused());
        objOut.writeObject(tetGame.getScore());
        objOut.writeObject(tetGame.getLevel());
        objOut.writeObject(tetGame.getGameSpeed());
        objOut.writeObject(tetGame.getPieceType());
        objOut.writeObject(tetGame.getNextPieceType());
        objOut.writeObject(tetGame.getPieceCol());
        objOut.writeObject(tetGame.getPieceRow());
        objOut.writeObject(tetGame.getPieceRotation());
        objOut.writeObject(tetGame.getBoard().getTiles());
        objOut.writeObject(tetGame.getDropCooldown());
    }

    /**
     * Loads the required member variables of Tetris for recovering a
     * previous game state.
     * @param tetGame The current instance of the game.
     * @param objIn The file to read from.
     */
    private static void readVariables(Tetris tetGame, ObjectInputStream objIn) throws
                                                                               IOException,
                                                                               ClassNotFoundException {
        tetGame.setNewGame((boolean) objIn.readObject());
        tetGame.setGameOver((boolean)objIn.readObject());
        tetGame.setPaused((boolean)objIn.readObject());
        tetGame.setScore((int)objIn.readObject());
        tetGame.setLevel((int)objIn.readObject());
        tetGame.setGameSpeed((float)objIn.readObject());
        tetGame.setPieceType((TileType) objIn.readObject());
        tetGame.setNextPieceType((TileType) objIn.readObject());
        tetGame.setPieceCol((int) objIn.readObject());
        tetGame.setPieceRow((int) objIn.readObject());
        tetGame.setPieceRotation((int) objIn.readObject());
        tetGame.getBoard().setTiles ((TileType[][]) objIn.readObject());
        tetGame.setDropCooldown((int) objIn.readObject());
    }

}