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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.game.taptap.TapTapGame;
import com.game.taptap.manager.RhythmManager;
import com.game.taptap.model.RhythmInfo;
import com.game.taptap.utils.GradientTextRenderer;
import com.game.taptap.utils.GradientLabel;

import com.game.taptap.screens.BeatmapEditorScreen;

// Clase para seleccionar ritmos
public class RhythmSelectScreen implements Screen {
    private TapTapGame game;
    private Stage stage;
    private RhythmManager rhythmManager;
    private Sound cursorSound;
    
    // Control de navegación
    private int selectedRhythmIndex = 0;
    private boolean keyPressed = false;
    private Array<RhythmInfo> rhythms;
    
    // Elementos del interfaz
    private Table mainTable;
    private Image backgroundImage;
    private Image bannerImage;
    private Image vinylImage;
    private Image defaultVinylImage;
    private GradientLabel titleLabel;
    private GradientLabel authorLabel;
    private GradientLabel rhymerLabel;
    private GradientLabel genreLabel;
    private GradientLabel lengthLabel;
    private GradientLabel difficultyLabel;
    private GradientLabel descriptionLabel;
    private GradientLabel maxScoreLabel;
    private Label instructionLabel;
    
    // Música actual (Selecciona la carpeta del ritmo actual)
    private Music currentMusic;
    
    // Animaciones (rotacion etc)
    private float vinylRotation = 0f;
    private float defaultVinylRotation = 0f;
    private float transitionTime = 0f;
    private boolean isTransitioning = false;
    
    // Fuentes de diferentes tamaños
    private ObjectMap<Float, BitmapFont> fontCache;
    private FreeTypeFontGenerator fontGenerator;
    
    // Layout personalizable (osea las imagenes, su ubicacion y tamaño)
    private float backgroundX, backgroundY, backgroundWidth, backgroundHeight;
    private float bannerX, bannerY, bannerWidth, bannerHeight;
    private float vinylX, vinylY, vinylWidth, vinylHeight;
    
    public RhythmSelectScreen(TapTapGame game) {
        this.game = game;
        

        initializeFontSystem();
        
        setDefaultLayout();
    }
    
    private void initializeFontSystem() {
        fontCache = new ObjectMap<Float, BitmapFont>();
        
        try {
            fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(TapTapGame.CUSTOM_FONT));
        } catch (Exception e) {
            fontGenerator = null;
        }
    }

    // Configuracion de la fuentes
    private BitmapFont getFontOfSize(float fontSize) {
        Float cacheKey = (float) Math.round(fontSize);
        
        if (fontCache.containsKey(cacheKey)) {
            return fontCache.get(cacheKey);
        }
        
        BitmapFont font;
        if (fontGenerator != null) {
            try {
                FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                parameter.size = Math.round(fontSize);
                parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
                
                if (RhythmSelectConfig.InfoConfig.BorderConfig.ENABLE_BORDERS) {
                    parameter.borderColor = new com.badlogic.gdx.graphics.Color(
                        RhythmSelectConfig.InfoConfig.BorderConfig.BORDER_R,
                        RhythmSelectConfig.InfoConfig.BorderConfig.BORDER_G,
                        RhythmSelectConfig.InfoConfig.BorderConfig.BORDER_B,
                        RhythmSelectConfig.InfoConfig.BorderConfig.BORDER_ALPHA
                    );
                    parameter.borderWidth = Math.max(1, Math.round(fontSize * RhythmSelectConfig.InfoConfig.BorderConfig.BORDER_WIDTH_MULTIPLIER / 24f));
                }
                
                font = fontGenerator.generateFont(parameter);
                fontCache.put(cacheKey, font);
            } catch (Exception e) {
                font = new BitmapFont();
                font.getData().setScale(fontSize / 48f);
                fontCache.put(cacheKey, font);
            }
        } else {
            font = new BitmapFont();
            font.getData().setScale(fontSize / 48f);
            fontCache.put(cacheKey, font);
        }
        
        return font;
    }
    
    private void setDefaultLayout() {
        // Background
        backgroundX = RhythmSelectConfig.BackgroundConfig.X;
        backgroundY = RhythmSelectConfig.BackgroundConfig.Y;
        if (RhythmSelectConfig.BackgroundConfig.SCALE_TO_SCREEN) {
            backgroundWidth = Gdx.graphics.getWidth();
            backgroundHeight = Gdx.graphics.getHeight();
        } else {
            backgroundWidth = RhythmSelectConfig.BackgroundConfig.WIDTH;
            backgroundHeight = RhythmSelectConfig.BackgroundConfig.HEIGHT;
        }
        
        // Banner
        bannerX = RhythmSelectConfig.BannerConfig.X;
        bannerY = RhythmSelectConfig.BannerConfig.Y;
        bannerWidth = RhythmSelectConfig.BannerConfig.WIDTH;
        bannerHeight = RhythmSelectConfig.BannerConfig.HEIGHT;
        
        vinylX = RhythmSelectConfig.VinylConfig.X;
        vinylY = RhythmSelectConfig.VinylConfig.Y;
        vinylWidth = RhythmSelectConfig.VinylConfig.WIDTH;
        vinylHeight = RhythmSelectConfig.VinylConfig.HEIGHT;
    }
    
    @Override
    public void show() {
        // Crear Escenario
        stage = new Stage(new ScreenViewport());
        
        // Cargar sonido del cursor
        if (game.assetManager.isLoaded(TapTapGame.CURSOR_SOUND)) {
            cursorSound = game.assetManager.get(TapTapGame.CURSOR_SOUND, Sound.class);
        }
        
        // Inicializar gestor de ritmos
        rhythmManager = new RhythmManager(game.assetManager);
        rhythmManager.loadAvailableRhythms();
        rhythms = rhythmManager.getAvailableRhythms();
        
        // Crear UI
        createUI();
        
        // Mostrar primer ritmo si existe
        if (rhythms.size > 0) {
            showRhythm(selectedRhythmIndex);
        } else {
            showNoRhythmsMessage();
        }
    }
    
    private void createUI() {
        // Crear tabla principal
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
        
        // Background image
        backgroundImage = new Image();
        backgroundImage.setPosition(backgroundX, backgroundY);
        backgroundImage.setSize(backgroundWidth, backgroundHeight);
        stage.addActor(backgroundImage);
        
        // Banner image
        bannerImage = new Image();
        bannerImage.setPosition(bannerX, bannerY);
        // El tamaño se establecerá cuando se cargue la textura
        stage.addActor(bannerImage);
        
        // Default Vinyl image (siempre visible con rotación)
        defaultVinylImage = new Image();
        createDefaultVinyl();
        stage.addActor(defaultVinylImage);
        
        // Vinyl image (de los ritmos)
        vinylImage = new Image();
        vinylImage.setPosition(vinylX, vinylY);
        // El tamaño y origen se establecerán cuando se cargue la textura
        stage.addActor(vinylImage);
        
        // Crear labels de información con configuración individual
        createInfoLabels();
    }
    
    private void createInfoLabels() {
        // Titulo
        titleLabel = createLabel("", 
            RhythmSelectConfig.InfoConfig.TitleConfig.X,
            RhythmSelectConfig.InfoConfig.TitleConfig.Y,
            RhythmSelectConfig.InfoConfig.TitleConfig.FONT_SIZE,
            RhythmSelectConfig.InfoConfig.TitleConfig.R,
            RhythmSelectConfig.InfoConfig.TitleConfig.G,
            RhythmSelectConfig.InfoConfig.TitleConfig.B);
        stage.addActor(titleLabel);
        
        // Autor
        authorLabel = createLabel("", 
            RhythmSelectConfig.InfoConfig.AuthorConfig.X,
            RhythmSelectConfig.InfoConfig.AuthorConfig.Y,
            RhythmSelectConfig.InfoConfig.AuthorConfig.FONT_SIZE,
            RhythmSelectConfig.InfoConfig.AuthorConfig.R,
            RhythmSelectConfig.InfoConfig.AuthorConfig.G,
            RhythmSelectConfig.InfoConfig.AuthorConfig.B);
        stage.addActor(authorLabel);
        
        // Rhymer
        rhymerLabel = createLabel("", 
            RhythmSelectConfig.InfoConfig.RhymerConfig.X,
            RhythmSelectConfig.InfoConfig.RhymerConfig.Y,
            RhythmSelectConfig.InfoConfig.RhymerConfig.FONT_SIZE,
            RhythmSelectConfig.InfoConfig.RhymerConfig.R,
            RhythmSelectConfig.InfoConfig.RhymerConfig.G,
            RhythmSelectConfig.InfoConfig.RhymerConfig.B);
        stage.addActor(rhymerLabel);
        
        // Genero
        genreLabel = createLabel("", 
            RhythmSelectConfig.InfoConfig.GenreConfig.X,
            RhythmSelectConfig.InfoConfig.GenreConfig.Y,
            RhythmSelectConfig.InfoConfig.GenreConfig.FONT_SIZE,
            RhythmSelectConfig.InfoConfig.GenreConfig.R,
            RhythmSelectConfig.InfoConfig.GenreConfig.G,
            RhythmSelectConfig.InfoConfig.GenreConfig.B);
        stage.addActor(genreLabel);
        
        // Duracion
        lengthLabel = createLabel("", 
            RhythmSelectConfig.InfoConfig.LengthConfig.X,
            RhythmSelectConfig.InfoConfig.LengthConfig.Y,
            RhythmSelectConfig.InfoConfig.LengthConfig.FONT_SIZE,
            RhythmSelectConfig.InfoConfig.LengthConfig.R,
            RhythmSelectConfig.InfoConfig.LengthConfig.G,
            RhythmSelectConfig.InfoConfig.LengthConfig.B);
        stage.addActor(lengthLabel);
        
        // Dificultad
        difficultyLabel = createLabel("", 
            RhythmSelectConfig.InfoConfig.DifficultyConfig.X,
            RhythmSelectConfig.InfoConfig.DifficultyConfig.Y,
            RhythmSelectConfig.InfoConfig.DifficultyConfig.FONT_SIZE,
            RhythmSelectConfig.InfoConfig.DifficultyConfig.R,
            RhythmSelectConfig.InfoConfig.DifficultyConfig.G,
            RhythmSelectConfig.InfoConfig.DifficultyConfig.B);
        stage.addActor(difficultyLabel);
        
        // Descripción
        descriptionLabel = createLabel("", 
            RhythmSelectConfig.InfoConfig.DescriptionConfig.X,
            RhythmSelectConfig.InfoConfig.DescriptionConfig.Y,
            RhythmSelectConfig.InfoConfig.DescriptionConfig.FONT_SIZE,
            RhythmSelectConfig.InfoConfig.DescriptionConfig.R,
            RhythmSelectConfig.InfoConfig.DescriptionConfig.G,
            RhythmSelectConfig.InfoConfig.DescriptionConfig.B);
        descriptionLabel.setWrap(true);
        descriptionLabel.setWidth(RhythmSelectConfig.InfoConfig.DescriptionConfig.WIDTH);
        stage.addActor(descriptionLabel);
        
        // Puntuación máxima
        maxScoreLabel = createLabel("", 
            RhythmSelectConfig.InfoConfig.MaxScoreConfig.X,
            RhythmSelectConfig.InfoConfig.MaxScoreConfig.Y,
            RhythmSelectConfig.InfoConfig.MaxScoreConfig.FONT_SIZE,
            RhythmSelectConfig.InfoConfig.MaxScoreConfig.R,
            RhythmSelectConfig.InfoConfig.MaxScoreConfig.G,
            RhythmSelectConfig.InfoConfig.MaxScoreConfig.B);
        stage.addActor(maxScoreLabel);
    }
    
    private GradientLabel createLabel(String text, float x, float y, float fontSize, float r, float g, float b) {
        BitmapFont font = getFontOfSize(fontSize);
        Label.LabelStyle style = new Label.LabelStyle(font, new com.badlogic.gdx.graphics.Color(r, g, b, 1f));
        GradientLabel label = new GradientLabel(text, style);
        label.setPosition(x, y);
        label.setAlignment(Align.left);
        return label;
    }
    
    private void createDefaultVinyl() {
        if (game.assetManager.isLoaded(TapTapGame.DEFAULT_VINYL)) {
            Texture defaultVinylTexture = game.assetManager.get(TapTapGame.DEFAULT_VINYL, Texture.class);
            defaultVinylImage.setDrawable(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(defaultVinylTexture));
            
            defaultVinylImage.setPosition(RhythmSelectConfig.DefaultVinylConfig.X, 
                                        RhythmSelectConfig.DefaultVinylConfig.Y);
            
            float finalWidth = RhythmSelectConfig.DefaultVinylConfig.WIDTH == -1f ? 
                              defaultVinylTexture.getWidth() : RhythmSelectConfig.DefaultVinylConfig.WIDTH;
            float finalHeight = RhythmSelectConfig.DefaultVinylConfig.HEIGHT == -1f ? 
                               defaultVinylTexture.getHeight() : RhythmSelectConfig.DefaultVinylConfig.HEIGHT;
            
            defaultVinylImage.setSize(finalWidth, finalHeight);
            
            defaultVinylImage.setOrigin(finalWidth / 2f, finalHeight / 2f);
            
        } else {
        }
    }
    
    private void showRhythm(int index) {
        if (index < 0 || index >= rhythms.size) return;
        
        RhythmInfo rhythm = rhythms.get(index);
        isTransitioning = true;
        transitionTime = 0f;
        
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }

        if (rhythm.getBackgroundTexture() != null) {
            backgroundImage.setDrawable(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(rhythm.getBackgroundTexture()));
            backgroundImage.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.fadeIn(0.5f, Interpolation.fade)
            ));
        }
        
        if (rhythm.getBannerTexture() != null) {
            bannerImage.setDrawable(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(rhythm.getBannerTexture()));
            
            float finalBannerWidth = bannerWidth == -1f ? rhythm.getBannerTexture().getWidth() : bannerWidth;
            float finalBannerHeight = bannerHeight == -1f ? rhythm.getBannerTexture().getHeight() : bannerHeight;
            bannerImage.setSize(finalBannerWidth, finalBannerHeight);
            
            if (RhythmSelectConfig.BannerConfig.ANIMATE_ON_CHANGE) {
                bannerImage.addAction(Actions.sequence(
                    Actions.scaleTo(0.8f, 0.8f),
                    Actions.alpha(0f),
                    Actions.parallel(
                        Actions.scaleTo(1f, 1f, RhythmSelectConfig.BannerConfig.ANIMATION_DURATION, Interpolation.bounce),
                        Actions.fadeIn(RhythmSelectConfig.BannerConfig.ANIMATION_DURATION, Interpolation.fade)
                    )
                ));
            } else {
                bannerImage.addAction(Actions.sequence(
                    Actions.alpha(0f),
                    Actions.fadeIn(RhythmSelectConfig.TransitionConfig.FADE_IN_DURATION, Interpolation.fade)
                ));
            }
        }
        
        if (rhythm.getVinylTexture() != null) {
            vinylImage.setDrawable(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(rhythm.getVinylTexture()));
            
            float finalVinylWidth = vinylWidth == -1f ? rhythm.getVinylTexture().getWidth() : vinylWidth;
            float finalVinylHeight = vinylHeight == -1f ? rhythm.getVinylTexture().getHeight() : vinylHeight;
            vinylImage.setSize(finalVinylWidth, finalVinylHeight);
            
            vinylImage.setOrigin(finalVinylWidth / 2f, finalVinylHeight / 2f);
            
            if (RhythmSelectConfig.VinylConfig.ANIMATE_ON_CHANGE) {
                vinylImage.addAction(Actions.sequence(
                    Actions.alpha(0f),
                    Actions.fadeIn(RhythmSelectConfig.VinylConfig.ANIMATION_DURATION, Interpolation.fade)
                ));
            } else {
                vinylImage.addAction(Actions.sequence(
                    Actions.alpha(0f),
                    Actions.fadeIn(RhythmSelectConfig.TransitionConfig.FADE_IN_DURATION, Interpolation.fade)
                ));
            }
        }
        
        updateLabelsWithAnimation(rhythm);
        
        if (rhythm.getMusic() != null && RhythmSelectConfig.AudioConfig.AUTO_PLAY_MUSIC) {
            currentMusic = rhythm.getMusic();
            currentMusic.setLooping(RhythmSelectConfig.AudioConfig.LOOP_MUSIC);
            currentMusic.setVolume(RhythmSelectConfig.AudioConfig.MUSIC_VOLUME);
            currentMusic.play();
        }
        
        stage.addAction(Actions.delay(RhythmSelectConfig.TransitionConfig.TRANSITION_DELAY, 
            Actions.run(() -> isTransitioning = false)));
    }
    
    // Actualizar y obtener información del ritmo actual
    private void updateLabelsWithAnimation(RhythmInfo rhythm) {
        float animDuration = RhythmSelectConfig.InfoConfig.ANIMATE_TEXT_CHANGE ? 
                           RhythmSelectConfig.InfoConfig.TEXT_ANIMATION_DURATION : 0f;
        
        if (animDuration > 0f) {
            titleLabel.addAction(Actions.sequence(
                Actions.fadeOut(animDuration),
                Actions.run(() -> {
                    titleLabel.setText(rhythm.getTitle() != null ? rhythm.getTitle() : "Desconocido");
                    GradientTextRenderer.updateLabelGradient(titleLabel, rhythm.getTitleGradient());
                }),
                Actions.fadeIn(animDuration)
            ));
            
            authorLabel.addAction(Actions.sequence(
                Actions.fadeOut(animDuration),
                Actions.run(() -> {
                    authorLabel.setText("Artista: " + (rhythm.getAuthor() != null ? rhythm.getAuthor() : "Desconocido"));
                    GradientTextRenderer.updateLabelGradient(authorLabel, rhythm.getAuthorGradient());
                }),
                Actions.fadeIn(animDuration)
            ));
            
            rhymerLabel.addAction(Actions.sequence(
                Actions.fadeOut(animDuration),
                Actions.run(() -> {
                    rhymerLabel.setText("Rhymer: " + (rhythm.getRhymer() != null ? rhythm.getRhymer() : "Desconocido"));
                    GradientTextRenderer.updateLabelGradient(rhymerLabel, rhythm.getRhymerGradient());
                }),
                Actions.fadeIn(animDuration)
            ));
            
            genreLabel.addAction(Actions.sequence(
                Actions.fadeOut(animDuration),
                Actions.run(() -> {
                    genreLabel.setText("Género: " + (rhythm.getGenre() != null ? rhythm.getGenre() : "Desconocido"));
                    GradientTextRenderer.updateLabelGradient(genreLabel, rhythm.getGenreGradient());
                }),
                Actions.fadeIn(animDuration)
            ));
            
            lengthLabel.addAction(Actions.sequence(
                Actions.fadeOut(animDuration),
                Actions.run(() -> {
                    lengthLabel.setText("Duración: " + (rhythm.getLength() != null ? rhythm.getLength() : "0:00"));
                    GradientTextRenderer.updateLabelGradient(lengthLabel, rhythm.getLengthGradient());
                }),
                Actions.fadeIn(animDuration)
            ));
            
            difficultyLabel.addAction(Actions.sequence(
                Actions.fadeOut(animDuration),
                Actions.run(() -> {
                    difficultyLabel.setText("Dificultad: " + (rhythm.getDifficulty() != null ? rhythm.getDifficulty() : "Normal"));
                    GradientTextRenderer.updateLabelGradient(difficultyLabel, rhythm.getDifficultyGradient());
                }),
                Actions.fadeIn(animDuration)
            ));
            
            descriptionLabel.addAction(Actions.sequence(
                Actions.fadeOut(animDuration),
                Actions.run(() -> {
                    descriptionLabel.setText(rhythm.getDescription() != null ? rhythm.getDescription() : "Sin descripción");
                    GradientTextRenderer.updateLabelGradient(descriptionLabel, rhythm.getDescriptionGradient());
                }),
                Actions.fadeIn(animDuration)
            ));
            
            maxScoreLabel.addAction(Actions.sequence(
                Actions.fadeOut(animDuration),
                Actions.run(() -> {
                    maxScoreLabel.setText("Mejor Puntuación: " + rhythm.getMaxScore());
                    GradientTextRenderer.updateLabelGradient(maxScoreLabel, rhythm.getMaxScoreGradient());
                }),
                Actions.fadeIn(animDuration)
            ));
        } else {
            // Actualización inmediata sin animación, (en caso que info.json no este bien configurada)
            titleLabel.setText(rhythm.getTitle() != null ? rhythm.getTitle() : "Desconocido");
            GradientTextRenderer.updateLabelGradient(titleLabel, rhythm.getTitleGradient());
            
            authorLabel.setText("Artista: " + (rhythm.getAuthor() != null ? rhythm.getAuthor() : "Desconocido"));
            GradientTextRenderer.updateLabelGradient(authorLabel, rhythm.getAuthorGradient());
            
            rhymerLabel.setText("Rhymer: " + (rhythm.getRhymer() != null ? rhythm.getRhymer() : "Desconocido"));
            GradientTextRenderer.updateLabelGradient(rhymerLabel, rhythm.getRhymerGradient());
            
            genreLabel.setText("Género: " + (rhythm.getGenre() != null ? rhythm.getGenre() : "Desconocido"));
            GradientTextRenderer.updateLabelGradient(genreLabel, rhythm.getGenreGradient());
            
            lengthLabel.setText("Duración: " + (rhythm.getLength() != null ? rhythm.getLength() : "0:00"));
            GradientTextRenderer.updateLabelGradient(lengthLabel, rhythm.getLengthGradient());
            
            difficultyLabel.setText("Dificultad: " + (rhythm.getDifficulty() != null ? rhythm.getDifficulty() : "Normal"));
            GradientTextRenderer.updateLabelGradient(difficultyLabel, rhythm.getDifficultyGradient());
            
            descriptionLabel.setText(rhythm.getDescription() != null ? rhythm.getDescription() : "Sin descripción");
            GradientTextRenderer.updateLabelGradient(descriptionLabel, rhythm.getDescriptionGradient());
            
            maxScoreLabel.setText("Mejor Puntuación: " + rhythm.getMaxScore());
            GradientTextRenderer.updateLabelGradient(maxScoreLabel, rhythm.getMaxScoreGradient());
        }
    }
    
    // Validad en la terminal como me gusta
    private void showNoRhythmsMessage() {
        titleLabel.setText("No se Encontraron Ritmos");
        authorLabel.setText("Por favor agrega carpetas de ritmos al directorio Rhythms");
        rhymerLabel.setText("");
        genreLabel.setText("");
        lengthLabel.setText("");
        difficultyLabel.setText("");
        descriptionLabel.setText("");
        maxScoreLabel.setText("");
    }
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
        
        // Actualizar rotación del vinyl si está configurado para rotar (La imagen de los assets de cada ritmo)
        if (!isTransitioning && vinylImage.getDrawable() != null && RhythmSelectConfig.VinylConfig.ROTATE) {
            vinylRotation += delta * RhythmSelectConfig.VinylConfig.ROTATION_SPEED;
            vinylImage.setRotation(vinylRotation);
        }
        
        // Actualizar rotación del vinyl predeterminado (el vinilo)
        if (defaultVinylImage.getDrawable() != null && RhythmSelectConfig.DefaultVinylConfig.ROTATE) {
            defaultVinylRotation += delta * RhythmSelectConfig.DefaultVinylConfig.ROTATION_SPEED;
            defaultVinylImage.setRotation(defaultVinylRotation);
        }
        
        // Manejar input
        handleInput();
        
        // Actualizar y dibujar stage (stage = escenario = ritmo)
        stage.act(delta);
        stage.draw();
    }
    
    private void handleInput() {
        if (isTransitioning || rhythms.size == 0) return;
        
        // Navegación vertical
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !keyPressed) {
            selectedRhythmIndex--;
            if (selectedRhythmIndex < 0) {
                selectedRhythmIndex = rhythms.size - 1;
            }
            showRhythm(selectedRhythmIndex);
            playSound();
            keyPressed = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && !keyPressed) {
            selectedRhythmIndex++;
            if (selectedRhythmIndex >= rhythms.size) {
                selectedRhythmIndex = 0;
            }
            showRhythm(selectedRhythmIndex);
            playSound();
            keyPressed = true;
        }
        
        // Selección
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !keyPressed) {
            selectCurrentRhythm();
            keyPressed = true;
        }
        
        // Editor, presionar H para entrar al editor
        if (Gdx.input.isKeyJustPressed(Input.Keys.H) && !keyPressed) {
            enterBeatmapEditor();
            keyPressed = true;
        }
        
        // Volver al menú principal
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !keyPressed) {
            goBackToMainMenu();
            keyPressed = true;
        }
        
        // Reset estado de tecla
        if (!Gdx.input.isKeyPressed(Input.Keys.UP) && 
            !Gdx.input.isKeyPressed(Input.Keys.DOWN) && 
            !Gdx.input.isKeyPressed(Input.Keys.ENTER) && 
            !Gdx.input.isKeyPressed(Input.Keys.H) && 
            !Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            keyPressed = false;
        }
    }
    
    private void selectCurrentRhythm() {
        if (selectedRhythmIndex >= 0 && selectedRhythmIndex < rhythms.size) {
            RhythmInfo selectedRhythm = rhythms.get(selectedRhythmIndex);
            System.out.println("Seleccionado ritmo: " + selectedRhythm.getTitle());
            
            // Enter para jugar el ritmo (gameplay)
            String rhythmPath = selectedRhythm.getFolderPath();
            game.setScreen(new GameplayScreen(game, rhythmPath));
        }
    }
    
    private void enterBeatmapEditor() {
        if (selectedRhythmIndex >= 0 && selectedRhythmIndex < rhythms.size) {
            RhythmInfo selectedRhythm = rhythms.get(selectedRhythmIndex);
            System.out.println("Entrando al editor de: " + selectedRhythm.getTitle());
            
            // Enter beatmap editor
            String rhythmPath = selectedRhythm.getFolderPath();
            game.setScreen(new BeatmapEditorScreen(game, rhythmPath));
        }
    }
    
    private void goBackToMainMenu() {
        game.setScreen(new MainMenuScreen(game));
    }
    
    private void playSound() {
        if (cursorSound != null) {
            cursorSound.play();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        
        if (RhythmSelectConfig.BackgroundConfig.SCALE_TO_SCREEN) {
            backgroundWidth = width;
            backgroundHeight = height;
            backgroundImage.setSize(backgroundWidth, backgroundHeight);
        }
    }
    
    // Sobrecargar métodos de Screen
    @Override
    public void pause() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }
    
    @Override
    public void resume() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }
    
    @Override
    public void hide() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
    }
    
    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (rhythmManager != null) {
            rhythmManager.dispose();
        }
        
        // Liberar fuentes en caché
        if (fontCache != null) {
            for (BitmapFont font : fontCache.values()) {
                if (font != game.font) {
                    font.dispose();
                }
            }
            fontCache.clear();
        }
        
        // Liberar generador de fuentes
        if (fontGenerator != null) {
            fontGenerator.dispose();
        }
    }
}
