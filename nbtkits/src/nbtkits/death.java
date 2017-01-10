package nbtkits;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class death implements Listener {

	public kits plugin;
	public death(kits kits) {
		this.plugin = kits;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) throws IOException {
		Player p = e.getEntity();
		if(p.hasPermission("kits.deathinv")) {
			InventoryNBTSer.setInv(p, plugin.folder);
			e.getDrops().clear();
		}
		if(p.hasPermission("kits.deathexp")) {
			e.setKeepLevel(true);
			e.setDroppedExp(0);
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) throws IOException {
		Player p = e.getPlayer();
		if(p.hasPermission("kits.deathinv")) {
			InventoryNBTSer.getInv(p, plugin.folder);
		}
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) throws IOException {
		Player p = e.getPlayer();
		if(!p.hasPlayedBefore()) {
			if(e.getPlayer().hasPermission("kits.starter")) {
				InventoryNBTSer.getKit(p, plugin.folder, "starter", true);
			}
		}
	}
}
