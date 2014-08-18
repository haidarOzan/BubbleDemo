package com.mygdx.game;

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
		size = 42;
		body = new Rectangle();
		body.x = MathUtils.random(0, 800 - size);
		body.y = 300 - size;
		body.width = size;
		body.height = size;
		speed = new Vector2(75, 50);
		if(MathUtils.randomBoolean()){
			speed.x = -speed.x;		}
		isAlive = true;
		hitCount = 1;
	}

	public void copy(Bubble child) {
		this.speed.y = java.lang.Math.abs(this.speed.y) + 25;
		child.body.y = this.body.y;
		child.body.x = this.body.x;
		child.size = this.size;
		child.body.width = this.size;
		child.body.height = this.size;
		child.speed.y = this.speed.y;
		child.speed.x = this.speed.x;
		child.speed.x = (-child.speed.x);
		child.hitCount = this.hitCount;
	}

	public void reset() {
		body.x = MathUtils.random(0, 800 - 64);
		size = 42;
		body.width = size;
		body.height = size;
		body.y = 300 - size;
		speed.x = 75;
		speed.y = 50;
		if(MathUtils.randomBoolean()){
			speed.x = -speed.x;		}
		isAlive = true;
		hitCount = 1;
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
