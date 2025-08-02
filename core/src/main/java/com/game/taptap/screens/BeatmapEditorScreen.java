package com.game.taptap.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.game.taptap.TapTapGame;
import com.game.taptap.gameplay.Beatmap;
import com.game.taptap.gameplay.Note;

// Editor profesional de beatmaps con interfaz visual intuitiva
public class BeatmapEditorScreen implements Screen {
    
    private TapTapGame game;
    private String rhythmPath;
    private Beatmap beatmap;
    private Music editorMusic;
    private Sound placementSound;
    private BitmapFont font;
    private float uiFontScale = 0.7f;
    private ShapeRenderer shapeRenderer;
    
    // Configuración visual del editor
    private static final int LANES = 4;
    private static final Color[] LANE_COLORS = {
        new Color(1f, 0f, 0f, 1f),
        new Color(0f, 0f, 1f, 1f),
        new Color(0f, 1f, 0f, 1f),
        new Color(1f, 1f, 0f, 1f)
    };
    private static final String[] LANE_KEYS = {"A", "S", "D", "F"};
    
    // Área del editor
    private float editorX, editorY, editorWidth, editorHeight;
    private float laneWidth;
    private float hitLineY;
    
    // Control de tiempo y scroll
    private float currentTime = 0f;
    private float scrollOffset = 0f;
    private float pixelsPerSecond = 200f;
    private float maxTime = 300f;
    private float songDuration = 0f;
    private boolean isPlaying = false;
    private float playbackStartTime = 0f;
    
    // Barra de progreso vertical
    private Rectangle progressBar;
    private boolean draggingProgressBar = false;
    
    // Control de navegación suave
    private float tKeyHoldTime = 0f;
    private float gKeyHoldTime = 0f;
    private static final float BASE_SCROLL_SPEED = 1.0f;
    private static final float MAX_SCROLL_SPEED = 15.0f;
    
    // Modos de edición
    private enum EditMode {
        TAP, HOLD
    }
    private EditMode currentMode = EditMode.TAP;
    
    // Estado de colocación de notas Hold
    private boolean placingHoldNote = false;
    private float holdStartTime = 0f;
    private int holdStartLane = 0;
    
    // Input handling
    private boolean escapePressed = false;
    private boolean spacePressed = false;
    private boolean hasUnsavedChanges = false;
    
    public BeatmapEditorScreen(TapTapGame game, String rhythmPath) {
        this.game = game;
        this.rhythmPath = rhythmPath;
        this.beatmap = new Beatmap();
        this.shapeRenderer = new ShapeRenderer();
    }
    
    @Override
    public void show() {
        beatmap = new Beatmap();
        font = game.font;
        if (game.assetManager.isLoaded(TapTapGame.CURSOR_SOUND)) {
            placementSound = game.assetManager.get(TapTapGame.CURSOR_SOUND, Sound.class);
        }
        loadEditorMusic();
        calculateEditorDimensions();
        System.out.println("Editor de Beatmaps iniciado");
    }
    
    private void loadEditorMusic() {
        try {
            String musicPath = rhythmPath + "/assets/";
            // Buscar el archivo de música en el directorio del ritmo
            if (rhythmPath.contains("Doom Slayer")) {
                String doomMusic = musicPath + "Mick Gordon - The Only Thing They Fear Is You.mp3";
                if (Gdx.files.internal(doomMusic).exists()) {
                    editorMusic = Gdx.audio.newMusic(Gdx.files.internal(doomMusic));
                    System.out.println("Música cargada: " + doomMusic);
                }
            } else if (rhythmPath.contains("FF16")) {
                String ff16Music = musicPath + "Final Fantasy XVI.mp3";
                if (Gdx.files.internal(ff16Music).exists()) {
                    editorMusic = Gdx.audio.newMusic(Gdx.files.internal(ff16Music));
                    System.out.println("Música cargada: " + ff16Music);
                }
            } else if (rhythmPath.contains("Mashle")) {
                String mashleMusic = musicPath + "Serious Steel.mp3";
                if (Gdx.files.internal(mashleMusic).exists()) {
                    editorMusic = Gdx.audio.newMusic(Gdx.files.internal(mashleMusic));
                    System.out.println("Música cargada: " + mashleMusic);
                }
            }
            // Agregar más casos según los ritmos disponibles
            
            if (editorMusic == null) {
                // Fallback a la música del menú si no se encuentra la específica
                if (game.assetManager.isLoaded(TapTapGame.MENU_MUSIC)) {
                    editorMusic = game.assetManager.get(TapTapGame.MENU_MUSIC, Music.class);
                    System.out.println("Usando música del menú como fallback");
                }
            }
            
            // Detectar duración de la canción
            if (editorMusic != null) {
                // En una implementación real, usar librería para leer metadatos
                if (rhythmPath.contains("Doom Slayer")) {
                    songDuration = 86f;
                } else if (rhythmPath.contains("FF16")) {
                    songDuration = 240f;
                } else if (rhythmPath.contains("Mashle")) {
                    songDuration = 210f;
                } else {
                    songDuration = 180f;
                }
                
                maxTime = songDuration;
                System.out.println("Duración de la canción: " + formatTime(songDuration));
                System.out.println("Tiempo máximo del editor: " + formatTime(maxTime));
            }
            
        } catch (Exception e) {
            System.out.println("Error cargando música: " + e.getMessage());
            // Usar música del menú como fallback
            if (game.assetManager.isLoaded(TapTapGame.MENU_MUSIC)) {
                editorMusic = game.assetManager.get(TapTapGame.MENU_MUSIC, Music.class);
                songDuration = 120f;
                maxTime = 150f;
            }
        }
    }
    
    private void calculateEditorDimensions() {
        float margin = 30f;
        editorX = Gdx.graphics.getWidth() * 0.25f;
        editorY = margin;
        editorWidth = Gdx.graphics.getWidth() * 0.45f;
        editorHeight = Gdx.graphics.getHeight() - (margin * 2);
        
        // Área para los lanes
        laneWidth = editorWidth * 0.9f / LANES;
        
        // Línea de hit
        hitLineY = editorY + editorHeight * 0.08f;
        
        // Barra de progreso vertical
        float progressBarWidth = 25f;
        float progressBarHeight = editorHeight - 40f;
        float progressBarX = editorX + editorWidth + 30f;
        float progressBarY = editorY + 20f;
        
        // Inicializar Rectangle de barra de progreso
        if (progressBar == null) {
            progressBar = new Rectangle();
        }
        progressBar.set(progressBarX, progressBarY, progressBarWidth, progressBarHeight);
    }
    
    // Formatea tiempo MM:SS
    private String formatTime(float timeInSeconds) {
        int minutes = (int) (timeInSeconds / 60);
        int seconds = (int) (timeInSeconds % 60);
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private void drawProgressBar() {
        if (songDuration <= 0) return;
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        shapeRenderer.rect(progressBar.x, progressBar.y, progressBar.width, progressBar.height);
        float progress = Math.min(currentTime / songDuration, 1.0f);
        float progressHeight = progressBar.height * progress;
        shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 0.9f);
        shapeRenderer.rect(progressBar.x, progressBar.y, progressBar.width, progressHeight);
        shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 0.8f);
        shapeRenderer.rect(progressBar.x - 1, progressBar.y - 1, progressBar.width + 2, progressBar.height + 2);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1.0f);
        shapeRenderer.rect(progressBar.x, progressBar.y, progressBar.width, progressBar.height);
        shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 0.9f);
        shapeRenderer.rect(progressBar.x, progressBar.y, progressBar.width, progressHeight);
    }
    
    private void drawProgressBarLabels() {
        if (songDuration <= 0) return;
        game.font.getData().setScale(0.6f);
        game.font.setColor(1, 1, 1, 1);
        String totalTimeStr = formatTime(songDuration);
        game.font.draw(game.batch, totalTimeStr, progressBar.x + progressBar.width + 10, progressBar.y + progressBar.height + 20);
        String currentTimeStr = formatTime(currentTime);
        float progress = Math.min(currentTime / songDuration, 1.0f);
        float currentY = progressBar.y + (progressBar.height * progress);
        game.font.draw(game.batch, currentTimeStr, progressBar.x + progressBar.width + 10, currentY + 5);
        game.font.draw(game.batch, "0:00", progressBar.x + progressBar.width + 10, progressBar.y - 5);
        game.font.getData().setScale(1f);
    }
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1f);
        update(delta);
        handleInput();
        drawEditor();
        drawUI();
    }
    
    private void update(float delta) {
        if (isPlaying && editorMusic != null) {
            currentTime += delta;
            if (currentTime >= songDuration) {
                isPlaying = false;
                currentTime = songDuration;
                if (editorMusic != null && editorMusic.isPlaying()) {
                    editorMusic.stop();
                }
                System.out.println("Canción terminada - detenida automáticamente");
                return;
            }
            float timeFromHitLine = currentTime - scrollOffset;
            float visibleRange = editorHeight / pixelsPerSecond;
            if (timeFromHitLine < 1f || timeFromHitLine > visibleRange - 1f) {
                scrollOffset = currentTime - 2f;
                scrollOffset = Math.max(scrollOffset, 0f);
                scrollOffset = Math.min(scrollOffset, maxTime);
            }
        } else {
            currentTime = scrollOffset;
        }
    }
    
    private void drawEditor() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        shapeRenderer.setColor(0.1f, 0.1f, 0.15f, 1f);
        shapeRenderer.rect(editorX, editorY, editorWidth, editorHeight);
        for (int i = 0; i <= LANES; i++) {
            float x = editorX + (editorWidth * 0.05f) + (i * laneWidth);
            shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
            shapeRenderer.rectLine(x, editorY, x, editorY + editorHeight, 2f);
        }
        shapeRenderer.setColor(1f, 0f, 0f, 0.8f);
        shapeRenderer.rectLine(editorX + (editorWidth * 0.05f), hitLineY, editorX + (editorWidth * 0.05f) + (laneWidth * LANES), hitLineY, 4f);
        for (int tenths = 0; tenths <= (int)(maxTime * 10); tenths++) {
            float time = tenths / 10.0f;
            float y = getYForTime(time);
            if (y >= editorY && y <= editorY + editorHeight) {
                float thickness;
                if (tenths % 10 == 0) {
                    thickness = 2.5f;
                    shapeRenderer.setColor(0.4f, 0.4f, 0.45f, 1f);
                } else if (tenths % 5 == 0) {
                    thickness = 1.8f;
                    shapeRenderer.setColor(0.3f, 0.3f, 0.35f, 1f);
                } else {
                    thickness = 1f;
                    shapeRenderer.setColor(0.2f, 0.2f, 0.25f, 1f);
                }
                shapeRenderer.rectLine(editorX + (editorWidth * 0.05f), y, editorX + (editorWidth * 0.05f) + (laneWidth * LANES), y, thickness);
            }
        }
        drawNotes();
        if (placingHoldNote) {
            drawPlacingHoldNote();
        }
        drawCurrentTimeIndicator();
        drawProgressBar();
        shapeRenderer.end();
        game.batch.begin();
        drawEditorLabels();
        drawProgressBarLabels();
        game.batch.end();
    }
    
    private void drawNotes() {
        for (Note note : beatmap.getNotes()) {
            float y = getYForTime(note.getTime());
            if (y < editorY - 20 || y > editorY + editorHeight + 20) continue;
            
            float x = editorX + (editorWidth * 0.05f) + (note.getLane() * laneWidth) + (laneWidth / 2f);
            Color color = LANE_COLORS[note.getLane()];
            
            if (note.getType() == Note.NoteType.TAP) {
                // Dibujar círculo para nota TAP (más pequeño)
                shapeRenderer.setColor(color);
                shapeRenderer.circle(x, y, 12f);
                shapeRenderer.setColor(1f, 1f, 1f, 1f);
                shapeRenderer.circle(x, y, 9f);
                shapeRenderer.setColor(color);
                shapeRenderer.circle(x, y, 6f);
            } else if (note.getType() == Note.NoteType.HOLD) {
                // Dibujar línea vertical para nota HOLD
                float startY = getYForTime(note.getTime());
                float endY = getYForTime(note.getEndTime());
                
                // Línea del hold (más delgada)
                shapeRenderer.setColor(color.r, color.g, color.b, 0.7f);
                shapeRenderer.rectLine(x, startY, x, endY, 6f);
                
                // Círculos de inicio y fin (más pequeños)
                shapeRenderer.setColor(color);
                shapeRenderer.circle(x, startY, 12f);
                shapeRenderer.circle(x, endY, 12f);
                shapeRenderer.setColor(1f, 1f, 1f, 1f);
                shapeRenderer.circle(x, startY, 9f);
                shapeRenderer.circle(x, endY, 9f);
                shapeRenderer.setColor(color);
                shapeRenderer.circle(x, startY, 6f);
                shapeRenderer.circle(x, endY, 6f);
            }
        }
    }
    
    private void drawPlacingHoldNote() {
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float startY = getYForTime(holdStartTime);
        float x = editorX + (editorWidth * 0.05f) + (holdStartLane * laneWidth) + (laneWidth / 2f);
        
        Color color = LANE_COLORS[holdStartLane];
        shapeRenderer.setColor(color.r, color.g, color.b, 0.5f);
        shapeRenderer.rectLine(x, startY, x, mouseY, 6f);
        
        shapeRenderer.setColor(color);
        shapeRenderer.circle(x, startY, 12f);
    }
    
    private void drawCurrentTimeIndicator() {
        float y = getYForTime(currentTime);
        if (y >= editorY && y <= editorY + editorHeight) {
            shapeRenderer.setColor(0f, 1f, 1f, 0.9f);
            shapeRenderer.rectLine(editorX + (editorWidth * 0.05f), y,
                                 editorX + (editorWidth * 0.05f) + (laneWidth * LANES), y, 4f);
        }
    }
    
    private void drawEditorLabels() {
        // Dibujar etiquetas de lanes
        for (int i = 0; i < LANES; i++) {
            float x = editorX + (editorWidth * 0.05f) + (i * laneWidth) + (laneWidth / 2f);
            float y = editorY + editorHeight + 20f;
            
            Color color = LANE_COLORS[i];
            font.setColor(color);
            font.draw(game.batch, LANE_KEYS[i], x - 5f, y);
        }
        
        font.getData().setScale(0.5f);
        
        for (int tenths = 0; tenths <= (int)(maxTime * 10); tenths++) {
            float time = tenths / 10.0f;
            float y = getYForTime(time);
            if (y >= editorY && y <= editorY + editorHeight) {
                
                if (tenths % 10 == 0) {
                    // Segundos completos
                    int totalSeconds = (int)time;
                    int minutes = totalSeconds / 60;
                    int seconds = totalSeconds % 60;
                    
                    if (minutes > 0) {
                        font.setColor(0.2f, 1f, 0.2f, 1f);
                        font.getData().setScale(0.6f);
                        String timeText = String.format("%d:%02d", minutes, seconds);
                        font.draw(game.batch, timeText, editorX - 80f, y + 6f);
                    } else {
                        font.setColor(1f, 1f, 0.2f, 1f);
                        font.getData().setScale(0.6f);
                        String timeText = String.format("%d.0", seconds);
                        font.draw(game.batch, timeText, editorX - 60f, y + 6f);
                    }
                    font.getData().setScale(0.5f);
                    
                } else if (tenths % 5 == 0) {
                    font.setColor(1f, 1f, 0.4f, 1f);
                    String timeText = String.format("%.1f", time);
                    font.draw(game.batch, timeText, editorX - 50f, y + 4f);
                    
                } else {
                    if (tenths % 2 == 0) {
                        font.setColor(0.7f, 0.7f, 0.7f, 1f);
                        font.getData().setScale(0.4f);
                        String timeText = String.format("%.1f", time);
                        font.draw(game.batch, timeText, editorX - 45f, y + 2f);
                        font.getData().setScale(0.5f);
                    }
                }
            }
        }
        
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
    }
    
    private void drawUI() {
        game.batch.begin();
        float startX = 20;
        float startY = Gdx.graphics.getHeight() - 20;
        float lineSpacing = 28f;
        float sectionSpacing = 38f;
        float y = startY;
        font.getData().setScale(uiFontScale);

        // Información del editor
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Editor de Beatmaps", startX, y); y -= lineSpacing;
        font.draw(game.batch, "Modo: " + currentMode.name(), startX, y); y -= lineSpacing;
        
        // Mostrar tiempo actual con más precisión y color
        font.setColor(Color.CYAN);
        font.draw(game.batch, "Tiempo Actual: " + String.format("%.1f", currentTime), startX, y); y -= lineSpacing;
        
        // Mostrar posición de navegación
        font.setColor(Color.YELLOW);
        font.draw(game.batch, "Navegando en: " + String.format("%.1f", scrollOffset), startX, y); y -= lineSpacing;
        
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Estado: " + (isPlaying ? "Reproduciendo" : "Pausado"), startX, y); y -= lineSpacing;
        font.draw(game.batch, "Notas: " + beatmap.getNotes().size, startX, y); y -= sectionSpacing;

        // Controles
        font.setColor(Color.LIGHT_GRAY);
        font.draw(game.batch, "Controles:", startX, y); y -= lineSpacing;
        font.draw(game.batch, "Click Izq: Colocar", startX, y); y -= lineSpacing;
        font.draw(game.batch, "Click Der: Eliminar", startX, y); y -= lineSpacing;
        font.draw(game.batch, "T/G: Navegar", startX, y); y -= lineSpacing;
        font.draw(game.batch, "Space: Play/Pause", startX, y); y -= lineSpacing;
        font.draw(game.batch, "1/2: TAP/HOLD", startX, y); y -= sectionSpacing;

        // Botón de guardar
        if (hasUnsavedChanges) {
            font.setColor(Color.YELLOW);
            font.draw(game.batch, "S - GUARDAR*", startX, y); y -= lineSpacing;
            font.setColor(Color.WHITE);
        } else {
            font.setColor(Color.WHITE);
            font.draw(game.batch, "S - Guardar", startX, y); y -= lineSpacing;
        }

        // Instrucciones para Hold
        if (currentMode == EditMode.HOLD) {
            font.setColor(Color.CYAN);
            String holdMsg = placingHoldNote ? "Click para finalizar Hold" : "Click para iniciar Hold";
            font.draw(game.batch, holdMsg, Gdx.graphics.getWidth() / 2f - 100, 60);
            font.setColor(Color.WHITE);
        }

        font.getData().setScale(1f); // Restaurar escala
        game.batch.end();
    }
    
    private float getYForTime(float time) {
        // Sistema fijo: el editor no se mueve, solo las notas
        return hitLineY + ((time - scrollOffset) * pixelsPerSecond);
    }
    
    private float getTimeForY(float y) {
        // Sistema fijo: calcular tiempo basado en posición fija del editor
        return scrollOffset + ((y - hitLineY) / pixelsPerSecond);
    }
    
    private void handleInput() {
        // Control de reproducción
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !spacePressed) {
            togglePlayback();
            spacePressed = true;
        } else if (!Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            spacePressed = false;
        }

        // Cambio de modo
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            currentMode = EditMode.TAP;
            placingHoldNote = false;
            System.out.println("Modo: TAP");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            currentMode = EditMode.HOLD;
            System.out.println("Modo: HOLD");
        }

        if (Gdx.input.isKeyPressed(Input.Keys.T)) {
            tKeyHoldTime += Gdx.graphics.getDeltaTime();
            float speed = BASE_SCROLL_SPEED + (tKeyHoldTime * 1.5f);
            speed = Math.min(speed, MAX_SCROLL_SPEED);
            scrollOffset -= speed * Gdx.graphics.getDeltaTime();
            scrollOffset = Math.max(scrollOffset, 0f);
            
            // Solo sincronizar currentTime con scrollOffset si NO está reproduciendo
            if (!isPlaying) {
                currentTime = scrollOffset;
            }
        } else {
            tKeyHoldTime = 0f;
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.G)) {
            gKeyHoldTime += Gdx.graphics.getDeltaTime();
            float speed = BASE_SCROLL_SPEED + (gKeyHoldTime * 1.5f);
            speed = Math.min(speed, MAX_SCROLL_SPEED);
            scrollOffset += speed * Gdx.graphics.getDeltaTime();
            scrollOffset = Math.min(scrollOffset, maxTime);
            
            if (!isPlaying) {
                currentTime = scrollOffset;
            }
        } else {
            gKeyHoldTime = 0f;
        }
        
        // Navegación rápida
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            scrollOffset = 0f;
            currentTime = 0f;
            if (isPlaying && editorMusic != null) {
                editorMusic.setPosition(0f);
            }
            System.out.println("Navegación rápida: Inicio (0:00)");
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
            scrollOffset = songDuration;
            currentTime = songDuration;
            if (isPlaying && editorMusic != null) {
                editorMusic.setPosition(songDuration);
            }
            System.out.println("Navegación rápida: Final (" + formatTime(songDuration) + ")");
        }

        // Input del mouse (solo para colocar/quitar notas, no para mover el timeline)
        handleMouseInput();

        // Guardar
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            saveBeatmap();
        }

        // Salir
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && !escapePressed) {
            exitEditor();
            escapePressed = true;
        } else if (!Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            escapePressed = false;
        }
    }
    
    private void handleMouseInput() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        
        // Verificar si el click es en la barra de progreso
        if (Gdx.input.justTouched() && progressBar != null && progressBar.contains(mouseX, mouseY)) {
            handleProgressBarClick(mouseX, mouseY);
            return;
        }
        
        // Verificar si el mouse está en el área del editor
        float laneStartX = editorX + (editorWidth * 0.05f);
        float laneEndX = laneStartX + (laneWidth * LANES);
        
        if (mouseX >= laneStartX && mouseX <= laneEndX && 
            mouseY >= editorY && mouseY <= editorY + editorHeight) {
            
            // Determinar lane
            int lane = (int) ((mouseX - laneStartX) / laneWidth);
            lane = Math.max(0, Math.min(LANES - 1, lane));
            
            // Determinar tiempo
            float time = getTimeForY(mouseY);
            time = Math.max(0f, Math.min(maxTime, time));
            
            // Click izquierdo - colocar nota
            if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                placeNote(time, lane);
            }
            
            // Click derecho - eliminar nota
            if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                deleteNoteAt(time, lane);
            }
        }
    }
    
    private void handleProgressBarClick(float mouseX, float mouseY) {
        if (songDuration <= 0) return;
        
        // Calcular el porcentaje basado en la posición Y del click en la barra
        float relativeY = mouseY - progressBar.y;
        float progress = relativeY / progressBar.height;
        progress = Math.max(0f, Math.min(1f, progress));
        
        // Convertir a tiempo
        float newTime = progress * songDuration;
        
        // Actualizar currentTime y scrollOffset
        currentTime = newTime;
        scrollOffset = newTime;
        
        // Si la música está reproduciéndose, ajustar la posición
        if (isPlaying && editorMusic != null) {
            editorMusic.setPosition(newTime);
        }
        
        System.out.println("Navegación en barra de progreso: " + formatTime(newTime));
    }
    
    private void placeNote(float time, int lane) {
        if (currentMode == EditMode.TAP) {
            // Verificar que no haya otra nota muy cerca
            if (!hasNoteNear(time, lane, 0.1f)) {
                Note newNote = new Note(time, lane, Note.NoteType.TAP);
                beatmap.addNote(newNote);
                hasUnsavedChanges = true;
                playPlacementSound();
                System.out.println("Nota TAP colocada: Lane " + LANE_KEYS[lane] + ", Tiempo " + String.format("%.2f", time));
            }
        } else if (currentMode == EditMode.HOLD) {
            if (!placingHoldNote) {
                // Iniciar colocación de Hold
                holdStartTime = time;
                holdStartLane = lane;
                placingHoldNote = true;
                System.out.println("Iniciando Hold en Lane " + LANE_KEYS[lane] + ", Tiempo " + String.format("%.2f", time));
            } else {
                // Finalizar colocación de Hold
                if (lane == holdStartLane && time > holdStartTime + 0.1f) {
                    Note newNote = new Note(holdStartTime, holdStartLane, Note.NoteType.HOLD, time);
                    beatmap.addNote(newNote);
                    hasUnsavedChanges = true;
                    playPlacementSound();
                    System.out.println("Nota HOLD colocada: Lane " + LANE_KEYS[holdStartLane] + 
                                     ", Tiempo " + String.format("%.2f - %.2f", holdStartTime, time));
                }
                placingHoldNote = false;
            }
        }
    }
    
    private void deleteNoteAt(float time, int lane) {
        Note noteToDelete = findNoteAt(time, lane);
        if (noteToDelete != null) {
            beatmap.removeNote(noteToDelete);
            hasUnsavedChanges = true;
            playPlacementSound();
            System.out.println("Nota eliminada: Lane " + LANE_KEYS[lane] + ", Tiempo " + String.format("%.2f", time));
        }
        placingHoldNote = false;
    }
    
    private Note findNoteAt(float time, int lane) {
        for (Note note : beatmap.getNotes()) {
            if (note.getLane() == lane) {
                if (note.getType() == Note.NoteType.TAP) {
                    if (Math.abs(note.getTime() - time) < 0.2f) {
                        return note;
                    }
                } else if (note.getType() == Note.NoteType.HOLD) {
                    if ((Math.abs(note.getTime() - time) < 0.2f) || 
                        (Math.abs(note.getEndTime() - time) < 0.2f) ||
                        (time >= note.getTime() && time <= note.getEndTime())) {
                        return note;
                    }
                }
            }
        }
        return null;
    }
    
    private boolean hasNoteNear(float time, int lane, float threshold) {
        for (Note note : beatmap.getNotes()) {
            if (note.getLane() == lane && Math.abs(note.getTime() - time) < threshold) {
                return true;
            }
        }
        return false;
    }
    
    private void togglePlayback() {
        isPlaying = !isPlaying;
        if (editorMusic != null) {
            if (isPlaying) {
                // Si estamos cerca del final, no reproducir
                if (currentTime >= songDuration - 0.1f) {
                    isPlaying = false;
                    currentTime = songDuration;
                    System.out.println("No se puede reproducir: muy cerca del final");
                    return;
                }
                
                // Asegurar que currentTime esté sincronizado con scrollOffset antes de reproducir
                currentTime = scrollOffset;
                
                // Reproducir desde la posición actual exacta
                editorMusic.setPosition(currentTime);
                editorMusic.play();
                System.out.println("Reproduciendo desde " + String.format("%.2f", currentTime));
            } else {
                editorMusic.pause();
                // Mantener currentTime donde está, no cambiarla
                System.out.println("Pausado en " + String.format("%.2f", currentTime));
            }
        }
    }
    
    private void saveBeatmap() {
        // Crear información del beatmap basada en el ritmo seleccionado
        if (rhythmPath.contains("Doom Slayer")) {
            beatmap.setTitle("The Only Thing They Fear Is You");
            beatmap.setArtist("Mick Gordon");
            beatmap.setDifficulty("Editor");
        } else if (rhythmPath.contains("FF16")) {
            beatmap.setTitle("Final Fantasy XVI");
            beatmap.setArtist("Masayoshi Soken");
            beatmap.setDifficulty("Editor");
        } else if (rhythmPath.contains("Mashle")) {
            beatmap.setTitle("Serious Steel");
            beatmap.setArtist("Anime OST");
            beatmap.setDifficulty("Editor");
        } else {
            beatmap.setTitle("Beatmap Editado");
            beatmap.setArtist("Editor");
            beatmap.setDifficulty("Normal");
        }
        
        // Guardar en el path del ritmo
        String beatmapPath = rhythmPath + "/beatmap.json";
        beatmap.saveToFile(beatmapPath);
        
        hasUnsavedChanges = false;
        System.out.println("Beatmap guardado en: " + beatmapPath);
        System.out.println("Título: " + beatmap.getTitle());
        System.out.println("Artista: " + beatmap.getArtist());
        System.out.println("Total de notas: " + beatmap.getNotes().size);
        
        // Mostrar estadísticas por lane
        for (int lane = 0; lane < LANES; lane++) {
            int tapCount = 0;
            int holdCount = 0;
            for (Note note : beatmap.getNotes()) {
                if (note.getLane() == lane) {
                    if (note.getType() == Note.NoteType.TAP) tapCount++;
                    else holdCount++;
                }
            }
            System.out.println("Lane " + LANE_KEYS[lane] + ": " + tapCount + " TAP, " + holdCount + " HOLD");
        }
        
        if (placementSound != null) {
            placementSound.play();
        }
    }
    
    private void exitEditor() {
        if (hasUnsavedChanges) {
            System.out.println("¡Advertencia! Tienes cambios sin guardar. Presiona S para guardar antes de salir.");
        }
        
        if (editorMusic != null) {
            editorMusic.stop();
        }
        
        // Volver al menú principal
        game.setScreen(new MainMenuScreen(game));
    }
    
    private void playPlacementSound() {
        if (placementSound != null) {
            placementSound.play();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        calculateEditorDimensions();
        shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
    }
    
    @Override
    public void pause() {
        if (editorMusic != null) {
            editorMusic.pause();
        }
        isPlaying = false;
    }
    
    @Override
    public void resume() {
        // No hacer nada - dejar que el usuario controle la reproducción
    }
    
    @Override
    public void hide() {
        if (editorMusic != null) {
            editorMusic.stop();
        }
    }
    
    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        // Los demás assets son manejados por TapTapGame
    }
}
