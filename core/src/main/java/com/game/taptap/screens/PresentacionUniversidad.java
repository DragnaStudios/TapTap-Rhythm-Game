package com.game.taptap.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.game.taptap.TapTapGame;

public class PresentacionUniversidad implements Screen {
    
    private TapTapGame game;
    private BitmapFont font;
    private Texture utp1Logo;
    private Texture utp2Logo;
    
    private String[] universityInfo = {
        "Universidad Tecnológica de Panamá",
        "Facultad de Ingeniería en Sistemas Computacionales",
        "Ingeniería de Software",
        "",
        "",
        "Proyecto Semestral",
        "",
        "",
        "Curso:",
        "Programación I",
        "",
        "",
        "Estudiante:",
        "Robert Pimentel (7-714-1252)",
        "",
        "",
        "Grupo:",
        "1SF122",
        "",
        "",
        "Facilitador:",
        "Rodrigo Yángüez",
        "",
        "",
        "Fecha:",
        "1/8/2025",
        "",
        "",
        "Panamá, 2025"
    };
    
    public PresentacionUniversidad(TapTapGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        font = game.font;
        
        try {
            if (game.assetManager.isLoaded(TapTapGame.UTP1_LOGO)) {
                utp1Logo = game.assetManager.get(TapTapGame.UTP1_LOGO, Texture.class);
            }
            if (game.assetManager.isLoaded(TapTapGame.UTP2_LOGO)) {
                utp2Logo = game.assetManager.get(TapTapGame.UTP2_LOGO, Texture.class);
            }
        } catch (Exception e) {
            System.err.println("Error loading UTP logos: " + e.getMessage());
        }
    }
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.2f, 0.4f, 1f);
        
        game.batch.begin();
        
        if (utp1Logo != null) {
            float logoSize = 100f;
            game.batch.draw(utp1Logo, 20, Gdx.graphics.getHeight() - logoSize - 20, logoSize, logoSize);
        }
        
        if (utp2Logo != null) {
            float logoSize = 100f;
            game.batch.draw(utp2Logo, Gdx.graphics.getWidth() - logoSize - 20, 
                           Gdx.graphics.getHeight() - logoSize - 20, logoSize, logoSize);
        }
        
        font.setColor(Color.WHITE);
        font.getData().setScale(0.8f);
        
        float startY = Gdx.graphics.getHeight() * 0.75f;
        float lineHeight = 35f;
        float currentY = startY;
        
        for (String line : universityInfo) {
            if (line.isEmpty()) {
                currentY -= lineHeight * 0.6f;
            } else {
                if (line.equals("Universidad Tecnológica de Panamá")) {
                    font.getData().setScale(1.0f);
                    font.setColor(Color.YELLOW);
                } else if (line.equals("Facultad de Ingeniería en Sistemas Computacionales") ||
                          line.equals("Ingeniería de Software")) {
                    font.getData().setScale(0.85f);
                    font.setColor(Color.CYAN);
                } else if (line.equals("Proyecto Semestral")) {
                    font.getData().setScale(0.9f);
                    font.setColor(Color.ORANGE);
                } else if (line.startsWith("Curso:") || line.startsWith("Estudiante:") ||
                          line.startsWith("Grupo:") || line.startsWith("Facilitador:") ||
                          line.startsWith("Fecha:")) {
                    font.getData().setScale(0.7f);
                    font.setColor(Color.LIGHT_GRAY);
                } else {
                    font.getData().setScale(0.8f);
                    font.setColor(Color.WHITE);
                }
                
                com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
                layout.setText(font, line);
                float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
                
                font.draw(game.batch, line, textX, currentY);
                currentY -= lineHeight;
            }
        }
        
        font.getData().setScale(0.6f);
        font.setColor(Color.GREEN);
        String continueMessage = "Presiona ENTER para continuar";
        
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        layout.setText(font, continueMessage);
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        
        font.draw(game.batch, continueMessage, textX, 60f);
        
        game.batch.end();
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
    
    @Override
    public void resize(int width, int height) {
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void dispose() {
    }
}
