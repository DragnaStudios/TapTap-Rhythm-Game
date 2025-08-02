package com.game.taptap.gameplay;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.Gdx;
import java.util.List;
import java.util.ArrayList;

// Clase principal muestra un beatmap
public class Beatmap {
    public static class TimingPoint {
        public float time;
        public float bpm;
        
        public TimingPoint() {}
        public TimingPoint(float time, float bpm) {
            this.time = time;
            this.bpm = bpm;
        }
        public float getTime() { return time; }
        public float getBpm() { return bpm; }
    }
    
    // Lista de todas las notas o beats del mapa (Beatmap.json)
    private Array<Note> notes;
    // Lista de puntos de timing
    private Array<TimingPoint> timingPoints;

    private String title;
    private String artist;
    private String difficulty;
    private float audioLeadIn;
    private float previewTime;

    // Constructor (inicializa listas y valores por defecto)
    public Beatmap() {
        this.notes = new Array<>();
        this.timingPoints = new Array<>();
        this.title = "";
        this.artist = "";
        this.difficulty = "Normal";
        this.audioLeadIn = 0f;
        this.previewTime = 0f;
    }
    
    // Devuelve la lista de notas
    public Array<Note> getNotes() { return notes; }

    // Devuelve la lista de puntos de timing
    public Array<TimingPoint> getTimingPoints() { return timingPoints; }

    // obtencion y colocador de datos del beatmap
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getDifficulty() { return difficulty; }
    public float getAudioLeadIn() { return audioLeadIn; }
    public float getPreviewTime() { return previewTime; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setAudioLeadIn(float audioLeadIn) { this.audioLeadIn = audioLeadIn; }
    public void setPreviewTime(float previewTime) { this.previewTime = previewTime; }
    
    // Métodos para manipular notas
    public void addNote(Note note) {
        notes.add(note);
        sortNotesByTime();
    }
    public void removeNote(Note note) { notes.removeValue(note, true); }
    public void clearNotes() { notes.clear(); }
    
    // Métodos para manipular puntos de timing
    public void addTimingPoint(TimingPoint timingPoint) {
        timingPoints.add(timingPoint);
        sortTimingPointsByTime();
    }
    public void removeTimingPoint(TimingPoint timingPoint) { timingPoints.removeValue(timingPoint, true); }
    public void clearTimingPoints() { timingPoints.clear(); }
    
    // Ordena las notas por tiempo ascendente
    private void sortNotesByTime() {
        notes.sort((note1, note2) -> Float.compare(note1.getTime(), note2.getTime()));
    }
    // Ordena los puntos de timing por tiempo ascendente
    private void sortTimingPointsByTime() {
        timingPoints.sort((tp1, tp2) -> Float.compare(tp1.getTime(), tp2.getTime()));
    }
    
    // Devuelve las notas activas en la ventana de tiempo actual (para render y lógica)
    public List<Note> getActiveNotes(float currentTime, float lookAheadTime) {
        List<Note> activeNotes = new ArrayList<>();
        for (Note note : notes) {
            // Solo incluye notas que están cerca del tiempo actual
            if (note.getTime() >= currentTime - 0.5f && note.getTime() <= currentTime + lookAheadTime) {
                activeNotes.add(note);
            }
        }
        return activeNotes;
    }
    
    // Devuelve la nota que puede ser golpeada en este instante y carril (Osea la linea)
    public Note getHittableNote(float currentTime, int lane, float hitWindow) {
        for (Note note : notes) {
            if (note.getLane() == lane && !note.isHit() && note.isHittable(currentTime, hitWindow)) {
                return note;
            }
        }
        return null;
    }
    
    // Carga un beatmap desde un archivo JSON
    public static Beatmap loadFromFile(String filePath) {
        try {
            Json json = new Json();
            String jsonString = Gdx.files.internal(filePath).readString();
            Beatmap beatmap = new Beatmap();
            return beatmap;
        } catch (Exception e) {
            return new Beatmap();
        }
    }
    
    // Guarda el beatmap a un archivo JSON (estructura básica)
    public void saveToFile(String filePath) {
        try {
            Json json = new Json();
            json.setOutputType(com.badlogic.gdx.utils.JsonWriter.OutputType.json);
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{\n");
            jsonBuilder.append("  \"title\": \"").append(title).append("\",\n");
            jsonBuilder.append("  \"artist\": \"").append(artist).append("\",\n");
            jsonBuilder.append("  \"difficulty\": \"").append(difficulty).append("\",\n");
            jsonBuilder.append("  \"audioLeadIn\": ").append(audioLeadIn).append(",\n");
            jsonBuilder.append("  \"previewTime\": ").append(previewTime).append(",\n");
            jsonBuilder.append("  \"notes\": [\n");
            for (int i = 0; i < notes.size; i++) {
                Note note = notes.get(i);
                jsonBuilder.append("    {\n");
                jsonBuilder.append("      \"time\": ").append(note.getTime()).append(",\n");
                jsonBuilder.append("      \"lane\": ").append(note.getLane()).append(",\n");
                jsonBuilder.append("      \"type\": \"").append(note.getType()).append("\"");
                if (note.getType() == Note.NoteType.HOLD) {
                    jsonBuilder.append(",\n      \"endTime\": ").append(note.getEndTime());
                }
                jsonBuilder.append("\n    }");
                if (i < notes.size - 1) {
                    jsonBuilder.append(",");
                }
                jsonBuilder.append("\n");
            }
            jsonBuilder.append("  ],\n");
            jsonBuilder.append("  \"timingPoints\": [\n");
            for (int i = 0; i < timingPoints.size; i++) {
                TimingPoint tp = timingPoints.get(i);
                jsonBuilder.append("    {\n");
                jsonBuilder.append("      \"time\": ").append(tp.getTime()).append(",\n");
                jsonBuilder.append("      \"bpm\": ").append(tp.getBpm()).append("\n");
                jsonBuilder.append("    }");
                if (i < timingPoints.size - 1) {
                    jsonBuilder.append(",");
                }
                jsonBuilder.append("\n");
            }
            jsonBuilder.append("  ]\n");
            jsonBuilder.append("}");
            Gdx.files.local(filePath).writeString(jsonBuilder.toString(), false);
        } catch (Exception e) {
        }
    }
    
    // Devuelve el número total de notas
    public int getTotalNotes() { return notes.size; }
    // Devuelve la duración del beatmap
    public float getDuration() {
        if (notes.size == 0) return 0f;
        float maxTime = 0f;
        for (Note note : notes) {
            maxTime = Math.max(maxTime, note.getEndTime());
        }
        return maxTime;
    }
    

    @Override
    public String toString() {
        return "Beatmap{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", noteCount=" + notes.size +
                ", timingPointCount=" + timingPoints.size +
                '}';
    }
}
