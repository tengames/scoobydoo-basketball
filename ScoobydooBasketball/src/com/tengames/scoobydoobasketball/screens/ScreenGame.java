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

import woodyx.basicapi.camera.XCamera;
import woodyx.basicapi.physics.BoxUtility;
import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.screen.XScreen;
import woodyx.basicapi.sound.SoundManager;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.tengames.scoobydoobasketball.interfaces.GlobalVariables;
import com.tengames.scoobydoobasketball.main.Assets;
import com.tengames.scoobydoobasketball.main.ScoobydooBasketball;
import com.tengames.scoobydoobasketball.objects.Ball;
import com.tengames.scoobydoobasketball.objects.Basket;
import com.tengames.scoobydoobasketball.objects.DynamicButton;
import com.tengames.scoobydoobasketball.objects.DynamicDialog;
import com.tengames.scoobydoobasketball.objects.Ghost;
import com.tengames.scoobydoobasketball.objects.PowerController;
import com.tengames.scoobydoobasketball.objects.ScoobyDoo;
import com.tengames.scoobydoobasketball.objects.ScoreTable;
import com.tengames.scoobydoobasketball.objects.StrengthBar;
import com.tengames.scoobydoobasketball.objects.Zombie;

public class ScreenGame extends XScreen implements Screen, InputProcessor {
	public static final byte STATE_NULL = 0;
	public static final byte STATE_WIN = 1;
	public static final byte STATE_FAIL = 2;

	private ScoobydooBasketball coreGame;

	// actors
	private DynamicButton[] buttons;
	private ScoreTable scoreTable;
	private StrengthBar strengthBar;
	private PowerController powerController;
	private DynamicDialog dialog;

	// world
	private World world;
	private ScoobyDoo scooby;
	private Ball ball;
	private Basket basket;
	private Zombie zombie;
	private Ghost ghost;

	private TweenManager tween;

	private float timeReset, timeCreateBall, timeZombie, timeGhost, timeHelp;

	private byte state;

	private boolean canCreateDialog;

	public ScreenGame(ScoobydooBasketball coreGame, Stage stage, Array<SpriteBatch> batchs, Array<XCamera> cameras) {
		super(stage, batchs, cameras);
		this.coreGame = coreGame;
		initialize(stage);
	}

	private void initialize(Stage stage) {
		// reset stage
		resetStage();
		// initialize params
		initializeParams();
		// create actors
		createActors();
		// create world
		createWorld();
		// check collision
		checkCollision();
		// start debug
//		startDebugBox(0);
	}

	private void initializeParams() {
		// read data
		SoundManager.SOUND_ENABLE = coreGame.androidListener.getSound();
		SoundManager.MUSIC_ENABLE = coreGame.androidListener.getSound();

		// play music
		SoundManager.playMusic(Assets.muGame, 1, true);

		// set input
		InputMultiplexer input = new InputMultiplexer(stage, this);
		Gdx.input.setInputProcessor(input);
		// create tween
		tween = new TweenManager();
		// intialize
		state = STATE_NULL;
		timeReset = -1;
		timeCreateBall = -1;
		timeZombie = 0;
		timeGhost = 0;
		timeHelp = 0;
		canCreateDialog = true;
	}

	private void createActors() {
		/**
		 * create buttons
		 */
		buttons = new DynamicButton[2];

		// button exit
		buttons[0] = new DynamicButton(Assets.taObjects.findRegion("bt-exit"), tween, new Vector2(50, 600),
				new Vector2(50, 430), 10f);
		buttons[0].addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SoundManager.playeSound(Assets.soClick);

				// show admob
				if (MathUtils.random(100) > 50)
					coreGame.androidListener.showIntertitial();

				// TODO: back to menu
				coreGame.setScreen(new ScreenMenu(coreGame, stage, batchs, cameras));
			}
		});

		// button sound
		buttons[1] = new DynamicButton(Assets.taObjects.findRegion("bt-sound-on"),
				Assets.taObjects.findRegion("bt-sound-off"), tween, new Vector2(750, 600), new Vector2(750, 430), 15f);
		buttons[1].setChecked(!SoundManager.SOUND_ENABLE);
		buttons[1].addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// change state
				SoundManager.SOUND_ENABLE = !SoundManager.SOUND_ENABLE;
				SoundManager.MUSIC_ENABLE = !SoundManager.MUSIC_ENABLE;

				// play music
				if (SoundManager.MUSIC_ENABLE)
					SoundManager.playMusic(Assets.muGame, 1, true);
				else
					SoundManager.pauseMusic(Assets.muGame);

				// save data
				coreGame.androidListener.setSound(SoundManager.SOUND_ENABLE);
			}
		});

		// score table
		scoreTable = new ScoreTable(tween, 20f, new Vector2(160, 230), 25);

		// strength bar
		strengthBar = new StrengthBar(tween, 20f, new Vector2(-200, 100), new Vector2(20, 100), 2.5f);

		// add to stage
		stage.addActor(buttons[0]);
		stage.addActor(buttons[1]);
		stage.addActor(scoreTable);
		stage.addActor(strengthBar);
	}

	private void createWorld() {
		// initialize world
		world = new World(GlobalVariables.GRAVITY, true);

		// create ground
		@SuppressWarnings("unused")
		ObjectModel mdGround = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON, new Vector2(800, 50), 0,
				new Vector2(0, 0), 0, 100, 0.2f, 0.1f, GlobalVariables.CATEGORY_SCENE, GlobalVariables.MASK_SCENE,
				"ground");

		// create scooby
		scooby = new ScoobyDoo(world, Assets.taObjects.findRegion("scoo-wait"), -100, 50, 0.6f, state, true);
		scooby.moveTo(state, 300);

		// create basket
		basket = new Basket(world, Assets.taObjects.findRegion("basket-1"), 600, 250, true);

		// create zombie
		zombie = new Zombie(tween, 1000, 50, 94, 119, true);

		// create ghost
		ghost = new Ghost(world, Assets.taObjects.findRegion("ghost-1"), 500, 250, 0.6f, true);

		// create power controller
		powerController = new PowerController(tween, Assets.taObjects.findRegion("power-control"), -100, -100, 0, true);
	}

	private void createBall(float x, float y, float power, float angle) {
		// play sound
		SoundManager.playeSound(Assets.soKick);
		// created
		ball = new Ball(world, Assets.taObjects.findRegion("ball"), BoxUtility.ConvertToWorld(x),
				BoxUtility.ConvertToWorld(y), 0.6f, power, angle, true);
	}

	@Override
	public void update(float deltaTime) {
		// update camera
		projectBatchs();
		updateCameras(deltaTime);
		// check finish
		checkFinish(deltaTime);
		// update actors
		updateActors(deltaTime);
		// update scooby
		updateScooby(deltaTime);
		// update ball
		updateBall(deltaTime);
		// update basket
		updateBasket(deltaTime);
		// update zombie
		if (state == STATE_NULL)
			updateZombie(deltaTime);
		// update ghost
		if (state == STATE_NULL)
			updateGhost(deltaTime);
		// update power controller
		updatePowerController(deltaTime);
	}

	private void checkFinish(float deltaTime) {
		if (scoreTable.getLancers() <= 0) {
			// check win
			if (scoreTable.getPoint() >= 20) {
				if (state == STATE_NULL) {
					state = STATE_WIN;
				}
			} else {
				// check fail
				if (state == STATE_NULL) {
					state = STATE_FAIL;
				}
			}
		}

		// check condition create dialog
		if (scooby.getFinish()) {
			if (canCreateDialog) {
				createDialog(state);
				canCreateDialog = false;
			}
		}
	}

	private void createDialog(byte state) {
		// play sound
		switch (state) {
		case STATE_FAIL:
			SoundManager.playeSound(Assets.soFail);
			break;
		case STATE_WIN:
			SoundManager.playeSound(Assets.soWin);
			break;
		default:
			break;
		}

		// add dark
		Image imgDark = new Image(Assets.taObjects.findRegion("dark"));
		imgDark.setBounds(0, 0, 800, 480);
		stage.addActor(imgDark);

		// trace
		if (scoreTable.getPoint() < 10) {
			coreGame.androidListener.traceScene("0" + scoreTable.getPoint());
		} else {
			coreGame.androidListener.traceScene(scoreTable.getPoint() + "");
		}

		// save hscore
		coreGame.androidListener.saveHscore(scoreTable.getPoint() * 10);

		// create dialog
		dialog = new DynamicDialog(tween, state, scoreTable.getPoint(), new Vector2(400, 600));
		stage.addActor(dialog);

		// check button
		dialog.getBtMenu().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SoundManager.playeSound(Assets.soClick);

				// show admob
				coreGame.androidListener.showIntertitial();

				// TODO: back to menu
				coreGame.setScreen(new ScreenMenu(coreGame, stage, batchs, cameras));
			}
		});

		dialog.getBtReplay().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SoundManager.playeSound(Assets.soClick);

				// show admob
				coreGame.androidListener.showIntertitial();

				// TODO: replay game
				coreGame.setScreen(new ScreenGame(coreGame, stage, batchs, cameras));
			}
		});

	}

	private void updateActors(float deltaTime) {
		// update buttons
		for (DynamicButton button : buttons) {
			button.update(deltaTime);
		}
		// update score table
		scoreTable.update(deltaTime);
		// update strength bar
		strengthBar.update(deltaTime);
		// update dialog
		if (dialog != null)
			dialog.update(deltaTime);
		// update time help
		if (timeHelp >= 0)
			timeHelp += deltaTime;
		if (timeHelp >= 16)
			timeHelp = -1;
	}

	private void updateScooby(float deltaTime) {
		// update scooby
		scooby.update(deltaTime);
		// check conditions
		if (scooby.getReady()) {
			// appear power controller, set prepare
			powerController.appear(scooby.getX() + scooby.getWidth() * 0.75f,
					scooby.getY() + scooby.getHeight() * 0.75f);
			powerController.setPrepare(true);
			// update strength bar
			strengthBar.setCanUpdate(true);
			// turn off flag
			scooby.setReady(false);
		}
	}

	private void updatePowerController(float deltaTime) {
		powerController.update(deltaTime);
	}

	private void updateBall(float deltaTime) {
		// set time delay
		if (timeCreateBall >= 0)
			timeCreateBall += deltaTime;
		if (timeCreateBall >= 0.45f) {
			// create ball
			createBall(powerController.getX(), powerController.getY(), strengthBar.getValue(),
					powerController.getAngle());
			// decrease lancers
			scoreTable.decreaseLancers(tween);
			// turn off
			timeCreateBall = -1;
		}
		if (ball != null) {
			ball.update(deltaTime);
			// check reset game
			if (ball.getY() <= BoxUtility.ConvertToBox(60)) {
				// delay time
				if (timeReset == -1)
					timeReset = 0;
			}
		}
		// set time delay reset game
		if (timeReset >= 0)
			timeReset += deltaTime;
		if (timeReset >= 4) {
			// reset ball
			ball.dispose(world);
			ball = null;
			// move scooby
			scooby.moveTo(state, 100 + MathUtils.random(300));
			// turn off
			timeReset = -1;
		}
	}

	private void updateBasket(float deltaTime) {
		basket.update(deltaTime);
	}

	private void updateZombie(float deltaTime) {
		zombie.update(deltaTime);
		// check appear conditions
		if (scoreTable.getLancers() <= 20) {
			timeZombie += deltaTime;
			if (timeZombie >= 20) {
				// appear
				zombie.appear(basket, (byte) (1 + MathUtils.random(3)));
				timeZombie = 0;
			}
		}
	}

	private void updateGhost(float deltaTime) {
		ghost.update(deltaTime);
		// check appear condition
		if (scoreTable.getLancers() <= 10) {
			timeGhost += deltaTime;
			if (timeGhost >= 10) {
				// appear
				ghost.appear(tween, new Vector2(scooby.getX() + BoxUtility.ConvertToBox(MathUtils.random(200)),
						scooby.getY() + BoxUtility.ConvertToBox(MathUtils.random(200))));
				timeGhost = 0;
			}
		}
	}

	@Override
	public void draw() {
		renderBackground();
		if (!scooby.getFinish())
			renderStage();
		renderObjects();
		if (scooby.getFinish())
			renderStage();
//		renderDebug(world, 0);
	}

	private void renderBackground() {
		bgDrawable(0, true);
		getSpriteBatch(0).draw(Assets.txBgGame, 0, 0, 800 * BoxUtility.WORLD_TO_BOX, 480 * BoxUtility.WORLD_TO_BOX);
		bgDrawable(0, false);
	}

	private void renderObjects() {
		objDrawable(0, true);
		renderHelp();
		scooby.render(getSpriteBatch(0));
		if (state == STATE_NULL)
			ghost.render(getSpriteBatch(0));
		basket.renderHanger(getSpriteBatch(0));
		if (ball != null)
			ball.render(getSpriteBatch(0));
		basket.renderBasket(getSpriteBatch(0));
		powerController.render(getSpriteBatch(0));
		getSpriteBatch(0).draw(Assets.taObjects.findRegion("ground"), BoxUtility.ConvertToBox(800 - 244), 0,
				BoxUtility.ConvertToBox(244), BoxUtility.ConvertToBox(50));
		if (state == STATE_NULL)
			zombie.render(getSpriteBatch(0));
		objDrawable(0, false);
	}

	private void renderHelp() {
		if (timeHelp >= 8) {
			getSpriteBatch(0).draw(Assets.taObjects.findRegion("help-1"), BoxUtility.ConvertToBox(400),
					BoxUtility.ConvertToBox(200), BoxUtility.ConvertToBox(210), BoxUtility.ConvertToBox(147));
			getSpriteBatch(0).draw(Assets.taObjects.findRegion("help-2"), BoxUtility.ConvertToBox(80),
					BoxUtility.ConvertToBox(60), BoxUtility.ConvertToBox(210), BoxUtility.ConvertToBox(147));
		}
	}

	@Override
	public void render(float deltaTime) {
		update(deltaTime);
		updateWorld(world, deltaTime);
		clearScreen();
		clearWorld(world);
		draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// play music
		SoundManager.playMusic(Assets.muGame, 1, true);
	}

	@Override
	public void hide() {
		// pause music
		SoundManager.pauseMusic(Assets.muGame);
	}

	@Override
	public void pause() {
		// pause music
		SoundManager.pauseMusic(Assets.muGame);
	}

	@Override
	public void resume() {
		// play music
		SoundManager.playMusic(Assets.muGame, 1, true);
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
			SoundManager.playeSound(Assets.soClick);

			if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
				// show admob
				if (MathUtils.random(100) > 50)
					coreGame.androidListener.showIntertitial();

				// TODO: back to menu
				coreGame.setScreen(new ScreenMenu(coreGame, stage, batchs, cameras));
			} else {
				coreGame.androidListener.showToast("Tap back button in order to go to Menu !");
			}
			mBackPressed = System.currentTimeMillis();
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	Vector3 point = new Vector3();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		getCamera(0).unproject(point.set(screenX, screenY, 0));
		// get touch
		if (powerController.getPrepare())
			powerController.getTouch(point.x, point.y);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// disappear power controller
		if (powerController.getPrepare()) {
			// disappear power controller
			powerController.disappear();
			// stop update strength bar
			strengthBar.setCanUpdate(false);
			// reset stateTime
			scooby.stateTime = 0;
			// change action
			scooby.setAction(ScoobyDoo.JUMP);
			// set time create ball
			timeCreateBall = 0;
			// turn off flag
			powerController.setPrepare(false);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		getCamera(0).unproject(point.set(screenX, screenY, 0));
		// dragg
		if (powerController.getPrepare())
			powerController.dragg(point.x, point.y);
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

	private void checkCollision() {
		world.setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void beginContact(Contact contact) {
				// check scooby collide with ball
				if (BoxUtility.detectCollision(contact, "scoobydoo", "ball")) {
					// set action fall
					scooby.setAction(ScoobyDoo.FALL);
				}
				// check ball collide basket
				if (BoxUtility.detectCollision(contact, "ball", "basket")) {
					// set animation
					basket.setAnimation();
				}
				// check goal
				if (BoxUtility.detectCollision(contact, "ball", "sensor")) {
					// play sound
					SoundManager.playeSound(Assets.soGoal);
					// increase point
					scoreTable.increasePoint(tween);
				}
			}
		});
	}

}
