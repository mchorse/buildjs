package mchorse.buildjs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;

/**
 * Command /buildjs
 * 
 * This command is responsible for executing JS scripts for building.
 */
public class CommandBuildJS extends CommandBase
{
    /**
     * Directory where are located JS scripts 
     */
    public File scripts;

    public CommandBuildJS()
    {
        this.scripts = new File(DimensionManager.getCurrentSaveRootDirectory() + "/buildjs/");
        this.scripts.mkdirs();
    }

    @Override
    public String getName()
    {
        return "buildjs";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "buildjs.commands.buildjs";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        if (!StringUtils.endsWith(args[0], ".js"))
        {
            throw new CommandException("buildjs.error.not_js", args[0]);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        File file = new File(this.scripts + "/" + args[0]);

        String script = "";

        try
        {
            script = FileUtils.readFileToString(file);
        }
        catch (FileNotFoundException e)
        {
            throw new CommandException("buildjs.error.not_found", args[0]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new CommandException("buildjs.error.unknown", e.getMessage());
        }

        if (sender instanceof EntityPlayerMP)
        {
            map.put("player", (EntityPlayerMP) sender);
        }

        map.put("args", args);
        map.put("api", new BuildAPI(server, sender));

        this.executeJS(args[0], script, map);
    }

    /**
     * Execute JS code
     * 
     * This uses Rhino. The implementation of this method was formed thanks 
     * to MDN's tutorial about embedding Rhino.
     * 
     * @link https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino/Embedding_tutorial
     */
    private void executeJS(String filename, String script, Map<String, Object> map) throws CommandException
    {
        Context context = Context.enter();
        Scriptable scope = context.initStandardObjects();

        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            ScriptableObject.putProperty(scope, entry.getKey(), Context.javaToJS(entry.getValue(), scope));
        }

        try
        {
            context.evaluateString(scope, script, filename, 1, null);
        }
        catch (Exception e)
        {
            /* Screw this */
            if (e instanceof CommandException)
            {
                throw (CommandException) e;
            }

            e.printStackTrace();
            throw new CommandException("buildjs.error.eval_js", e.getMessage());
        }
        finally
        {
            Context.exit();
        }
    }

    /**
     * Get tab completions.
     * 
     * This method allows users to select a file with tab completion. Not 
     * recursive.
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos)
    {
        if (args.length == 1)
        {
            List<String> files = new ArrayList<String>();

            for (String file : this.scripts.list())
            {
                if (StringUtils.endsWith(file, ".js"))
                {
                    files.add(file);
                }
            }

            if (files.size() > 0)
            {
                return getListOfStringsMatchingLastWord(args, files);
            }
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}