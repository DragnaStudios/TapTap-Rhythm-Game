package com.game.taptap.screens;

// Configuración de layout para la pantalla de selección de ritmos
// Permite personalizar las posiciones y tamaños de todos los elementos

public class RhythmSelectConfig {
    
    // Configuración del fondo (Background)
    public static class BackgroundConfig {
        public static float X = 0f;
        public static float Y = 0f;
        public static boolean SCALE_TO_SCREEN = true;
        public static float WIDTH = 1920f;
        public static float HEIGHT = 1080f;
    }
    
    // Configuración del banner (imagen principal del ritmo)
    public static class BannerConfig {
        public static float X = 1240f;
        public static float Y = 840f;
        public static float WIDTH = -1f;
        public static float HEIGHT = -1f;
        public static boolean ANIMATE_ON_CHANGE = true;
        public static float ANIMATION_DURATION = 0.3f;
    }
    
    // Configuración del vinyl (imagen del disco, de los assets de los ritmos)
    public static class VinylConfig {
        public static float X = 284f;
        public static float Y = 504f;
        public static float WIDTH = -1f;
        public static float HEIGHT = -1f;
        public static boolean ROTATE = true;
        public static float ROTATION_SPEED = 45f;
        public static boolean ANIMATE_ON_CHANGE = true;
        public static float ANIMATION_DURATION = 0.4f;
    }
    
    // Configuración del vinyl predeterminado (La que gira)
    public static class DefaultVinylConfig {
        public static float X = 60f;
        public static float Y = 280f;
        public static float WIDTH = -1f;
        public static float HEIGHT = -1f;
        public static boolean ROTATE = true;
        public static float ROTATION_SPEED = 45f;
        public static String ASSET_NAME = "Vinyl.png";
    }
    
    // Configuración de la información del ritmo (texto)
    public static class InfoConfig {
        public static boolean ANIMATE_TEXT_CHANGE = true;
        public static float TEXT_ANIMATION_DURATION = 0.2f;
        
        // Configuración de bordes, trazos para el texto
        public static class BorderConfig {
            public static boolean ENABLE_BORDERS = true;
            public static float BORDER_WIDTH_MULTIPLIER = 2.0f;
            public static float BORDER_R = 0f, BORDER_G = 0f, BORDER_B = 0f;
            public static float BORDER_ALPHA = 1f;
        }
        
        // Configuración individual para cada elemento de texto
        public static class TitleConfig {
            public static float X = 50f;
            public static float Y = 975f;
            public static float FONT_SIZE = 56f;
            public static float R = 1f, G = 1f, B = 0f;
        }
        
        public static class AuthorConfig {
            public static float X = 50f;
            public static float Y = 50f;
            public static float FONT_SIZE = 28f;
            public static float R = 1f, G = 1f, B = 1f;
        }
        
        public static class RhymerConfig {
            public static float X = 500f;
            public static float Y = 50f;
            public static float FONT_SIZE = 28f;
            public static float R = 0.8f, G = 0.8f, B = 1f;
        }
        
        public static class GenreConfig {
            public static float X = 50f;
            public static float Y = 150f;
            public static float FONT_SIZE = 28f;
            public static float R = 1f, G = 0.8f, B = 0.8f;
        }
        
        public static class LengthConfig {
            public static float X = 300f;
            public static float Y = 280f;
            public static float FONT_SIZE = 20f;
            public static float R = 1f, G = 1f, B = 1f;
        }
        
        public static class DifficultyConfig {
            public static float X = 50f;
            public static float Y = 190f;
            public static float FONT_SIZE = 28f;
            public static float R = 1f, G = 0.5f, B = 0.5f;
        }
        
        public static class DescriptionConfig {
            public static float X = 50f;
            public static float Y = 100f;
            public static float WIDTH = 400f;
            public static float FONT_SIZE = 24f;
            public static float R = 0.9f, G = 0.9f, B = 0.9f;
        }
        
        public static class MaxScoreConfig {
            public static float X = 1400f;
            public static float Y = 800f;
            public static float FONT_SIZE = 32f;
            public static float R = 0f, G = 1f, B = 0f;
        }
        
        // Configuración de ubicacion del texto de intrucciones (Lo eliminaria pero si lo hago se tildea el juego por alguna razon)
        public static class InstructionConfig {
            public static float X = 50f;
            public static float Y = 50f;
            public static float FONT_SIZE = 20f;
            public static float R = 0.7f, G = 0.7f, B = 0.7f;
        }
    }
    
    // Configuración de transiciones
    public static class TransitionConfig {
        public static float FADE_IN_DURATION = 0.5f;
        public static float TRANSITION_DELAY = 0.5f;
        public static boolean SMOOTH_TRANSITIONS = true;
    }
    
    // Configuración de audio
    public static class AudioConfig {
        public static float MUSIC_VOLUME = 0.7f;
        public static boolean AUTO_PLAY_MUSIC = true;
        public static boolean LOOP_MUSIC = true;
    }
    
    // Solo lo crre para manejar mejor la visualizacion de las imagenes etc.
    public static void resetToDefaults() {
        // Background
        BackgroundConfig.X = 0f;
        BackgroundConfig.Y = 0f;
        BackgroundConfig.SCALE_TO_SCREEN = true;
        BackgroundConfig.WIDTH = 1920f;
        BackgroundConfig.HEIGHT = 1080f;
        
        // Banner
        BannerConfig.X = 100f;
        BannerConfig.Y = 500f;
        BannerConfig.WIDTH = -1f;
        BannerConfig.HEIGHT = -1f;
        BannerConfig.ANIMATE_ON_CHANGE = true;
        BannerConfig.ANIMATION_DURATION = 0.3f;
        
        // Vinyl
        VinylConfig.X = 600f;
        VinylConfig.Y = 400f;
        VinylConfig.WIDTH = -1f;
        VinylConfig.HEIGHT = -1f;
        VinylConfig.ROTATE = true;
        VinylConfig.ROTATION_SPEED = 45f;
        VinylConfig.ANIMATE_ON_CHANGE = true;
        VinylConfig.ANIMATION_DURATION = 0.4f;
        
        // Por defecto Vinyl (Vinilo)
        DefaultVinylConfig.X = 1400f;
        DefaultVinylConfig.Y = 300f;
        DefaultVinylConfig.WIDTH = -1f;
        DefaultVinylConfig.HEIGHT = -1f;
        DefaultVinylConfig.ROTATE = true;
        DefaultVinylConfig.ROTATION_SPEED = 45f;
        DefaultVinylConfig.ASSET_NAME = "Vinyl.png";
        
        // Info - Configuraciones individuales para cada elemento
        InfoConfig.ANIMATE_TEXT_CHANGE = true;
        InfoConfig.TEXT_ANIMATION_DURATION = 0.2f;
        
        InfoConfig.TitleConfig.X = 1000f;
        InfoConfig.TitleConfig.Y = 700f;
        InfoConfig.TitleConfig.FONT_SIZE = 48f;
        InfoConfig.TitleConfig.R = 1f; InfoConfig.TitleConfig.G = 1f; InfoConfig.TitleConfig.B = 0f;
        
        InfoConfig.AuthorConfig.X = 1000f;
        InfoConfig.AuthorConfig.Y = 650f;
        InfoConfig.AuthorConfig.FONT_SIZE = 32f;
        InfoConfig.AuthorConfig.R = 1f; InfoConfig.AuthorConfig.G = 1f; InfoConfig.AuthorConfig.B = 1f;
        
        InfoConfig.RhymerConfig.X = 1000f;
        InfoConfig.RhymerConfig.Y = 600f;
        InfoConfig.RhymerConfig.FONT_SIZE = 28f;
        InfoConfig.RhymerConfig.R = 0.8f; InfoConfig.RhymerConfig.G = 0.8f; InfoConfig.RhymerConfig.B = 1f;
        
        InfoConfig.GenreConfig.X = 1000f;
        InfoConfig.GenreConfig.Y = 550f;
        InfoConfig.GenreConfig.FONT_SIZE = 28f;
        InfoConfig.GenreConfig.R = 1f; InfoConfig.GenreConfig.G = 0.8f; InfoConfig.GenreConfig.B = 0.8f;
        
        InfoConfig.LengthConfig.X = 1000f;
        InfoConfig.LengthConfig.Y = 500f;
        InfoConfig.LengthConfig.FONT_SIZE = 24f;
        InfoConfig.LengthConfig.R = 1f; InfoConfig.LengthConfig.G = 1f; InfoConfig.LengthConfig.B = 1f;
        
        InfoConfig.DifficultyConfig.X = 1000f;
        InfoConfig.DifficultyConfig.Y = 450f;
        InfoConfig.DifficultyConfig.FONT_SIZE = 24f;
        InfoConfig.DifficultyConfig.R = 1f; InfoConfig.DifficultyConfig.G = 0.5f; InfoConfig.DifficultyConfig.B = 0.5f;
        
        InfoConfig.DescriptionConfig.X = 1000f;
        InfoConfig.DescriptionConfig.Y = 400f;
        InfoConfig.DescriptionConfig.WIDTH = 400f;
        InfoConfig.DescriptionConfig.FONT_SIZE = 20f;
        InfoConfig.DescriptionConfig.R = 0.9f; InfoConfig.DescriptionConfig.G = 0.9f; InfoConfig.DescriptionConfig.B = 0.9f;
        
        InfoConfig.MaxScoreConfig.X = 1000f;
        InfoConfig.MaxScoreConfig.Y = 300f;
        InfoConfig.MaxScoreConfig.FONT_SIZE = 24f;
        InfoConfig.MaxScoreConfig.R = 0f; InfoConfig.MaxScoreConfig.G = 1f; InfoConfig.MaxScoreConfig.B = 0f;
        
        InfoConfig.InstructionConfig.X = 50f;
        InfoConfig.InstructionConfig.Y = 50f;
        InfoConfig.InstructionConfig.FONT_SIZE = 20f;
        InfoConfig.InstructionConfig.R = 0.7f; InfoConfig.InstructionConfig.G = 0.7f; InfoConfig.InstructionConfig.B = 0.7f;
        
        // Transiciones
        TransitionConfig.FADE_IN_DURATION = 0.5f;
        TransitionConfig.TRANSITION_DELAY = 0.5f;
        TransitionConfig.SMOOTH_TRANSITIONS = true;
        
        // Audio
        AudioConfig.MUSIC_VOLUME = 0.7f;
        AudioConfig.AUTO_PLAY_MUSIC = true;
        AudioConfig.LOOP_MUSIC = true;
    }
}
