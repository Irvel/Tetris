package tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.Objects;

/**
 * The {@code BoardPanel} class is responsible for displaying the game grid and
 * handling things related to the game board.
 * @author Brendan Jones
 *
 */
public class BoardPanel extends JPanel {

	/**
	 * Serial Version UID.
	 */
	private static final long lSERIALVERSIONUID = 5055679736784226108L;

	/**
	 * Minimum color component values for tiles. This is required if we
	 * want to show both light and dark shading on our tiles.
	 */
	public static final int iCOLOR_MIN = 35;
	
	/**
	 * Maximum color component values for tiles. This is required if we
	 * want to show both light and dark shading on our tiles.
	 */
	public static final int iCOLOR_MAX = 255 - iCOLOR_MIN;
	
	/**
	 * The width of the border around the game board.
	 */
	private static final int iBORDER_WIDTH = 5;
	
	/**
	 * The number of columns on the board.
	 */
	public static final int iCOL_COUNT = 10;
		
	/**
	 * The number of visible rows on the board.
	 */
	private static final int iVISIBLE_ROW_COUNT = 20;
	
	/**
	 * The number of rows that are hidden from view.
	 */
	private static final int iHIDDEN_ROW_COUNT = 2;
	
	/**
	 * The total number of rows that the board contains.
	 */
	public static final int iROW_COUNT = iVISIBLE_ROW_COUNT + iHIDDEN_ROW_COUNT;
	
	/**
	 * The number of pixels that a tile takes up.
	 */
	public static final int iTILE_SIZE = 24;

	/**
	 * The number of extra pixels that the glow of a tile takes up.
	 */
	public static final int iGLOW_OFFSET = 1;
	
	/**
	 * The width of the shading on the tiles.
	 */
	public static final int iSHADE_WIDTH = 4;
	
	/**
	 * The central x coordinate on the game board.
	 */
	private static final int iCENTER_X = iCOL_COUNT * iTILE_SIZE / 2;
	
	/**
	 * The central y coordinate on the game board.
	 */
	private static final int iCENTER_Y = iVISIBLE_ROW_COUNT * iTILE_SIZE / 2;
		
	/**
	 * The total width of the panel.
	 */
	public static final int iPANEL_WIDTH = iCOL_COUNT * iTILE_SIZE + iBORDER_WIDTH * 2;
	
	/**
	 * The total height of the panel.
	 */
	public static final int iPANEL_HEIGHT = iVISIBLE_ROW_COUNT * iTILE_SIZE + iBORDER_WIDTH * 2;
	
	/**
	 * The larger font to display.
	 */
	private static final Font fonLARGE_FONT = new Font("Tahoma", Font.BOLD, 44);

	/**
	 * The medium-sized font to display.
	 */
	private static final Font fonMEDIUM_FONT = new Font("Tahoma", Font.BOLD, 28);

	/**
	 * The smaller font to display.
	 */
	private static final Font fonSMALL_FONT = new Font("Tahoma", Font.PLAIN, 14);
	
	/**
	 * The Tetris instance.
	 */
	private Tetris tetTetris;
	
	/**
	 * The tiles that make up the board.
	 */
	private TileType[][] tilTile;

	/**
	 * The current amount of alpha a tile is being drawn with in order to
	 * animate a "shining" effect.
	 */
	private float fAlphaAmount;

	/**
	 * How much is the current alpha being modified with the drawing of each
	 * tile.
	 */
	private float fAlphaFactor;

	/**
	 * The level of displacement from the gradient center to animate motion.
	 */
	private float iGradientModifier;
        /**
         * 
         */
        private Image imgBackground;
        /**
         * Integer to chose which image to paint
         */
        private int iImage;

        
	/**
	 * Crates a new GameBoard instance.
	 * @param tetTetris The Tetris instance to use.
	 */
	public BoardPanel(Tetris tetTetris) {
		this.tetTetris = tetTetris;
		this.tilTile = new TileType[iROW_COUNT][iCOL_COUNT];
		this.fAlphaAmount = 0.2f;
		this.fAlphaFactor = 0.01f;
		this.iGradientModifier = 0;
                iImage = 1;
		setPreferredSize(new Dimension(iPANEL_WIDTH, iPANEL_HEIGHT));
		setBackground(Color.BLACK);
	}
	
	/**
	 * Resets the board and clears away any tiles.
	 */
	public void clear() {
		/*
		 * Loop through every tile index and set it's value
		 * to null to clear the board.
		 */
		for(int iRow = 0; iRow < iROW_COUNT; iRow++) {
			for(int iCol = 0; iCol < iCOL_COUNT; iCol++) {
				tilTile[iRow][iCol] = null;
			}
		}
	}
	
	/**
	 * Determines whether or not a piece can be placed at the coordinates.
	 * @param tileType THe type of piece to use.
	 * @param iX The x coordinate of the piece.
	 * @param iY The y coordinate of the piece.
	 * @param iRotation The rotation of the piece.
	 * @return Whether or not the position is valid.
	 */
	public boolean isValidAndEmpty(TileType tileType, int iX, int iY, int iRotation) {
				
		//Ensure the piece is in a valid column.
		if(iX < -tileType.getLeftInset(iRotation) || iX + tileType.getDimension() - tileType.getRightInset(iRotation) >= iCOL_COUNT) {
			return false;
		}
		
		//Ensure the piece is in a valid row.
		if(iY < -tileType.getTopInset(iRotation) || iY + tileType.getDimension() - tileType.getBottomInset(iRotation) >= iROW_COUNT) {
			return false;
		}
		
		/*
		 * Loop through every tile in the piece and see if it conflicts with an existing tile.
		 * 
		 * Note: It's fine to do this even though it allows for wrapping because we've already
		 * checked to make sure the piece is in a valid location.
		 */
		for(int iCol = 0; iCol < tileType.getDimension(); iCol++) {
			for(int iRow = 0; iRow < tileType.getDimension(); iRow++) {
				if(tileType.isTile(iCol, iRow, iRotation) && isOccupied(iX + iCol, iY + iRow)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Adds a piece to the game board. Note: Doesn't check for existing pieces,
	 * and will overwrite them if they exist.
	 * @param tilType The type of piece to place.
	 * @param iX The x coordinate of the piece.
	 * @param iY The y coordinate of the piece.
	 * @param iRotation The rotation of the piece.
	 */
	public void addPiece(TileType tilType, int iX, int iY, int iRotation) {
		/*
		 * Loop through every tile within the piece and add it
		 * to the board only if the boolean that represents that
		 * tile is set to true.
		 */
		for(int iCol = 0; iCol < tilType.getDimension(); iCol++) {
			for(int iRow = 0; iRow < tilType.getDimension(); iRow++) {
				if(tilType.isTile(iCol, iRow, iRotation)) {
					setTile(iCol + iX, iRow + iY, tilType);
				}
			}
		}
	}
	
	/**
	 * Checks the board to see if any lines have been cleared, and
	 * removes them from the game.
	 * @return The number of lines that were cleared.
	 */
	public int checkLines() {
		int iCompletedLines = 0;
		
		/*
		 * Here we loop through every line and check it to see if
		 * it's been cleared or not. If it has, we increment the
		 * number of completed lines and check the next row.
		 * 
		 * The checkLine function handles clearing the line and
		 * shifting the rest of the board down for us.
		 */
		for(int iRow = 0; iRow < iROW_COUNT; iRow++) {
			if(checkLine(iRow)) {
				iCompletedLines++;
			}
		}
		return iCompletedLines;
	}
			
	/**
	 * Checks whether or not {@code row} is full.
	 * @param iLine The row to check.
	 * @return Whether or not this row is full.
	 */
	private boolean checkLine(int iLine) {
		/*
		 * Iterate through every column in this row. If any of them are
		 * empty, then the row is not full.
		 */
		for(int iCol = 0; iCol < iCOL_COUNT; iCol++) {
			if(!isOccupied(iCol, iLine)) {
				return false;
			}
		}
		
		/*
		 * Since the line is filled, we need to 'remove' it from the game.
		 * To do this, we simply shift every row above it down by one.
		 */
		for(int iRow = iLine - 1; iRow >= 0; iRow--) {
			for(int iCol = 0; iCol < iCOL_COUNT; iCol++) {
				setTile(iCol, iRow + 1, getTile(iCol, iRow));
			}
		}
		return true;
	}
	
	
	/**
	 * Checks to see if the tile is already occupied.
	 * @param iX The x coordinate to check.
	 * @param iY The y coordinate to check.
	 * @return Whether or not the tile is occupied.
	 */
	private boolean isOccupied(int iX, int iY) {
		return tilTile[iY][iX] != null;
	}
	
	/**
	 * Sets a tile located at the desired column and row.
	 * @param iX The column.
	 * @param iY The row.
	 * @param tilType The value to set to the tile to.
	 */
	private void setTile(int iX, int iY, TileType tilType) {
		tilTile[iY][iX] = tilType;
	}

	/**
	 * Gets a tile by it's column and row.
	 * @param iX The column.
	 * @param iY The row.
	 * @return The tile.
	 */
	private TileType getTile(int iX, int iY) {
		return this.tilTile[iY][iX];
	}

	/**
	 * Gets the entire tile matrix tile.
	 * @return The tile matrix.
	 */
	public TileType[][] getTiles() {
		return this.tilTile;
	}

	/**
	 * Gets the current alpha amount
	 * @return The alpha amount
	 */
	public float getAlphaAmount() {
		return this.fAlphaAmount;
	}

	/**
	 * Gets the current alpha modifying factor
	 * @return The alpha factor
	 */
	public float getAlphaFactor() {
		return this.fAlphaFactor;
	}

	/**
	 * Sets the tile matrix
	 * @param tilMat The tile matrix.
	 */
	public void setTiles(TileType[][] tilMat) {
		this.tilTile = tilMat;
	}

	/**
	 * Sets the current alpha amount
	 * @param fAlphaAmount The alpha amount
	 */
	public void setAlphaAmount(float fAlphaAmount) {
		this.fAlphaAmount = fAlphaAmount;
	}

	/**
	 * Sets the current alpha factor
	 * @param fAlphaFactor The alpha factor
	 */
	public void setAlphaFactor(float fAlphaFactor) {
		this.fAlphaFactor = fAlphaFactor;
	}

	/**
	 * Increases the brightness of a given color by a given factor
	 * @param iBrightFactor The brightness factor.
	 * @param colColor The original color to increase brightness on.
	 * @return     a new <code>Color</code> object that is
	 *             a brighter version of the received <code>Color</code>
	 *             by a factor of brightFactor.
	 */
	private Color brighter(Color colColor, int iBrightFactor) {
		for (int i = 0; i < iBrightFactor; i++){
			colColor = colColor.brighter();
		}
		return colColor;
	}
        
        public void whichImage(int imaImage){
            this.iImage = imaImage;
        }
        
        private void setBackground(int imaImage, Graphics graGraphics){
            URL urlImagen;
            this.iImage = imaImage;
            if (imaImage == 1){
                urlImagen = this.getClass().getResource("background.jpg");
            }
			else{
                urlImagen = this.getClass().getResource("black.png");
            }
            imgBackground = Toolkit.getDefaultToolkit().getImage(urlImagen);
            graGraphics.drawImage(imgBackground,0,0,this);
        }
	
        
	@Override
	public void paintComponent(Graphics graGraphics) {
		super.paintComponent(graGraphics);
                setBackground(iImage,graGraphics);
                graGraphics.drawImage(imgBackground, 0, 0, this);
		//This helps simplify the positioning of things.
		graGraphics.translate(iBORDER_WIDTH, iBORDER_WIDTH);
		
		/*
		 * Draw the board differently depending on the current game state.
		 */
		if(tetTetris.isPaused()) {
			// The game is paused
			drawPaused(graGraphics);
		}
		else if(tetTetris.isNewGame() || tetTetris.isGameOver()) {
			// The game is either at the beginning or the end
			drawStartEndGame(graGraphics);
		}
		else {
			/*
			 * Draw the tiles onto the board.
			 */
			drawStaticTiles(graGraphics);
			
			/*
			 * Draw the current piece. This cannot be drawn like the rest of the
			 * pieces because it's still not part of the game board. If it were
			 * part of the board, it would need to be removed every frame which
			 * would just be slow and confusing.
			 */
			TileType tilType = tetTetris.getPieceType();
			int iPieceCol = tetTetris.getPieceCol();
			int iPieceRow = tetTetris.getPieceRow();
			int iRotation = tetTetris.getPieceRotation();

			/*
			 * When the alpha has reached the maximum value, start decreasing
			 * it and viceversa.
			 */
			if(fAlphaAmount >= 0.8f || fAlphaAmount <= 0.1f){
				fAlphaFactor *= -1;
			}

			/*
		 	 * Draw the falling piece onto the board
		 	 */
			drawFallingPiece(graGraphics, tilType, iPieceCol, iPieceRow, iRotation);

			/*
		 	 * Draw the ghost (semi-transparent piece that shows where the
		 	 * current piece will land).
		 	 */
			drawGhostPiece(graGraphics, tilType, iPieceCol, iPieceRow, iRotation);

			/*
		 	 * Draw the background tile grid
		 	 */
			drawBackgroundGrid(graGraphics);
		}
		
		/*
		 * Draw the outline.
		 */
		graGraphics.setColor(Color.WHITE);
		graGraphics.drawRect(0, 0, iTILE_SIZE * iCOL_COUNT, iTILE_SIZE * iVISIBLE_ROW_COUNT);
	}

	/**
	 * Draws the static tiles at the bottom of the game
	 * @param graGraphics The graphics object.
	 */
	private void drawStaticTiles(Graphics graGraphics) {
		/*
		 * Draw the tiles onto the board.
	   	 */
		for(int iCol = 0; iCol < iCOL_COUNT; iCol++) {
            for(int iRow = iHIDDEN_ROW_COUNT; iRow < iROW_COUNT; iRow++) {
                TileType tileType = getTile(iCol, iRow);
                if(tileType != null) {
                    drawTile(tileType,
                             iCol * iTILE_SIZE,
                             (iRow - iHIDDEN_ROW_COUNT) * iTILE_SIZE,
                             graGraphics);
                }
            }
        }
	}

	/**
	 * Draws the current falling piece
	 * @param graGraphics The graphics object.
	 * @param tilType The type of tile to draw.
	 * @param iPieceCol The current column of the piece to draw
	 * @param iPieceRow The current row of the piece to draw
	 * @param iRotation The current rotation of the piece to draw
	 */
	private void drawFallingPiece(Graphics graGraphics, TileType tilType, int iPieceCol, int iPieceRow, int iRotation) {
		//Draw the piece onto the board.
		for(int col = 0; col < tilType.getDimension(); col++) {
            for(int row = 0; row < tilType.getDimension(); row++) {
                if(iPieceRow + row >= 2 && tilType.isTile(col, row, iRotation)) {
                    int iX = (iPieceCol + col) * iTILE_SIZE;
                    int iY = (iPieceRow + row - iHIDDEN_ROW_COUNT) * iTILE_SIZE;
                    // Draw base block
                    drawTile(tilType, iX, iY, graGraphics);

                    // Draw glow
                    drawTileAlpha(tilType.getLightColor().brighter().brighter(),
                                  tilType.getBaseColor(),
                                  iX,
                                  iY,
                                  graGraphics,
                                  fAlphaAmount);

                    // Increase the amount of alpha to be drawn
                    fAlphaAmount += fAlphaFactor;
                }
            }
        }
	}

	/**
	 * Draw the background grid above the pieces (serves as a useful visual
	 * for players, and makes the pieces look nicer by breaking them up.
	 * @param graGraphics The graphics object.
	 */
	private void drawBackgroundGrid(Graphics graGraphics) {
		graGraphics.setColor(Color.DARK_GRAY);
		for(int iCol = 0; iCol < iCOL_COUNT; iCol++) {
            for(int iRow = 0; iRow < iVISIBLE_ROW_COUNT; iRow++) {
                graGraphics.drawLine(0, iRow * iTILE_SIZE, iCOL_COUNT * iTILE_SIZE, iRow * iTILE_SIZE);
                graGraphics.drawLine(iCol * iTILE_SIZE, 0, iCol * iTILE_SIZE, iVISIBLE_ROW_COUNT * iTILE_SIZE);
            }
        }
	}

	/**
	 * Draw the ghost (semi-transparent piece that shows where the current piece will land). I couldn't think of
	 * a better way to implement this so it'll have to do for now. We simply take the current position and move
	 * down until we hit a row that would cause a collision.
	 * @param graGraphics The graphics object.
	 * @param tilType The type of tile to draw.
	 * @param iPieceCol The current column of the piece to draw
	 * @param iPieceRow The current row of the piece to draw
	 * @param iRotation The current rotation of the piece to draw
	 */
	private void drawGhostPiece(Graphics graGraphics, TileType tilType, int iPieceCol, int iPieceRow, int iRotation) {
		Color colBase = tilType.getBaseColor();
		// Create a semi-transparent color
		colBase = new Color(colBase.getRed(), colBase.getGreen(), colBase.getBlue(), 20);

		for(int iLowest = iPieceRow; iLowest < iROW_COUNT; iLowest++) {
            // If no collision is detected, try the next row.
            if(isValidAndEmpty(tilType, iPieceCol, iLowest, iRotation)) {
                continue;
            }

            // Draw the ghost one row higher than the one the collision took
			// place at.
            iLowest--;

            // Draw the ghost piece.
            for(int col = 0; col < tilType.getDimension(); col++) {
                for(int row = 0; row < tilType.getDimension(); row++) {
                    if(iLowest + row >= 2 && tilType.isTile(col, row, iRotation)) {
                        drawTile(colBase, colBase.brighter(), colBase.darker(), (iPieceCol + col) * iTILE_SIZE, (iLowest + row - iHIDDEN_ROW_COUNT) * iTILE_SIZE, graGraphics);
                    }
                }
            }
            break;
        }
	}

	/**
	 * Draw the text in the panel before a game starts and after a game is over.
	 * @param graGraphics The graphics object.
	 */
	private void drawStartEndGame(Graphics graGraphics) {
		graGraphics.setFont(fonLARGE_FONT);
		graGraphics.setColor(Color.WHITE);

		/*
	     * Because both the game over and new game screens are nearly identical,
		 * we can handle them together and just use a ternary operator to change
		 * the messages that are displayed.
		 */
		String msg = tetTetris.isNewGame() ? "TETRIS" : "GAME OVER";
		if(Objects.equals(msg, "GAME OVER")){
            graGraphics.setFont(fonMEDIUM_FONT);
        }

		/*
		 * Draw a light gray shadow before the main text
		 */
		graGraphics.setColor(Color.LIGHT_GRAY);
		graGraphics.drawString(msg,
                     iCENTER_X - graGraphics.getFontMetrics().stringWidth(msg) / 2,
                     150 + 2);
		/*
	     * Draw the main text
		 */
		graGraphics.setColor(Color.WHITE);
		graGraphics.drawString(msg, iCENTER_X - graGraphics.getFontMetrics().stringWidth(msg) / 2, 150);
		graGraphics.setFont(fonSMALL_FONT);
		msg = "Press Enter to Play" + (tetTetris.isNewGame() ? "" : " Again");
		graGraphics.drawString(msg, iCENTER_X - graGraphics.getFontMetrics().stringWidth(msg) / 2, 300);
	}

	/**
	 * Draw the text in the panel when the game is paused
	 * @param graGraphics The graphics object.
	 */
	private void drawPaused(Graphics graGraphics) {
		graGraphics.setFont(fonMEDIUM_FONT);
		String msg = "PAUSED";
		/*
		 * Draw a light gray shadow before the main text
		 */
		graGraphics.setColor(Color.LIGHT_GRAY);
		graGraphics.drawString(msg,
                     iCENTER_X - graGraphics.getFontMetrics().stringWidth(msg) / 2,
                     iCENTER_Y + 2);

		/*
		 * Draw the main text
		 */
		graGraphics.setColor(Color.WHITE);
		graGraphics.drawString(msg, iCENTER_X - graGraphics.getFontMetrics().stringWidth(msg) / 2,
                     iCENTER_Y);
	}

	/**
	 * Draws a tile onto the board.
	 * @param tilType The type of tile to draw.
	 * @param iX The column.
	 * @param iY The row.
	 * @param graGraphics The graphics object.
	 */
	private void drawTile(TileType tilType, int iX, int iY, Graphics graGraphics) {
		drawTile(tilType.getBaseColor(),
				 tilType.getLightColor(),
				 tilType.getDarkColor(),
				 iX,
				 iY,
				 graGraphics);
	}
	
	/**
	 * Draws a tile onto the board.
	 * @param colBase The base color of tile.
	 * @param colLight The light color of the tile.
	 * @param colDark The dark color of the tile.
	 * @param iX The column.
	 * @param iY The row.
	 * @param graGraphics The graphics object.
	 */
	private void drawTile(Color colBase, Color colLight, Color colDark, int iX, int iY,
						  Graphics graGraphics) {
		/*
		 * Fill the entire tile with the base color.
		 */
		graGraphics.setColor(colBase);
		graGraphics.fillRect(iX, iY, iTILE_SIZE, iTILE_SIZE);
		
		/*
		 * Fill the bottom and right edges of the tile with the dark shading color.
		 */
		graGraphics.setColor(colDark);
		graGraphics.fillRect(iX, iY + iTILE_SIZE - iSHADE_WIDTH, iTILE_SIZE,
				   iSHADE_WIDTH);
		graGraphics.fillRect(iX + iTILE_SIZE - iSHADE_WIDTH, iY, iSHADE_WIDTH,
				   iTILE_SIZE);
		
		/*
		 * Fill the top and left edges with the light shading. We draw a
		 * single line
		 * for each row or column rather than a rectangle so that we can draw a nice
		 * looking diagonal where the light and dark shading meet.
		 */
		graGraphics.setColor(colLight);
		for(int i = 0; i < iSHADE_WIDTH; i++) {
			graGraphics.drawLine(iX, iY + i, iX + iTILE_SIZE - i - 1, iY + i);
			graGraphics.drawLine(iX + i, iY, iX + i, iY + iTILE_SIZE - i - 1);
		}
	}

	/**
	 * Draws a tile onto the board.
	 * @param colBase The base color of tile.
	 * @param colDark The dark color of the tile.
	 * @param iX The column.
	 * @param iY The row.
	 * @param graGraphics The graphics object.
	 */
	private void drawTileAlpha(Color colBase, Color colDark, int iX,
							   int iY, Graphics graGraphics, float fAlphaValue) {
		/*
		 * Create a new Graphics2D instance to allow alpha to be drawn into
		 * the object. Then save the current composite to restore normal
		 * non-alpha painting.
		 */
		Graphics2D g2d = (Graphics2D) graGraphics;
		Composite cCurrentComposite = g2d.getComposite();

		/*
		 * Create a radial gradient with the light and dark colors to give the
		 * tile a more dynamic look. The increasing iGradientModifier gives the
		 * effect of the tile shining
		 */
		Point2D center = new Point2D.Float(iX/2 + iGradientModifier, iY/2);
		iGradientModifier+= .1;
		float radius = 10;
		float[] dist = {0.05f, .95f};
		Color[] colors = {colBase.brighter(), colDark};
		RadialGradientPaint paint =
				new RadialGradientPaint(center,
										radius,
										dist,
										colors,
										MultipleGradientPaint.CycleMethod.REFLECT);

		/*
		 * Fill the entire tile with the light and dark colors gradient.
		 */
		g2d.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, fAlphaValue));
		g2d.setPaint(paint);
		g2d.fillRect(iX - iGLOW_OFFSET,
					 iY - iGLOW_OFFSET,
					 iTILE_SIZE + iGLOW_OFFSET * 2,
					 iTILE_SIZE + iGLOW_OFFSET * 2);

		g2d.setComposite(cCurrentComposite);
	}

}
