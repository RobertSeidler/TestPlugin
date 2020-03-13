package live.lih_programmer.minecraft.TestPlugin;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;


class TestController implements Listener{
    @EventHandler
	public void onPlayerJoined(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.sendMessage(player.getDisplayName() + " has logged onto the Server.");
    }
    
    @EventHandler
    public void onPlayerMoved(PlayerMoveEvent event){
        // Player player = event.getPlayer(); 
        // player.sendMessage(player.getDisplayName() + " has moved.");
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event){
        Entity entity = (event.getEntity());
        entity.sendMessage(event.getItem().getName() + " Item was picked up.");
    }
}