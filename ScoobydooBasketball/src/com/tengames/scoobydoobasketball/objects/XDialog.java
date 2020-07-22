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
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.TweenPaths;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class XDialog extends Group {
	public static final byte TYPE_0 = 0;
	public static final byte TYPE_1 = 1;

	protected Dialog dialog;
	protected TweenManager tweenManager;

	private boolean isFinishInit;
	private boolean isFinishDispose;

	/**
	 * Normal dialog
	 * 
	 * @param windowStyle
	 * @param trBackground
	 * @param size
	 * @param position
	 * @param target
	 * @param type
	 */
	public XDialog(WindowStyle windowStyle, TextureRegion trBackground, Vector2 size, Vector2 position, Vector2 target,
			int type) {
		// initialize params
		if (tweenManager == null)
			tweenManager = new TweenManager();
		isFinishDispose = false;
		isFinishInit = false;

		// set attributes
		this.setSize(size.x, size.y);
		this.setPosition(position.x - size.x / 2, position.y - size.y / 2);
		this.setOrigin(size.x / 2, size.y / 2);

		// initialize objects
		dialog = new Dialog("", windowStyle);
		dialog.setTouchable(Touchable.disabled);
		dialog.setBackground(new TextureRegionDrawable(trBackground));
		dialog.setSize(size.x, size.y);
		dialog.setPosition(0, 0);

		this.addActor(dialog);

		appear(type, target);

	}

	public boolean getFinishInit() {
		return isFinishInit;
	}

	public boolean getFinishDispose() {
		return isFinishDispose;
	}

	public void update(float deltaTime) {
		tweenManager.update(deltaTime);
	}

	public void dispose(int type) {
		switch (type) {
		case TYPE_0:
			Tween.to(this, ActorAccessor.SCALE_XY, 0.25f).target(1, 0).path(TweenPaths.catmullRom).ease(Quad.INOUT)
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int arg0, BaseTween<?> arg1) {
							isFinishDispose = true;
						}
					}).start(tweenManager);
			break;

		case TYPE_1:
			Tween.to(this, ActorAccessor.OPACITY, 0.5f).target(0).path(TweenPaths.catmullRom).ease(Quad.INOUT)
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int arg0, BaseTween<?> arg1) {
							isFinishDispose = true;
						}
					}).start(tweenManager);
			break;

		default:
			break;
		}
	}

	private void appear(int type, Vector2 target) {
		switch (type) {
		case TYPE_0:
			Tween.to(this, ActorAccessor.CPOS_XY, 0.5f).target(target.x, target.y).waypoint(target.x, target.y)
					.path(TweenPaths.catmullRom).ease(Back.OUT).setCallback(new TweenCallback() {
						@Override
						public void onEvent(int arg0, BaseTween<?> arg1) {
							isFinishInit = true;
						}
					}).start(tweenManager);
			break;

		case TYPE_1:
			Timeline.createSequence().push(Tween.set(this, ActorAccessor.OPACITY).target(0))
					.push(Tween.set(this, ActorAccessor.SCALE_XY).target(1, 0)).beginParallel()
					.push(Tween.to(this, ActorAccessor.OPACITY, 0.25f).target(1))
					.push(Tween.to(this, ActorAccessor.SCALE_XY, 0.25f).target(1, 1).path(TweenPaths.catmullRom)
							.ease(Quad.INOUT))
					.end().setCallback(new TweenCallback() {
						@Override
						public void onEvent(int arg0, BaseTween<?> arg1) {
							isFinishInit = true;
						}
					}).start(tweenManager);
			break;

		default:
			break;
		}
	}
}
