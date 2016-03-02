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
	private static final int ISTATS_INSET = 175;
	
	/**
	 * The y coordinate of the controls category.
	 */
	private static final int ICONTROLS_INSET = 270;
	
	/**
	 * The number of pixels to offset between each string.
	 */
	private static final int ITEXT_STRIDE = 25;
	
	/**
	 * The small font.
	 */
	private static final Font FSMALL_FONT = new Font("Helvetica", Font.BOLD, 11);
	
	/**
	 * The large font.
	 */
	private static final Font FLARGE_FONT = new Font("Helvetica", Font.BOLD, 13);
	
	/**
	 * The color to draw the text and preview box in.
	 */
	private static final Color CDRAW_COLOR = new Color(128, 192, 128);
	
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
	 * @param tetris The Tetris instance to use.
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
			int cols = type.getCols();
			int rows = type.getRows();
			int dimension = type.getDimension();
		
			/*
			 * Calculate the top left corner (origin) of the piece.
			 */
			int startX = (ISQUARE_CENTER_X - (cols * ITILE_SIZE / 2));
			int startY = (ISQUARE_CENTER_Y - (rows * ITILE_SIZE / 2));
		
			/*
			 * Get the insets for the preview. The default
			 * rotation is used for the preview, so we just use 0.
			 */
			int top = type.getTopInset(0);
			int left = type.getLeftInset(0);
		
			/*
			 * Loop through the piece and draw it's tiles onto the preview.
			 */
			for(int row = 0; row < dimension; row++) {
				for(int col = 0; col < dimension; col++) {
					if(type.isTile(col, row, 0)) {
						drawTile(type, startX + ((col - left) * ITILE_SIZE), startY + ((row - top) * ITILE_SIZE), g);
					}
				}
			}
		}
	}
	
	/**
	 * Draws a tile onto the preview window.
	 * @param type The type of tile to draw.
	 * @param x The x coordinate of the tile.
	 * @param y The y coordinate of the tile.
	 * @param g The graphics object.
	 */
	private void drawTile(TileType type, int x, int y, Graphics g) {
		/*
		 * Fill the entire tile with the base color.
		 */
		g.setColor(type.getBaseColor());
		g.fillRect(x, y, ITILE_SIZE, ITILE_SIZE);
		
		/*
		 * Fill the bottom and right edges of the tile with the dark shading color.
		 */
		g.setColor(type.getDarkColor());
		g.fillRect(x, y + ITILE_SIZE - ISHADE_WIDTH, ITILE_SIZE, ISHADE_WIDTH);
		g.fillRect(x + ITILE_SIZE - ISHADE_WIDTH, y, ISHADE_WIDTH, ITILE_SIZE);
		
		/*
		 * Fill the top and left edges with the light shading. We draw a single line
		 * for each row or column rather than a rectangle so that we can draw a nice
		 * looking diagonal where the light and dark shading meet.
		 */
		g.setColor(type.getLightColor());
		for(int i = 0; i < ISHADE_WIDTH; i++) {
			g.drawLine(x, y + i, x + ITILE_SIZE - i - 1, y + i);
			g.drawLine(x + i, y, x + i, y + ITILE_SIZE - i - 1);
		}
	}
	
}
