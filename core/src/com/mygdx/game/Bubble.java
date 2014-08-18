package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Bubble implements Poolable {
	Rectangle body;
	Vector2 speed;
	boolean isAlive;
	float size;
	int hitCount;

	Bubble() {
		body = new Rectangle();
		body.x = MathUtils.random(0, 800 - 64);
		size = 64;
		body.y = 300 - size;
		speed = new Vector2(75, 50);
		isAlive = true;
		hitCount = 0;
	}

	public void copy(Bubble child) {
		this.speed.y = java.lang.Math.abs(this.speed.y) + 5;
		child = this;
		child.speed.x = (-child.speed.x);
	}

	public void reset() {
		body.x = MathUtils.random(0, 800 - 64);
		size = 64;
		body.y = 300 - size;
		speed.x = 30;
		speed.y = 50;
		isAlive = true;
		hitCount = 0;
	}

	public void pop(Bubble bubble) {
		size *= hitCount;
		hitCount++;
		size /= hitCount;
		copy(bubble);
	}

	public void kill() {
		isAlive = false;
	}

}
