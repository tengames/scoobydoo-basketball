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
import aurelienribon.tweenengine.equations.Bounce;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DynamicButton extends Button {
	private TweenManager tween;

	// for normal button
	public DynamicButton(TextureRegion region, TweenManager tween, Vector2 position, Vector2 target, float duration) {
		super(new TextureRegionDrawable(region));
		// initialize
		this.tween = tween;
		this.setPosition(position.x, position.y);
		// create tween
		Tween.to(this, ActorAccessor.CPOS_XY, duration).target(target.x, target.y).ease(Bounce.OUT).start(tween);
	}

	// for sound button
	public DynamicButton(TextureRegion up, TextureRegion down, TweenManager tween, Vector2 position, Vector2 target,
			float duration) {
		super(new TextureRegionDrawable(up), new TextureRegionDrawable(down), new TextureRegionDrawable(down));
		// initialize
		this.tween = tween;
		this.setPosition(position.x, position.y);
		// create tween
		Tween.to(this, ActorAccessor.CPOS_XY, duration).target(target.x, target.y).ease(Bounce.OUT).start(tween);
	}

	public void update(float deltaTime) {
		tween.update(deltaTime);
	}

}
