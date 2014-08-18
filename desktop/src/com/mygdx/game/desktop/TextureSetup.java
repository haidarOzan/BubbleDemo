package com.mygdx.game.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
 
public class TextureSetup {

    public static void main(String[] args) {
        TexturePacker.process("/Demo Game/frames/rope", "/Demo Game/frames/rope", "rope2.pack");
    }
}
