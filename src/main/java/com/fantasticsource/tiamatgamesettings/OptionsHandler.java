package com.fantasticsource.tiamatgamesettings;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.event.GametypeChangedEvent;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.fantasticsource.tiamatgamesettings.TiamatGameSettings.MODID;

public class OptionsHandler
{
    public static final File
            VANILLA_OPTIONS_FILE = new File(MCTools.getConfigDir() + ".." + File.separator + "options.txt"),
            BACKUP_OPTIONS_FILE = new File(MCTools.getConfigDir() + ".." + File.separator + "optionsBackup.txt");

    @SubscribeEvent
    public static void gametypeChanged(GametypeChangedEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.player != mc.player) return;


        try
        {
            if (!BACKUP_OPTIONS_FILE.exists()) Tools.copyFile(VANILLA_OPTIONS_FILE, BACKUP_OPTIONS_FILE);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


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
                Tools.copyFile(VANILLA_OPTIONS_FILE, file);
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
            File defaultsDir = new File(MCTools.getConfigDir() + MODID + File.separator + "defaults");
            dir.mkdirs();
            defaultsDir.mkdirs();
            File file = new File(dir, "options.txt"), defaults = new File(defaultsDir, newGameType.getName() + ".txt");

            try
            {
                if (file.exists())
                {
                    if (!defaults.exists()) Tools.copyFile(file, defaults);
                }
                else
                {
                    if (defaults.exists())
                    {
                        Tools.copyFile(defaults, file);
                        long ms = System.currentTimeMillis();
                        while (!file.exists())
                        {
                            if (System.currentTimeMillis() - ms > 1000) throw new IllegalStateException("Was unable to create file: " + file);
                        }
                    }
                    else Tools.copyFile(VANILLA_OPTIONS_FILE, defaults);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (file.exists())
            {
                //GameSettings.loadOptions() does not apply resource packs; need to do it manually


                //Copy current resource pack info
                ArrayList<String> resourcePacks = new ArrayList<>(mc.gameSettings.resourcePacks);


                //Replace options.txt
                try
                {
                    Tools.copyFile(file, VANILLA_OPTIONS_FILE);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }


                //Load options.txt
                Minecraft.getMinecraft().gameSettings.loadOptions();


                //Check resource packs, and reapply them if there is a mismatch
                boolean match = mc.gameSettings.resourcePacks.size() == resourcePacks.size();
                if (match)
                {
                    for (String s : resourcePacks)
                    {
                        if (!mc.gameSettings.resourcePacks.contains(s))
                        {
                            match = false;
                            break;
                        }
                    }
                }

                if (!match)
                {
                    mc.getResourcePackRepository().updateRepositoryEntriesAll();

                    List<ResourcePackRepository.Entry> list = new ArrayList<>();
                    for (ResourcePackRepository.Entry entry : mc.getResourcePackRepository().getRepositoryEntriesAll())
                    {
                        if (mc.gameSettings.resourcePacks.contains(entry.getResourcePackName()))
                        {
                            list.add(entry);
                            break;
                        }
                    }
                    mc.getResourcePackRepository().setRepositories(list);

                    mc.gameSettings.saveOptions();
                    mc.refreshResources();
                }
            }
        }
    }
}
