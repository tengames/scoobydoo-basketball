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
import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.sprite.ObjectSprite;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.scoobydoobasketball.interfaces.GlobalVariables;
import com.tengames.scoobydoobasketball.main.Assets;

public class Ghost extends ObjectSprite {
	private static final byte VELOCITY = 20;

	private ObjectModel model;
	private Animation animation;
	private Vector2 target, oldPosition;
	private float stateTime;
	private boolean isVisible, isVertical, isHorizon;

	public Ghost(World world, TextureRegion textureRegion, float x, float y, float scale, boolean isInBox) {
		super(textureRegion, x, y, textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale,
				isInBox);
		// initialize
		stateTime = 0;
		isVisible = false;
		isVertical = false;
		isHorizon = false;

		// create model
		model = new ObjectModel(world, ObjectModel.KINEMATIC, ObjectModel.POLYGON,
				new Vector2(this.getOriginSize().x / 2, this.getOriginSize().y / 2), 0, new Vector2(x, y), 0, 5, 0.2f,
				1f, GlobalVariables.CATEGORY_GHOST, GlobalVariables.MASK_GHOST, "ghost");

		// deactive model
		model.getBody().setActive(false);

		// create animation
		TextureRegion[] trGhost = Assets.getKeyFrames(Assets.taObjects, "ghost-", 1, 5);
		animation = Assets.loadAnimation(0.1f, trGhost);
	}

	public void update(float deltaTime) {
		// update state time
		stateTime += deltaTime;

		// update follow
		this.updateFollowModel(model, 0, -this.getOriginSize().y / 4, true);

		// update move vertical
		if (isVertical) {
			if (oldPosition.y < target.y) {
				if (this.getY() < target.y) {
					model.getBody().setLinearVelocity(0, VELOCITY * deltaTime);
				} else {
					// move done
					isVertical = false;
				}
			} else {
				if (this.getY() > target.y) {
					model.getBody().setLinearVelocity(0, -VELOCITY * deltaTime);
				} else {
					// move done
					isVertical = false;
				}
			}
		}

		// update move horizon
		if (isHorizon) {
			if (oldPosition.x < target.x) {
				if (this.getX() < target.x) {
					model.getBody().setLinearVelocity(VELOCITY * deltaTime, 0);
				} else {
					// move done
					isHorizon = false;
				}
			} else {
				if (this.getX() > target.x) {
					model.getBody().setLinearVelocity(-VELOCITY * deltaTime, 0);
				} else {
					// move done
					isHorizon = false;
				}
			}
		}

		// stop move
		if (!isHorizon && !isVertical) {
			model.getBody().setLinearVelocity(0, 0);
		}
	}

	public void render(SpriteBatch batch) {
		if (isVisible) {
			TextureRegion keyFrame = null;
			keyFrame = animation.getKeyFrame(stateTime, true);
			this.renderKeyFrame(keyFrame, batch);
		}
	}

	public void appear(TweenManager tween, Vector2 target) {
		// set visible
		isVisible = true;

		// active model
		model.getBody().setActive(true);

		// move
		isVertical = true;
		isHorizon = true;

		// get old position, target
		this.oldPosition = this.getPosition();
		this.target = new Vector2(target.x, target.y);

		// create tween
		Timeline.createSequence().push(Tween.set(this, SpriteAccessor.OPACITY).target(0))
				.push(Tween.to(this, SpriteAccessor.OPACITY, 10).target(1).ease(Quad.OUT)).pushPause(10)
				.push(Tween.to(this, SpriteAccessor.OPACITY, 10).target(0).ease(Quad.IN))
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int arg0, BaseTween<?> arg1) {
						// invisible
						isVisible = false;

						// deactive
						model.getBody().setActive(false);
					}
				}).start(tween);
	}

}
