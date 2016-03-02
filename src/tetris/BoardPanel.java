package tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.net.URL;

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
	public static final int ITILE_SIZE = 24;

	/**
	 * The number of extra pixels that the glow of a tile takes up.
	 */
	public static final int IGLOW_OFFSET = 1;
	
	/**
	 * The width of the shading on the tiles.
	 */
	public static final int ISHADE_WIDTH = 4;
	
	/**
	 * The central x coordinate on the game board.
	 */
	private static final int ICENTER_X = iCOL_COUNT * ITILE_SIZE / 2;
	
	/**
	 * The central y coordinate on the game board.
	 */
	private static final int ICENTER_Y = iVISIBLE_ROW_COUNT * ITILE_SIZE / 2;
		
	/**
	 * The total width of the panel.
	 */
	public static final int IPANEL_WIDTH = iCOL_COUNT * ITILE_SIZE + iBORDER_WIDTH * 2;
	
	/**
	 * The total height of the panel.
	 */
	public static final int PANEL_HEIGHT = iVISIBLE_ROW_COUNT * ITILE_SIZE + iBORDER_WIDTH * 2;
	
	/**
	 * The larger font to display.
	 */
	private static final Font LARGE_FONT = new Font("Tahoma", Font.BOLD, 16);

	/**
	 * The smaller font to display.
	 */
	private static final Font SMALL_FONT = new Font("Tahoma", Font.BOLD, 12);
	
	/**
	 * The Tetris instance.
	 */
	private Tetris tetTris;
	
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
	 * @param tetTris The Tetris instance to use.
	 */
	public BoardPanel(Tetris tetTris) {
		this.tetTris = tetTris;
		this.tilTile = new TileType[iROW_COUNT][iCOL_COUNT];
		this.fAlphaAmount = 0.1f;
		this.fAlphaFactor = 0.01f;
		this.iGradientModifier = 0;
                iImage = 1;
		setPreferredSize(new Dimension(IPANEL_WIDTH, PANEL_HEIGHT));
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
		for(int i = 0; i < iROW_COUNT; i++) {
			for(int j = 0; j < iCOL_COUNT; j++) {
				tilTile[i][j] = null;
			}
		}
	}
	
	/**
	 * Determines whether or not a piece can be placed at the coordinates.
	 * @param type THe type of piece to use.
	 * @param x The x coordinate of the piece.
	 * @param y The y coordinate of the piece.
	 * @param rotation The rotation of the piece.
	 * @return Whether or not the position is valid.
	 */
	public boolean isValidAndEmpty(TileType type, int x, int y, int rotation) {
				
		//Ensure the piece is in a valid column.
		if(x < -type.getLeftInset(rotation) || x + type.getDimension() - type.getRightInset(rotation) >= iCOL_COUNT) {
			return false;
		}
		
		//Ensure the piece is in a valid row.
		if(y < -type.getTopInset(rotation) || y + type.getDimension() - type.getBottomInset(rotation) >= iROW_COUNT) {
			return false;
		}
		
		/*
		 * Loop through every tile in the piece and see if it conflicts with an existing tile.
		 * 
		 * Note: It's fine to do this even though it allows for wrapping because we've already
		 * checked to make sure the piece is in a valid location.
		 */
		for(int col = 0; col < type.getDimension(); col++) {
			for(int row = 0; row < type.getDimension(); row++) {
				if(type.isTile(col, row, rotation) && isOccupied(x + col, y + row)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Adds a piece to the game board. Note: Doesn't check for existing pieces,
	 * and will overwrite them if they exist.
	 * @param type The type of piece to place.
	 * @param x The x coordinate of the piece.
	 * @param y The y coordinate of the piece.
	 * @param rotation The rotation of the piece.
	 */
	public void addPiece(TileType type, int x, int y, int rotation) {
		/*
		 * Loop through every tile within the piece and add it
		 * to the board only if the boolean that represents that
		 * tile is set to true.
		 */
		for(int col = 0; col < type.getDimension(); col++) {
			for(int row = 0; row < type.getDimension(); row++) {
				if(type.isTile(col, row, rotation)) {
					setTile(col + x, row + y, type);
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
		int completedLines = 0;
		
		/*
		 * Here we loop through every line and check it to see if
		 * it's been cleared or not. If it has, we increment the
		 * number of completed lines and check the next row.
		 * 
		 * The checkLine function handles clearing the line and
		 * shifting the rest of the board down for us.
		 */
		for(int row = 0; row < iROW_COUNT; row++) {
			if(checkLine(row)) {
				completedLines++;
			}
		}
		return completedLines;
	}
			
	/**
	 * Checks whether or not {@code row} is full.
	 * @param line The row to check.
	 * @return Whether or not this row is full.
	 */
	private boolean checkLine(int line) {
		/*
		 * Iterate through every column in this row. If any of them are
		 * empty, then the row is not full.
		 */
		for(int col = 0; col < iCOL_COUNT; col++) {
			if(!isOccupied(col, line)) {
				return false;
			}
		}
		
		/*
		 * Since the line is filled, we need to 'remove' it from the game.
		 * To do this, we simply shift every row above it down by one.
		 */
		for(int row = line - 1; row >= 0; row--) {
			for(int col = 0; col < iCOL_COUNT; col++) {
				setTile(col, row + 1, getTile(col, row));
			}
		}
		return true;
	}
	
	
	/**
	 * Checks to see if the tile is already occupied.
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 * @return Whether or not the tile is occupied.
	 */
	private boolean isOccupied(int x, int y) {
		return tilTile[y][x] != null;
	}
	
	/**
	 * Sets a tile located at the desired column and row.
	 * @param x The column.
	 * @param y The row.
	 * @param type The value to set to the tile to.
	 */
	private void setTile(int  x, int y, TileType type) {
		tilTile[y][x] = type;
	}

	/**
	 * Gets a tile by it's column and row.
	 * @param x The column.
	 * @param y The row.
	 * @return The tile.
	 */
	private TileType getTile(int x, int y) {
		return tilTile[y][x];
	}

	/**
	 * Gets the entire tile matrix tile.
	 * @return The tile matrix.
	 */
	public TileType[][] getTiles() {
		return tilTile;
	}

	/**
	 * Sets the tile matrix
	 * @param tilMat The tile matrix.
	 */
	public void setTiles(TileType[][] tilMat) {
		tilTile = tilMat;
	}

	/**
	 * Increases the brightness of a given color by a given factor
	 * @param brightFactor The brightness factor.
	 * @param color The original color to increase brightness on.
	 * @return     a new <code>Color</code> object that is
	 *             a brighter version of the received <code>Color</code>
	 *             by a factor of brightFactor.
	 */
	private Color brighter(Color color, int brightFactor) {
		for (int i = 0; i < brightFactor; i++){
			color = color.brighter();
		}
		return color;
	}
        
        public void whichImage(int iImage){
            this.iImage = iImage;
        }
        
        private void setBackground(int iImage, Graphics g){
            URL urlImagen;
            this.iImage = iImage;
            if (iImage == 1){
                urlImagen = this.getClass().getResource("background.jpg");
            }else{
                urlImagen = this.getClass().getResource("black.png");
            }
            imgBackground = Toolkit.getDefaultToolkit().getImage(urlImagen);
            g.drawImage(imgBackground,0,0,this);
        }
	
        
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
                setBackground(iImage,g);
                g.drawImage(imgBackground,0,0,this);
		//This helps simplify the positioning of things.
		g.translate(iBORDER_WIDTH, iBORDER_WIDTH);
		
		/*
		 * Draw the board differently depending on the current game state.
		 */
		if(tetTris.isPaused()) {
			g.setFont(LARGE_FONT);
			g.setColor(Color.WHITE);
			String msg = "PAUSED";
			g.drawString(msg, ICENTER_X - g.getFontMetrics().stringWidth(msg) / 2, ICENTER_Y);
		} else if(tetTris.isNewGame() || tetTris.isGameOver()) {
			g.setFont(LARGE_FONT);
			g.setColor(Color.WHITE);
			
			/*
			 * Because both the game over and new game screens are nearly identical,
			 * we can handle them together and just use a ternary operator to change
			 * the messages that are displayed.
			 */
			String msg = tetTris.isNewGame() ? "TETRIS" : "GAME OVER";
			g.drawString(msg, ICENTER_X - g.getFontMetrics().stringWidth(msg) / 2, 150);
			g.setFont(SMALL_FONT);
			msg = "Press Enter to Play" + (tetTris.isNewGame() ? "" : " Again");
			g.drawString(msg, ICENTER_X - g.getFontMetrics().stringWidth(msg) / 2, 300);
		} else {
			
			/*
			 * Draw the tiles onto the board.
			 */
			for(int x = 0; x < iCOL_COUNT; x++) {
				for(int y = iHIDDEN_ROW_COUNT; y < iROW_COUNT; y++) {
					TileType tile = getTile(x, y);
					if(tile != null) {
						drawTile(tile, x * ITILE_SIZE, (y - iHIDDEN_ROW_COUNT) * ITILE_SIZE, g);
					}
				}
			}
			
			/*
			 * Draw the current piece. This cannot be drawn like the rest of the
			 * pieces because it's still not part of the game board. If it were
			 * part of the board, it would need to be removed every frame which
			 * would just be slow and confusing.
			 */
			TileType type = tetTris.getPieceType();
			int pieceCol = tetTris.getPieceCol();
			int pieceRow = tetTris.getPieceRow();
			int rotation = tetTris.getPieceRotation();
			/*
			 * When the alpha has reched the maximum value, start decreasing
			 * it and viceversa.
			 */
			if(fAlphaAmount >= 0.8f || fAlphaAmount <= 0.09f){
				fAlphaFactor *= -1;
			}

			//Draw the piece onto the board.
			for(int col = 0; col < type.getDimension(); col++) {
				for(int row = 0; row < type.getDimension(); row++) {
					if(pieceRow + row >= 2 && type.isTile(col, row, rotation)) {
						int iX = (pieceCol + col) * ITILE_SIZE;
						int iY = (pieceRow + row - iHIDDEN_ROW_COUNT) * ITILE_SIZE;
						// Draw base block
						drawTile(type, iX, iY, g);

						// Draw glow
						drawTileAlpha(type.getLightColor().brighter(),
									  type.getBaseColor(),
									  iX,
									  iY,
									  g,
									  fAlphaAmount);

						// Increase the amount of alpha to be drawn
						fAlphaAmount += fAlphaFactor;
					}
				}
			}
			
			/*
			 * Draw the ghost (semi-transparent piece that shows where the current piece will land). I couldn't think of
			 * a better way to implement this so it'll have to do for now. We simply take the current position and move
			 * down until we hit a row that would cause a collision.
			 */
			Color base = type.getBaseColor();
			base = new Color(base.getRed(), base.getGreen(), base.getBlue(), 20);
			for(int lowest = pieceRow; lowest < iROW_COUNT; lowest++) {
				//If no collision is detected, try the next row.
				if(isValidAndEmpty(type, pieceCol, lowest, rotation)) {					
					continue;
				}
				
				//Draw the ghost one row higher than the one the collision took place at.
				lowest--;
				
				//Draw the ghost piece.
				for(int col = 0; col < type.getDimension(); col++) {
					for(int row = 0; row < type.getDimension(); row++) {
						if(lowest + row >= 2 && type.isTile(col, row, rotation)) {
							drawTile(base, base.brighter(), base.darker(), (pieceCol + col) * ITILE_SIZE, (lowest + row - iHIDDEN_ROW_COUNT) * ITILE_SIZE, g);
						}
					}
				}
				
				break;
			}
			
			/*
			 * Draw the background grid above the pieces (serves as a useful visual
			 * for players, and makes the pieces look nicer by breaking them up.
			 */
			g.setColor(Color.DARK_GRAY);
			for(int x = 0; x < iCOL_COUNT; x++) {
				for(int y = 0; y < iVISIBLE_ROW_COUNT; y++) {
					g.drawLine(0, y * ITILE_SIZE, iCOL_COUNT * ITILE_SIZE, y * ITILE_SIZE);
					g.drawLine(x * ITILE_SIZE, 0, x * ITILE_SIZE, iVISIBLE_ROW_COUNT * ITILE_SIZE);
				}
			}
		}
		
		/*
		 * Draw the outline.
		 */
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, ITILE_SIZE * iCOL_COUNT, ITILE_SIZE * iVISIBLE_ROW_COUNT);
	}
	
	/**
	 * Draws a tile onto the board.
	 * @param type The type of tile to draw.
	 * @param x The column.
	 * @param y The row.
	 * @param g The graphics object.
	 */
	private void drawTile(TileType type, int x, int y, Graphics g) {
		drawTile(type.getBaseColor(), type.getLightColor(), type.getDarkColor(), x, y, g);
	}
	
	/**
	 * Draws a tile onto the board.
	 * @param base The base color of tile.
	 * @param light The light color of the tile.
	 * @param dark The dark color of the tile.
	 * @param x The column.
	 * @param y The row.
	 * @param g The graphics object.
	 */
	private void drawTile(Color base, Color light, Color dark, int x, int y, Graphics g) {
		/*
		 * Fill the entire tile with the base color.
		 */
		g.setColor(base);
		g.fillRect(x, y, ITILE_SIZE, ITILE_SIZE);
		
		/*
		 * Fill the bottom and right edges of the tile with the dark shading color.
		 */
		g.setColor(dark);
		g.fillRect(x, y + ITILE_SIZE - ISHADE_WIDTH, ITILE_SIZE,
					 ISHADE_WIDTH);
		g.fillRect(x + ITILE_SIZE - ISHADE_WIDTH, y, ISHADE_WIDTH,
					 ITILE_SIZE);
		
		/*
		 * Fill the top and left edges with the light shading. We draw a
		 * single line
		 * for each row or column rather than a rectangle so that we can draw a nice
		 * looking diagonal where the light and dark shading meet.
		 */
		g.setColor(light);
		for(int i = 0; i < ISHADE_WIDTH; i++) {
			g.drawLine(x, y + i, x + ITILE_SIZE - i - 1, y + i);
			g.drawLine(x + i, y, x + i, y + ITILE_SIZE - i - 1);
		}
	}

	/**
	 * Draws a tile onto the board.
	 * @param base The base color of tile.
	 * @param dark The dark color of the tile.
	 * @param x The column.
	 * @param y The row.
	 * @param g The graphics object.
	 */
	private void drawTileAlpha(Color base, Color dark, int x,
							   int y, Graphics g, float fAlphaValue) {
		/*
		 * Create a new Graphics2D instance to allow alpha to be drawn into
		 * the object. Then save the current composite to restore normal
		 * non-alpha painting.
		 */
		Graphics2D g2d = (Graphics2D) g;
		Composite cCurrentComposite = g2d.getComposite();
		/*
		 * Create a radial gradient with the light and dark colors to give the
		 * tile a more dynamic look. The increasing iGradientModifier gives the
		  * effect of the tile shining
		 */
		Point2D center = new Point2D.Float(x/2 + iGradientModifier, y/2);
		iGradientModifier+= .1;
		float radius = 10;
		float[] dist = {0.05f, .95f};
		Color[] colors = {base.brighter(), dark};
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
		g2d.fillRect(x - IGLOW_OFFSET,
					 y - IGLOW_OFFSET,
					 ITILE_SIZE + IGLOW_OFFSET * 2,
					 ITILE_SIZE + IGLOW_OFFSET * 2);

		g2d.setComposite(cCurrentComposite);
	}

}
