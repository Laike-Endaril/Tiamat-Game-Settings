package com.fantasticsource.tiamatgamesettings;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.event.GametypeChangedEvent;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.Minecraft;
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
        Minecraft mc = Minecraft.getMinecraft();

        GameType oldGameType = event.oldGameType;
        if (oldGameType != null)
        {
            //Save gametype-specific options

            mc.gameSettings.saveOptions();

            File dir = new File(MCTools.getConfigDir() + MODID + File.separator + oldGameType.getName());
            dir.mkdirs();
            File file = new File(dir, "options.txt");
            try
            {
                Tools.copyFile(DEFAULT_OPTIONS_FILE, file);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


        GameType newGameType = event.newGameType;
        if (newGameType != null)
        {
            //Load gametype-specific options

            File dir = new File(MCTools.getConfigDir() + MODID + File.separator + newGameType.getName());
            dir.mkdirs();
            File file = new File(dir, "options.txt");
            if (file.exists())
            {
                try
                {
                    Tools.copyFile(file, DEFAULT_OPTIONS_FILE);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                Minecraft.getMinecraft().gameSettings.loadOptions();
            }
        }
    }
}
