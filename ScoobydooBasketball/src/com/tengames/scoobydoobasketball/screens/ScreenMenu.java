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
package com.tengames.scoobydoobasketball.screens;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import woodyx.basicapi.camera.XCamera;
import woodyx.basicapi.screen.XScreen;
import woodyx.basicapi.sound.SoundManager;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.tengames.scoobydoobasketball.interfaces.GlobalVariables;
import com.tengames.scoobydoobasketball.json.JSONObject;
import com.tengames.scoobydoobasketball.main.Assets;
import com.tengames.scoobydoobasketball.main.ScoobydooBasketball;
import com.tengames.scoobydoobasketball.objects.DynamicButton;
import com.tengames.scoobydoobasketball.objects.DynamicDialogHScore;
import com.tengames.scoobydoobasketball.objects.Presentation;

@SuppressWarnings("deprecation")
public class ScreenMenu extends XScreen implements Screen, InputProcessor {
	private static final boolean IS_ABOUT = false; // bt about enable

	private ScoobydooBasketball coreGame;
	private Presentation[] presens;
	private DynamicButton[] buttons;
	private TweenManager tween;

	private DynamicDialogHScore dialog;

	public ScreenMenu(ScoobydooBasketball coreGame, Stage stage, Array<SpriteBatch> batchs, Array<XCamera> cameras) {
		super(stage, batchs, cameras);
		this.coreGame = coreGame;
		initialize();
	}

	private void initialize() {
		// read data
		SoundManager.SOUND_ENABLE = coreGame.androidListener.getSound();
		SoundManager.MUSIC_ENABLE = coreGame.androidListener.getSound();

		// play music
		SoundManager.playMusic(Assets.muMenu, 1, true);

		// reset stage
		resetStage();

		// set input
		InputMultiplexer input = new InputMultiplexer(stage, this);
		Gdx.input.setInputProcessor(input);
		Gdx.input.setCatchBackKey(true);

		// create tween
		tween = new TweenManager();

		// create background
		Image imgBackground = new Image(Assets.txBgMenu);
		imgBackground.setPosition(0, 0);
		stage.addActor(imgBackground);

		// create actors
		presens = new Presentation[3];
		presens[1] = new Presentation(tween, Assets.taObjects.findRegion("logo-1"), 10, 400, 340);
		presens[2] = new Presentation(tween, Assets.taObjects.findRegion("logo-2"), 15f, 400, 280);
		presens[0] = new Presentation(tween, Assets.taObjects.findRegion("scoobydoo-menu"), 20f, new Vector2(-200, 50),
				new Vector2(60, 20));
		for (Presentation presen : presens) {
			stage.addActor(presen);
		}

		/**
		 * create buttons
		 */
		buttons = new DynamicButton[4];

		// button sound
		buttons[0] = new DynamicButton(Assets.taObjects.findRegion("bt-sound-on"),
				Assets.taObjects.findRegion("bt-sound-off"), tween, new Vector2(750, 600), new Vector2(750, 430), 25);
		buttons[0].setChecked(!SoundManager.SOUND_ENABLE);
		buttons[0].addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// change state
				SoundManager.SOUND_ENABLE = !SoundManager.SOUND_ENABLE;
				SoundManager.MUSIC_ENABLE = !SoundManager.MUSIC_ENABLE;

				// play music
				if (SoundManager.MUSIC_ENABLE)
					SoundManager.playMusic(Assets.muMenu, 1, true);
				else
					SoundManager.pauseMusic(Assets.muMenu);

				// save data
				coreGame.androidListener.setSound(SoundManager.SOUND_ENABLE);
			}
		});

		TextureRegion trTemp = null;
		if (IS_ABOUT) {
			trTemp = Assets.taObjects.findRegion("bt-more");
		} else {
			trTemp = Assets.taObjects.findRegion("bt-more");
		}
		// button more
		buttons[1] = new DynamicButton(trTemp, tween, new Vector2(500, 600), new Vector2(500, 100), 30f);
		buttons[1].addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO: About us!
				SoundManager.playeSound(Assets.soClick);
				// TODO: go to store
				coreGame.androidListener.gotoStore(GlobalVariables.STORE_URL);
			}
		});

		// button play
		buttons[2] = new DynamicButton(Assets.taObjects.findRegion("bt-play"), tween, new Vector2(650, 600),
				new Vector2(650, 200), 35f);
		buttons[2].addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SoundManager.playeSound(Assets.soClick);
				// TODO: play game
				coreGame.setScreen(new ScreenGame(coreGame, stage, batchs, cameras));
			}
		});

		// button highscore
		buttons[3] = new DynamicButton(Assets.taObjects.findRegion("bt-noads"), tween, new Vector2(700, 600),
				new Vector2(700, 50), 40f);
		buttons[3].addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO: more game
				SoundManager.playeSound(Assets.soClick);

				// show dialog
				final Image imgDark = new Image(Assets.taObjects.findRegion("dark"));
				imgDark.setSize(800, 480);
				imgDark.setPosition(0, 0);

				WindowStyle windowStyle = new WindowStyle();
				windowStyle.titleFont = Assets.fScore;
				dialog = new DynamicDialogHScore(windowStyle, Assets.taObjects.findRegion("dialog"),
						new Vector2(374, 292), new Vector2(400, 900), new Vector2(400, 240),
						coreGame.androidListener.getHscore());

				dialog.getBtSubmit().addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
//						 play sound
						SoundManager.playSound(Assets.soClick, 1);

						// remove
						imgDark.remove();
						dialog.remove();
						dialog = null;

						// TODO: highscore
						try {
							sendRequest();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

				dialog.getBtExit().addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
//						 play sound
						SoundManager.playSound(Assets.soClick, 1);

						// remove
						imgDark.remove();
						dialog.remove();
						dialog = null;
					}
				});

				// add to stage
				stage.addActor(imgDark);
				stage.addActor(dialog);
			}
		});

		for (DynamicButton button : buttons) {
			stage.addActor(button);
		}
	}

	// HTTP GET request
	@SuppressWarnings({ "resource", "unused" })
	private void sendRequest() throws Exception {
		coreGame.androidListener.showLoading(true);

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(getHighScoreReq(coreGame.androidListener.getDeviceId(), "5443632811212800",
				"Scoobydoo%20Basketball", coreGame.androidListener.getHscore(), ""));

		// add request header
		request.addHeader("User-Agent", "Android");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		coreGame.androidListener.showLoading(false);

		// show result and code
		if (response == null) {
			coreGame.androidListener.showToast("Network Error !");
		}

		if (response.getStatusLine().getStatusCode() == 600) { // error code
			coreGame.androidListener.showDialog(result.toString());
		} else if (response.getStatusLine().getStatusCode() == 200) { // valid code
			JSONObject json = new JSONObject(result.toString());

			if (!json.getString(GlobalVariables.RES_INFORM).equals("")) {

			}

			if (!json.getString(GlobalVariables.RES_BONUS).equals("")) {

			}

			if (!json.getString(GlobalVariables.RES_MESSAGE).equals("")) {

			}

			if (!json.getString(GlobalVariables.RES_LINK).equals("")) {
				coreGame.androidListener.gotoHighscore(json.getString(GlobalVariables.RES_LINK));
			}

		}

	}

	private String getHighScoreReq(String deviceId, String gameId, String gameName, int score, String params) {
		return (GlobalVariables.HSCORE_URL + "?" + GlobalVariables.REQ_DEVICEID + "=" + deviceId + "&"
				+ GlobalVariables.REQ_GAMEID + "=" + gameId + "&" + GlobalVariables.REQ_GAMENAME + "=" + gameName + "&"
				+ GlobalVariables.REQ_DEVICETYPE + "=" + GlobalVariables.DEV_ADR + "&" + GlobalVariables.REQ_SCORE + "="
				+ score + "&" + GlobalVariables.REQ_PARAMS + "=" + params);
	}

	@Override
	public void update(float deltaTime) {
		// update actor
		for (Presentation presen : presens) {
			presen.update(deltaTime);
		}
		for (DynamicButton button : buttons) {
			button.update(deltaTime);
		}
		// update dialog
		if (dialog != null)
			dialog.update(deltaTime);
	}

	@Override
	public void draw() {
		renderStage();
	}

	@Override
	public void render(float deltaTime) {
		update(deltaTime);
		clearScreen();
		draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// play music
		SoundManager.playMusic(Assets.muMenu, 1, true);
	}

	@Override
	public void hide() {
		// pause music
		SoundManager.pauseMusic(Assets.muMenu);
	}

	@Override
	public void pause() {
		// pause music
		SoundManager.pauseMusic(Assets.muMenu);
	}

	@Override
	public void resume() {
		// play music
		SoundManager.playMusic(Assets.muMenu, 1, true);
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
	private long mBackPressed;

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.BACK) {
			// play sound
			SoundManager.playSound(Assets.soClick, 1);

			if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
				// exit game
				System.exit(0);
			} else {
				coreGame.androidListener.showToast("Tap back button in order to exit !");
			}

			mBackPressed = System.currentTimeMillis();
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
