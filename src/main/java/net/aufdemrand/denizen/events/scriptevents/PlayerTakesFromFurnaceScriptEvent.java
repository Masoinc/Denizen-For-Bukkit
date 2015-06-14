package net.aufdemrand.denizen.events.scriptevents;

import net.aufdemrand.denizen.objects.*;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;

import java.util.HashMap;

public class PlayerTakesFromFurnaceScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player takes item from furnace
    // player takes <item> from furnace
    // player takes <material> from furnace
    //
    // @Triggers when a player takes an item from a furnace.
    // @Context
    // <context.location> returns the dLocation of the furnace.
    // <context.item> returns the dItem taken out of the furnace.
    //
    // @Determine
    // Element(Number) to set the amount of experience the player will get.
    //
    // -->

    public PlayerTakesFromFurnaceScriptEvent() {
        instance = this;
    }
    public static PlayerTakesFromFurnaceScriptEvent instance;
    public dLocation location;
    public dItem item;
    public Element xp;
    public FurnaceExtractEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return lower.startsWith("player takes")
                && (CoreUtilities.getXthArg(4, lower).equals("furnace"));
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String itemTest = CoreUtilities.getXthArg(2, CoreUtilities.toLowerCase(s));

        return (itemTest.equals("block")
                || !itemTest.equals(item.identifySimpleNoIdentifier()));
    }

    @Override
    public String getName() {
        return "PlayerTakesFromFurnace";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        FurnaceExtractEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        String lower = CoreUtilities.toLowerCase(determination);

        if (aH.Argument.valueOf(lower).matchesPrimitive(aH.PrimitiveType.Integer)) {
            xp = aH.Argument.valueOf(lower).asElement();
            return true;
        }
        return super.applyDetermination(container, determination);
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        context.put("location", location);
        context.put("item", item);
        return context;
    }

    @EventHandler
    public void onPlayerTakesFromFurnace(FurnaceExtractEvent event) {
        item = new dItem(dMaterial.getMaterialFrom(event.getItemType()), event.getItemAmount());
        location = new dLocation(event.getBlock().getLocation());
        xp = new Element(event.getExpToDrop());
        this.event = event;
        fire();
        event.setExpToDrop(xp.asInt());
    }

}