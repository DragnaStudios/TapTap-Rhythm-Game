package com.game.taptap.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.game.taptap.TapTapGame;

import java.util.ArrayList;
import java.util.List;

// Animacion de las notas de fondo, nada relevante de nuevo
public class MenuBackgroundEffect {
    
    private static final int MAX_NOTES = 8;
    private static final float NOTE_SPAWN_RATE = 1.5f;
    private static final float NOTE_SIZE = 100f;
    private static final float MIN_FALL_SPEED = 50f;
    private static final float MAX_FALL_SPEED = 120f;
    private static final float MAX_ROTATION_SPEED = 30f;
    
    private TapTapGame game;
    private List<FallingNote> notes;
    private float spawnTimer;
    
    // Texturas de las notas
    private Texture note1Texture;
    private Texture note2Texture;
    private Texture note3Texture;
    private Texture note4Texture;
    private Texture[] noteTextures;
    
    public MenuBackgroundEffect(TapTapGame game) {
        this.game = game;
        this.notes = new ArrayList<>();
        this.spawnTimer = NOTE_SPAWN_RATE - 0.5f;
        loadTextures();
    }
    
    private void loadTextures() {
        // Cargar las texturas de las notas si están disponibles
        if (game.assetManager.isLoaded(TapTapGame.NOTE_1)) {
            note1Texture = game.assetManager.get(TapTapGame.NOTE_1, Texture.class);
        }
        if (game.assetManager.isLoaded(TapTapGame.NOTE_2)) {
            note2Texture = game.assetManager.get(TapTapGame.NOTE_2, Texture.class);
        }
        if (game.assetManager.isLoaded(TapTapGame.NOTE_3)) {
            note3Texture = game.assetManager.get(TapTapGame.NOTE_3, Texture.class);
        }
        if (game.assetManager.isLoaded(TapTapGame.NOTE_4)) {
            note4Texture = game.assetManager.get(TapTapGame.NOTE_4, Texture.class);
        }
        
        // Crear array con las texturas disponibles
        List<Texture> availableTextures = new ArrayList<>();
        if (note1Texture != null) availableTextures.add(note1Texture);
        if (note2Texture != null) availableTextures.add(note2Texture);
        if (note3Texture != null) availableTextures.add(note3Texture);
        if (note4Texture != null) availableTextures.add(note4Texture);
        
        if (!availableTextures.isEmpty()) {
            noteTextures = availableTextures.toArray(new Texture[0]);
        }
    }
    
    // Sistema para mostrar las notas que caen en el fondo del menú
    public void update(float delta) {
        if (noteTextures == null || noteTextures.length == 0) return;
        
        spawnTimer += delta;
        
        if (spawnTimer >= NOTE_SPAWN_RATE && notes.size() < MAX_NOTES) {
            spawnRandomNote();
            spawnTimer = 0f;
        }

        // Actualizar y eliminar notas que han salido de la pantalla
        for (int i = notes.size() - 1; i >= 0; i--) {
            FallingNote note = notes.get(i);
            note.update(delta);
            
            if (note.y + NOTE_SIZE < 0) {
                notes.remove(i);
            }
        }
    }
    
    public void render(SpriteBatch batch) {
        if (noteTextures == null || noteTextures.length == 0) return;
        
        for (FallingNote note : notes) {
            note.render(batch);
        }
    }
    
    private void spawnRandomNote() {
        Texture randomTexture = noteTextures[MathUtils.random(noteTextures.length - 1)];
        
        // Generar propiedades aleatorias  (Para que no se vean iguales)
        float x = MathUtils.random(-NOTE_SIZE, Gdx.graphics.getWidth());
        float y = Gdx.graphics.getHeight() + NOTE_SIZE;
        float fallSpeed = MathUtils.random(MIN_FALL_SPEED, MAX_FALL_SPEED);
        float rotationSpeed = MathUtils.random(-MAX_ROTATION_SPEED, MAX_ROTATION_SPEED);
        float scale = MathUtils.random(0.8f, 1.2f);
        float alpha = MathUtils.random(0.3f, 0.7f);
        
        // Crear y agregar la nueva nota
        FallingNote note = new FallingNote(randomTexture, x, y, fallSpeed, rotationSpeed, scale, alpha);
        notes.add(note);
    }
    
    // Clase interna para representar una nota que cae
    private static class FallingNote {
        private Texture texture;
        private float x, y;
        private float fallSpeed;
        private float rotationSpeed;
        private float rotation;
        private float scale;
        private float alpha;
        
        public FallingNote(Texture texture, float x, float y, float fallSpeed, 
                          float rotationSpeed, float scale, float alpha) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.fallSpeed = fallSpeed;
            this.rotationSpeed = rotationSpeed;
            this.rotation = MathUtils.random(0f, 360f);
            this.scale = scale;
            this.alpha = alpha;
        }
        
        // Animacion del titulo
        public void update(float delta) {
            y -= fallSpeed * delta;
            
            rotation += rotationSpeed * delta;
            
            if (rotation > 360f) rotation -= 360f;
            if (rotation < 0f) rotation += 360f;
        }
        
        public void render(SpriteBatch batch) {
            batch.setColor(1f, 1f, 1f, alpha);
            
            float centerX = x + (NOTE_SIZE * scale) / 2f;
            float centerY = y + (NOTE_SIZE * scale) / 2f;
            
            // Efecto
            batch.draw(texture, 
                x, y,
                (NOTE_SIZE * scale) / 2f, (NOTE_SIZE * scale) / 2f,
                NOTE_SIZE * scale, NOTE_SIZE * scale,
                1f, 1f,
                rotation,
                0, 0, texture.getWidth(), texture.getHeight(),
                false, false);
        }
    }
    
    // Limpia todas las notas (memoria)

    public void clear() {
        notes.clear();
        spawnTimer = 0f;
    }
    
    //Pausa el efecto

    public void pause() {
        spawnTimer = NOTE_SPAWN_RATE;
    }
    
    public void resume() {
        spawnTimer = 0f;
    }
}
