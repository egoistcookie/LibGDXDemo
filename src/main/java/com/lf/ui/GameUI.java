package com.lf.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

public class GameUI {
    private Stage stage;
    private Skin skin;

    public GameUI() {
        stage = new Stage(new ScreenViewport());
        VisUI.load();
        skin = VisUI.getSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
    }

    public void render() {
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        VisUI.dispose();
    }
}