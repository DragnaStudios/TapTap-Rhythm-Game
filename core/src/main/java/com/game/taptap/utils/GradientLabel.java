package com.game.taptap.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.game.taptap.model.RhythmInfo;

public class GradientLabel extends Label {
    private RhythmInfo.ColorGradient gradient;
    private Color tempColor = new Color();
    
    public GradientLabel(CharSequence text, LabelStyle style) {
        super(text, style);
    }
    
    public GradientLabel(CharSequence text, LabelStyle style, RhythmInfo.ColorGradient gradient) {
        super(text, style);
        this.gradient = gradient;
    }
    
    public void setGradient(RhythmInfo.ColorGradient gradient) {
        this.gradient = gradient;
    }
    
    public RhythmInfo.ColorGradient getGradient() {
        return gradient;
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (gradient == null) {
            super.draw(batch, parentAlpha);
            return;
        }
        
        validate();
        
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        
        BitmapFont font = getStyle().font;
        String text = getText().toString();
        
        if (text != null && !text.isEmpty()) {
            drawGradientText(batch, font, text, getX(), getY() + getHeight(), gradient);
        }
    }
    
    private void drawGradientText(Batch batch, BitmapFont font, String text, 
                                 float x, float y, RhythmInfo.ColorGradient gradient) {
        if (text == null || text.isEmpty()) return;
        
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);
        float totalWidth = layout.width;
        
        if (totalWidth <= 0) {
            font.draw(batch, text, x, y);
            return;
        }
        
        float currentX = x;
        Color originalColor = font.getColor().cpy();
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);
            
            float charProgress = (currentX - x) / totalWidth;
            charProgress = Math.max(0f, Math.min(1f, charProgress));
            
            interpolateColor(tempColor, gradient, charProgress);
            
            font.setColor(tempColor);
            
            font.draw(batch, charStr, currentX, y);
            
            layout.setText(font, charStr);
            currentX += layout.width;
        }
        
        font.setColor(originalColor);
    }
    
    private void interpolateColor(Color result, RhythmInfo.ColorGradient gradient, float progress) {
        progress = Math.max(0f, Math.min(1f, progress));
        
        result.r = gradient.startR + (gradient.endR - gradient.startR) * progress;
        result.g = gradient.startG + (gradient.endG - gradient.startG) * progress;
        result.b = gradient.startB + (gradient.endB - gradient.startB) * progress;
        result.a = gradient.startA + (gradient.endA - gradient.startA) * progress;
    }
}
