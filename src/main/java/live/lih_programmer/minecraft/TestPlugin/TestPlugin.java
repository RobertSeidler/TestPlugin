package live.lih_programmer.minecraft.TestPlugin;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin implements Listener{
	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		getLogger().info("onEnable has been invoked!");
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new TestController(), this);
		
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		getLogger().info("onDisable has been invoked!");
	}
}
