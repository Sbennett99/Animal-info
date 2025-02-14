package net.x5h4d0wx.animalinfo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.x5h4d0wx.animalinfo.entity.AnimalInteractionListener;

public class AnimalInfo implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        UseEntityCallback.EVENT.register(new AnimalInteractionListener());
    }
}
