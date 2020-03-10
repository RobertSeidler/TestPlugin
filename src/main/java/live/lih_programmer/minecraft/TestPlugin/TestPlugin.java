package live.lih_programmer.minecraft.TestPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		getLogger().info("onEnable has been invoked!");
		
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		getLogger().info("onDisable has been invoked!");
	}
}
