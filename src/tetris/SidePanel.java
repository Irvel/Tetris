package tetris;

import javax.swing.*;
import java.awt.*;

/**
 * The {@code SidePanel} class is responsible for displaying various information
 * on the game such as the next piece, the score and current level, and controls.
 * @author Brendan Jones
 *
 */
public class SidePanel extends JPanel {
	
	/**
	 * Serial Version UID.
	 */
	private static final long lSERIALVERSIONUID = 2181495598854992747L;

	/**
	 * The dimensions of each tile on the next piece preview.
	 */
	private static final int iTILE_SIZE = BoardPanel.iTILE_SIZE >> 1;
	
	/**
	 * The width of the shading on each tile on the next piece preview.
	 */
	private static final int iSHADE_WIDTH = BoardPanel.iSHADE_WIDTH >> 1;
	
	/**
	 * The number of rows and columns in the preview window. Set to
	 * 5 because we can show any piece with some sort of padding.
	 */
	private static final int iTILE_COUNT = 5;
	
	/**
	 * The center x of the next piece preview box.
	 */
	private static final int iSQUARE_CENTER_X = 130;
	
	/**
	 * The center y of the next piece preview box.
	 */
	private static final int iSQUARE_CENTER_Y = 65;
	
	/**
	 * The size of the next piece preview box.
	 */
	private static final int iSQUARE_SIZE = (iTILE_SIZE * iTILE_COUNT >> 1);
	
	/**
	 * The number of pixels used on a small insets (generally used for categories).
	 */
	private static final int iSMALL_INSET = 20;
	
	/**
	 * The number of pixels used on a large insets.
	 */
	private static final int iLARGE_INSET = 40;
	
	/**
	 * The y coordinate of the stats category.
	 */
	private static final int iSTATS_INSET = 140;
	
	/**
	 * The y coordinate of the controls category.
	 */
	private static final int iCONTROLS_INSET = 240;
	
	/**
	 * The number of pixels to offset between each string.
	 */
	private static final int iTEXT_STRIDE = 25;
	
	/**
	 * The small font.
	 */
	private static final Font fonSMALL_FONT = new Font("Dialog", Font
			.BOLD, 11);
	
	/**
	 * The large font.
	 */
	private static final Font fonLARGE_FONT = new Font("Dialog", Font.BOLD,
													   13);
	
	/**
	 * The color to draw the text and preview box in.
	 */
	private static final Color colDRAW_COLOR = new Color(173, 255, 255);
	
	/**
	 * The Tetris instance.
	 */
	private Tetris tetTetris;

	
	/**
	 * Creates a new SidePanel and sets it's display properties.
	 * @param tetTetris The Tetris instance to use.
	 */
	public SidePanel(Tetris tetTetris) {
		this.tetTetris = tetTetris;
		
		setPreferredSize(new Dimension(200, BoardPanel.iPANEL_HEIGHT));
		setBackground(Color.BLACK);
	}

	@Override
	public void paintComponent(Graphics graGraphics) {
		super.paintComponent(graGraphics);
		//Set the color for drawing.
		graGraphics.setColor(colDRAW_COLOR);
		/*
     	 * Draw the information of the side panel
     	 */
		drawPreviewPiece(graGraphics);
		drawStats(graGraphics);
		drawControls(graGraphics);
	}

	/**
	 * Draws the next piece preview at the top of the side panel
	 * @param graGraphics The Graphics object to be drawn to.
	 */
	private void drawPreviewPiece(Graphics graGraphics) {
    	/*
     	 * Draw the next piece preview box.
     	 */
		graGraphics.setFont(fonLARGE_FONT);
		graGraphics.drawString("Next Piece:", iSMALL_INSET, 70);
		graGraphics.drawRect(iSQUARE_CENTER_X - iSQUARE_SIZE, iSQUARE_CENTER_Y - iSQUARE_SIZE, iSQUARE_SIZE * 2, iSQUARE_SIZE * 2);

		/*
		 * Draw a preview of the next piece that will be spawned. The code is pretty much
		 * identical to the drawing code on the board, just smaller and centered, rather
		 * than constrained to a grid.
		 */
		TileType type = tetTetris.getNextPieceType();
		if(!tetTetris.isGameOver() && type != null) {
			/*
			 * Get the size properties of the current piece.
			 */
			int iCols = type.getCols();
			int iRows = type.getRows();
			int iDimension = type.getDimension();

			/*
			 * Calculate the top left corner (origin) of the piece.
			 */
			int iStartX = (iSQUARE_CENTER_X - (iCols * iTILE_SIZE / 2));
			int iStartY = (iSQUARE_CENTER_Y - (iRows * iTILE_SIZE / 2));

			/*
			 * Get the insets for the preview. The default
			 * rotation is used for the preview, so we just use 0.
			 */
			int iTop = type.getTopInset(0);
			int iLeft = type.getLeftInset(0);

			/*
			 * Loop through the piece and draw it's tiles onto the preview.
			 */
			for(int iRow = 0; iRow < iDimension; iRow++) {
				for(int iCol = 0; iCol < iDimension; iCol++) {
					if(type.isTile(iCol, iRow, 0)) {
						drawTile(type,
								 iStartX + ((iCol - iLeft) * iTILE_SIZE),
								 iStartY + ((iRow - iTop) * iTILE_SIZE),
								 graGraphics);
					}
				}
			}
		}
	}

	/**
	 * Draws the current controls on the side panel
	 * @param graGraphics The Graphics object to draw the string to.
	 *
	 */
	private void drawControls(Graphics graGraphics) {
		/*
		 * This variable stores the current y coordinate of the string.
		 * This way we can re-order, add, or remove new strings if necessary
		 * without needing to change the other strings.
		 */
		int iOffset;
		/*
		 * Draw the "Controls" category.
		 */
		graGraphics.setFont(fonLARGE_FONT);
		graGraphics.drawString("Controls",
							   iSMALL_INSET, iOffset = iCONTROLS_INSET);
		graGraphics.setFont(fonSMALL_FONT);
		graGraphics.drawString("A - Move Left",
							   iLARGE_INSET, iOffset += iTEXT_STRIDE);
		graGraphics.drawString("D - Move Right",
							   iLARGE_INSET, iOffset += iTEXT_STRIDE);
		graGraphics.drawString("Q - Rotate Anticlockwise",
							   iLARGE_INSET, iOffset += iTEXT_STRIDE);
		graGraphics.drawString("E - Rotate Clockwise",
							   iLARGE_INSET, iOffset += iTEXT_STRIDE);
		graGraphics.drawString("S - Drop",
							   iLARGE_INSET, iOffset += iTEXT_STRIDE);
		graGraphics.drawString("P - Pause Game",
							   iLARGE_INSET, iOffset += iTEXT_STRIDE);
		graGraphics.drawString("G - Save Game",
							   iLARGE_INSET, iOffset += iTEXT_STRIDE);
		graGraphics.drawString("C - Load Game",
							   iLARGE_INSET, iOffset += iTEXT_STRIDE);
		graGraphics.drawString("T - Stop/Start Track", iLARGE_INSET, iOffset +=
				iTEXT_STRIDE);
	}

	/**
	 * Draws the current stats on the side panel
	 * @param graGraphics The Graphics object to draw the string to.
	 */
	private void drawStats(Graphics graGraphics) {
		/*
		 * This variable stores the current y coordinate of the string.
		 * This way we can re-order, add, or remove new strings if necessary
		 * without needing to change the other strings.
		 */
		int iOffset;
		/*
		 * Draw the "Stats" category.
		 */
		graGraphics.setFont(fonLARGE_FONT);
		graGraphics.drawString("Stats", iSMALL_INSET, iOffset = iSTATS_INSET);
		graGraphics.setFont(fonSMALL_FONT);
		graGraphics.drawString("Level: " + tetTetris.getLevel(),
							   iLARGE_INSET, iOffset += iTEXT_STRIDE);
		graGraphics.drawString("Score: " + tetTetris.getScore(),
							   iLARGE_INSET, iOffset += iTEXT_STRIDE);
	}

	/**
	 * Draws a tile onto the preview window.
	 * @param tilType The type of tile to draw.
	 * @param iX The X coordinate of the tile.
	 * @param iY The Y coordinate of the tile.
	 * @param graGraphics The graphics object.
	 */
	private void drawTile(TileType tilType, int iX, int iY, Graphics graGraphics) {
		/*
		 * Fill the entire tile with the base color.
		 */
		graGraphics.setColor(tilType.getBaseColor());
		graGraphics.fillRect(iX, iY, iTILE_SIZE, iTILE_SIZE);
		
		/*
		 * Fill the bottom and right edges of the tile with the dark shading color.
		 */
		graGraphics.setColor(tilType.getDarkColor());
		graGraphics.fillRect(iX, iY + iTILE_SIZE - iSHADE_WIDTH,
							 iTILE_SIZE, iSHADE_WIDTH);
		graGraphics.fillRect(iX + iTILE_SIZE - iSHADE_WIDTH, iY, iSHADE_WIDTH,
							 iTILE_SIZE);
		
		/*
		 * Fill the top and left edges with the light shading. We draw a single line
		 * for each row or column rather than a rectangle so that we can draw a nice
		 * looking diagonal where the light and dark shading meet.
		 */
		graGraphics.setColor(tilType.getLightColor());
		for(int i = 0; i < iSHADE_WIDTH; i++) {
			graGraphics.drawLine(iX, iY + i, iX + iTILE_SIZE - i - 1, iY + i);
			graGraphics.drawLine(iX + i, iY, iX + i, iY + iTILE_SIZE - i - 1);
		}
	}

}
