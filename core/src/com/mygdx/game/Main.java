package com.mygdx.game;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


import static com.mygdx.game.Utils.*;

public class Main extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture background;
    private Texture city;
    private Texture stage;
    private Texture over;

    private Player goku;
    private Vegeta vegeta;
    private Open_Player player;
	static Utils utils;
    static WorldCreator worldcreator;
	int mx, my, speed = 5000;
	static String mode;

	public static final int Attack = 0, Kick = 1;
	public static final float PPM = 0.3f;
	public static final int Tile = 20;
    public static int movesg;
    public static int movesv =2;
    static boolean animation;
	static String Game = "Menu", Mode; // this is a String that will determine what the current mode is(main menu, level, etc.)
    static Rectangle rect;
    public static OrthographicCamera camera;
    public static final int UP = 0, Down = 1, Left = 2, Right = 3;
    static boolean animation1;
    public static int moves1;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    Box2DDebugRenderer b2dr;
    public static World world;


	@Override
	public void create () {
        world = new World(new Vector2(0,0),true);
        batch = new SpriteBatch();
		goku = new Player(200,200);
		vegeta = new Vegeta(600,100);
		utils = new Utils();
		player = new Open_Player();

		background = new Texture("Assets/Backgrounds/Mainmenu.png");
		city = new Texture("Assets/Backgrounds/city.png");
		stage = new Texture("Assets/Backgrounds/stage.png");
        over = new Texture("Assets/Backgrounds/gameover.png");

        b2dr = new Box2DDebugRenderer();

        camera = new OrthographicCamera(366f,220f);

        mapLoader = new TmxMapLoader();

        map = mapLoader.load("Assets/Maps/World map.tmx");

        renderer = new OrthogonalTiledMapRenderer(map, PPM);

        worldcreator = new WorldCreator(world,map);

        world.setContactListener(new WorldContactListener());

	}

	@Override
	public void render () {
        if (Game.equals("Menu")) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            utils.music.play();
            mx = Gdx.input.getX();
            my = Math.abs(660 - Gdx.input.getY());
            rect = new Rectangle(mx,my,3,3); // mouse rect made for collision (1 by 1 square)
            if(Utils.Font.getBoundingRectangle().overlaps(rect)){
                Utils.choice = 1;
            }
            else{
                Utils.choice = 0;
            }
            batch.begin();
            batch.draw(background, 0, 0);
            utils.update(batch,480,265);
            batch.end();
            if (Font.getBoundingRectangle().overlaps(rect) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
                utils.music.stop();
                Game = "Level1";
                mode = "open";

            }
        }

        if (player.body.getPosition().x >513 && player.body.getPosition().y > 327 && mode != "gameover" && Player.fstat[0] >0 ) {
            utils.worldmusic.stop();
            mode = "battle";
            battle.battle();
            rect  = new Rectangle(200,200,20,20);
            batch.begin();
            batch.draw(stage,0,0);
            goku.update(batch,600,200);
            vegeta.update(batch,600,300);
            Utils.attacks.draw(batch);
            batch.end();
        }

        if (Player.fstat[0] <0 && mode != "open"){
            utils.bossbattle.stop();
            utils.victory.play();

            mode = "win";

            batch.begin();
            batch.draw(over, 0, 0);
            rect = new Rectangle (360,50,20,20);
            Utils.contin.draw(batch);

            batch.end();
            if (contin.getBoundingRectangle().overlaps(rect) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            {
                mode = "open";
            }


        }

        if (Game.equals("Level1")&& mode.equals ("open")) {
            camera.zoom = PPM;
            //Rendering the map
            world.step(1/60f,6,2);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            camera.update();

            renderer.setView(camera);
            renderer.render();
            batch.setProjectionMatrix(camera.combined);
            b2dr.render(world,camera.combined);

            System.out.println("[" + (int)Open_Player.Goku.getX() + "," + (int)Open_Player.Goku.getY() + "]" + ", " + "[" + (int)player.body.getPosition().x + "," + (int)player.body.getPosition().y + "]");

            utils.worldmusic.play();

            mx = Gdx.input.getX();
            my = Gdx.input.getY();

            batch.begin();
            update();
            batch.end();
            move();
        }
    }

    public void update(){
        player.update(batch);
    }

    public void move(){
        player.body.setLinearVelocity(0,0);

        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            moves1 = UP;
            player.body.applyLinearImpulse(new Vector2(0,speed*5),player.body.getWorldCenter(),true);
            animation1 = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            moves1 = Down;
            player.body.applyLinearImpulse(new Vector2(0,-speed*5),player.body.getWorldCenter(),true);
            animation1 = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            moves1 = Left;
            player.body.applyLinearImpulse(new Vector2(-speed*5,0),player.body.getWorldCenter(),true);
            animation1 = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            moves1 = Right;
            player.body.applyLinearImpulse(new Vector2(speed*5,0),player.body.getWorldCenter(),true);
            animation1 = true;
        }
        else{
            player.body.applyLinearImpulse(new Vector2(player.body.getLinearVelocity().x * -1, player.body.getLinearVelocity().y * -1), player.body.getWorldCenter(), true);
            animation1 = false;
            player.frames = 0;
        }

        player.setX(player.body.getPosition().x);
        player.setY(player.body.getPosition().y);

        camera.position.x = player.getX();
        camera.position.y = player.getY();
    }

	@Override
	public void dispose (){
		batch.dispose();
		utils.music.dispose();
		renderer.dispose();
	}
}
