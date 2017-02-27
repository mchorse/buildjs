package mchorse.buildjs;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import net.minecraft.command.CommandException;

/**
 * Encapsulated execution of a BuildJS script 
 * 
 * This uses Rhino. The implementation of this method was formed thanks 
 * to MDN's tutorial about embedding Rhino.
 *  
 * @link https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino/Embedding_tutorial
 */
public class BuildScript
{
    public void execute(String filename, String script, Map<String, Object> map) throws CommandException
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
}