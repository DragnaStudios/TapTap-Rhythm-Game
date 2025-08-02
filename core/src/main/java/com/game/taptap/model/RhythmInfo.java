package com.game.taptap.model;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;

// Clase que representa la informacion que se muestra en la pantalla de seleccion de ritmos
public class RhythmInfo {
    private String title;
    private String author;
    private String rhymer;
    private String genre;
    private String length;
    private String difficulty;
    private String description;
    private int maxScore;
    private String folderPath;
    
    // Configuración de colores personalizados
    private ColorGradient titleGradient;
    private ColorGradient authorGradient;
    private ColorGradient rhymerGradient;
    private ColorGradient genreGradient;
    private ColorGradient lengthGradient;
    private ColorGradient difficultyGradient;
    private ColorGradient descriptionGradient;
    private ColorGradient maxScoreGradient;
    
    // Mostrar los gradientes de izquierda a derecha
    public static class ColorGradient {
        public float startR, startG, startB, startA;
        public float endR, endG, endB, endA;
        
        public ColorGradient(float startR, float startG, float startB, float startA,
                           float endR, float endG, float endB, float endA) {
            this.startR = startR;
            this.startG = startG;
            this.startB = startB;
            this.startA = startA;
            this.endR = endR;
            this.endG = endG;
            this.endB = endB;
            this.endA = endA;
        }
        
        public ColorGradient(float r, float g, float b, float a) {
            this(r, g, b, a, r, g, b, a);
        }
        
        public ColorGradient(float r, float g, float b) {
            this(r, g, b, 1f);
        }
    }
    
    // Assets del ritmo
    private Texture backgroundTexture;
    private Texture bannerTexture;
    private Texture vinylTexture;
    private Music music;
    
    // Configuración de posicionamiento (valores predeterminados)
    public static class LayoutConfig {
        public static float BACKGROUND_X = 0f;
        public static float BACKGROUND_Y = 0f;
        public static float BACKGROUND_WIDTH = 1920f;
        public static float BACKGROUND_HEIGHT = 1080f;
        
        public static float BANNER_X = 100f;
        public static float BANNER_Y = 500f;
        public static float BANNER_WIDTH = 400f;
        public static float BANNER_HEIGHT = 200f;
        
        public static float VINYL_X = 600f;
        public static float VINYL_Y = 400f;
        public static float VINYL_WIDTH = 300f;
        public static float VINYL_HEIGHT = 300f;
        
        public static float INFO_X = 1000f;
        public static float INFO_Y = 600f;
        public static float INFO_LINE_HEIGHT = 40f;
    }
    
    public RhythmInfo(String folderPath) {
        this.folderPath = folderPath;
        this.maxScore = 0;
    }
    
    // Obtencion y colocadores
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getRhymer() { return rhymer; }
    public void setRhymer(String rhymer) { this.rhymer = rhymer; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public String getLength() { return length; }
    public void setLength(String length) { this.length = length; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }
    
    public String getFolderPath() { return folderPath; }
    
    public Texture getBackgroundTexture() { return backgroundTexture; }
    public void setBackgroundTexture(Texture backgroundTexture) { this.backgroundTexture = backgroundTexture; }
    
    public Texture getBannerTexture() { return bannerTexture; }
    public void setBannerTexture(Texture bannerTexture) { this.bannerTexture = bannerTexture; }
    
    public Texture getVinylTexture() { return vinylTexture; }
    public void setVinylTexture(Texture vinylTexture) { this.vinylTexture = vinylTexture; }
    
    public Music getMusic() { return music; }
    public void setMusic(Music music) { this.music = music; }
    
    // Obtencion y colocadores
    public ColorGradient getTitleGradient() { return titleGradient; }
    public void setTitleGradient(ColorGradient titleGradient) { this.titleGradient = titleGradient; }
    
    public ColorGradient getAuthorGradient() { return authorGradient; }
    public void setAuthorGradient(ColorGradient authorGradient) { this.authorGradient = authorGradient; }
    
    public ColorGradient getRhymerGradient() { return rhymerGradient; }
    public void setRhymerGradient(ColorGradient rhymerGradient) { this.rhymerGradient = rhymerGradient; }
    
    public ColorGradient getGenreGradient() { return genreGradient; }
    public void setGenreGradient(ColorGradient genreGradient) { this.genreGradient = genreGradient; }
    
    public ColorGradient getLengthGradient() { return lengthGradient; }
    public void setLengthGradient(ColorGradient lengthGradient) { this.lengthGradient = lengthGradient; }
    
    public ColorGradient getDifficultyGradient() { return difficultyGradient; }
    public void setDifficultyGradient(ColorGradient difficultyGradient) { this.difficultyGradient = difficultyGradient; }
    
    public ColorGradient getDescriptionGradient() { return descriptionGradient; }
    public void setDescriptionGradient(ColorGradient descriptionGradient) { this.descriptionGradient = descriptionGradient; }
    
    public ColorGradient getMaxScoreGradient() { return maxScoreGradient; }
    public void setMaxScoreGradient(ColorGradient maxScoreGradient) { this.maxScoreGradient = maxScoreGradient; }
    
    // Limpiar memoria
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        if (bannerTexture != null) {
            bannerTexture.dispose();
            bannerTexture = null;
        }
        if (vinylTexture != null) {
            vinylTexture.dispose();
            vinylTexture = null;
        }
        if (music != null) {
            music.dispose();
            music = null;
        }
    }
}
