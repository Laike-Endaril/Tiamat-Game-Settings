package com.fantasticsource.tiamatgamesettings;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.event.GametypeChangedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;

import static com.fantasticsource.tiamatgamesettings.TiamatGameSettings.MODID;

public class OptionsHandler
{
    protected static GameSettings defaults;

    @SubscribeEvent
    public static void gametypeChanged(GametypeChangedEvent event)
    {
        if (defaults == null) defaults = Minecraft.getMinecraft().gameSettings;
        new GameSettingData(event.newGameType).apply();
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event)
    {
        if (Minecraft.getMinecraft().gameSettings == null) System.out.println("NULL");
    }

    public static class GameSettingData
    {
        public final GameType gameType;
        public final GameSettings gameSettings;

        public GameSettingData(GameType gameType)
        {
            this.gameType = gameType;
            File file = new File(MCTools.getConfigDir() + MODID + File.separator + gameType.getName());
            file.mkdirs();
            gameSettings = new GameSettings(Minecraft.getMinecraft(), file);
        }

        public void apply()
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.gameSettings = gameSettings;
            mc.getRenderManager().options = gameSettings;

            EntityPlayerSP player = mc.player;
            if (player != null) player.movementInput = new MovementInputFromOptions(gameSettings);
        }
    }
}
