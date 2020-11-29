package com.fantasticsource.tiamatgamesettings;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.event.GametypeChangedEvent;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.IOException;

import static com.fantasticsource.tiamatgamesettings.TiamatGameSettings.MODID;

public class OptionsHandler
{
    public static final File DEFAULT_OPTIONS_FILE = new File(MCTools.getConfigDir() + ".." + File.separator + "options.txt");

    @SubscribeEvent
    public static void gametypeChanged(GametypeChangedEvent event)
    {
        new GameSettingData(event.newGameType).apply();
    }

    public static class GameSettingData
    {
        public final GameType gameType;
        public final GameSettings gameSettings;

        public GameSettingData(GameType gameType)
        {
            Minecraft mc = Minecraft.getMinecraft();
            this.gameType = gameType;
            File dir = new File(MCTools.getConfigDir() + MODID + File.separator + gameType.getName());
            dir.mkdirs();
            File file = new File(dir, "options.txt");
            if (!file.exists())
            {
                if (!DEFAULT_OPTIONS_FILE.exists())
                {
                    mc.gameSettings.saveOptions();
                }

                try
                {
                    Tools.copyFile(DEFAULT_OPTIONS_FILE, file);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            gameSettings = new GameSettings(mc, dir);
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
