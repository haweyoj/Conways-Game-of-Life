/**
 * This class represent the static board.
 * 
 * @author Carlo Nguyen
 * @author Haweya Jama
 * @author Idris Milamean
 */

package golClasses;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import java.lang.Exception;
public class Board {

	// Data field
	private byte[][] board = new byte[100][100];
	private int cellSize = 5;

	protected Canvas graphics;
	protected ColorPicker colorChangerBtn;
	protected GraphicsContext gc;
	protected Slider sizeSliderBtn;

	public Board(GraphicsContext gc, Canvas graphics, ColorPicker colorChangerBtn, Slider sizeSliderBtn) {
		this.gc = gc;
		this.graphics = graphics;
		this.colorChangerBtn = colorChangerBtn;
		this.sizeSliderBtn = sizeSliderBtn;

		byte[][] board = new byte[100][100];
		this.board = board;
	}


	/**
	 * This method makes a given cell alive (changing the value to 1)
	 * @param x and y variables that tells us where the cell is on the board
	 * */
	public void setCellState(int x, int y) throws Exception {
		try {
			board[x][y] = 1;
		}

		catch(NullPointerException np) {
			//handle 
		}
	}

	/**
	 * This is a "helping-method" which contains two main draw-methods.
	 * drawGrid draws the grid of the board.
	 * drawBoard draws the board's array.
	 */
	public void draw() {  	
		drawGrid();
		drawBoard();

	}

	public int getCellSize() {
		return cellSize;
	}


	/**
	 * This method is connected to a slider.
	 * Allows the user to select size to the board and drawn cells.
	 * The method sets the new size to cellSize, gc clears the board, then draw() is executed.
	 * And draws everything to the screen.
	 */
	public void sizeChange() {
		cellSize = (int) sizeSliderBtn.getValue();
		gc.clearRect(0, 0, graphics.getWidth(), graphics.getHeight());
		draw();
	}

	/**
	 * This method draws the grid of the board.
	 * for-loops for X and Y, which adds the size of the cell each iterate to the grid
	 * according to the height and width of the Canvas.
	 */
	public void drawGrid() {

		gc.setFill(Color.BLACK);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);

		for (int x = 0; x < graphics.getWidth(); x += cellSize) {
			gc.strokeLine(x, 1000, x, 0);
		}
		for (int y = 0; y < graphics.getWidth(); y += cellSize) {
			gc.strokeLine(0, y, 1000, y);
		}
	}

	/**
	 * This method draws the board
	 * If the X and Y is equal to 1 there is a living cell.
	 * 
	 * @param i is point to X axis
	 * @param j is point to Y axis
	 */
	public void drawBoard() {

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {                
				if (board[i][j] == 1) {
					gc.fillRect(i*cellSize, j*cellSize,cellSize,cellSize);
					gc.setFill(colorChangerBtn.getValue());
				}
			}
		}
	}



	/**
	 * This method clears the current Board
	 * Clears the board first.
	 * Then a for-loop that search board for any living cells
	 * if true, then set that coordinate [x][y] equal to 0
	 * Draw grid again and stop the Animation.
	 */
	public void clearBoard() {

		gc.clearRect(0, 0, graphics.getWidth(), graphics.getHeight());

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {                
				if (board[i][j] == 1) {
					gc.clearRect(i*cellSize, j*cellSize,cellSize,cellSize);
					board[i][j] = 0;
				}
			}
		}

		drawGrid();
	}

	/**
	 * this method updates the board by applying the GoL rules to the board. 
	 * By using a temporary array that stores the previous state of the board, 
	 * the next generation of the board is created.
	 */
	public void nextGeneration() { 	
		byte[][] updated = new byte[board.length][board[0].length];

		for(int i = 0; i < board.length; i++) { // copies board
			for( int j =0; j < board[i].length; j++) {
				updated[i][j] = board[i][j];
			}
		}

		updateBoard(updated);
		gc.clearRect(0, 0, graphics.getWidth(), graphics.getHeight());

		for (int i = 0; i < updated.length; i++) {	
			for (int j = 0; j < updated[i].length; j++) {

				if(updated[i][j] == 0) { //the cell is dead
					if(neighbours(i,j) == 3) {
						gc.fillRect(i*cellSize, j*cellSize,cellSize,cellSize);

					}           	
				}   	
				else { // the cell is alive
					if(neighbours(i,j)< 2 || neighbours(i,j) > 3) {
						gc.clearRect(i*cellSize, j*cellSize,cellSize,cellSize);
					}                          
				}
			}

		}     
		board = updated;
		draw();
	}

	/**
	 * This method counts the number of neighbouring cells a given cell has,
	 * by iterating through the board with two for-loops that only checks the 8
	 * cells surrounding it.
	 *@return the number of neighbours the cell has.
	 *@param the x and y coordinates of the cell (placement of the cell in the grid)
	 * */
	public int neighbours(int x, int y) {

		int nr = 0; 
		if(board[x][y] == 1) { //so that the cell doesn't count itself
			nr = -1;
		}

		for(int i = x-1; i <= x+1; i++){

			if(i < board.length && i >= 0){ //cells on the edges (rows)

				for(int j = y-1; j <= y + 1; j++){

					if(j < board[i].length && j >= 0){ // cells on the edges (columns)

						if (board[i][j] == 1) {
							nr++;
						}
					}
				}
			}
		}
		return nr;   	
	}


	/**
	 * This method updates the board by applying the GoL rules,
	 * by iterating through the board with two for-loops and assigning
	 * 1 or 0 using the GoL rules.
	 * @param the array which contains the previous generation
	 * */
	public void updateBoard(byte[][] updated) {

		for (int i = 0; i < updated.length; i++) {

			for (int j = 0; j < updated[i].length; j++) {

				if(board[i][j] == 0) { //the cell is dead
					if(neighbours(i,j) == 3) {
						updated[i][j] = 1;
					}           	
				}

				else { // the cell is alive
					if(neighbours(i,j)< 2 || neighbours(i,j) > 3) {
						updated[i][j] = 0;
					}               
				}
			}

		} 

	}

	public void setBoard(byte[][] gameBoard) {
		this.board = gameBoard;
	}

	public byte[][] getBoard() {
		return board;
	} 	    
}
