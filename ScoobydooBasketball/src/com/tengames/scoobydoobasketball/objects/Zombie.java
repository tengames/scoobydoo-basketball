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

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tengames.scoobydoobasketball.main.Assets;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quart;
import woodyx.basicapi.accessor.SpriteAccessor;
import woodyx.basicapi.physics.BoxUtility;
import woodyx.basicapi.sprite.ObjectSprite;

public class Zombie extends ObjectSprite {
	public static final byte SKILL_NULL = 0;
	public static final byte SKILL_UP = 1;
	public static final byte SKILL_DOWN = 2;
	public static final byte SKILL_LEFT = 3;
	public static final byte SKILL_RIGHT = 4;

	private Animation animation;
	private TweenManager tween;
	private float stateTime;
	private byte skill;

	public Zombie(TweenManager tween, float x, float y, float width, float height, boolean isInBox) {
		super(x, y, width, height, isInBox);
		// initialize
		this.tween = tween;
		this.stateTime = 0;

		// create animation
		TextureRegion[] trZombie = Assets.getKeyFrames(Assets.taObjects, "zoombie-", 1, 16);
		animation = Assets.loadAnimation(0.1f, trZombie);
	}

	public void appear(final Basket basket, final byte skill) {
		Timeline.createSequence()
				.push(Tween.set(this, SpriteAccessor.POS_XY).target(BoxUtility.ConvertToBox(1000), this.getY()))
				.push(Tween.set(this, SpriteAccessor.OPACITY).target(1))
				.push(Tween.to(this, SpriteAccessor.POS_XY, 30f).ease(Linear.INOUT).target(BoxUtility.ConvertToBox(700),
						this.getY()))
				.pushPause(10f).push(Tween.to(this, SpriteAccessor.OPACITY, 10f).ease(Quart.INOUT).target(0))
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int arg0, BaseTween<?> arg1) {
						// effect skill
						basket.affectSkills(skill);
					}
				}).start(tween);
	}

	public void update(float deltaTime) {
		// update state time
		stateTime += deltaTime;
		// update tween
		tween.update(deltaTime);
	}

	public void render(SpriteBatch batch) {
		TextureRegion keyFrame = null;
		keyFrame = animation.getKeyFrame(stateTime, true);
		this.renderKeyFrame(keyFrame, batch);
	}

	public byte getSkill() {
		return skill;
	}
}
