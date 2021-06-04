package com.fantasticsource.tiamatgamesettings;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = TiamatGameSettings.MODID, name = TiamatGameSettings.NAME, version = TiamatGameSettings.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.044c,)", acceptableRemoteVersions = "*")
public class TiamatGameSettings
{
    public static final String MODID = "tiamatgamesettings";
    public static final String NAME = "Tiamat Game Settings";
    public static final String VERSION = "1.12.2.003";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(TiamatGameSettings.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            MinecraftForge.EVENT_BUS.register(OptionsHandler.class);
        }
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }
}
