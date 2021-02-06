package com.ngameweb.model;

public class GameHistory {
	
	int id;
	int n;
	int gameBoard;
	int winner;
	
	public GameHistory(int id, int n, int gameBoard, int winner) {
		super();
		this.id = id;
		this.n = n;
		this.gameBoard = gameBoard;
		this.winner = winner;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(int gameBoard) {
		this.gameBoard = gameBoard;
	}

	public int getWinner() {
		return winner;
	}

	public void setWinner(int winner) {
		this.winner = winner;
	}
}
