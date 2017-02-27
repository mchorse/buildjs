package mchorse.buildjs;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * BuildJS main entry 
 * 
 * This mod adds only one command and this command allows you to execute JS 
 * scripts using /buildjs command. This mod is mainly focused at building 
 * things with JS, however it seems like Rhino allows you to execute Java code 
 * from JS.
 */
@Mod(modid = BuildJS.MODID, version = BuildJS.VERSION)
public class BuildJS
{
    public static final String MODID = "buildjs";
    public static final String VERSION = "0.1";

    @Mod.Instance(MODID)
    public BuildJS instance;

    @EventHandler
    public void startingServer(FMLServerStartingEvent event)
    {
        /* Register commands */
        event.registerServerCommand(new CommandBuildJS());
    }
}