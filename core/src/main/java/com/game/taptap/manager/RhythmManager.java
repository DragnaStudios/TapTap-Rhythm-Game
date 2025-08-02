package com.game.taptap.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.game.taptap.model.RhythmInfo;

// Metodo para cargar y gestionar ritmos disponibles en la carpeta Rhythms
public class RhythmManager {
    private Array<RhythmInfo> availableRhythms;
    private AssetManager assetManager;
    private Json json;
    
    public RhythmManager(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.availableRhythms = new Array<>();
        this.json = new Json();
    }

    // Carga los ritmos disponibles desde la carpeta Rhythms
    public void loadAvailableRhythms() {
        availableRhythms.clear();
        
        try {
            FileHandle rhythmsFolder = Gdx.files.internal("Rhythms");
            if (!rhythmsFolder.exists()) {
                return;
            }
            
            FileHandle[] subFolders = rhythmsFolder.list();
            
            for (FileHandle folder : subFolders) {
                if (folder.isDirectory()) {
                    RhythmInfo rhythm = loadRhythmFromFolder(folder);
                    if (rhythm != null) {
                        availableRhythms.add(rhythm);
                    }
                }
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Carga un ritmo en especifico
    private RhythmInfo loadRhythmFromFolder(FileHandle folder) {
        try {
            // Buscar archivo Info.json (Para obtener metadatos del ritmo)
            FileHandle infoFile = folder.child("Info.json");
            if (!infoFile.exists()) {
                return null;
            }
            
            // Crear objeto RhythmInfo
            RhythmInfo rhythm = new RhythmInfo(folder.path());
            
            // Leer y parsear Info.json
            String jsonContent = infoFile.readString();
            JsonValue jsonData = json.fromJson(null, jsonContent);
            
            rhythm.setTitle(jsonData.getString("titulo", jsonData.getString("title", "Unknown")));
            rhythm.setAuthor(jsonData.getString("autor", jsonData.getString("author", "Unknown")));
            rhythm.setRhymer(jsonData.getString("rhymer", "Unknown"));
            rhythm.setGenre(jsonData.getString("genero", jsonData.getString("genre", "Unknown")));
            rhythm.setLength(jsonData.getString("duracion", jsonData.getString("length", "0:00")));
            rhythm.setDifficulty(jsonData.getString("dificultad", jsonData.getString("difficulty", "Normal")));
            rhythm.setDescription(jsonData.getString("descripcion", jsonData.getString("description", "No description")));
            rhythm.setMaxScore(jsonData.getInt("puntuacionMaxima", jsonData.getInt("maxscore", 0)));
            
            // Cargar gradientes de color si existen en el Info.json use un metodo para que lo leyera en rgba
            loadColorGradients(rhythm, jsonData);
            
            // Cargar assets de la carpeta assets
            loadRhythmAssets(rhythm, folder);
            
            return rhythm;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    // Carga los assets del ritmo (Background, Banner, Vinyl y música)
    private void loadRhythmAssets(RhythmInfo rhythm, FileHandle folder) {
        // Buscar carpeta assets Si no existe assets, crea el folder
        FileHandle assetsFolder = folder.child("assets");
        if (!assetsFolder.exists()) {
            assetsFolder = folder.child("Assets");
        }
        
        if (!assetsFolder.exists()) {
            return;
        }
        
        try {
            // Cargar Background.png
            FileHandle backgroundFile = assetsFolder.child("Background.png");
            if (backgroundFile.exists()) {
                String assetPath = "Rhythms/" + folder.name() + "/" + assetsFolder.name() + "/Background.png";
                assetManager.load(assetPath, Texture.class);
                assetManager.finishLoadingAsset(assetPath);
                rhythm.setBackgroundTexture(assetManager.get(assetPath, Texture.class));
                System.out.println("Cargado Background para: " + rhythm.getTitle());
            }
            
            // Cargar Banner.png
            FileHandle bannerFile = assetsFolder.child("Banner.png");
            if (bannerFile.exists()) {
                String assetPath = "Rhythms/" + folder.name() + "/" + assetsFolder.name() + "/Banner.png";
                assetManager.load(assetPath, Texture.class);
                assetManager.finishLoadingAsset(assetPath);
                rhythm.setBannerTexture(assetManager.get(assetPath, Texture.class));
                System.out.println("Cargado Banner para: " + rhythm.getTitle());
            }
            
            // Cargar Vinyl_Image.png o Vinyl.png
            FileHandle vinylFile = assetsFolder.child("Vinyl_Image.png");
            if (!vinylFile.exists()) {
                // Si no existe Vinyl_Image.png, buscar Vinyl.png
                vinylFile = assetsFolder.child("Vinyl.png");
            }
            
            if (vinylFile.exists()) {
                String assetPath = "Rhythms/" + folder.name() + "/" + assetsFolder.name() + "/" + vinylFile.name();
                assetManager.load(assetPath, Texture.class);
                assetManager.finishLoadingAsset(assetPath);
                rhythm.setVinylTexture(assetManager.get(assetPath, Texture.class));
            } else {
            }
            
            // Buscar archivo de música (Se permiten mp3, ogg y wav)
            String[] musicExtensions = {".mp3", ".ogg", ".wav"};
            
            boolean musicFound = false;
            FileHandle[] assetFiles = assetsFolder.list();
            
            for (FileHandle assetFile : assetFiles) {
                if (musicFound) break;
                if (!assetFile.isDirectory()) {
                    String fileName = assetFile.name().toLowerCase();
                    for (String ext : musicExtensions) {
                        if (fileName.endsWith(ext)) {
                            String assetPath = "Rhythms/" + folder.name() + "/" + assetsFolder.name() + "/" + assetFile.name();
                            try {
                                assetManager.load(assetPath, Music.class);
                                assetManager.finishLoadingAsset(assetPath);
                                rhythm.setMusic(assetManager.get(assetPath, Music.class));
                                musicFound = true;
                                break;
                            } catch (Exception musicEx) {
                            }
                        }
                    }
                }
            }
            
            if (!musicFound) {
                System.out.println("No se encontró música para: " + rhythm.getTitle());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Obtencion de los ritmos disponibles
    public Array<RhythmInfo> getAvailableRhythms() {
        return availableRhythms;
    }

    // Ordena por indice
    public RhythmInfo getRhythm(int index) {
        if (index >= 0 && index < availableRhythms.size) {
            return availableRhythms.get(index);
        }
        return null;
    }
    
    // Valor de carpetas de ritmos
    public int getRhythmCount() {
        return availableRhythms.size;
    }
    
    // Para la musica de todos los ritmos
    public void stopAllMusic() {
        for (RhythmInfo rhythm : availableRhythms) {
            if (rhythm.getMusic() != null && rhythm.getMusic().isPlaying()) {
                rhythm.getMusic().stop();
            }
        }
    }
    
    // Carga los gradientes de color desde el Info.json
    private void loadColorGradients(RhythmInfo rhythm, JsonValue jsonData) {
        try {
            JsonValue colorGradients = jsonData.get("gradientesColor");
            if (colorGradients == null) {
                colorGradients = jsonData.get("colorGradients");
            }
            
            if (colorGradients == null) {
                setDefaultGradients(rhythm);
                return;
            }
            
            rhythm.setTitleGradient(createGradientFromJson(
                getGradientValue(colorGradients, "titulo", "title")));
            rhythm.setAuthorGradient(createGradientFromJson(
                getGradientValue(colorGradients, "autor", "author")));
            rhythm.setRhymerGradient(createGradientFromJson(
                getGradientValue(colorGradients, "rhymer", "rhymer")));
            rhythm.setGenreGradient(createGradientFromJson(
                getGradientValue(colorGradients, "genero", "genre")));
            rhythm.setLengthGradient(createGradientFromJson(
                getGradientValue(colorGradients, "duracion", "length")));
            rhythm.setDifficultyGradient(createGradientFromJson(
                getGradientValue(colorGradients, "dificultad", "difficulty")));
            rhythm.setDescriptionGradient(createGradientFromJson(
                getGradientValue(colorGradients, "descripcion", "description")));
            rhythm.setMaxScoreGradient(createGradientFromJson(
                getGradientValue(colorGradients, "puntuacionMaxima", "maxScore")));
            
        } catch (Exception e) {
            setDefaultGradients(rhythm);
        }
    }
    
    private JsonValue getGradientValue(JsonValue colorGradients, String spanishKey, String englishKey) {
        JsonValue value = colorGradients.get(spanishKey);
        if (value == null) {
            value = colorGradients.get(englishKey);
        }
        return value;
    }

    private RhythmInfo.ColorGradient createGradientFromJson(JsonValue gradientData) {
        if (gradientData == null) {
            return new RhythmInfo.ColorGradient(1f, 1f, 1f);
        }
        
        try {
            
            JsonValue startColor = gradientData.get("colorInicio");
            if (startColor == null) {
                startColor = gradientData.get("startColor");
            }
            
            JsonValue endColor = gradientData.get("colorFin");
            if (endColor == null) {
                endColor = gradientData.get("endColor");
            }
            
            if (startColor == null || endColor == null) {
                return new RhythmInfo.ColorGradient(1f, 1f, 1f);
            }
            
            // Determinar si es formato hexadecimal o array
            float[] startRGBA = parseColor(startColor);
            float[] endRGBA = parseColor(endColor);
            
            System.out.println("Creando gradiente final con colores: [" + 
                startRGBA[0] + "," + startRGBA[1] + "," + startRGBA[2] + "," + startRGBA[3] + "] -> [" +
                endRGBA[0] + "," + endRGBA[1] + "," + endRGBA[2] + "," + endRGBA[3] + "]");
            
            return new RhythmInfo.ColorGradient(
                startRGBA[0], startRGBA[1], startRGBA[2], startRGBA[3],
                endRGBA[0], endRGBA[1], endRGBA[2], endRGBA[3]
            );
            
        } catch (Exception e) {
            e.printStackTrace();
            return new RhythmInfo.ColorGradient(1f, 1f, 1f);
        }
    }
    
    // Persea un color hexadecional o array RGBA
    private float[] parseColor(JsonValue colorValue) {
        if (colorValue.isString()) {
            return parseHexColor(colorValue.asString());
        } else if (colorValue.isArray()) {
            return new float[] {
                colorValue.get(0).asFloat(),
                colorValue.get(1).asFloat(),
                colorValue.get(2).asFloat(),
                colorValue.get(3).asFloat()
            };
        } else {
            return new float[] {1f, 1f, 1f, 1f};
        }
    }
    
    private float[] parseHexColor(String hexColor) {
        try {
            
            if (hexColor.startsWith("#")) {
                hexColor = hexColor.substring(1);
            }
            
            float r, g, b, a = 1f;
            
            if (hexColor.length() == 6) {
                r = Integer.parseInt(hexColor.substring(0, 2), 16) / 255f;
                g = Integer.parseInt(hexColor.substring(2, 4), 16) / 255f;
                b = Integer.parseInt(hexColor.substring(4, 6), 16) / 255f;
                
            } else if (hexColor.length() == 8) {
                r = Integer.parseInt(hexColor.substring(0, 2), 16) / 255f;
                g = Integer.parseInt(hexColor.substring(2, 4), 16) / 255f;
                b = Integer.parseInt(hexColor.substring(4, 6), 16) / 255f;
                a = Integer.parseInt(hexColor.substring(6, 8), 16) / 255f;
                
            } else {
                return new float[] {1f, 1f, 1f, 1f};
            }
            
            return new float[] {r, g, b, a};
            
        } catch (Exception e) {
            return new float[] {1f, 1f, 1f, 1f};
        }
    }
    
    // Establece gradientes por defecto si no se encuentran en el Info.json
    // Por defecto
    private void setDefaultGradients(RhythmInfo rhythm) {
        rhythm.setTitleGradient(new RhythmInfo.ColorGradient(1f, 1f, 0f));
        rhythm.setAuthorGradient(new RhythmInfo.ColorGradient(1f, 1f, 1f));
        rhythm.setRhymerGradient(new RhythmInfo.ColorGradient(0.8f, 0.8f, 1f));
        rhythm.setGenreGradient(new RhythmInfo.ColorGradient(1f, 0.8f, 0.8f));
        rhythm.setLengthGradient(new RhythmInfo.ColorGradient(1f, 1f, 1f));
        rhythm.setDifficultyGradient(new RhythmInfo.ColorGradient(1f, 0.5f, 0.5f));
        rhythm.setDescriptionGradient(new RhythmInfo.ColorGradient(0.9f, 0.9f, 0.9f));
        rhythm.setMaxScoreGradient(new RhythmInfo.ColorGradient(0f, 1f, 0f));
    }
    
    // Libera memoria
    public void dispose() {
        for (RhythmInfo rhythm : availableRhythms) {
            rhythm.dispose();
        }
        availableRhythms.clear();
    }
}
