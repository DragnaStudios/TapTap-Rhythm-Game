package com.game.taptap.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.game.taptap.model.RhythmInfo;

public class GradientTextRenderer {
    
    public static Label createGradientLabel(String text, BitmapFont font, 
                                          RhythmInfo.ColorGradient gradient, 
                                          float x, float y) {
        if (gradient == null) {
            Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
            Label label = new Label(text, style);
            label.setPosition(x, y);
            return label;
        }
        
        Color startColor = new Color(gradient.startR, gradient.startG, gradient.startB, gradient.startA);
        Label.LabelStyle style = new Label.LabelStyle(font, startColor);
        Label label = new Label(text, style);
        label.setPosition(x, y);
        
        return label;
    }
    
    public static void drawGradientText(Batch batch, BitmapFont font, String text, 
                                       float x, float y, RhythmInfo.ColorGradient gradient) {
        if (gradient == null || text == null || text.isEmpty()) {
            font.draw(batch, text, x, y);
            return;
        }
        
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);
        float totalWidth = layout.width;
        
        if (totalWidth <= 0) {
            font.draw(batch, text, x, y);
            return;
        }
        
        float currentX = x;
        Color tempColor = new Color();
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
    
    private static void interpolateColor(Color result, RhythmInfo.ColorGradient gradient, float progress) {
        progress = Math.max(0f, Math.min(1f, progress));
        
        result.r = gradient.startR + (gradient.endR - gradient.startR) * progress;
        result.g = gradient.startG + (gradient.endG - gradient.startG) * progress;
        result.b = gradient.startB + (gradient.endB - gradient.startB) * progress;
        result.a = gradient.startA + (gradient.endA - gradient.startA) * progress;
    }
    
    public static void updateLabelGradient(Label label, RhythmInfo.ColorGradient gradient) {
        if (label == null) {
            return;
        }
        
        if (label instanceof GradientLabel) {
            GradientLabel gradientLabel = (GradientLabel) label;
            gradientLabel.setGradient(gradient);
        } else {
            if (gradient == null) {
                Color whiteColor = new Color(1f, 1f, 1f, 1f);
                if (label.getStyle() != null) {
                    label.getStyle().fontColor = whiteColor;
                }
                label.setColor(whiteColor);
                return;
            }
            
            try {
                Color startColor = new Color(gradient.startR, gradient.startG, gradient.startB, gradient.startA);
                
                if (label.getStyle() != null) {
                    label.getStyle().fontColor = startColor;
                }
                label.setColor(startColor);
                
            } catch (Exception e) {
                Color whiteColor = new Color(1f, 1f, 1f, 1f);
                if (label.getStyle() != null) {
                    label.getStyle().fontColor = whiteColor;
                }
                label.setColor(whiteColor);
            }
        }
    }
}
