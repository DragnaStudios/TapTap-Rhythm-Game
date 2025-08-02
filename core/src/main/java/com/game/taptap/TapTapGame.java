package com.game.taptap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.game.taptap.screens.PresentacionUniversidad;
import com.game.taptap.screens.MainMenuScreen;

public class TapTapGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public AssetManager assetManager;
    
    // Constantes para los assets
    public static final String MENU_BACKGROUND = "menu_background.png";
    public static final String MENU_MUSIC = "menu_music.mp3";
    public static final String CURSOR_SOUND = "cursor_sound.wav";
    public static final String CUSTOM_FONT = "next_art_regular.otf";
    public static final String MENU_SELECT_RHYTHM = "menu_select_rhythm.png";
    public static final String MENU_EXIT = "menu_exit.png";
    public static final String MENU_TAP = "menu_tap.png";
    public static final String MENU_TAP1 = "menu_tap1.png";
    
    // Constantes para las notas del efecto de fondo (Las animaciones de fondo de caida)
    public static final String NOTE_1 = "note_1.png";
    public static final String NOTE_2 = "note_2.png";
    public static final String NOTE_3 = "note_3.png";
    public static final String NOTE_4 = "note_4.png";
    
    public static final String DEFAULT_VINYL = "Vinyl.png";
    
    // Constantes para los logos UTP
    public static final String UTP1_LOGO = "UTP1.png";
    public static final String UTP2_LOGO = "UTP2.png";
    
    //Metodo para mostrar y cargar los assets del menú principal
    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        
        loadMenuAssets();
        
        loadCustomFont();
        
        this.setScreen(new PresentacionUniversidad(this));
    }
    
    private void loadCustomFont() {
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(CUSTOM_FONT));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 48;
            parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
            parameter.borderColor = com.badlogic.gdx.graphics.Color.BLACK;
            parameter.borderWidth = 2;
            
            font = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            System.out.println("No se pudo cargar la fuente personalizada, usando fuente por defecto");
            font = new BitmapFont();
            font.getData().setScale(2.0f);
        }
    }
    
    // Cargar Imagenes del menu principal y otros assets
    private void loadMenuAssets() {
        assetManager.load(MENU_BACKGROUND, Texture.class);
        
        assetManager.load(MENU_SELECT_RHYTHM, Texture.class);
        assetManager.load(MENU_EXIT, Texture.class);
        
        assetManager.load(MENU_TAP, Texture.class);
        assetManager.load(MENU_TAP1, Texture.class);
        
        try {
            assetManager.load(NOTE_1, Texture.class);
        } catch (Exception e) {
            System.out.println("No se pudo cargar nota 1: " + NOTE_1);
        }
        
        try {
            assetManager.load(NOTE_2, Texture.class);
        } catch (Exception e) {
            System.out.println("No se pudo cargar nota 2: " + NOTE_2);
        }
        
        try {
            assetManager.load(NOTE_3, Texture.class);
        } catch (Exception e) {
            System.out.println("No se pudo cargar nota 3: " + NOTE_3);
        }
        
        try {
            assetManager.load(NOTE_4, Texture.class);
        } catch (Exception e) {
            System.out.println("No se pudo cargar nota 4: " + NOTE_4);
        }
        
        try {
            assetManager.load(DEFAULT_VINYL, Texture.class);
        } catch (Exception e) {
            System.out.println("No se pudo cargar vinyl predeterminado: " + DEFAULT_VINYL);
        }
        
        try {
            assetManager.load(UTP1_LOGO, Texture.class);
        } catch (Exception e) {
            System.out.println("No se pudo cargar logo UTP1: " + UTP1_LOGO);
        }
        
        try {
            assetManager.load(UTP2_LOGO, Texture.class);
        } catch (Exception e) {
            System.out.println("No se pudo cargar logo UTP2: " + UTP2_LOGO);
        }
        
        try {
            assetManager.load(MENU_MUSIC, Music.class);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la música del menú: " + MENU_MUSIC);
        }
        
        try {
            assetManager.load(CURSOR_SOUND, Sound.class);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el sonido del cursor: " + CURSOR_SOUND);
        }
        
        assetManager.finishLoading();
    }
    
    // Renderizar el juego y elimina la memoria
    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        assetManager.dispose();
    }
}
