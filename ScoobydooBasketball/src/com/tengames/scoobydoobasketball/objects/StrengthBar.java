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

import woodyx.basicapi.accessor.ActorAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.tengames.scoobydoobasketball.main.Assets;

public class StrengthBar extends Group {
	private TweenManager tween;
	private Image imgStrengthBar, imgStrengths[];
	private float timeChange, countTime, value, oldTimeChange;
	private boolean canUpdate;

	public StrengthBar(TweenManager tween, float duration, Vector2 position, Vector2 target, float timeChange) {
		// initialize
		this.tween = tween;
		this.oldTimeChange = timeChange;
		this.timeChange = timeChange;
		countTime = 0;
		value = 0;
		canUpdate = false;

		// create images
		imgStrengthBar = new Image(Assets.taObjects.findRegion("strength-bar"));
		imgStrengthBar.setPosition(0, 0);

		this.setBounds(position.x, position.y, imgStrengthBar.getWidth(), imgStrengthBar.getHeight());

		imgStrengths = new Image[5];

		imgStrengths[0] = new Image(Assets.taObjects.findRegion("strength-1"));
		imgStrengths[0].setPosition(imgStrengths[0].getWidth() + 1, 5);

		imgStrengths[1] = new Image(Assets.taObjects.findRegion("strength-2"));
		imgStrengths[1].setPosition(imgStrengths[0].getWidth(), imgStrengths[0].getHeight() + 7);

		imgStrengths[2] = new Image(Assets.taObjects.findRegion("strength-3"));
		imgStrengths[2].setPosition(imgStrengths[0].getWidth() / 2 + 3,
				imgStrengths[0].getHeight() + imgStrengths[1].getHeight() + 7);

		imgStrengths[3] = new Image(Assets.taObjects.findRegion("strength-4"));
		imgStrengths[3].setPosition(imgStrengths[0].getWidth() / 2 + 2,
				imgStrengths[0].getHeight() + imgStrengths[1].getHeight() + imgStrengths[2].getHeight() + 7);

		imgStrengths[4] = new Image(Assets.taObjects.findRegion("strength-5"));
		imgStrengths[4].setPosition(imgStrengths[0].getWidth() / 4 + 3, imgStrengths[0].getHeight()
				+ imgStrengths[1].getHeight() + imgStrengths[2].getHeight() + imgStrengths[3].getHeight() + 8);

		// add to stage
		this.addActor(imgStrengthBar);
		for (Image imgStrength : imgStrengths) {
			if (imgStrength != null)
				this.addActor(imgStrength);
		}

		// create tween
		Tween.to(this, ActorAccessor.POS_XY, duration).target(target.x, target.y).ease(Quad.OUT).start(tween);
	}

	public void update(float deltaTime) {
		// update tween
		tween.update(deltaTime);
		// check count time, reset state
		if (countTime >= timeChange) {
			// reset count time
			countTime = 0;
			// reset strength
			for (Image imgStrength : imgStrengths) {
				if (imgStrength != null)
					imgStrength.setVisible(false);
			}
		}
		if (canUpdate)
			countTime += deltaTime;
		// check conditions
		if (countTime < timeChange / 5) {
			// set visible
			if (!imgStrengths[0].isVisible())
				imgStrengths[0].setVisible(true);
			// get value
			value = 2;
		} else if (countTime >= timeChange / 5 && countTime < timeChange * 2 / 5) {
			// set visible
			if (!imgStrengths[1].isVisible())
				imgStrengths[1].setVisible(true);
			// get value
			value = 4;
		} else if (countTime >= timeChange * 2 / 5 && countTime < timeChange * 3 / 5) {
			// set visible
			if (!imgStrengths[2].isVisible())
				imgStrengths[2].setVisible(true);
			// get value
			value = 6;
		} else if (countTime >= timeChange * 3 / 5 && countTime < timeChange * 4 / 5) {
			// set visible
			if (!imgStrengths[3].isVisible())
				imgStrengths[3].setVisible(true);
			// get value
			value = 7;
		} else {
			// set visible
			if (!imgStrengths[4].isVisible())
				imgStrengths[4].setVisible(true);
			// get value
			value = 9;
		}
	}

	public float getValue() {
		return value;
	}

	public void setTimeChange(float timeChange) {
		if (this.timeChange == oldTimeChange)
			this.timeChange = timeChange;
	}

	public void setCanUpdate(boolean canUpdate) {
		this.canUpdate = canUpdate;
	}

	public boolean getCanUpdate() {
		return canUpdate;
	}
}
