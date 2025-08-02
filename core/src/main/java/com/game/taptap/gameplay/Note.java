package com.game.taptap.gameplay;
public class Note {
    
    public enum NoteType {
        TAP,
        HOLD
    }
    
    public enum HitType {
        PERFECT,
        GREAT,
        GOOD,
        MISS
    }
    
    private float time;
    private int lane;
    private NoteType type;
    private float endTime;
    private boolean isHit;
    private HitType hitType;
    
    // Constructor para las notas
    public Note(float time, int lane, NoteType type) {
        this.time = time;
        this.lane = lane;
        this.type = type;
        this.endTime = time;
        this.isHit = false;
        this.hitType = null;
    }
    
    // Constructor para las ritmos Hold (no sirve bien F)
    public Note(float time, int lane, NoteType type, float endTime) {
        this.time = time;
        this.lane = lane;
        this.type = type;
        this.endTime = endTime;
        this.isHit = false;
        this.hitType = null;
    }
    
    // Obtener datos
    public float getTime() {
        return time;
    }
    
    public int getLane() {
        return lane;
    }
    
    public NoteType getType() {
        return type;
    }
    
    public float getEndTime() {
        return endTime;
    }
    
    public boolean isHit() {
        return isHit;
    }
    
    public HitType getHitType() {
        return hitType;
    }
    
    // Colocar datos
    public void setTime(float time) {
        this.time = time;
    }
    
    public void setLane(int lane) {
        this.lane = lane;
    }
    
    public void setType(NoteType type) {
        this.type = type;
    }
    
    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }
    
    public void setHit(boolean hit) {
        this.isHit = hit;
    }
    
    public void setHitType(HitType hitType) {
        this.hitType = hitType;
    }
    
    // Metodo para determinar si fue fallo, bien o perfecto
    public HitType getHitType(float currentTime) {
        float timingDifference = Math.abs(currentTime - time);
        
        if (timingDifference <= 0.05f) {
            return HitType.PERFECT;
        } else if (timingDifference <= 0.1f) {
            return HitType.GREAT;
        } else if (timingDifference <= 0.15f) {
            return HitType.GOOD;
        } else {
            return HitType.MISS;
        }
    }
    
    // Revisa si fue golpeada la nota
    public boolean isHittable(float currentTime, float hitWindow) {
        return Math.abs(currentTime - time) <= hitWindow;
    }

    // Obtener duración para notas HOLD
    public float getDuration() {
        return endTime - time;
    }
    
    @Override
    public String toString() {
        return "Note{" +
                "time=" + time +
                ", lane=" + lane +
                ", type=" + type +
                ", endTime=" + endTime +
                ", isHit=" + isHit +
                ", hitType=" + hitType +
                '}';
    }
}
