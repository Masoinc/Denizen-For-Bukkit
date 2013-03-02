package net.aufdemrand.denizen.npc.traits;

import net.aufdemrand.denizen.utilities.Utilities;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.minecraft.server.v1_4_R1.EntityHuman;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SittingTrait extends Trait implements Listener  {
	
	@Persist("sitting")
    private boolean sitting = false;
	
	@Persist("chair location")
	private Location chairLocation = null;
	
	EntityHuman eh = null;
	
	@Override
	public void run() {
		if (eh == null || chairLocation == null) return;
		
		//if (npc.getBukkitEntity().getPassenger() == null && sitting) eh.mount(eh);
			
		if (!Utilities.checkLocation(npc.getBukkitEntity(), chairLocation, 1)) stand();
            
	}
	
	@Override
	public void onSpawn() {
		eh = ((CraftPlayer) npc.getBukkitEntity()).getHandle();
	}
	
	/**
	 * Makes the NPC sit
	 */
	public void sit() {
		if (sitting == true) {
			dB.log("...npc is sitting");
			return;
		}
		
		eh.mount(eh);
		
		sitting = true;
		chairLocation = npc.getBukkitEntity().getLocation();
		return;
	}
	
	/**
	 * Makes the NPC sit a the specified location
	 * 
	 * @param location
	 */
	public void sit(Location location) {
		if (sitting == true) {
			return;
		}
		
		/*
		 * Teleport NPC to the location before
		 * sending the sit packet to the clients.
		 */
		//npc.getBukkitEntity().teleport(location.add(0.5, 0, 0.5));
		eh.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		dB.echoDebug("...NPC moved to chair");
		
		eh.mount(eh);
		
		sitting = true;
		chairLocation = location;
	}
	
	/**
	 * Makes the NPC stand
	 */
	public void stand() {
		if (sitting == false) {
			return;
		}
		
		eh.mount(null);
		
		chairLocation = null;
		sitting = false;
	}
	
	/**
	 * Checks if the NPC is currently sitting
	 * 
	 * @return boolean
	 */
	public boolean isSitting() {
		return sitting;
	}
	
	/**
	 * Gets the chair the NPC is sitting on
	 * Returns null if the NPC isnt sitting
	 * 
	 * @return Location
	 */
	public Location getChair() {
		return chairLocation;
	}
	
    
    /**
     * If someone tries to break the poor
     * NPC's chair, we need to stop them!
     *
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
    	if (chairLocation == null) return;
        if (event.getBlock().getLocation().equals(chairLocation)) {
            event.setCancelled(true);
        }
    }
	
	public SittingTrait() {
		super("sitting");
	}

}
