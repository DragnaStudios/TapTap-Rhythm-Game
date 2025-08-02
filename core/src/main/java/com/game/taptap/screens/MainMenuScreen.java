package com.game.taptap.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.game.taptap.TapTapGame;

public class MainMenuScreen implements Screen {
    private TapTapGame game;
    private Texture backgroundTexture;
    private Music menuMusic;
    private Sound cursorSound;
    private BitmapFont font;
    
    // Texturas de las opciones del menú
    private Texture selectRhythmTexture;
    private Texture exitTexture;
    
    // Texturas del título
    private Texture menuTapTexture;
    private Texture menuTap1Texture;
    
    // Efecto de notas cayendo en el fondo
    private MenuBackgroundEffect backgroundEffect;
    
    // Opciones del menú
    private int selectedOption = 0;
    private boolean keyPressed = false;
    private final int TOTAL_OPTIONS = 2;
    
    // Efecto de respiración
    private float breathingTimer = 0f;
    private float breathingSpeed = 2.0f;
    private float minScale = 1.0f;
    private float maxScale = 1.1f;
    
    // Animación de entrada del título (efecto tap)
    private float titleAnimationTimer = 0f;
    private boolean titleAnimationStarted = false;
    
    // Estados de animación para cada imagen
    private boolean menuTapVisible = false;
    private boolean menuTap1Visible = false;
    private float menuTapAnimationProgress = 0f;
    private float menuTap1AnimationProgress = 0f;
    
    // Animación de fade-in para opciones del menú
    private boolean menuOptionsVisible = false;
    private float menuOptionsAnimationProgress = 0f;
    private boolean titleAnimationComplete = false;
    
    // Control de sonidos
    private boolean menuTapSoundPlayed = false;
    private boolean menuTap1SoundPlayed = false;
    
    public MainMenuScreen(TapTapGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        // Cargar assets
        if (game.assetManager.isLoaded(TapTapGame.MENU_BACKGROUND)) {
            backgroundTexture = game.assetManager.get(TapTapGame.MENU_BACKGROUND, Texture.class);
        }
        
        // Cargar texturas del menú
        if (game.assetManager.isLoaded(TapTapGame.MENU_SELECT_RHYTHM)) {
            selectRhythmTexture = game.assetManager.get(TapTapGame.MENU_SELECT_RHYTHM, Texture.class);
        }
        
        if (game.assetManager.isLoaded(TapTapGame.MENU_EXIT)) {
            exitTexture = game.assetManager.get(TapTapGame.MENU_EXIT, Texture.class);
        }
        
        // Cargar texturas del título
        if (game.assetManager.isLoaded(TapTapGame.MENU_TAP)) {
            menuTapTexture = game.assetManager.get(TapTapGame.MENU_TAP, Texture.class);
        }
        
        if (game.assetManager.isLoaded(TapTapGame.MENU_TAP1)) {
            menuTap1Texture = game.assetManager.get(TapTapGame.MENU_TAP1, Texture.class);
        }
        
        if (game.assetManager.isLoaded(TapTapGame.MENU_MUSIC)) {
            menuMusic = game.assetManager.get(TapTapGame.MENU_MUSIC, Music.class);
            menuMusic.setLooping(true);
            menuMusic.play();
        }
        
        if (game.assetManager.isLoaded(TapTapGame.CURSOR_SOUND)) {
            cursorSound = game.assetManager.get(TapTapGame.CURSOR_SOUND, Sound.class);
        }
        
        font = game.font;
        
        // Inicializar efecto de fondo de notas
        backgroundEffect = new MenuBackgroundEffect(game);
        
        // Inicializar animación del título
        titleAnimationTimer = 0f;
        titleAnimationStarted = false;
        menuTapVisible = false;
        menuTap1Visible = false;
        menuTapAnimationProgress = 0f;
        menuTap1AnimationProgress = 0f;
        
        // Inicializar animación de opciones
        menuOptionsVisible = false;
        menuOptionsAnimationProgress = 0f;
        titleAnimationComplete = false;
        
        // Inicializar control de sonidos
        menuTapSoundPlayed = false;
        menuTap1SoundPlayed = false;
    }
    
    @Override
    public void render(float delta) {
        // Limpiar pantalla
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        
        // Actualizar timer de respiración
        breathingTimer += delta * breathingSpeed;
        
        // Actualizar efecto de notas de fondo
        backgroundEffect.update(delta);
        
        // Actualizar animación del título
        updateTitleAnimation(delta);
        
        // Actualizar animación de opciones del menú
        updateMenuOptionsAnimation(delta);
        
        // Manejar input solo si las opciones están visibles
        if (titleAnimationComplete && menuOptionsAnimationProgress >= 1f) {
            handleInput();
        }
        
        // Comenzar a dibujar
        game.batch.begin();
        
        // Dibujar fondo si existe
        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        
        // Dibujar efecto de notas de fondo (encima del fondo, debajo del resto)
        backgroundEffect.render(game.batch);
        
        // Calcular posiciones centrales
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        
        // Dibujar título con animación - imagen "menu_tap.png"
        if (menuTapTexture != null && menuTapVisible) {
            float tapScale = getTapAnimationScale(menuTapAnimationProgress);
            float tapRotation = getTapAnimationRotation(menuTapAnimationProgress);
            float tapAlpha = getTapAnimationAlpha(menuTapAnimationProgress);
            float tapOffsetX = getTapAnimationOffsetX(menuTapAnimationProgress);
            float tapOffsetY = getTapAnimationOffsetY(menuTapAnimationProgress);
            
            float tapX = centerX + MenuTitleConfig.MENU_TAP_OFFSET_X + tapOffsetX;
            float tapY = centerY + MenuTitleConfig.MENU_TAP_OFFSET_Y + tapOffsetY;
            
            game.batch.setColor(1f, 1f, 1f, tapAlpha);
            game.batch.draw(menuTapTexture, 
                tapX - (menuTapTexture.getWidth() * tapScale) / 2f, 
                tapY - (menuTapTexture.getHeight() * tapScale) / 2f,
                (menuTapTexture.getWidth() * tapScale) / 2f, 
                (menuTapTexture.getHeight() * tapScale) / 2f,
                menuTapTexture.getWidth() * tapScale, 
                menuTapTexture.getHeight() * tapScale,
                1f, 1f, tapRotation, 0, 0, 
                menuTapTexture.getWidth(), menuTapTexture.getHeight(), 
                false, false);
        }
        
        // Dibujar título con animación - imagen "menu_tap1.png"
        if (menuTap1Texture != null && menuTap1Visible) {
            float tap1Scale = getTapAnimationScale(menuTap1AnimationProgress);
            float tap1Rotation = getTapAnimationRotation(menuTap1AnimationProgress);
            float tap1Alpha = getTapAnimationAlpha(menuTap1AnimationProgress);
            float tap1OffsetX = getTapAnimationOffsetX(menuTap1AnimationProgress);
            float tap1OffsetY = getTapAnimationOffsetY(menuTap1AnimationProgress);
            
            float tap1X = centerX + MenuTitleConfig.MENU_TAP1_OFFSET_X + tap1OffsetX;
            float tap1Y = centerY + MenuTitleConfig.MENU_TAP1_OFFSET_Y + tap1OffsetY;
            
            game.batch.setColor(1f, 1f, 1f, tap1Alpha);
            game.batch.draw(menuTap1Texture,
                tap1X - (menuTap1Texture.getWidth() * tap1Scale) / 2f,
                tap1Y - (menuTap1Texture.getHeight() * tap1Scale) / 2f,
                (menuTap1Texture.getWidth() * tap1Scale) / 2f,
                (menuTap1Texture.getHeight() * tap1Scale) / 2f,
                menuTap1Texture.getWidth() * tap1Scale,
                menuTap1Texture.getHeight() * tap1Scale,
                1f, 1f, tap1Rotation, 0, 0,
                menuTap1Texture.getWidth(), menuTap1Texture.getHeight(),
                false, false);
        }
        
        game.batch.setColor(1f, 1f, 1f, 1f);
        
        float menuOptionsAlpha = getMenuOptionsAlpha();
        
        if (selectRhythmTexture != null && menuOptionsVisible) {
            float selectScale = selectedOption == 0 ? getBreathingScale() : 1.0f;
            float selectWidth = selectRhythmTexture.getWidth() * selectScale;
            float selectHeight = selectRhythmTexture.getHeight() * selectScale;
            float selectX = centerX - selectWidth / 2f;
            float selectY = centerY - 50 - selectHeight / 2f;
            
            game.batch.setColor(1f, 1f, 1f, menuOptionsAlpha);
            game.batch.draw(selectRhythmTexture, selectX, selectY, selectWidth, selectHeight);
        }
        
        if (exitTexture != null && menuOptionsVisible) {
            float exitScale = selectedOption == 1 ? getBreathingScale() : 1.0f;
            float exitWidth = exitTexture.getWidth() * exitScale;
            float exitHeight = exitTexture.getHeight() * exitScale;
            float exitX = centerX - exitWidth / 2f;
            float exitY = centerY - 200 - exitHeight / 2f;
            
            game.batch.setColor(1f, 1f, 1f, menuOptionsAlpha);
            game.batch.draw(exitTexture, exitX, exitY, exitWidth, exitHeight);
        }
        
        game.batch.setColor(1f, 1f, 1f, 1f);
        
        game.batch.end();
    }
    
    private float getBreathingScale() {
        float breathingFactor = (float) Math.sin(breathingTimer);
        return minScale + (maxScale - minScale) * (breathingFactor + 1) / 2;
    }
    
    private void updateTitleAnimation(float delta) {
        if (!titleAnimationStarted) {
            titleAnimationStarted = true;
        }
        
        titleAnimationTimer += delta;
        
        if (titleAnimationTimer >= 0f && !menuTapVisible) {
            menuTapVisible = true;
            menuTapAnimationProgress = 0f;
            if (!menuTapSoundPlayed) {
                playSound();
                menuTapSoundPlayed = true;
            }
        }
        
        if (menuTapVisible && menuTapAnimationProgress < 1f) {
            menuTapAnimationProgress += delta / TapAnimationConfig.TAP_ANIMATION_DURATION;
            if (menuTapAnimationProgress > 1f) {
                menuTapAnimationProgress = 1f;
            }
        }
        
        if (titleAnimationTimer >= TapAnimationConfig.TAP_DELAY && !menuTap1Visible) {
            menuTap1Visible = true;
            menuTap1AnimationProgress = 0f;
            if (!menuTap1SoundPlayed) {
                playSound();
                menuTap1SoundPlayed = true;
            }
        }
        
        if (menuTap1Visible && menuTap1AnimationProgress < 1f) {
            menuTap1AnimationProgress += delta / TapAnimationConfig.TAP_ANIMATION_DURATION;
            if (menuTap1AnimationProgress > 1f) {
                menuTap1AnimationProgress = 1f;
            }
        }
        
        if (menuTap1AnimationProgress >= 1f && !titleAnimationComplete) {
            titleAnimationComplete = true;
            float totalTitleTime = TapAnimationConfig.TAP_DELAY + TapAnimationConfig.TAP_ANIMATION_DURATION;
            if (titleAnimationTimer >= totalTitleTime + TapAnimationConfig.MENU_OPTIONS_DELAY) {
                menuOptionsVisible = true;
            }
        }
    }
    
    private void updateMenuOptionsAnimation(float delta) {
        if (titleAnimationComplete) {
            float totalTitleTime = TapAnimationConfig.TAP_DELAY + TapAnimationConfig.TAP_ANIMATION_DURATION;
            
            if (titleAnimationTimer >= totalTitleTime + TapAnimationConfig.MENU_OPTIONS_DELAY && !menuOptionsVisible) {
                menuOptionsVisible = true;
                menuOptionsAnimationProgress = 0f;
            }
            
            if (menuOptionsVisible && menuOptionsAnimationProgress < 1f) {
                menuOptionsAnimationProgress += delta / TapAnimationConfig.MENU_OPTIONS_FADE_DURATION;
                if (menuOptionsAnimationProgress > 1f) {
                    menuOptionsAnimationProgress = 1f;
                }
            }
        }
    }
    
    private float getMenuOptionsAlpha() {
        if (!menuOptionsVisible) return 0f;
        if (menuOptionsAnimationProgress >= 1f) return 1f;
        
        return menuOptionsAnimationProgress;
    }
    
    private float getTapAnimationScale(float progress) {
        if (progress <= 0f) return TapAnimationConfig.TAP_SCALE_START;
        if (progress >= 1f) return 1f;
        
        if (progress < 0.7f) {
            float phaseProgress = progress / 0.7f;
            float eased = phaseProgress * phaseProgress * (3f - 2f * phaseProgress);
            return TapAnimationConfig.TAP_SCALE_START + 
                   (TapAnimationConfig.TAP_SCALE_OVERSHOOT - TapAnimationConfig.TAP_SCALE_START) * eased;
        } else {
            float phaseProgress = (progress - 0.7f) / 0.3f;
            float easeOut = 1f - (1f - phaseProgress) * (1f - phaseProgress) * (1f - phaseProgress);
            return TapAnimationConfig.TAP_SCALE_OVERSHOOT - 
                   (TapAnimationConfig.TAP_SCALE_OVERSHOOT - 1f) * easeOut;
        }
    }
    
    private float getTapAnimationRotation(float progress) {
        if (progress <= 0f) return TapAnimationConfig.TAP_ROTATION_MAX;
        if (progress >= 1f) return 0f;
        
        // Rotación que decrece suavemente sin oscilaciones bruscas
        float rotationDecay = 1f - progress; // Decaimiento lineal
        float smoothRotation = (float) Math.cos(progress * Math.PI * 1.5f);
        
        // Aplicar suavizado adicional hacia el final
        if (progress > 0.7f) {
            float endProgress = (progress - 0.7f) / 0.3f;
            smoothRotation *= (1f - endProgress * endProgress);
        }
        
        return TapAnimationConfig.TAP_ROTATION_MAX * rotationDecay * smoothRotation * 0.5f;
    }
    
    private float getTapAnimationAlpha(float progress) {
        if (progress <= 0f) return 0f;
        if (progress >= 1f) return 1f;
        
        // Fade-in ultra suave con aceleración inicial lenta
        if (progress < 0.3f) {
            // Inicio muy gradual
            float phaseProgress = progress / 0.3f;
            return phaseProgress * phaseProgress * 0.6f;
        } else {
            // Aceleración hacia la opacidad completa
            float phaseProgress = (progress - 0.3f) / 0.7f;
            float smoothStep = phaseProgress * phaseProgress * (3f - 2f * phaseProgress);
            return 0.6f + (0.4f * smoothStep);
        }
    }
    
    private float getTapAnimationOffsetX(float progress) {
        if (progress <= 0f) return -TapAnimationConfig.TAP_SLIDE_DISTANCE;
        if (progress >= 1f) return 0f;
        
        // Deslizamiento horizontal con easing out suave hasta el final
        float easeOut = 1f - (1f - progress) * (1f - progress) * (1f - progress);
        return -TapAnimationConfig.TAP_SLIDE_DISTANCE * (1f - easeOut);
    }
    
    private float getTapAnimationOffsetY(float progress) {
        if (progress <= 0f) return TapAnimationConfig.TAP_SLIDE_DISTANCE * 0.5f;
        if (progress >= 1f) return 0f;
        
        // Deslizamiento vertical con easing out más suave
        float easeOut = 1f - (1f - progress) * (1f - progress) * (1f - progress);
        return TapAnimationConfig.TAP_SLIDE_DISTANCE * 0.5f * (1f - easeOut);
    }
    
    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && !keyPressed) {
            selectedOption--;
            if (selectedOption < 0) {
                selectedOption = TOTAL_OPTIONS - 1;
            }
            playSound();
            keyPressed = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !keyPressed) {
            selectedOption++;
            if (selectedOption >= TOTAL_OPTIONS) {
                selectedOption = 0;
            }
            playSound();
            keyPressed = true;
        }
        
        // Selección con Enter o Espacio
        if ((Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) && !keyPressed) {
            selectOption();
            keyPressed = true;
        }
        
        // Reset del estado de tecla presionada
        if (!Gdx.input.isKeyPressed(Input.Keys.UP) && 
            !Gdx.input.isKeyPressed(Input.Keys.DOWN) && 
            !Gdx.input.isKeyPressed(Input.Keys.ENTER) && 
            !Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            keyPressed = false;
        }
    }
    
    private void playSound() {
        if (cursorSound != null) {
            cursorSound.play();
        }
    }
    
    private void selectOption() {
        switch (selectedOption) {
            case 0: // Select Rhythm
                game.setScreen(new RhythmSelectScreen(game));
                break;
            case 1: // Exit
                Gdx.app.exit();
                break;
        }
    }
    
    @Override
    public void resize(int width, int height) {
        // Manejar cambio de tamaño de ventana si es necesario
    }
    
    @Override
    public void pause() {
        if (menuMusic != null) {
            menuMusic.pause();
        }
        // Pausar efecto de fondo
        backgroundEffect.pause();
    }
    
    @Override
    public void resume() {
        if (menuMusic != null) {
            menuMusic.play();
        }
        // Reanudar efecto de fondo
        backgroundEffect.resume();
    }
    
    @Override
    public void hide() {
        if (menuMusic != null) {
            menuMusic.stop();
        }
        // Limpiar efecto de fondo al salir del menú
        backgroundEffect.clear();
    }
    
    @Override
    public void dispose() {
        // Los assets se liberan en TapTapGame
    }
}
