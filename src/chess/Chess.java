package chess;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import board.Board;
import board.Board.*;

public class Chess {
	static Piece[][] board;

	//odd means white moves 
	//even means black moves
	static int turn = 1;

	static boolean wCheck = false;
	static boolean bCheck = false;


	public static void init(){
		board = Board.setupBoard();
	}

	public static void printBoard() {
		for(int i = 7; i >= 0; i --) {
			for(int j = 0; j < 8; j ++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.print(i + 1 + "\n");
		}
		System.out.println(" a  b  c  d  e  f  g  h ");
	}

	public static Piece[][] cloneBoard(){
		Piece[][] clone = new Piece[8][8];

		for(int i = 0; i < 8; i ++) {
			for(int j = 0; j < 8; j ++) {
				clone[i][j] = board[i][j];
			}
		}

		return clone;
	}

	public static boolean contains(ArrayList<int[]> array, int[] i) {
		for(int[] j: array) {
			if(j[0] == i[0] && j[1] == i[1]) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkmate(int colorInt) {
		String color = "";

		switch(colorInt) {
		case 0: color = "b"; break;
		case 1: color = "w"; break;
		}

		//find king position
		int king_r = -1;
		int king_c = -1;
		loop: for(int i = 0; i < 8; i ++) {
			for(int j = 0; j < 8; j ++) {
				if(board[i][j].toString().equals(color + "K")) {
					king_r = i;
					king_c = j;
					break loop;
				}
			}
		}

		//move king
		for(int i = king_r - 1; i <= king_r + 1; i ++) {
			for(int j = king_c - 1; j <= king_c + 1; j ++) {
				try {
					if(canMove(king_r, king_c, i, j)) {

						Piece[][] clone = cloneBoard();
						move(clone, king_r, king_c, i, j);
						if(!check(clone, colorInt)) {
							return false;
						}
					}
				} catch(Exception ArrayIndexOutOfBound) {
					continue;
				}
			}
		}

		ArrayList<int[]> pos = new ArrayList<int[]>();

		for(int i = 0; i < 8; i ++) {
			if(i != king_r) {
				int[] temp = {i, king_c};
				pos.add(temp);
			}
			if(i != king_c) {
				int[] temp = {king_r, i};
				pos.add(temp);
			}
			for(int j = 0; j < 8; j ++) {
				if(i == king_r && j == king_c) {
					break;
				}
				if(Math.abs(i - j) == Math.abs(king_r - king_c)) {
					int[] temp = {i, j};
					pos.add(temp);
				}
			}
		}

		int[][] temp = {{king_r + 2, king_c + 1},{king_r + 2, king_c - 1},{king_r + 1, king_c + 2},{king_r + 1, king_c - 2},{king_r - 1, king_c + 2},{king_r - 1, king_c - 2},{king_r - 2, king_c + 1},{king_r - 2, king_c - 1}};
		for(int[] i: temp) {
			pos.add(i);
		}

		//now pos has all the possible position that can effect check status

		//find all your pieces
		for(int r = 0; r < 8; r ++) {
			for(int c = 0; c < 8; c ++) {
				if(!board[r][c].getColor().equals(color)) {
					continue;
				}
				for(int to_r = 0; to_r < 8; to_r ++) {
					for(int to_c = 0; to_c < 8; to_c ++) {
						int[] temp_pos = {to_r, to_c};
						if(contains(pos, temp_pos) && canMove(r, c, to_r, to_c)) {
							Piece[][] clone = cloneBoard();
							move(clone, r, c, to_r, to_c);
							if(!check(clone, colorInt)) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	public static boolean stalemate(int colorInt) {
		String color = "";

		switch(colorInt) {
		case 0: color = "b"; break;
		case 1: color = "w"; break;
		}

		for(int i = 0; i < 8; i ++) {
			for(int j = 0; j < 8; j++) {

				Piece piece = board[i][j];
				//find pieces of the same color
				if(!piece.getColor().equals(color)) {
					continue;
				}

				//posSet is the collection of all possible moves for the piece at i, j
				ArrayList<int[]> posSet = new ArrayList<int[]>();

				if(piece.getName().equals("p")) {
					//black
					int p_mod = 0;
					if(colorInt == 0) {
						p_mod = -1;
					} else {
						p_mod = 1;
					}

					int[][] Ppos = {{i, j + p_mod}, {i, j + 2 * p_mod}, {i - 1, j + p_mod}, {i + 1, j + p_mod}};
					for(int[] pos: Ppos) {
						posSet.add(pos);
					}
				}

				if(piece.getName().equals("K")){
					int[][] Kpos = {{i + 1, j + 1}, {i + 1, j}, {i + 1, j - 1}, {i, j + 1}, {i, j - 1}, {i - 1, j + 1}, {i - 1, j}, {i - 1, j - 1}};
					for(int[] pos: Kpos) {
						posSet.add(pos);
					}
				}

				if(piece.getName().equals("N")) {
					int[][] Npos = {{i + 2, j + 1},{i + 2, j - 1},{i + 1, j + 2},{i + 1, j - 2},
							{i - 1, j + 2},{i - 1, j - 2},{i - 2, j + 1},{i - 2, j - 1}};
					for(int[] pos: Npos) {
						posSet.add(pos);
					}	
				}

				if(piece.getName().equals("Q") || piece.getName().equals("R")){
					for(int k = 0; k < 8; k ++) {
						int[] temp1 = {k, j};
						int[] temp2 = {i, k};
						posSet.add(temp1);
						posSet.add(temp2);
					}
				}

				if(piece.getName().equals("Q") || piece.getName().equals("B")){
					for(int k = 0; k < 8; k ++) {
						for(int l = 0; l < 8; l ++) {
							if(Math.abs(i - j) == Math.abs(k - l)) {
								int[] temp = {k, l};
								posSet.add(temp);
							}
						}
					}
				}

				for(int[] pos: posSet) {
					try {
						if(canMove(i, j, pos[0], pos[1])){
							return false;
						}
					}catch(Exception ArrayIndexOutOfBoundsException) {
						continue;
					}
				}
			}
		}
		return true;
	}

	public static boolean castling(int r, int c, int to_r, int to_c) {
		King piece = (King)board[r][c];
		//add hasMoved.
		if(piece.hasmoved) {
			return false;
		}

		Piece[][] temp = cloneBoard();

		if(piece.getColor().equals("w")){
			if(to_r == 0 && to_c == 6){
				if(!board[0][7].getName().equals("R") || !board[0][7].getColor().equals("w")) {
					return false;
				} else {
					Rook rook = (Rook)board[0][7];
					if(rook.hasmoved) {
						return false;
					}
				}

				if(!board[0][5].toString().equals("  ") || !board[0][6].toString().equals("##")) {
					return false;
				}

				move(temp, r, c, 0, 5);
				if(check(temp, turn%2)) {
					System.out.println("\tCannot move, check f1");
					return false;
				}

				move(temp, 0, 5, 0, 6);
				if(check(temp, turn%2)) {
					System.out.println("\tCannot move, check g1");
					return false;
				}

				move(board, 0, 7, 0, 5);
				return true;

			} else if(to_r == 0 && to_c == 2){
				if(!board[0][0].getName().equals("R") || !board[0][0].getColor().equals("w")) {
					return false;
				} else {
					Rook rook = (Rook)board[0][0];
					if(rook.hasmoved) {
						return false;
					}
				}

				if(!board[0][1].toString().equals("  ") || !board[0][2].toString().equals("##") || !board[0][3].toString().equals("  ")) {
					return false;
				}

				move(temp, r, c, 0, 3);
				if(check(temp, turn%2)) {
					System.out.println("\tCannot move, check d1");
					return false;
				}

				move(temp, 0, 3, 0, 2);
				if(check(temp, turn%2)) {
					System.out.println("\tCannot move, check c1");
					return false;
				}
				move(board, 0, 0, 0, 3);
				return true;
			}
		}else{
			if(to_r == 7 && to_c == 6){
				if(!board[7][7].getName().equals("R") || !board[7][7].getColor().equals("b")) {
					return false;
				} else {
					Rook rook = (Rook)board[7][7];
					if(rook.hasmoved) {
						return false;
					}
				}

				if(!board[7][6].toString().equals("  ") || !board[7][5].toString().equals("##")) {
					return false;
				}

				move(temp, r, c, 7, 5);
				if(check(temp, turn%2)) {
					System.out.println("\tCannot move, check f8");
					return false;
				}

				move(temp, 7, 5, 7, 6);
				if(check(temp, turn%2)) {
					System.out.println("\tCannot move, check g1");
					return false;
				}

				move(board, 7, 7, 7, 5);
				return true;

			} else if(to_r == 7 && to_c ==2){
				if(!board[7][0].getName().equals("R") || !board[7][0].getColor().equals("b")) {
					return false;
				} else {
					Rook rook = (Rook)board[7][0];
					if(rook.hasmoved) {
						return false;
					}
				}
				if(!board[7][1].toString().equals("##") || !board[7][2].toString().equals("  ") || !board[7][3].toString().equals("##")) {
					return false;
				}

				move(temp, r, c, 7, 3);
				if(check(temp, turn%2)) {
					System.out.println("\tCannot move, check d8");
					return false;
				}

				move(temp, 7, 3, 7, 2);
				if(check(temp, turn%2)) {
					System.out.println("\tCannot move, check c8");
					return false;
				}

				move(board, 7, 0, 7, 3);
				return true;
			}
		}
		return false;
	}

	/* combine castling(), enPassant(), and canMove() methods to
	 * determine if a move can be made
	 */
	public static boolean canMove(int r, int c, int to_r, int to_c) {

		Piece piece = new Piece("");

		try {
			piece = board[r][c];
		} catch(Exception ArrayIndexOutOfBoundsException) {
			return false;
		}

		//odd for white, even for black
		//1 for white, 0 for black
		int color = turn%2;
		String oppoColor = "";
		int pawn_mod = 0;

		//System.out.println("color error check:");
		//System.out.println(piece);
		//check if operating the wrong color
		switch(piece.getColor()) {
		case " ": return false;
		case "#": return false;
		case "w":
			if(color != 1) {
				return false;
			}
			pawn_mod = 1;
			oppoColor = "b";
			break;
		case "b":
			if(color != 0) {
				return false;
			}
			pawn_mod = -1;
			oppoColor = "w";
			break;
		}

		//System.out.println("space availability check:");
		//check if moving on a piece with same color
		if(piece.getColor().equals(board[to_r][to_c].getColor())) {
			return false;
		}

		//check move without special moves

		//King and Pawn has special moves
		boolean pawn_special = false;

		if(piece.getName().equals("p")) {
			//if pawn move one step forward and land on a oppoColor piece
			if(piece.canMove(r, c, to_r, to_c)) {
				if(board[to_r][to_c].getColor().equals(oppoColor)) {
					return false;
				}
			} else {
				//if moving 2 steps forward and has any piece in the way, return false
				if(to_r == r + (pawn_mod * 2) && c == to_c) {
					if(((Pawn)piece).hasmoved) {
						return false;
					}
					if(!board[to_r][to_c].getColor().equals(" ") && !board[to_r][to_c].getColor().equals("#")) {
						return false;
					}
					if(!board[r + pawn_mod][to_c].getColor().equals(" ") && !board[r + pawn_mod][to_c].getColor().equals("#")) {
						return false;
					}
					pawn_special = true;
				} else if(to_r == r + pawn_mod && Math.abs(to_c - c) == 1) {
					//if attacking 
					//if En Passant
					if(board[to_r][to_c].getColor().equals(oppoColor)) {
						pawn_special = true;
					} else if(board[r][to_c].toString().equals(oppoColor + "p")) {
						if(((Pawn)board[r][to_c]).enPassant_able) {
							pawn_special = true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				}
				
			}
		}

		//		if(piece.getName().equals("p") && !piece.canMove(r, c, to_r, to_c)) {
		//			if(!((Pawn)piece).hasmoved) {
		//				if (piece.getColor().equals("w") && to_r == r + 2 && to_c == c){
		//					if((board[to_r][to_c].getName().equals(" ") || board[to_r][to_c].getName().equals("#")) && (board[r + 1][c].getName().equals(" ") || board[r + 1][c].getName().equals("#"))) {
		//						pawn_special = true;
		//					} else {
		//						return false;
		//					}
		//				} else if (piece.getColor().equals("b") && to_r == r - 2 && to_c == c){
		//					if((board[to_r][to_c].getName().equals(" ") || board[to_r][to_c].getName().equals("#")) && (board[r - 1][c].getName().equals(" ") || board[r - 1][c].getName().equals("#"))) {
		//						pawn_special = true;
		//					} else {
		//						return false;
		//					}
		//				} 
		//			}
		//
		//			if (Math.abs(r - to_r) == 1 &&  Math.abs(c - to_c) == 1) {
		//				//white, move up
		//				if(color == 1 && to_r != r + 1) {
		//					return false;
		//				} else if(color == 0 && to_r != r - 1) {
		//					return false;
		//				}
		//				Piece temp = board[r][to_c];
		//				if(board[to_r][to_c].getColor().equals(oppoColor)) {
		//					pawn_special = true;
		//				}
		//				if(!temp.getName().equals("p") || temp.getColor().equals(piece.getColor())) {
		//					return false;
		//				}
		//				if(((Pawn)temp).enPassant_able) {
		//					if((r+c)%2 == 1) {
		//						board[r][to_c] = new Piece("#");
		//					} else {
		//						board[r][to_c] = new Piece(" ");
		//					}
		//					pawn_special = true;
		//				} 
		//			} 
		//			if(!pawn_special) {
		//				return false;
		//			}
		//		}


		//System.out.println("King special move check:");
		if(!piece.canMove(r, c, to_r, to_c) && !pawn_special) {
			if(piece.getName().equals("K")) {
				return castling(r, c, to_r, to_c);
			}
			return false;
		}


		//check if there are pieces in the way
		//skip if piece is a Knight
		if(!piece.getName().equals("N")) {
			if(r == to_r) {
				//if piece moves left
				if(c > to_c) {
					for(int i = c - 1; i > to_c; i --) {
						if(!board[r][i].toString().equals("##") && !board[r][i].toString().equals("  ")) {
							//System.out.println("left error");
							return false;
						}
					}
				} else {
					//if piece moves right
					for(int i = c + 1; i < to_c; i ++) {
						if(!board[r][i].toString().equals("##") && !board[r][i].toString().equals("  ")) {
							//System.out.println("right error");
							return false;
						}
					}
				}
			} else if(c == to_c) {
				//if piece moves down
				if(r > to_r) {
					for(int i = r - 1; i > to_r; i --) {
						if(!board[i][c].toString().equals("##") && !board[i][c].toString().equals("  ")) {
							//System.out.println("down error");
							return false;
						}
					}
				} else {
					//if piece moves up
					for(int i = r + 1; i < to_r; i ++) {
						if(!board[i][c].toString().equals("##") && !board[i][c].toString().equals("  ")) {
							//System.out.println("up error");
							return false;
						}
					}
				}
			} else if(r > to_r) {
				//if piece moves left down or left up
				int j;
				if(c > to_c) {
					j = c - 1;
				} else {
					j = c + 1;
				}
				for(int i = r - 1; i > to_r; i --) {
					//break if piece going left down and is one move away from destination
					if(c > to_c && j == to_c + 1) {
						break;
						//left up
					} else if ( c < to_c && j == to_c - 1) {
						break;
					}
					//return false if there is a piece in the way
					if(!board[i][j].toString().equals("##") && !board[i][j].toString().equals("  ")) {
						//System.out.println("down diagonal error");
						return false;
					}
					//in/decrement j
					if(c > to_c) {
						j --;
					} else {
						j ++;
					}
				}
			} else if(r < to_r){
				int j;
				if(c > to_c) {
					j = c - 1;
				} else {
					j = c + 1;
				}
				for(int i = r + 1; i < to_r; i ++) {
					//break if piece going right down and is one move away from destination
					if(c > to_c && j == to_c + 1) {
						break;
						//right up
					} else if ( c < to_c && j == to_c - 1) {
						break;
					}
					//return false if there is a piece in the way
					if(!board[i][j].toString().equals("##") && !board[i][j].toString().equals("  ")) {
						//System.out.println("up diagonal error");
						return false;
					}
					//in/decrement j
					if(c > to_c) {
						j --;
					} else {
						j ++;
					}
				}
			}

		}


		//if it is in check, a move must be made in response
		Piece[][] temp = new Piece[8][8];
		for(int i = 0; i < 8; i ++) {
			for(int j = 0; j < 8; j ++) {
				temp[i][j] = board[i][j];
			}
		}
		move(temp, r, c, to_r, to_c);

		//return false if next step is check
		if(check(temp, color)) {
			//System.out.println("\tCannot move, check");
			return false;
		}




		return true;
	}

	//return true if it is in check for the color 
	public static boolean check(Piece[][] board, int colorInt) {

		int r = 0;
		int c = 0;
		int pawn_mod = 0;
		String color = "";
		String oppoColor = "";

		switch(colorInt) {
		case 0:
			color = "b";
			oppoColor = "w";
			pawn_mod = -1;
			break;
		case 1:
			color = "w";
			oppoColor = "b";
			pawn_mod = 1;
			break;
		}

		//get King position
		loop: for(r = 0; r < 8; r ++) {
			for(c = 0; c < 8; c ++) {
				if(board[r][c].toString().equals(color + "K")) {
					break loop;
				}
			}
		}

		//check if Knight or King can attack King
		int[][] NAttkPos = {{r + 2, c + 1},{r + 2, c - 1},{r + 1, c + 2},{r + 1, c - 2},
				{r - 1, c + 2},{r - 1, c - 2},{r - 2, c + 1},{r - 2, c - 1}};
		int[][] KAttkPos = {{r + 1, c + 1},{r + 1, c},{r + 1, c - 1},{r, c + 1},
				{r, c - 1},{r - 1, c + 1},{r - 1, c},{r - 1, c - 1}};

		for(int[] Npos:NAttkPos) {
			try {
				if(board[Npos[0]][Npos[1]].toString().equals(oppoColor + "N")) {
					return true;
				}
			} catch(Exception ArrayIndexOutOfBoundsException) {
				continue;
			}
		}

		for(int[] Kpos:KAttkPos) {
			try {
				if(board[Kpos[0]][Kpos[1]].toString().equals(oppoColor + "K")) {
					return true;
				}
			} catch(Exception ArryaIndexOutOfBoundsException) {
				continue;
			}
		}

		//check Pawn
		//CHECKED WORKING
		for(int i = -1; i < 2; i ++) {
			//System.out.println("Checking (" + (c+i) + ", " + (r + pawn_mod) + "), toString gives " + board[r + pawn_mod][c + i]);
			try {
				if(i == 0) {
					continue;
				}
				if(board[r + pawn_mod][c + i].toString().equals(oppoColor + "p")) {
					return true;
				}
			}catch(Exception ArrayIndexOutOfBoundsException) {
				continue;
			}
		}

		//check horizontally (Queen or Rook)
		//CHECKED WORKING
		for(int i = c + 1; i < 8; i ++) {
			//check to the right piece by piece if has Queen or Rook
			if(board[r][i].toString().equals(oppoColor + "Q") || board[r][i].toString().equals(oppoColor + "R")) {
				return true;
				//stop checking if there's another piece in the way
			} else if (!board[r][i].toString().equals("##") && !board[r][i].toString().equals("  ")) {
				break;
			}
		}

		//CHECKED WORKING
		for(int i = c - 1; i >= 0; i --) {
			//check to the left piece by piece if has Queen or Rook

			if(board[r][i].toString().equals(oppoColor + "Q") || board[r][i].toString().equals(oppoColor + "R")) {
				return true;
				//stop checking if there's another piece in the way
			} else if (!board[r][i].toString().equals("##") && !board[r][i].toString().equals("  ")) {
				break;
			}
		}
		//check vertically (Queen or Rook)
		//CHECKED WORKING
		for(int i = r + 1; i < 8; i ++) {
			//check upwards piece by piece if has Queen or Rook
			if(board[i][c].toString().equals(oppoColor + "Q") || board[i][c].toString().equals(oppoColor + "R")) {
				return true;
				//stop checking if there's another piece in the way
			} else if (!board[i][c].toString().equals("##") && !board[i][c].toString().equals("  ")) {
				break;
			}
		}
		//CHECKED WORKING
		for(int i = r - 1; i >= 0; i --) {
			//check downwards by piece if has Queen or Rook
			if(board[i][c].toString().equals(oppoColor + "Q") || board[i][c].toString().equals(oppoColor + "R")) {
				return true;
				//stop checking if there's another piece in the way
			} else if (!board[i][c].toString().equals("##") && !board[i][c].toString().equals("  ")) {
				break;
			}
		}
		//check diagonally (/) (Queen or Bishop)
		//CHECKED WORKING
		int j = c + 1;
		for(int i = r + 1; i < 8; i ++) {
			if (j > 7) {
				break;
			}
			//Diagonally upwards (/^)
			if(board[i][j].toString().equals(oppoColor + "Q") || board[i][j].toString().equals(oppoColor + "B")) {
				return true;
			} else if(!board[i][j].toString().equals("##") && !board[i][j].toString().equals("  ")) {
				break;
			}
			j ++;
		}
		//CHECKED WORKING
		j = c - 1;
		for(int i = r - 1; i >= 0; i --) {
			if (j < 0) {
				break;
			}
			//Diagonally downwards (</)
			if(board[i][j].toString().equals(oppoColor + "Q") || board[i][j].toString().equals(oppoColor + "B")) {
				return true;
			} else if(!board[i][j].toString().equals("##") && !board[i][j].toString().equals("  ")) {
				break;
			}
			j --;
		}
		//check diagonally (\) (Queen or Bishop)
		//CHECKED WORKING
		j = c - 1;
		for(int i = r + 1; i < 8; i ++) {
			if (j < 0) {
				break;
			}
			//Diagonally downwards (^\)
			if(board[i][j].toString().equals(oppoColor + "Q") || board[i][j].toString().equals(oppoColor + "B")) {
				return true;
			} else if(!board[i][j].toString().equals("##") && !board[i][j].toString().equals("  ")) {
				break;
			}
			j --;
		}
		//CHECKED WORKING
		j = c + 1;
		for(int i = r - 1; i >= 0; i --) {
			if (j > 7) {
				break;
			}
			//Diagonally downwards (</)
			if(board[i][j].toString().equals(oppoColor + "Q") || board[i][j].toString().equals(oppoColor + "B")) {
				return true;
			} else if(!board[i][j].toString().equals("##") && !board[i][j].toString().equals("  ")) {
				break;
			}
			j ++;
		}

		return false;
	}

	public static void move(Piece[][] board, int r, int c, int to_r, int to_c) {
		//Move the piece
		board[to_r][to_c] = board[r][c];
		String color = " ";
		if((r + c)%2 == 0) {
			color = "#";
		}
		board[r][c] = new Piece(color);
	}

	public static int[] convert(String s) {
		/* coord[0 ~ 4] is coordinates
		 * coord[5] is promotion (if applicable)
		 * coord[6] is input error or draw/resign
		 */
		int[] coord = new int[6];
		try {
			coord[0] = Integer.parseInt(s.substring(1,2)) - 1;
			coord[2] = Integer.parseInt(s.substring(4,5)) - 1;
		} catch(Exception NumberFormatException) {
			coord[5] = -1;
			return coord;
		}

		if(s.length() < 5 || s.length() == 6 || s.charAt(2) != ' ') {
			coord[5] = -1;
			return coord;
		}

		char file_from = s.charAt(0);

		switch(file_from) {
		case 'a':
			coord[1] = 0; break;
		case 'b':
			coord[1] = 1; break;
		case 'c':
			coord[1] = 2; break;
		case 'd':
			coord[1] = 3; break;
		case 'e':
			coord[1] = 4; break;
		case 'f':
			coord[1] = 5; break;
		case 'g':
			coord[1] = 6; break;
		case 'h':
			coord[1] = 7; break;
		}

		char file_to = s.charAt(3);

		switch(file_to) {
		case 'a':
			coord[3] = 0; break;
		case 'b':
			coord[3] = 1; break;
		case 'c':
			coord[3] = 2; break;
		case 'd':
			coord[3] = 3; break;
		case 'e':
			coord[3] = 4; break;
		case 'f':
			coord[3] = 5; break;
		case 'g':
			coord[3] = 6; break;
		case 'h':
			coord[3] = 7; break;
		}

		/* -1 0 1 2 3 4 5
		 * dr R N B Q K p
		 */

		if(s.length() > 5) {
			if(s.substring(6).equals("draw?")) {
				coord[5] = 0;
			}
			switch(s.substring(6,7)) {
			case "R": coord[4] = 0;  break;
			case "N": coord[4] = 1;  break;
			case "B": coord[4] = 2;  break;
			case "Q": coord[4] = 3;  break;
			case "K": coord[4] = 4;  break;
			case "p": coord[4] = 5;  break;
			}

			if(s.length() > 8 && s.substring(8).equals("draw?")) {
				coord[5] = 0;
			}
		}



		return coord;
	}


	public static void main(String[] args) throws IOException {
		init();
		printBoard();
		boolean drawRequest = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String color = "";
		String oppoColor = "";
		String c = "";
		while(true) {
			switch(turn%2) {
			case 1: color = "White"; oppoColor = "Black"; c = "w"; break;
			case 0: color = "Black"; oppoColor = "White"; c = "b"; break;
			}
			System.out.print("\n" + color + "'s move: ");
			//System.out.println(turn);
			//System.out.println("\tCheck: " + check(board, turn%2));

			String s = reader.readLine();
			System.out.println();

			if(drawRequest && s.equals("draw")) {
				System.out.println("\ndraw");
				return;
			}

			if(s.equals("resign")) {
				System.out.println("\n" + oppoColor + " wins");
				return;
			}

			drawRequest = false;

			int[] data = convert(s);

			if(data[5] == -1 || !canMove(data[0], data[1], data[2], data[3])) {
				System.out.println("Illegal move, try again");
				continue;
			}

			if(data[5] == 0) {
				drawRequest = true;
			}

			boolean promotioned = false;
			if(board[data[0]][data[1]].getName().equals("p")) {
				if(data[2] == 7 || data[2] == 0) {
					if(s.length() < 7) {
						System.out.println("Illegal move, try again");
						continue;
					}
					Piece promotion = new Piece(" ");
					/*  data[4]:
					 *  0 1 2 3 4
					 *  R N B Q K
					 */
					switch(data[4]) {
					case 0: promotion = new Rook(c); break;
					case 1: promotion = new Knight(c); break;
					case 2: promotion = new Bishop(c); break;
					case 3: promotion = new Queen(c); break;
					case 4: System.out.println("Illegal move, try again"); continue;
					default: System.out.println("Illegal move, try again"); continue;
					}
					move(board, data[0], data[1], data[2], data[3]);
					board[data[2]][data[3]] = promotion;
					promotioned = true;
				}
			}

			if(!promotioned) {
				move(board, data[0], data[1], data[2], data[3]);
			}
			Piece piece = board[data[2]][data[3]];
			for(int i = 0; i < 8; i ++) {
				for(int j = 0; j < 8; j ++) {
					if(board[i][j].getName().equals("p") && board[i][j].getColor().equals(c)) {
						((Pawn)board[i][j]).enPassant_able = false;
					}
				}
			}


			if(piece.getName().equals("p")) {

				((Pawn)piece).hasmoved = true;
				if(Math.abs(data[0] - data[2]) == 2) {
					((Pawn)piece).enPassant_able = true;
				}
			}
			printBoard();

			turn ++; 

			if(check(board, turn % 2)) {
				System.out.println("\nCheck");
				if(checkmate(turn % 2)) {
					System.out.println("\nCheckmate");
					System.out.println("\n" + color + " wins");
					return;
				}
			} else {
				if(stalemate(turn % 2)) {
					System.out.println("\nStalemate");
					System.out.println("\ndraw");
					return;
				}
			}
		}
	}
}
