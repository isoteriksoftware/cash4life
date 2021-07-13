package com.isoterik.cash4life.cashpuzzles.utils;

import com.badlogic.gdx.math.Vector2;

public class Cell {
	private int row;
	private int column;
	private char letter;

	private Vector2 position;

	public Cell(int row, int column) {
		this.row = row;
		this.column = column;
		letter = ' ';

		position = Vector2.Zero;
	}

	public void setPoints(int row, int column) {
		this.row = row; this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public char getLetter() {
		return letter;
	}
	
	public void setLetter(char c) {
		letter = c;
	}

	public Vector2 getPosition() {
	    return position;
    }

    public void setPosition(Vector2 position) {
	    this.position = position;
    }

	public void setPosition(float x, float y) {
		this.position = new Vector2(x, y);
	}
	
	@Override
	public String toString() {
		return "Cell [row=" + row + ", column=" + column + "]";
	}
}
