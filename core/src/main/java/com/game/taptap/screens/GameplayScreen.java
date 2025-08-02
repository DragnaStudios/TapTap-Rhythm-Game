package com.game.taptap.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.game.taptap.TapTapGame;
import com.game.taptap.gameplay.Beatmap;
import com.game.taptap.gameplay.Note;

import java.util.List;

public class GameplayScreen implements Screen {
    
    private TapTapGame game;
    private String rhythmPath;
    private Beatmap beatmap;
    private Music gameMusic;
    private Sound hitSound;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private Texture backgroundImage;
    
    private boolean gameStarted;
    private boolean gamePaused;
    private float gameTime;
    private int score;
    private int combo;
    private int maxCombo;
    private float countdownTime;
    
    private Array<Note> activeNotes;
    private static final int LANES = 4;
    private static final float LOOK_AHEAD_TIME = 3.0f;
    private static final float PERFECT_WINDOW = 0.05f;
    private static final float GOOD_WINDOW = 0.12f;
    private static final float MISS_WINDOW = 0.18f;
    private static final float VISUAL_HIT_ZONE = 0.15f;
    
    private float laneWidth;
    private float laneStartX;
    private float hitLineY;
    private float noteSpeed;
    private static final Color[] LANE_COLORS = {
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW
    };
    
    private boolean[] lanePressed;
    private boolean[] laneHolding;
    private boolean escapePressed;
    
    private String lastHitFeedback = "";
    private float feedbackTime = 0f;
    private static final float FEEDBACK_DURATION = 1.0f;
    
    private boolean[] explosionActive = new boolean[LANES];
    private float[] explosionTime = new float[LANES];
    private static final float EXPLOSION_DURATION = 0.2f;
    
    public GameplayScreen(TapTapGame game, String rhythmPath) {
        this.game = game;
        this.rhythmPath = rhythmPath;
        this.beatmap = new Beatmap();
        this.activeNotes = new Array<>();
        this.lanePressed = new boolean[LANES];
        this.laneHolding = new boolean[LANES];
        this.explosionActive = new boolean[LANES];
        this.explosionTime = new float[LANES];
        this.gameStarted = false;
        this.gamePaused = false;
        this.gameTime = 0f;
        this.countdownTime = 3f;
        this.score = 0;
        this.combo = 0;
        this.maxCombo = 0;
        this.escapePressed = false;
        this.shapeRenderer = new ShapeRenderer();
    }
    
    @Override
    public void show() {
        loadBeatmap();
        font = game.font;
        if (game.assetManager.isLoaded(TapTapGame.CURSOR_SOUND)) {
            hitSound = game.assetManager.get(TapTapGame.CURSOR_SOUND, Sound.class);
        }
        float gameAreaWidth = Gdx.graphics.getWidth() * 0.6f;
        laneWidth = gameAreaWidth / (float) LANES;
        laneStartX = (Gdx.graphics.getWidth() - gameAreaWidth) / 2f;
        hitLineY = Gdx.graphics.getHeight() * 0.2f;
        noteSpeed = Gdx.graphics.getHeight() * 0.4f;
        loadGameMusic();
        loadBackgroundImage();
        gameStarted = false;
        gameTime = 0f;
        countdownTime = 3f;
    }
    
    private void loadBeatmap() {
        try {
            String beatmapPath = rhythmPath + "/beatmap.json";
            String jsonString = Gdx.files.internal(beatmapPath).readString();
            
            com.badlogic.gdx.utils.JsonValue root = new com.badlogic.gdx.utils.JsonReader().parse(jsonString);
            
            beatmap = new Beatmap();
            beatmap.setTitle(root.getString("title", "Unknown"));
            beatmap.setArtist(root.getString("artist", "Unknown"));
            beatmap.setDifficulty(root.getString("difficulty", "Normal"));
            beatmap.setAudioLeadIn(root.getFloat("audioLeadIn", 0f));
            beatmap.setPreviewTime(root.getFloat("previewTime", 0f));
            
            com.badlogic.gdx.utils.JsonValue notesArray = root.get("notes");
            if (notesArray != null) {
                for (com.badlogic.gdx.utils.JsonValue noteValue : notesArray) {
                    float time = noteValue.getFloat("time");
                    int lane = noteValue.getInt("lane");
                    String typeStr = noteValue.getString("type");
                    Note.NoteType type = Note.NoteType.valueOf(typeStr);
                    
                    Note note;
                    if (type == Note.NoteType.HOLD && noteValue.has("endTime")) {
                        float endTime = noteValue.getFloat("endTime");
                        note = new Note(time, lane, type, endTime);
                    } else {
                        note = new Note(time, lane, type);
                    }
                    
                    beatmap.addNote(note);
                }
            }
            
            System.out.println("Loaded beatmap: " + beatmap.getTitle() + " by " + beatmap.getArtist());
            System.out.println("Notes loaded: " + beatmap.getTotalNotes());
            
        } catch (Exception e) {
            System.err.println("Error loading beatmap: " + e.getMessage());
            e.printStackTrace();
            beatmap = new Beatmap();
        }
    }

    private void loadGameMusic() {
        try {
            String musicPath = rhythmPath + "/assets/";
            
            String[] possibleFiles = {
                "Mick Gordon - The Only Thing They Fear Is You.mp3",
                "music.mp3", "song.mp3", "audio.mp3"
            };
            
            for (String fileName : possibleFiles) {
                try {
                    String fullPath = musicPath + fileName;
                    if (Gdx.files.internal(fullPath).exists()) {
                        gameMusic = Gdx.audio.newMusic(Gdx.files.internal(fullPath));
                        System.out.println("Loaded music: " + fullPath);
                        return;
                    }
                } catch (Exception e) {
                }
            }
            
            if (game.assetManager.isLoaded(TapTapGame.MENU_MUSIC)) {
                gameMusic = game.assetManager.get(TapTapGame.MENU_MUSIC, Music.class);
                System.out.println("Using fallback menu music");
            }
        } catch (Exception e) {
            System.err.println("Error loading game music: " + e.getMessage());
        }
    }
    
    private void loadBackgroundImage() {
        try {
            String[] possibleExtensions = {"png", "jpg", "jpeg"};
            
            for (String extension : possibleExtensions) {
                String imagePath = rhythmPath + "/assets/GameplayImage." + extension;
                
                if (Gdx.files.internal(imagePath).exists()) {
                    backgroundImage = new Texture(Gdx.files.internal(imagePath));
                    System.out.println("Loaded background image: " + imagePath);
                    return;
                }
            }
            
            System.out.println("GameplayImage not found for rhythm: " + rhythmPath);
            backgroundImage = null;
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
            backgroundImage = null;
        }
    }    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
        
        if (!gamePaused) {
            update(delta);
        }
        
        handleInput();
        
        if (backgroundImage != null) {
            game.batch.begin();
            game.batch.setColor(1f, 1f, 1f, 0.3f);
            game.batch.draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            game.batch.setColor(1f, 1f, 1f, 1f);
            game.batch.end();
        }
        
        drawAllShapes();
        
        game.batch.begin();
        drawKeyIndicators();
        drawUI();
        drawFeedback();
        game.batch.end();
    }
    
    private void drawAllShapes() {
        shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        drawLanesWithShape();
        
        drawHitLineWithShape();
        
        drawHitCirclesWithShape();
        
        drawNotesWithShape();
        
        shapeRenderer.end();
    }
    
    private void drawLanesWithShape() {
        shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 0.8f);
        float gameAreaWidth = laneWidth * LANES;
        shapeRenderer.rect(laneStartX, 0, gameAreaWidth, Gdx.graphics.getHeight());
        
        shapeRenderer.setColor(0.4f, 0.4f, 0.5f, 0.8f);
        for (int i = 1; i < LANES; i++) {
            float x = laneStartX + i * laneWidth;
            shapeRenderer.rect(x - 1, 0, 2, Gdx.graphics.getHeight());
        }
        
        shapeRenderer.setColor(0.6f, 0.6f, 0.7f, 1f);
        shapeRenderer.rect(laneStartX - 3, 0, 3, Gdx.graphics.getHeight());
        shapeRenderer.rect(laneStartX + gameAreaWidth, 0, 3, Gdx.graphics.getHeight());
    }
    
    private void drawHitLineWithShape() {
        float gameAreaWidth = laneWidth * LANES;
        
        shapeRenderer.setColor(1f, 1f, 0f, 0.3f);
        shapeRenderer.rect(laneStartX, hitLineY - 6, gameAreaWidth, 12);
        
        shapeRenderer.setColor(1f, 1f, 0f, 0.9f);
        shapeRenderer.rect(laneStartX, hitLineY - 3, gameAreaWidth, 6);
        
        shapeRenderer.setColor(1f, 1f, 1f, 0.8f);
        shapeRenderer.rect(laneStartX, hitLineY - 1, gameAreaWidth, 2);
    }
    
    private void drawHitCirclesWithShape() {
        for (int i = 0; i < LANES; i++) {
            Color color = LANE_COLORS[i];
            
            float circleX = laneStartX + i * laneWidth + laneWidth / 2f;
            float circleY = hitLineY;
            float baseRadius = laneWidth * 0.18f;
            
            shapeRenderer.setColor(color.r, color.g, color.b, 0.05f);
            shapeRenderer.circle(circleX, circleY, baseRadius * 2.0f);
            
            shapeRenderer.setColor(color.r, color.g, color.b, 0.1f);
            shapeRenderer.circle(circleX, circleY, baseRadius * 1.5f);
            
            shapeRenderer.setColor(color.r, color.g, color.b, 0.15f);
            shapeRenderer.circle(circleX, circleY, baseRadius * 1.2f);
            
            shapeRenderer.setColor(color.r, color.g, color.b, 1f);
            shapeRenderer.circle(circleX, circleY, baseRadius + 0.5f);
            
            shapeRenderer.setColor(1f, 1f, 1f, 1f);
            shapeRenderer.circle(circleX, circleY, baseRadius);
            
            if (lanePressed[i]) {
                shapeRenderer.setColor(color.r, color.g, color.b, 0.6f);
                shapeRenderer.circle(circleX, circleY, baseRadius * 1.3f);
                
                shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 1f);
                shapeRenderer.circle(circleX, circleY, baseRadius);
            }
            
            shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 0.4f);
            shapeRenderer.circle(circleX, circleY, baseRadius * 0.7f);
            
            if (explosionActive[i]) {
                float explosionProgress = 1f - (explosionTime[i] / EXPLOSION_DURATION);
                float explosionRadius = baseRadius * (1f + explosionProgress * 1.2f);
                float explosionAlpha = 1f - explosionProgress;
                
                for (int ring = 0; ring < 2; ring++) {
                    float ringRadius = explosionRadius * (0.9f + ring * 0.15f);
                    float ringAlpha = explosionAlpha * (1f - ring * 0.3f);
                    
                    shapeRenderer.setColor(color.r, color.g, color.b, ringAlpha * 0.4f);
                    shapeRenderer.circle(circleX, circleY, ringRadius);
                }
                
                shapeRenderer.setColor(1f, 1f, 1f, explosionAlpha * 0.5f);
                shapeRenderer.circle(circleX, circleY, baseRadius * (1f + explosionProgress * 0.3f));
            }
        }
    }
    
    private void drawNotesWithShape() {
        for (Note note : activeNotes) {
            renderNote(note);
        }
    }
    
    private void drawKeyIndicators() {
        String[] keys = {"A", "S", "D", "F"};
        font.getData().setScale(0.8f);
        
        for (int i = 0; i < LANES; i++) {
            float keyX = laneStartX + i * laneWidth + laneWidth / 2f;
            float keyY = hitLineY;
            
            font.setColor(Color.RED);
            
            com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
            layout.setText(font, keys[i]);
            
            font.draw(game.batch, keys[i], 
                keyX - layout.width / 2f, 
                keyY + layout.height / 2f);
        }
        
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
    }
    
    private void update(float delta) {
        gameTime += delta;
        
        if (!gameStarted) {
            countdownTime -= delta;
            if (countdownTime <= 0) {
                gameStarted = true;
                gameTime = 0f;
                if (gameMusic != null) {
                    gameMusic.play();
                }
            }
            return;
        }
        
        if (feedbackTime > 0) {
            feedbackTime -= delta;
        }
        
        for (int i = 0; i < LANES; i++) {
            if (explosionActive[i]) {
                explosionTime[i] -= delta;
                if (explosionTime[i] <= 0) {
                    explosionActive[i] = false;
                }
            }
        }
        
        List<Note> notesToActivate = beatmap.getActiveNotes(gameTime, LOOK_AHEAD_TIME);
        
        for (Note note : notesToActivate) {
            if (!activeNotes.contains(note, true)) {
                activeNotes.add(note);
            }
        }
        
        for (int i = activeNotes.size - 1; i >= 0; i--) {
            Note note = activeNotes.get(i);
            if (note.getTime() < gameTime - MISS_WINDOW) {
                if (!note.isHit()) {
                    combo = 0;
                    showFeedback("FALLO", Color.RED);
                }
                activeNotes.removeIndex(i);
            }
        }
    }
    
    private void renderNote(Note note) {
        if (note.isHit() && (note.getHitType() == Note.HitType.PERFECT || note.getHitType() == Note.HitType.GREAT)) {
            return;
        }
        
        float timeUntilHit = note.getTime() - gameTime;
        float noteY = hitLineY + (timeUntilHit * noteSpeed);
        
        if (noteY < -100 || noteY > Gdx.graphics.getHeight() + 100) return;
        
        float noteX = laneStartX + note.getLane() * laneWidth + laneWidth / 2f;
        float radius = laneWidth * 0.15f;
        
        Color color = LANE_COLORS[note.getLane()];
        
        float distanceToHit = Math.abs(timeUntilHit);
        float proximityAlpha = 1f - Math.min(distanceToHit / VISUAL_HIT_ZONE, 1f);
        
        if (proximityAlpha > 0) {
            shapeRenderer.setColor(color.r, color.g, color.b, proximityAlpha * 0.2f);
            shapeRenderer.circle(noteX, noteY, radius * (1.3f + proximityAlpha * 0.3f));
        }
        
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.circle(noteX, noteY, radius + 2);
        
        shapeRenderer.setColor(color.r, color.g, color.b, 1f);
        shapeRenderer.circle(noteX, noteY, radius);
        
        shapeRenderer.setColor(1f, 1f, 1f, 0.4f);
        shapeRenderer.circle(noteX, noteY - radius * 0.2f, radius * 0.3f);
        
        if (note.getType() == Note.NoteType.HOLD) {
            float holdEndY = hitLineY + ((note.getEndTime() - gameTime) * noteSpeed);
            if (holdEndY > noteY) {
                float trailWidth = radius * 1.2f;
                
                shapeRenderer.setColor(1f, 1f, 1f, 0.9f);
                shapeRenderer.rect(noteX - trailWidth/2f - 2, noteY, 2, holdEndY - noteY);
                shapeRenderer.rect(noteX + trailWidth/2f, noteY, 2, holdEndY - noteY);
                
                shapeRenderer.setColor(color.r, color.g, color.b, 0.8f);
                shapeRenderer.rect(noteX - trailWidth/2f, noteY, trailWidth, holdEndY - noteY);
                
                shapeRenderer.setColor(1f, 1f, 1f, 0.2f);
                shapeRenderer.rect(noteX - 1, noteY, 2, holdEndY - noteY);
            }
        }
    }
    
    private void drawUI() {
        font.setColor(Color.WHITE);
        font.getData().setScale(0.8f);
        font.draw(game.batch, "Puntuación: " + score, 20, Gdx.graphics.getHeight() - 20);
        
        if (combo > 0) {
            font.setColor(Color.YELLOW);
            font.draw(game.batch, "Combo: " + combo + "x", 20, Gdx.graphics.getHeight() - 60);
        }
        
        font.setColor(Color.WHITE);
        font.getData().setScale(0.6f);
        String songInfo = beatmap.getTitle() + " - " + beatmap.getArtist();
        float songInfoWidth = font.getRegion().getRegionWidth() * songInfo.length() * 0.6f;
        font.draw(game.batch, songInfo, 
            (Gdx.graphics.getWidth() - songInfoWidth) / 2f, 
            Gdx.graphics.getHeight() - 20);
        
        if (!gameStarted) {
            int countdown = (int) Math.ceil(countdownTime);
            if (countdown > 0) {
                font.setColor(Color.RED);
                font.getData().setScale(3.0f);
                String countdownText = String.valueOf(countdown);
                
                com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
                layout.setText(font, countdownText);
                
                font.draw(game.batch, countdownText, 
                    (Gdx.graphics.getWidth() - layout.width) / 2f, 
                    (Gdx.graphics.getHeight() + layout.height) / 2f);
            } else if (countdownTime > -0.5f) {
                font.setColor(Color.GREEN);
                font.getData().setScale(2.5f);
                String goText = "¡ADELANTE!";
                
                com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
                layout.setText(font, goText);
                
                font.draw(game.batch, goText, 
                    (Gdx.graphics.getWidth() - layout.width) / 2f, 
                    (Gdx.graphics.getHeight() + layout.height) / 2f);
            }
        }
        
        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
    }
    
    private void drawFeedback() {
        if (feedbackTime > 0 && !lastHitFeedback.isEmpty()) {
            float fadeAlpha = feedbackTime / FEEDBACK_DURATION;
            
            float scale = 1.0f;
            if (lastHitFeedback.equals("PERFECTO")) {
                font.setColor(1f, 1f, 0f, fadeAlpha);
                scale = 1.2f;
            } else if (lastHitFeedback.equals("BIEN")) {
                font.setColor(0f, 1f, 0f, fadeAlpha);
                scale = 1.2f;
            } else if (lastHitFeedback.contains("HOLD")) {
                font.setColor(0f, 1f, 1f, fadeAlpha);
                scale = 1.2f;
            } else {
                font.setColor(1f, 0f, 0f, fadeAlpha);
                scale = 1.0f;
            }
            
            font.getData().setScale(scale);
            
            com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
            layout.setText(font, lastHitFeedback);
            
            float feedbackX = 1600f;
            float feedbackY = 650f;
            
            font.draw(game.batch, lastHitFeedback, feedbackX, feedbackY);
            
            font.getData().setScale(1.0f);
            font.setColor(Color.WHITE);
        }
    }
    
    private void showFeedback(String text, Color color) {
        lastHitFeedback = text;
        feedbackTime = FEEDBACK_DURATION;
    }
    
    private void triggerExplosion(int lane) {
        explosionActive[lane] = true;
        explosionTime[lane] = EXPLOSION_DURATION;
    }
    
    private void handleInput() {
        boolean[] currentPressed = new boolean[LANES];
        currentPressed[0] = Gdx.input.isKeyPressed(Input.Keys.A);
        currentPressed[1] = Gdx.input.isKeyPressed(Input.Keys.S);
        currentPressed[2] = Gdx.input.isKeyPressed(Input.Keys.D);
        currentPressed[3] = Gdx.input.isKeyPressed(Input.Keys.F);
        
        for (int lane = 0; lane < LANES; lane++) {
            if (currentPressed[lane] && !lanePressed[lane]) {
                hitLane(lane);
            }
            
            if (!currentPressed[lane] && lanePressed[lane]) {
                releaseHoldNote(lane);
            }
            
            laneHolding[lane] = currentPressed[lane];
            lanePressed[lane] = currentPressed[lane];
        }
        
        updateHoldNotes();
        
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && !escapePressed) {
            returnToMenu();
            escapePressed = true;
        } else if (!Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            escapePressed = false;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            togglePause();
        }
    }
    
    private void hitLane(int lane) {
        Note hitNote = findClosestHittableNote(lane);
        
        if (hitNote != null && !hitNote.isHit()) {
            float timingDifference = Math.abs(gameTime - hitNote.getTime());
            
            String feedback;
            int points;
            
            if (timingDifference <= PERFECT_WINDOW) {
                feedback = "PERFECTO";
                points = 3;
                combo++;
                hitNote.setHitType(Note.HitType.PERFECT);
                showFeedback(feedback, Color.YELLOW);
                triggerExplosion(lane);
            } else if (timingDifference <= GOOD_WINDOW) {
                feedback = "BIEN";
                points = 1;
                combo++;
                hitNote.setHitType(Note.HitType.GREAT);
                showFeedback(feedback, Color.GREEN);
                triggerExplosion(lane);
            } else if (timingDifference <= MISS_WINDOW) {
                feedback = "FALLO";
                points = 0;
                combo = 0;
                hitNote.setHitType(Note.HitType.MISS);
                showFeedback(feedback, Color.RED);
            } else {
                return;
            }
            
            hitNote.setHit(true);
            score += points;
            maxCombo = Math.max(maxCombo, combo);
            
            if (hitSound != null) {
                hitSound.play();
            }
        }
    }
    
    private Note findClosestHittableNote(int lane) {
        Note closestNote = null;
        float closestDistance = Float.MAX_VALUE;
        
        for (Note note : activeNotes) {
            if (note.getLane() == lane && !note.isHit()) {
                float distance = Math.abs(note.getTime() - gameTime);
                if (distance <= MISS_WINDOW && distance < closestDistance) {
                    closestDistance = distance;
                    closestNote = note;
                }
            }
        }
        
        return closestNote;
    }
    
    private void releaseHoldNote(int lane) {
        for (Note note : activeNotes) {
            if (note.getLane() == lane && note.getType() == Note.NoteType.HOLD && note.isHit()) {
                float currentTime = gameTime;
                float holdEndTime = note.getEndTime();
                
                if (currentTime < holdEndTime - GOOD_WINDOW) {
                    combo = 0;
                } else if (currentTime <= holdEndTime + GOOD_WINDOW) {
                    int holdPoints = 2;
                    score += holdPoints;
                }
                break;
            }
        }
    }
    
    private void updateHoldNotes() {
        for (Note note : activeNotes) {
            if (note.getType() == Note.NoteType.HOLD && note.isHit()) {
                int lane = note.getLane();
                float currentTime = gameTime;
                
                if (currentTime >= note.getTime() && currentTime <= note.getEndTime()) {
                    if (!laneHolding[lane]) {
                        combo = 0;
                    }
                }
            }
        }
    }
    
    private void togglePause() {
        gamePaused = !gamePaused;
        if (gameMusic != null) {
            if (gamePaused) {
                gameMusic.pause();
            } else {
                gameMusic.play();
            }
        }
    }
    
    private void returnToMenu() {
        if (gameMusic != null) {
            gameMusic.stop();
        }
        game.setScreen(new MainMenuScreen(game));
    }
    
    @Override
    public void resize(int width, int height) {
        float gameAreaWidth = width * 0.6f;
        laneWidth = gameAreaWidth / (float) LANES;
        laneStartX = (width - gameAreaWidth) / 2f;
        hitLineY = height * 0.2f;
        noteSpeed = height * 0.4f;
    }
    
    @Override
    public void pause() {
        if (gameMusic != null) {
            gameMusic.pause();
        }
        gamePaused = true;
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
        if (gameMusic != null) {
            gameMusic.stop();
        }
    }
    
    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        
        if (backgroundImage != null) {
            backgroundImage.dispose();
        }
    }
}
