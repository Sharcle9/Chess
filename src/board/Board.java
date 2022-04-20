package board;

public class Board {
	
	interface movable{
		public boolean canMove(int r, int c, int to_r, int to_c);
	}
	
	public static class Piece implements movable{
		String name;
		String color;
		
		public Piece(String color) {
			this.color = color;
			this.name = color;
		}
		
		public String toString() {
			return color + name; 
		}
		
		public String getColor() {
			return color;
		}
		
		public String getName() {
			return name;
		}

		//not a piece, bad input, return false
		//due to dynamic bonding, this will not be called for a piece
		public boolean canMove(int r, int c, int to_r, int to_c) {
			return true;
		} 
		
	}
	
	public static class Pawn extends Piece{
		public boolean enPassant_able = false;
		public boolean hasmoved = false;
		public Pawn(String color) {
			super(color);
			name = "p";
		}	
		
		public boolean canMove(int r, int c, int to_r, int to_c) {
			if (color.equals("w") && to_r == r+1 && to_c == c){
				return true;
			}else if (color.equals("b") && to_r == r-1 && to_c == c){
				return true;
			}else{
				return false;
			}	
		}
	}
	
	public static class King extends Piece{
		public boolean hasmoved = false;
		public King(String color) {
			super(color);
			name = "K";
		}	
		
		public boolean canMove(int r, int c, int to_r, int to_c) {
			if(Math.abs(r - to_r) <= 1 && Math.abs(c - to_c) <= 1) {
				return true;
			}
			
			return false;
		}
	}
	
	public static class Queen extends Piece{
		public Queen(String color) {
			super(color);
			name = "Q";
		}	
		
		public boolean canMove(int r, int c, int to_r, int to_c) {
			int disR = Math.abs(to_r - r); // distance between rows
			int disC = Math.abs(to_c - c);// distance between cols
			if(disR == disC){
				return true;
			}
			
			if (to_r != r && to_c == c){
				return true;
			}else if (to_c != c && to_r == r){
				return true;
			}
			
			return false;
			
		}
	}
	
	public static class Rook extends Piece{
		public boolean hasmoved = false;
		public Rook(String color) {
			super(color);
			name = "R";
		}	
		
		public boolean canMove(int r, int c, int to_r, int to_c) {
			if (to_r != r && to_c == c){
				return true;
			}else if (to_c != c && to_r == r){
				return true;
			}
			
			return false;
		}
	}
	
	public static class Knight extends Piece{
		public Knight(String color) {
			super(color);
			name = "N";
		}	
		
		public boolean canMove(int r, int c, int to_r, int to_c) {
			Math.abs(to_c);
			if(Math.abs(to_c - c) == 1 && Math.abs(to_r - r) == 2){
				return true;
			}else if(Math.abs(to_c - c) == 2 && Math.abs(to_r - r) == 1){
				return true;
			}		
			return false;
		}
	}
	
	public static class Bishop extends Piece{
		public Bishop(String color) {
			super(color);
			name = "B";
		}	
		
		public boolean canMove(int r, int c, int to_r, int to_c) {
			int disR = Math.abs(to_r - r); // distance between rows
			int disC = Math.abs(to_c - c);// distance between cols
			if(disR == disC){
				return true;
			}
			return false;
		}
	}
	
	public static Piece[][] setupBoard(){
		Piece[][] board = new Piece[8][8];
		
		setupBoard(board, "w");
		setupBoard(board, "b");
		
		for(int i = 0; i < 8; i ++) {
			for(int j = 2; j < 6; j ++) {
				if((i + j)%2 == 0) {
					board[j][i] = new Piece("#");
				} else {
					board[j][i] = new Piece(" ");
				}
			}
		}
		
		return board;
		
	}
	
	public static Piece[][] setupBoard(Piece[][] board, String color) {
		int x = 0;
		int y = 1;
		if(color.equals("b")) {
			x = 7;
			y = 6;
		}
		
		board[x][0] = new Rook(color);
		board[x][1] = new Knight(color);
		board[x][2] = new Bishop(color);
		board[x][3] = new Queen(color);
		board[x][4] = new King(color);
		board[x][5] = new Bishop(color);
		board[x][6] = new Knight(color);
		board[x][7] = new Rook(color);
		
		for(int i = 0; i < 8; i ++) {
			board[y][i] = new Pawn(color);
		}
		
		return board;
	}
}