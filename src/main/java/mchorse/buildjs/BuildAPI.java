package mchorse.buildjs;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * Building API
 * 
 * Should be used by JS scripts. No other purpose rather than simplifying (i.e. 
 * wrapping) method calls in JS code. 
 */
public class BuildAPI
{
    public MinecraftServer server;
    public ICommandSender sender;

    /**
     * Constructor. 
     * 
     * Can't you see it or something? Here it is! 
     */
    public BuildAPI(MinecraftServer server, ICommandSender sender)
    {
        this.server = server;
        this.sender = sender;
    }

    /* Blocks */

    /**
     * Get block from block registry by its ID 
     */
    public Block getBlock(String id)
    {
        return Block.REGISTRY.getObject(new ResourceLocation(id));
    }

    /**
     * Set default block variant in overworld at given (X, Y, Z) 
     */
    public void setBlock(Block block, int x, int y, int z)
    {
        this.setBlock(block, 0, x, y, z);
    }

    /**
     * Set a block with given variant in overworld at given (X, Y, Z) 
     */
    public void setBlock(Block block, int meta, int x, int y, int z)
    {
        this.setBlock(this.sender.getEntityWorld(), block, meta, x, y, z);
    }

    /**
     * Set default block in the world at given (X, Y, Z) 
     */
    public void setBlock(World world, Block block, int x, int y, int z)
    {
        this.setBlock(world, block, 0, x, y, z);
    }

    /**
     * Set a block with given variant in the world at given (X, Y, Z)
     */
    @SuppressWarnings("deprecation")
    public void setBlock(World world, Block block, int meta, int x, int y, int z)
    {
        IBlockState state = block.getStateFromMeta(meta);

        world.setBlockState(new BlockPos(x, y, z), state);
    }

    /* Commands */

    /**
     * Execute given command on the same sender 
     */
    public void executeCommand(String command)
    {
        server.commandManager.executeCommand(this.sender, command);
    }

    /**
     * Log a message to the sender 
     */
    public void log(String message)
    {
        this.sender.sendMessage(new TextComponentString(message));
    }

    /**
     * Log an error message to the sender
     */
    public void error(String message) throws CommandException
    {
        throw new CommandException("buildjs.error.js", message);
    }
}