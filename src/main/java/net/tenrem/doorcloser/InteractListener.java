package net.tenrem.doorcloser;

import org.bukkit.block.data.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Door.Hinge;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;


import org.bukkit.*;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import com.cjcrafter.foliascheduler.FoliaCompatibility;
import com.cjcrafter.foliascheduler.ServerImplementation;
import com.cjcrafter.foliascheduler.TaskImplementation;


import java.util.Map;
import java.util.HashMap;

public final class InteractListener implements Listener {
    // this is a bukkit / minecraft constant. Put here only for clarity
    private static final int TICKS_PER_SECOND = 20;

    private final DoorCloserPlugin _plugin;
    private final ServerImplementation scheduler;

    public Map<Vector, TaskImplementation<Void>> activeScheduledTasks = new HashMap<>();
    

    public InteractListener(DoorCloserPlugin plugin) {
        _plugin = plugin;
        scheduler = new FoliaCompatibility(plugin).getServerImplementation();
    }



    // This is going to fire for every interaction, so need to exit it quickly if it's not what we want to handle
    @EventHandler(priority=EventPriority.LOWEST)
    public void blockInteract(PlayerInteractEvent e) {
        // if the event has been canceled, and we're not ignoring canceled events, quit
        // the only reason we check here instead of in the @EventHandler directive is
        // so this can be changed in the config file
        if (e.isCancelled() && Settings.ignoreCanceledEvents) {
            return;
        }

        Action action = e.getAction();

        // right clicks only
        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = e.getClickedBlock();
            BlockData blockData = clickedBlock.getBlockData();
            Block pairedDoorBlock = null;

            // check to see if we care about this type of block. In our case, we want
            // something that implements Openable (gate, trap door, door).
            if (blockData instanceof Openable) {
                Player player = e.getPlayer();
                Material blockDoorType = blockData.getMaterial();

                if (blockData instanceof Door) {
                    if (!Settings.doorsInScope.contains(blockDoorType)) return;
                } else if (blockData instanceof TrapDoor) {
                    if (!Settings.trapDoorsInScope.contains(blockDoorType)) return;
                } else if (blockData instanceof Gate) {
                    if (!Settings.gatesInScope.contains(blockDoorType)) return;
                } else {
                    // It's some other Openable we don't handle
                    return;
                }

                // Get persistent lower left half of the double door or just lower half of a single door
                if (blockData instanceof Door) {
                    if (((Door)blockData).getHalf() == Half.TOP) {
                        clickedBlock = clickedBlock.getRelative(BlockFace.DOWN);
                        blockData = clickedBlock.getBlockData();
                    }
                    // Sync double door
                    if (Settings.synchronizeDoubleDoor) {
                        Block door2Block = GetPairedDoorBlockIfDoubleDoor(clickedBlock);
                        Door pairedDoor = DoorFromBlock(door2Block);

                        if (door2Block != null && pairedDoor != null) {
                            if (((Door) blockData).isOpen()) {
                                CloseDoor(door2Block);
                            } else {
                                OpenDoor(door2Block);
                            }
                            PlayCloseNoise(door2Block);
                        }
                    }

                    // At this point door(s) finished open/close cycle, lets check task
                    clickedBlock = GetDoubleDoorLocation(clickedBlock);
                    if (Settings.synchronizeDoubleDoor) {
                        pairedDoorBlock = GetPairedDoorBlockIfDoubleDoor(clickedBlock);
                    }
                }

                Vector blockLocation = clickedBlock.getLocation().toVector();

                if (activeScheduledTasks.containsKey(blockLocation)) {
                    activeScheduledTasks.get(blockLocation).cancel();
                    activeScheduledTasks.remove(blockLocation);
                    return;
                }

                // check to see if player has bypass permission
                if (player.hasPermission(Settings.bypassPermission)) {
                    return;
                }

                // check to see if we're ignoring creative mode
                if ((Settings.ignoreIfInCreative) && (player.getGameMode() == GameMode.CREATIVE)) {
                    return;
                }

                // check to see if we're ignoring sneaking
                if ((Settings.ignoreIfSneaking) && (player.isSneaking())) {
                    return;
                }

                // check to see if it is a type of block we want to close. Note that
                // we're not doing any type checking on these. I don't want to have to
                // maintain a finite list of doors/gates that has to be updated with
                // each version of Minecraft.

                if (blockData instanceof TrapDoor) {
                    activeScheduledTasks.put(blockLocation, ScheduleClose(clickedBlock, null, blockLocation, Settings.secondsToRemainOpen));
                } else if (blockData instanceof Gate) {
                    activeScheduledTasks.put(blockLocation, ScheduleClose(clickedBlock, null, blockLocation, Settings.secondsToRemainOpen));
                } else if (blockData instanceof Door) {
                    activeScheduledTasks.put(blockLocation, ScheduleClose(clickedBlock, pairedDoorBlock, blockLocation, Settings.secondsToRemainOpen));
                }
            }
        }
    }

    // handles getting the Openable from a specific block
    // returns null if not an Openable
    private Openable OpenableFromBlock(Block block) {
        if (block != null) {
            BlockData data = block.getBlockData();

            if (data != null && data instanceof Openable) {
                return (Openable)data;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // handles getting the door from a specific block
    // returns null if not a door
    private Door DoorFromBlock(Block block) {
        if (block != null) {
            BlockData data = block.getBlockData();

            if (data != null && data instanceof Door) {
                return (Door)data;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void PlayCloseNoise(Block doorBlock) {
        if (doorBlock != null) {
            BlockData data = doorBlock.getBlockData();

            if (Settings.playSound) {
                if (data instanceof TrapDoor) {
                    doorBlock.getWorld().playSound(doorBlock.getLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1, 1);
                } else if (data instanceof Gate) {
                    doorBlock.getWorld().playSound(doorBlock.getLocation(), Sound.BLOCK_FENCE_GATE_CLOSE, 1, 1);
                } else if (data instanceof Door) {
                    doorBlock.getWorld().playSound(doorBlock.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1);
                }
            }
        }
    }

    public TaskImplementation<Void> ScheduleClose(Block door1Block, Block pairedDoorBlock, Vector blockLocation, float seconds) {
        long delayTicks = Math.max(1L, (long) (seconds * TICKS_PER_SECOND));

        return scheduler.region(door1Block).runDelayed(task -> {
            boolean closedFirstDoor = true;

            if (door1Block != null) {
                Openable door1Data = OpenableFromBlock(door1Block);
                if (door1Data != null) {
                    if (door1Data.isOpen()) {
                        CloseDoor(door1Block);
                        closedFirstDoor = true;
                    } else {
                        OpenDoor(door1Block);
                        closedFirstDoor = false;
                    }
                    PlayCloseNoise(door1Block);
                } else {
                    _plugin.getLogger().warning("Tried to close door block, but block data was null or not correct type.");
                }
            } else {
                _plugin.getLogger().warning("Null main door block sent to ScheduleClose.");
            }

            if (pairedDoorBlock != null) {
                Openable pairedDoorData = OpenableFromBlock(pairedDoorBlock);

                if (pairedDoorData != null) {
                    if (closedFirstDoor) {
                        CloseDoor(pairedDoorBlock);
                    } else {
                        OpenDoor(pairedDoorBlock);
                    }
                    PlayCloseNoise(pairedDoorBlock);
                } else {
                    _plugin.getLogger().warning("Tried to close paired door block, but block data was null or not correct type.");
                }
            }
            activeScheduledTasks.remove(blockLocation);
        }, delayTicks);
    }


    // Get persistent location from a double/single door to use as a key for scheduled task Map.
    // We assume that doorBlock is bottom half of an actual door.
    private Block GetDoubleDoorLocation(Block doorBlock){
        if (doorBlock.getBlockData() instanceof Door) {
            Door doorData = (Door)(doorBlock.getBlockData());
            Hinge hinge = doorData.getHinge();

            Block pairedDoor = null;

            BlockFace face = doorData.getFacing();

            //_plugin.getLogger().info("[L] DEBUG: door face=" + face.toString());
            //_plugin.getLogger().info("[L] DEBUG: door isOpen()=" + doorData.isOpen());
            //_plugin.getLogger().info("[L] DEBUG: door hinge=" + hinge.toString());

            switch (face) {
                case NORTH:
                    if (hinge == Hinge.LEFT) {
                        pairedDoor = doorBlock.getRelative(BlockFace.EAST);
                    } else {
                        return doorBlock;
                    }
                    break;

                case SOUTH:
                    if (hinge == Hinge.LEFT) {
                        pairedDoor = doorBlock.getRelative(BlockFace.WEST);
                    } else {
                        return doorBlock;
                    }
                    break;

                case EAST:
                    if (hinge == Hinge.LEFT) {
                        pairedDoor = doorBlock.getRelative(BlockFace.SOUTH);
                    } else {
                        return doorBlock;
                    }
                    break;

                case WEST:
                    if (hinge == Hinge.LEFT) {
                        pairedDoor = doorBlock.getRelative(BlockFace.NORTH);
                    } else {
                        return doorBlock;
                    }
                    break;

                default:
                    return doorBlock;
            }

            if (pairedDoor != null) {
                // check the block we found that is opposite the hinge. If it
                // is a door and has a hinge that is opposite this one, then
                // it is our pair

                BlockData data = pairedDoor.getBlockData();

                // check to see if that block is actually a door
                if (data instanceof Door) {
                    Door door2 = (Door)data;

                    //_plugin.getLogger().info("[L] DEBUG: Door neighbor is a door.");
                    //_plugin.getLogger().info("[L] DEBUG: paired door face=" + door2.getFacing().toString());
                    //_plugin.getLogger().info("[L] DEBUG: paired door isOpen()=" + door2.isOpen());
                    //_plugin.getLogger().info("[L] DEBUG: paired door hinge=" + door2.getHinge().toString());


                    if ((hinge == Hinge.LEFT && door2.getHinge() == Hinge.RIGHT) ||
                            (hinge == Hinge.RIGHT && door2.getHinge() == Hinge.LEFT)) {
                        //_plugin.getLogger().info("DEBUG: Found paired / double door");

                        // we're good!
                        return pairedDoor;
                    } else {
                        //_plugin.getLogger().info("DEBUG: Neighbor has hinge on same side. Not a double door");

                        // neighbor block has hinge on same side
                        // not the pair for this door
                        return doorBlock;
                    }
                } else {
                    //_plugin.getLogger().info("DEBUG: Neighbor block is not a door. Not a double door.");

                    // neighbor block is not a door.
                    // door is a single door, not a double door
                    return doorBlock;
                }
            } else {
                // no block next door. That would be ... odd
                // door is a single door, not double
                return doorBlock;
            }
        } else {
            _plugin.getLogger().warning("Bogus block type passed into GetPairedDoorBlockIfDoubleDoor.");
            return doorBlock;
        }
    }

    private Block GetPairedDoorBlockIfDoubleDoor(Block doorBlock) {
        if (doorBlock.getBlockData() instanceof Door) {
            Door doorData = (Door)(doorBlock.getBlockData());
            Hinge hinge = doorData.getHinge();

            Block pairedDoor = null;

            BlockFace face = doorData.getFacing();

            //_plugin.getLogger().info("DEBUG: door face=" + face.toString());
            //_plugin.getLogger().info("DEBUG: door isOpen()=" + doorData.isOpen());
            //_plugin.getLogger().info("DEBUG: door hinge=" + hinge.toString());

            switch (face) {
                case NORTH:
                    if (hinge == Hinge.LEFT) {
                        pairedDoor = doorBlock.getRelative(BlockFace.EAST);
                    } else {
                        pairedDoor = doorBlock.getRelative(BlockFace.WEST);
                    }
                    break;

                case SOUTH:
                    if (hinge == Hinge.LEFT) {
                        pairedDoor = doorBlock.getRelative(BlockFace.WEST);
                    } else {
                        pairedDoor = doorBlock.getRelative(BlockFace.EAST);
                    }
                    break;

                case EAST:
                    if (hinge == Hinge.LEFT) {
                        pairedDoor = doorBlock.getRelative(BlockFace.SOUTH);
                    } else {
                        pairedDoor = doorBlock.getRelative(BlockFace.NORTH);
                    }
                    break;

                case WEST:
                    if (hinge == Hinge.LEFT) {
                        pairedDoor = doorBlock.getRelative(BlockFace.NORTH);
                    } else {
                        pairedDoor = doorBlock.getRelative(BlockFace.SOUTH);
                    }
                    break;

                default:
                    pairedDoor = null;
                    break;
            }

            if (pairedDoor != null) {
                // check the block we found that is opposite the hinge. If it
                // is a door and has a hinge that is opposite this one, then
                // it is our pair

                BlockData data = pairedDoor.getBlockData();

                // check to see if that block is actually a door
                if (data instanceof Door) {
                    Door door2 = (Door)data;

                    //_plugin.getLogger().info("DEBUG: Door neighbor is a door.");
                    //_plugin.getLogger().info("DEBUG: paired door face=" + door2.getFacing().toString());
                    //_plugin.getLogger().info("DEBUG: paired door isOpen()=" + door2.isOpen());
                    //_plugin.getLogger().info("DEBUG: paired door hinge=" + door2.getHinge().toString());


                    if ((hinge == Hinge.LEFT && door2.getHinge() == Hinge.RIGHT) ||
                        (hinge == Hinge.RIGHT && door2.getHinge() == Hinge.LEFT)) {
                        //_plugin.getLogger().info("DEBUG: Found paired / double door");

                        // we're good!
                        return pairedDoor;
                    } else {
                        //_plugin.getLogger().info("DEBUG: Neighbor has hinge on same side. Not a double door");

                        // neighbor block has hinge on same side
                        // not the pair for this door
                        return null;
                    }
                } else {
                    //_plugin.getLogger().info("DEBUG: Neighbor block is not a door. Not a double door.");

                    // neighbor block is not a door.
                    // door is a single door, not a double door
                    return null;
                }
            } else {
                // no block next door. That would be ... odd
                // door is a single door, not double
                return null;
            }
        } else {
            _plugin.getLogger().warning("Bogus block type passed into GetPairedDoorBlockIfDoubleDoor.");
            return null;
        }
    }


    private void OpenDoor(Block doorBlock) {

        BlockData data = doorBlock.getBlockData();

        if (data instanceof Openable) {
            if (!((Openable)data).isOpen()) {
                ((Openable)data).setOpen(true);

                doorBlock.setBlockData(data);
            }
        } else {
            // someone passed in a bogus block
        }
    }

    private void CloseDoor(Block doorBlock) {

        BlockData data = doorBlock.getBlockData();

        if (data instanceof Openable) {
            if (((Openable)data).isOpen()) {
                ((Openable)data).setOpen(false);

                doorBlock.setBlockData(data);
            }
        } else {
            // someone passed in a bogus block
        }
    }
}
