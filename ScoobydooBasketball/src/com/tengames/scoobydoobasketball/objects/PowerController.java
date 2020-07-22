/*
The MIT License

Copyright (c) 2014 kong <tengames.inc@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.tengames.scoobydoobasketball.objects;

import woodyx.basicapi.accessor.SpriteAccessor;
import woodyx.basicapi.sprite.ObjectSprite;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class PowerController extends ObjectSprite {
	private TweenManager tween;
	private float angle;
	private boolean isPrepare;

	public PowerController(TweenManager tween, TextureRegion textureRegion, float x, float y, float angle,
			boolean isInBox) {
		super(textureRegion, x, y, isInBox);
		this.tween = tween;
		this.angle = angle;
		this.isPrepare = false;

		this.setOrigin(0, this.getHeight() / 2);
		this.setRotation(angle);
	}

	public void update(float deltaTime) {
		// update tween
		tween.update(deltaTime);
		// update dragg
		if (isPrepare) {
			this.setRotation(angle * MathUtils.radiansToDegrees);
		}
	}

	public void render(SpriteBatch batch) {
		this.draw(batch);
	}

	public void appear(float x, float y) {
		// set new position
		this.setPosition(x, y);
		// create timeline
		Timeline.createSequence().push(Tween.set(this, SpriteAccessor.SCALE_XY).target(0, 1)).beginParallel()
				.push(Tween.to(this, SpriteAccessor.SCALE_XY, 3f).target(1, 1).ease(Elastic.OUT))
				.push(Tween.to(this, SpriteAccessor.OPACITY, 3f).target(1).ease(Elastic.OUT)).end().start(tween);
	}

	public void disappear() {
		Tween.to(this, SpriteAccessor.OPACITY, 5f).target(0).ease(Linear.INOUT).start(tween);
	}

	public boolean getTouch(float touchX, float touchY) {
		if (touchX >= (this.getX() - 4 * this.getWidth()) && touchX <= (this.getX() + 5 * this.getWidth())
				&& touchY >= (this.getY() - 4 * this.getHeight()) && touchY <= (this.getY() + 5 * this.getHeight())) {
			setPrepare(true);
			return true;
		}
		return false;
	}

	public void dragg(float touchX, float touchY) {
		angle = MathUtils.atan2((touchY - this.getY()), (touchX - this.getX()));
		if (angle < 0)
			angle = 0;
		else if (angle > 1.5f)
			angle = 1.5f;
	}

	public void setPrepare(boolean isPrepare) {
		this.isPrepare = isPrepare;
	}

	public float getAngle() {
		return angle;
	}

	public boolean getPrepare() {
		return isPrepare;
	}
}
