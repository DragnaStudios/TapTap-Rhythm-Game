package com.game.taptap;

import com.badlogic.gdx.ApplicationAdapter;


// Para poder entrar al menu del juego es necesario renderizar la informacion del mismo
// Por eso esta este metodo override

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private TapTapGame game;

    @Override
    public void create() {
        game = new TapTapGame();
        game.create();
    }

    @Override
    public void render() {
        game.render();
    }

    @Override
    public void dispose() {
        game.dispose();
    }
}
