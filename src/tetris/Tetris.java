package tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import static tetris.StateHandler.loadGame;
import static tetris.StateHandler.saveGame;

/**
 * The {@code Tetris} class is responsible for handling much of the game logic and
 * reading user input.
 * @author Brendan Jones
 *
 */
public class Tetris extends JFrame {
	
	/**
	 * The Serial Version UID.
	 */
	private static final long serialVersionUID = -4722429764792514382L;

	/**
	 * The number of milliseconds per frame.
	 */
	private static final long FRAME_TIME = 1000L / 50L;
	
	/**
	 * The number of pieces that exist.
	 */
	private static final int TYPE_COUNT = TileType.values().length;
		
	/**
	 * The BoardPanel instance.
	 */
	private BoardPanel board;
	
	/**
	 * The SidePanel instance.
	 */
	private SidePanel side;
	
	/**
	 * Whether or not the game is paused.
	 */
	private boolean isPaused;
	
	/**
	 * Whether or not we've played a game yet. This is set to true
	 * initially and then set to false when the game starts.
	 */
	private boolean isNewGame;
	
	/**
	 * Whether or not the game is over.
	 */
	private boolean isGameOver;
	
	/**
	 * The current level we're on.
	 */
	private int iLevel;
	
	/**
	 * The current score.
	 */
	private int iScore;
	
	/**
	 * The Random number generator. This is used to
	 * spit out pieces randomly.
	 */
	private Random rRandom;
	
	/**
	 * The clock that handles the update logic.
	 */
	private Clock lLogicTimer;
				
	/**
	 * The current type of tile.
	 */
	private TileType tilCurrentType;
	
	/**
	 * The next type of tile.
	 */
	private TileType tilNextType;
		
	/**
	 * The current column of our tile.
	 */
	private int iCurrentCol;
	
	/**
	 * The current row of our tile.
	 */
	private int iCurrentRow;
	
	/**
	 * The current rotation of our tile.
	 */
	private int iCurrentRotation;
		
	/**
	 * Ensures that a certain amount of time passes after a piece is
	 * spawned before we can drop it.
	 */
	private int iDropCooldown;
	
	/**
	 * The speed of the game.
	 */
	private float fGameSpeed;

	/**
	 * The  first sound for each time a tile falls to the bottom.
	 */
	private SoundClip sBottom;
		
        /**
         * The second sound for each time a tile falls to the bottom.
         */
        private SoundClip sBottom2;
        /**
         * The sound to play when the game is over
         */
        private SoundClip sOver;
	/**
	 * The sound for the game
	 */
	private SoundClip sTrack;
        
	/**
	 * Check whether the game is paused or not
	 */
	private boolean bPaused;
        /**
         * Auxiliar variable to know which sound to play
         */
        private int iAux;
        /**
         * Boolean to check if the theme track is played
         */
        private boolean bCheck;
        
	/**
	 * The shaker helper object for the frame
	 */
	private ShakeFrame shaShaker;

	/**
	 * Creates a new Tetris instance. Sets up the window's properties,
	 * and adds a controller listener.
	 */
        
        private void setBasicProperties(){
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
        }
        
        private void initBoardPanel(){
            this.board = new BoardPanel(this);
            this.side = new SidePanel(this);
            /**
            * Set the background image to black
            **/
            board.whichImage(0);
        }
        
        private void addInstancestoWindow(){
            add(board, BorderLayout.CENTER);
            add(side, BorderLayout.EAST);
        }
        
        /*
        * Drop - When pressed, we check to see that the game is not
        * paused and that there is no drop cooldown, then set the
	* logic timer to run at a speed of 25 cycles per second.    
        */
        private void goDown(){
            if(!isPaused && iDropCooldown == 0){
                lLogicTimer.setCyclesPerSecond(25.0f);
            }
        }
        /*
        * Move Left - When pressed, we check to see that the game is
	* not paused and that the position to the left of the current
	* position is valid. If so, we decrement the current column by 1.
	*/        
        private void moveLeft(){
            if(!isPaused && board.isValidAndEmpty(tilCurrentType, iCurrentCol - 1, 
                                                iCurrentRow, iCurrentRotation)) {
                iCurrentCol--;
            }
        }
        /*
        * Move Right - When pressed, we check to see that the game is
        * not paused and that the position to the right of the current
        * position is valid. If so, we increment the current column by 1.
	*/
        private void moveRight(){
            if(!isPaused && board.isValidAndEmpty(tilCurrentType, iCurrentCol + 1, 
                                                iCurrentRow, iCurrentRotation)) {
                iCurrentCol++;
            }   
        }
        /*
        * Rotate Anticlockwise - When pressed, check to see that the game is not paused
        * and then attempt to rotate the piece anticlockwise. Because of the size and
	* complexity of the rotation code, as well as it's similarity to clockwise
        * rotation, the code for rotating the piece is handled in another method.
	*/
        private void rotateAntiClockwise(){
            if(!isPaused){
                rotatePiece((iCurrentRotation == 0) ? 3 : iCurrentRotation - 1);
            }    
        }
        /*
        * Rotate Clockwise - When pressed, check to see that the game is not paused
        * and then attempt to rotate the piece clockwise. Because of the size and
        * complexity of the rotation code, as well as it's similarity to anticlockwise
        * rotation, the code for rotating the piece is handled in another method.
	*/
        private void rotateClockwise(){
            if(!isPaused) {
                rotatePiece((iCurrentRotation == 3) ? 0 : iCurrentRotation + 1);
            }
        }
        /*
        *Pause Game - When pressed, check to see that we're currently playing a game.
        * If so, toggle the pause variable and update the logic timer to reflect this
        * change, otherwise the game will execute a huge number of updates and essentially
        * cause an instant game over when we unpause if we stay paused for more than a
        * minute or so.
	*/
        private void pauseGame(){

            bPaused = !bPaused;
            if(bPaused){
                sTrack.stop();
                board.whichImage(0);
            }else{
                board.whichImage(1);
                sTrack.setLooping(true);
                sTrack.play();
            }
            if(!isGameOver && !isNewGame) {
                isPaused = !isPaused;
		lLogicTimer.setPaused(isPaused);
            }
        }
        /*
        * Start Game - When pressed, check to see that we're in either a game over or new
        * game state. If so, reset the game.
	*/
        private void startAgain(){
            if(isGameOver || isNewGame){
                
		resetGame();
            }          
        }
        /*
        * Save Game - When pressed, check to see that we're currently playing a game.
        * If so, save the game's current state.
        */
        private void save(){
            if(!isGameOver && !isNewGame) {
                saveGame(Tetris.this);
            }   
        }
        /*
        * Load Game - When pressed, reset the game and load a
        * previous game state.
        */
        private void load(){
            //resetGame();
            loadGame(Tetris.this);
            //Tetris.this.getBoard().setInstance(Tetris.this);
            //Tetris.this.getSide().setInstance(Tetris.this);
            lLogicTimer.reset();
        }
        /**
         * 
         * stopTrack() - When pressed, the theme track is paused
         * or played
         */
        private void stopTrack(){
            if (bCheck){
                sTrack.stop();
                bCheck = false;
            }else{
                sTrack.setLooping(true);
                sTrack.play();
                bCheck = true;
            }
        }
        
        /**
	 * Sets the action based on the key pressed
	 * @param keyEvent The key pressed
	 */
        private void keyAction(KeyEvent keyEvent){
            switch (keyEvent.getKeyCode()){
                case KeyEvent.VK_S:
                    goDown();
                    break;
                case KeyEvent.VK_A:
                    moveLeft();
                    break;
                case KeyEvent.VK_D:
                    moveRight();
                    break;
                case KeyEvent.VK_Q:
                    rotateAntiClockwise();
                    break;
                case KeyEvent.VK_E:
                    rotateClockwise();
                    break;
                case KeyEvent.VK_P:
                    pauseGame();
                    break;
                default:
                    keyAction2(keyEvent);
                    break;
            }
        }
        /**
	 * Sets the action based on the key pressed
	 * @param keyEvent The key pressed
	 */
        private void keyAction2(KeyEvent keyEvent){
            switch (keyEvent.getKeyCode()){
                case KeyEvent.VK_ENTER:
                    startAgain();
                    break;
                case KeyEvent.VK_G:
                    save();
                    break;
                case KeyEvent.VK_C:
                    load();
                    break;
                case KeyEvent.VK_T:
                    stopTrack();
                    break;
                default:
                    break;
            }
        }
        /*
        * Here we resize the frame to hold the BoardPanel and SidePanel instances,
	* center the window on the screen, and show it to the user.
	*/
        private void resize(){
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }
	private Tetris() {
           
		/*
		 * Set the basic properties of the window.
		 */
                super("Tetris");
		setBasicProperties();
		/*
		 * Initialize the BoardPanel and SidePanel instances.
		 */
		initBoardPanel();
		
		/*
		 * Add the BoardPanel and SidePanel instances to the window.
		 */
		addInstancestoWindow();
		
		/*
		 * Adds a custom anonymous KeyListener to the frame.
		 */
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				keyAction(keyEvent);
			}
			
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				switch(keyEvent.getKeyCode()) {
				/*
				 * Drop - When released, we set the speed of the logic timer
				 * back to whatever the current game speed is and clear out
				 * any cycles that might still be elapsed.
				 */
				case KeyEvent.VK_S:
					lLogicTimer.setCyclesPerSecond(fGameSpeed);
					lLogicTimer.reset();
					break;
				}	
                        }
		});
		resize();
	}

	/**
	 * Starts the game running. Initializes everything and enters the game loop.
	 */
	private void startGame() {
		/*
		 * Initialize our Random number generator, logic timer, and new game variables.
		 */
		this.rRandom = new Random();
		this.isNewGame = true;
		this.fGameSpeed = 1.0f;
		this.sBottom = new SoundClip("zap1.wav");
                this.sBottom2 = new SoundClip("zap2.wav");
                this.sOver = new SoundClip("over.wav");
		this.sTrack = new SoundClip("tetris.wav");
                this.iAux = 1;
		this.bPaused = false;
		this.bCheck = true;
		this.shaShaker = new ShakeFrame(this);

		/*
		 * Setup the timer to keep the game from running before the user presses enter
		 * to start it.
		 */
		this.lLogicTimer = new Clock(fGameSpeed);
		lLogicTimer.setPaused(true);
		
		while(true) {
                
                        
			//Get the time that the frame started.
			long start = System.nanoTime();
			//Update the logic timer.
			lLogicTimer.update();
			
			/*
			 * If a cycle has elapsed on the timer, we can update the game and
			 * move our current piece down.
			 */
			if(lLogicTimer.hasElapsedCycle()) {
				updateGame();
			}
		
			//Decrement the drop cool down if necessary.
			if(iDropCooldown > 0) {
				iDropCooldown--;
			}
			
			//Display the window to the user.
			renderGame();
			
			/*
			 * Sleep to cap the framerate.
			 */
			long delta = (System.nanoTime() - start) / 1000000L;
			if(delta < FRAME_TIME) {
				try {
					Thread.sleep(FRAME_TIME - delta);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Updates the game and handles the bulk of it's logic.
	 */
	private void updateGame() {
		/*
		 * Check to see if the piece's position can move down to the next row.
		 */
		if(board.isValidAndEmpty(tilCurrentType, iCurrentCol, iCurrentRow + 1, iCurrentRotation)) {
			//Increment the current row if it's safe to do so.
			iCurrentRow++;
		} else {
			/*
			 * We've either reached the bottom of the board, or landed on another piece, so
			 * we need to add the piece to the board.
			 */
			board.addPiece(tilCurrentType, iCurrentCol, iCurrentRow, iCurrentRotation);
			if (iAux == 1){
				sBottom.play();
				iAux = 0;
			}else{
				sBottom2.play();
				iAux = 1;
			}

			/*
			 * Check to see if adding the new piece resulted in any cleared lines. If so,
			 * increase the player's score. (Up to 4 lines can be cleared in a single go;
			 * [1 = 100pts, 2 = 200pts, 3 = 400pts, 4 = 800pts]).
			 */
			int cleared = board.checkLines();
			if(cleared > 0) {
				iScore += 50 << cleared;
				shaShaker.startShaking();
			}
			
			/*
			 * Increase the speed slightly for the next piece and update the game's timer
			 * to reflect the increase.
			 */
			fGameSpeed += 0.035f;
			lLogicTimer.setCyclesPerSecond(fGameSpeed);
			lLogicTimer.reset();
			
			/*
			 * Set the drop cooldown so the next piece doesn't automatically come flying
			 * in from the heavens immediately after this piece hits if we've not reacted
			 * yet. (~0.5 second buffer).
			 */
			iDropCooldown = 25;
			
			/*
			 * Update the difficulty level. This has no effect on the game, and is only
			 * used in the "Level" string in the SidePanel.
			 */
			iLevel = (int)(fGameSpeed * 1.70f);
			
			/*
			 * Spawn a new piece to control.
			 */
			spawnPiece();
		}		
	}
	
	/**
	 * Forces the BoardPanel and SidePanel to repaint.
	 */
	private void renderGame() {
		board.repaint();
		side.repaint();
	}
	
	/**
	 * Resets the game variables to their default values at the start
	 * of a new game.
	 */
	private void resetGame() {
		this.iLevel = 1;
		this.iScore = 0;
		this.fGameSpeed = 1.0f;
		this.tilNextType = TileType.values()[rRandom.nextInt(TYPE_COUNT)];
		this.isNewGame = false;
		this.isGameOver = false;		
		board.clear();
		lLogicTimer.reset();
		lLogicTimer.setCyclesPerSecond(fGameSpeed);
		spawnPiece();
		sTrack.setLooping(true);
		sTrack.play();
		sTrack.setLooping(true);
                board.whichImage(1);
	}

	/**
	 * Spawns a new piece and resets our piece's variables to their default
	 * values.
	 */
	private void spawnPiece() {
		/*
		 * Poll the last piece and reset our position and rotation to
		 * their default variables, then pick the next piece to use.
		 */
		this.tilCurrentType = tilNextType;
		this.iCurrentCol = tilCurrentType.getSpawnColumn();
		this.iCurrentRow = tilCurrentType.getSpawnRow();
		this.iCurrentRotation = 0;
		this.tilNextType = TileType.values()[rRandom.nextInt(TYPE_COUNT)];
		
		/*
		 * If the spawn point is invalid, we need to pause the game and flag that we've lost
		 * because it means that the pieces on the board have gotten too high.
		 */
		if(!board.isValidAndEmpty(tilCurrentType, iCurrentCol, iCurrentRow, iCurrentRotation)) {
                        sBottom.stop();
                        sBottom2.stop();
                        sTrack.stop();
                        sOver.play();
			this.isGameOver = true;
                        board.whichImage(0);
			lLogicTimer.setPaused(true);
		}		
	}

	/**
	 * Attempts to set the rotation of the current piece to newRotation.
	 * @param newRotation The rotation of the new peice.
	 */
	private void rotatePiece(int newRotation) {
		/*
		 * Sometimes pieces will need to be moved when rotated to avoid clipping
		 * out of the board (the I piece is a good example of this). Here we store
		 * a temporary row and column in case we need to move the tile as well.
		 */
		int newColumn = iCurrentCol;
		int newRow = iCurrentRow;
		
		/*
		 * Get the insets for each of the sides. These are used to determine how
		 * many empty rows or columns there are on a given side.
		 */
		int left = tilCurrentType.getLeftInset(newRotation);
		int right = tilCurrentType.getRightInset(newRotation);
		int top = tilCurrentType.getTopInset(newRotation);
		int bottom = tilCurrentType.getBottomInset(newRotation);
		
		/*
		 * If the current piece is too far to the left or right, move the piece away from the edges
		 * so that the piece doesn't clip out of the map and automatically become invalid.
		 */
		if(iCurrentCol < -left) {
			newColumn -= iCurrentCol - left;
		} else if(iCurrentCol + tilCurrentType.getDimension() - right >=
				BoardPanel.iCOL_COUNT) {
			newColumn -= (iCurrentCol + tilCurrentType.getDimension() -
					right) - BoardPanel.iCOL_COUNT + 1;
		}
		
		/*
		 * If the current piece is too far to the top or bottom, move the piece away from the edges
		 * so that the piece doesn't clip out of the map and automatically become invalid.
		 */
		if(iCurrentRow < -top) {
			newRow -= iCurrentRow - top;
		} else if(iCurrentRow + tilCurrentType.getDimension() - bottom >=
				BoardPanel.iROW_COUNT) {
			newRow -= (iCurrentRow + tilCurrentType.getDimension() - bottom)
					- BoardPanel.iROW_COUNT + 1;
		}
		
		/*
		 * Check to see if the new position is acceptable. If it is, update the rotation and
		 * position of the piece.
		 */
		if(board.isValidAndEmpty(tilCurrentType, newColumn, newRow, newRotation)) {
			iCurrentRotation = newRotation;
			iCurrentRow = newRow;
			iCurrentCol = newColumn;
		}
	}
        
	
	/**
	 * Checks to see whether or not the game is paused.
	 * @return Whether or not the game is paused.
	 */
	public boolean isPaused() {
		return isPaused;
	}
	
	/**
	 * Checks to see whether or not the game is over.
	 * @return Whether or not the game is over.
	 */
	public boolean isGameOver() {
		return isGameOver;
	}
	
	/**
	 * Checks to see whether or not we're on a new game.
	 * @return Whether or not this is a new game.
	 */
	public boolean isNewGame() {
		return isNewGame;
	}
	
	/**
	 * Gets the current score.
	 * @return The score.
	 */
	public int getScore() {
		return iScore;
	}
	
	/**
	 * Gets the current level.
	 * @return The level.
	 */
	public int getLevel() {
		return iLevel;
	}

	/**
	 * Gets the current game speed.
	 * @return The game speed.
	 */
	public float getGameSpeed() {
		return fGameSpeed;
	}
	
	/**
	 * Gets the current type of piece we're using.
	 * @return The piece type.
	 */
	public TileType getPieceType() {
		return tilCurrentType;
	}
	
	/**
	 * Gets the next type of piece we're using.
	 * @return The next piece type.
	 */
	public TileType getNextPieceType() {
		return tilNextType;
	}
	
	/**
	 * Gets the column of the current piece.
	 * @return The column.
	 */
	public int getPieceCol() {
		return iCurrentCol;
	}
	
	/**
	 * Gets the row of the current piece.
	 * @return The row.
	 */
	public int getPieceRow() {
		return iCurrentRow;
	}
	
	/**
	 * Gets the rotation of the current piece.
	 * @return The rotation.
	 */
	public int getPieceRotation() {
		return iCurrentRotation;
	}

	/**
	 * Gets the board panel of the current game
	 * @return The board panel.
	 */
	public BoardPanel getBoard() {
		return board;
	}

	/**
	 * Gets the amount of time after a piece has been spawned before it can 
	 * be dropped
	 * @return The drop cooldown amount.
	 */
	public int getDropCooldown() {
		return iDropCooldown;
	}

	/**
	 * Sets whether or not the game is paused.
	 * @param isPaused Whether or not the game is paused.
	 */
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}
	
	/**
	 * Sets whether or not the game is over.
	 * @param isGameOver Whether or not the game is over.
	 */
	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}
	
	/**
	 * Sets whether or not we're on a new game.
	 * @param isNewGame Whether or not this is a new game.
	 */
	public void setNewGame(boolean isNewGame) {
		this.isNewGame = isNewGame;
	}
	
	/**
	 * Sets the current score.
	 * @param iScore The score.
	 */
	public void setScore(int iScore) {
		this.iScore = iScore;
	}
	
	/**
	 * Sets the current level.
	 * @param iLevel The level.
	 */
	public void setLevel(int iLevel) {
		this.iLevel = iLevel;
	}

	/**
	 * Sets the current game speed.
	 * @param fGameSpeed The game speed.
	 */
	public void setGameSpeed(float fGameSpeed) {
		this.fGameSpeed = fGameSpeed;
	}
	
	/**
	 * Sets the current type of piece we're using.
	 * @param tilCurrentType  piece type.
	 */
	public void setPieceType(TileType tilCurrentType) {
		this.tilCurrentType = tilCurrentType;
	}
	
	/**
	 * Sets the next type of piece we're using.
	 * @param tilNextType The next piece.
	 */
	public void setNextPieceType(TileType tilNextType) {
		this.tilNextType = tilNextType;
	}
	
	/**
	 * Sets the column of the current piece.
	 * @param iCurrentCol The column.
	 */
	public void setPieceCol(int iCurrentCol) {
		this.iCurrentCol = iCurrentCol;
	}
	
	/**
	 * Sets the row of the current piece.
	 * @param iCurrentRow The row.
	 */
	public void setPieceRow(int iCurrentRow) {
		this.iCurrentRow = iCurrentRow;
	}
	
	/**
	 * Sets the rotation of the current piece.
	 * @param iCurrentRotation The rotation.
	 */
	public void setPieceRotation(int iCurrentRotation) {
		this.iCurrentRotation = iCurrentRotation;
	}

	/**
	 * Sets the amount of time after a piece has been spawned before it can 
	 * be dropped
	 * @param iDropCooldown The drop cooldown amount.
	 */
	public void setDropCooldown(int iDropCooldown) {
		this.iDropCooldown = iDropCooldown;
	}

	/**
	 * Entry-point of the game. Responsible for creating and starting a new
	 * game instance.
	 * @param args Unused.
	 */
	public static void main(String[] args) {
		Tetris tetris = new Tetris();
		tetris.startGame();
	}

}
