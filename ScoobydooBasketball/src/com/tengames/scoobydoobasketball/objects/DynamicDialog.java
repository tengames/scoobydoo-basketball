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
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.tengames.scoobydoobasketball.main.Assets;
import com.tengames.scoobydoobasketball.screens.ScreenGame;

public class DynamicDialog extends Group {
	private TweenManager tween;
	private Button btReplay, btMenu;
	private Label lbPoint;

	public DynamicDialog(TweenManager tween, byte type, int point, Vector2 position) {
		// get tween
		this.tween = tween;

		this.setPosition(position.x, position.y);

		// add gui
		Image imgTable = new Image(Assets.taObjects.findRegion("table"));
		imgTable.setPosition(0, 0);

		this.setSize(imgTable.getWidth(), imgTable.getHeight());

		Image imgText = null;
		switch (type) {
		case ScreenGame.STATE_WIN:
			imgText = new Image(Assets.taObjects.findRegion("text-win"));
			break;
		case ScreenGame.STATE_FAIL:
			imgText = new Image(Assets.taObjects.findRegion("text-fail"));
			break;
		default:
			break;
		}
		imgText.setPosition((this.getWidth() - imgText.getWidth()) / 2, this.getHeight());

		LabelStyle lbSPoint = new LabelStyle(Assets.fScore, Color.WHITE);
		lbPoint = new Label("POINT: " + point, lbSPoint);
		lbPoint.setPosition(this.getWidth() / 2 - 50, this.getHeight() / 2 - 20);

		btReplay = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-replay")));
		btReplay.setPosition(-btReplay.getWidth() / 2, -btReplay.getHeight() * 1.5f);

//		btShare = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-share")));
//		btShare.setPosition((this.getWidth()-btShare.getWidth())/2, -btShare.getHeight()/2);

		btMenu = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-menu")));
		btMenu.setPosition(this.getWidth() - btMenu.getWidth() / 2, -btMenu.getHeight() * 1.5f);

		// add
		this.addActor(imgTable);
		this.addActor(imgText);
		this.addActor(lbPoint);
//		this.addActor(btShare);
		this.addActor(btReplay);
		this.addActor(btMenu);

		// create tween
		Tween.to(this, ActorAccessor.POS_XY, 1f).target(position.x, 150).ease(Elastic.OUT).start(tween);
	}

	public void update(float deltaTime) {
		// update tween
		tween.update(deltaTime);
	}

//	public Button getBtShare() {
//		return btShare;
//	}

	public Button getBtMenu() {
		return btMenu;
	}

	public Button getBtReplay() {
		return btReplay;
	}
}
