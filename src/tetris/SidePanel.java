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
	private static final long LSERIALVERSIONUID = 2181495598854992747L;

	/**
	 * The dimensions of each tile on the next piece preview.
	 */
	private static final int ITILE_SIZE = BoardPanel.ITILE_SIZE >> 1;
	
	/**
	 * The width of the shading on each tile on the next piece preview.
	 */
	private static final int ISHADE_WIDTH = BoardPanel.ISHADE_WIDTH >> 1;
	
	/**
	 * The number of rows and columns in the preview window. Set to
	 * 5 because we can show any piece with some sort of padding.
	 */
	private static final int ITILE_COUNT = 5;
	
	/**
	 * The center x of the next piece preview box.
	 */
	private static final int ISQUARE_CENTER_X = 130;
	
	/**
	 * The center y of the next piece preview box.
	 */
	private static final int ISQUARE_CENTER_Y = 65;
	
	/**
	 * The size of the next piece preview box.
	 */
	private static final int ISQUARE_SIZE = (ITILE_SIZE * ITILE_COUNT >> 1);
	
	/**
	 * The number of pixels used on a small insets (generally used for categories).
	 */
	private static final int ISMALL_INSET = 20;
	
	/**
	 * The number of pixels used on a large insets.
	 */
	private static final int ILARGE_INSET = 40;
	
	/**
	 * The y coordinate of the stats category.
	 */
	private static final int ISTATS_INSET = 140;
	
	/**
	 * The y coordinate of the controls category.
	 */
	private static final int ICONTROLS_INSET = 240;
	
	/**
	 * The number of pixels to offset between each string.
	 */
	private static final int ITEXT_STRIDE = 25;
	
	/**
	 * The small font.
	 */
	private static final Font FSMALL_FONT = new Font("Dialog", Font
			.BOLD, 11);
	
	/**
	 * The large font.
	 */
	private static final Font FLARGE_FONT = new Font("Dialog", Font.BOLD,
													 13);
	
	/**
	 * The color to draw the text and preview box in.
	 */
	private static final Color CDRAW_COLOR = new Color(173, 255, 255);
	
	/**
	 * The Tetris instance.
	 */
	private Tetris tetTetris;
        
        /**
         * Background image
         */
        private Image imgBackground;
	
	/**
	 * Creates a new SidePanel and sets it's display properties.
	 * @param tetTetris The Tetris instance to use.
	 */
	public SidePanel(Tetris tetTetris) {
		this.tetTetris = tetTetris;
		
		setPreferredSize(new Dimension(200, BoardPanel.PANEL_HEIGHT));
		setBackground(Color.BLACK);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Set the color for drawing.
		g.setColor(CDRAW_COLOR);
		
		/*
		 * This variable stores the current y coordinate of the string.
		 * This way we can re-order, add, or remove new strings if necessary
		 * without needing to change the other strings.
		 */
		int offset;
		
		/*
		 * Draw the "Stats" category.
		 */
		g.setFont(FLARGE_FONT);
		g.drawString("Stats", ISMALL_INSET, offset = ISTATS_INSET);
		g.setFont(FSMALL_FONT);
		g.drawString("Level: " + tetTetris.getLevel(), ILARGE_INSET, offset += ITEXT_STRIDE);
		g.drawString("Score: " + tetTetris.getScore(), ILARGE_INSET, offset += ITEXT_STRIDE);
		
		/*
		 * Draw the "Controls" category.
		 */
		g.setFont(FLARGE_FONT);
		g.drawString("Controls", ISMALL_INSET, offset = ICONTROLS_INSET);
		g.setFont(FSMALL_FONT);
		g.drawString("A - Move Left", ILARGE_INSET, offset += ITEXT_STRIDE);
		g.drawString("D - Move Right", ILARGE_INSET, offset += ITEXT_STRIDE);
		g.drawString("Q - Rotate Anticlockwise", ILARGE_INSET, offset += ITEXT_STRIDE);
		g.drawString("E - Rotate Clockwise", ILARGE_INSET, offset += ITEXT_STRIDE);
		g.drawString("S - Drop", ILARGE_INSET, offset += ITEXT_STRIDE);
		g.drawString("P - Pause Game", ILARGE_INSET, offset += ITEXT_STRIDE);
		g.drawString("G - Save Game", ILARGE_INSET, offset += ITEXT_STRIDE);
		g.drawString("C - Load Game", ILARGE_INSET, offset += ITEXT_STRIDE);
		g.drawString("T - Stop/Start Track", ILARGE_INSET, offset +=
				ITEXT_STRIDE);

		/*
		 * Draw the next piece preview box.
		 */
		g.setFont(FLARGE_FONT);
		g.drawString("Next Piece:", ISMALL_INSET, 70);
		g.drawRect(ISQUARE_CENTER_X - ISQUARE_SIZE, ISQUARE_CENTER_Y - ISQUARE_SIZE, ISQUARE_SIZE * 2, ISQUARE_SIZE * 2);
		
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
