package live.lih_programmer.minecraft.TestPlugin;

import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public class TestPlugin extends JavaPlugin implements Listener{

	private static Plugin instance;

	private String COMMAND_FAILED = "Execution failed.";
	public static Enchantment COLLECT_ENCH;

	public TestPlugin(){
		super();
		instance = this;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("givecollectchest")){
			try{
				int lvl = Integer.parseInt(args[0]);
				ItemStack chest = (new EnchantedItem("Chainmail", "Collect " + lvl, TestPlugin.COLLECT_ENCH, lvl).getItemStack());
				((Player) sender).getInventory().addItem(chest);
				return true;
			} catch(Exception e){
				sender.sendMessage(COMMAND_FAILED);
				e.printStackTrace();
			}
		} else if(command.getName().equalsIgnoreCase("givecollectscroll")){
			try{
				ItemStack scroll = ((CustomEnchantment)COLLECT_ENCH).createScroll(1);
				((Player) sender).getInventory().addItem(scroll);
				return true;
			} catch(Exception e){
				sender.sendMessage(COMMAND_FAILED);
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void onEnable() {
		getLogger().info("onEnable has been invoked!");
		
		COLLECT_ENCH = new CustomEnchantment(69, "Collect", "Regularly picks up items from a big radius around the Player.", 2, 1);

		defineScheduler();
	}

	private void defineScheduler(){
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		
			@Override
			public void run() {
				applyCollectAbillity();
			}
			
			private void collectNearbyItems(Player player, Item item){
				HashMap<Integer, ItemStack> leftOverItems = player.getInventory().addItem(((Item)item).getItemStack());
				item.remove();
				for (Integer key : leftOverItems.keySet()) {
					player.getWorld().dropItemNaturally(player.getLocation(), leftOverItems.get(key));
				}
			}
		
			private void applyCollectAbillity(){
				Collection<? extends Player> playersOnline = TestPlugin.getPlugin(TestPlugin.class).getServer().getOnlinePlayers();
				for (Player player : playersOnline) {
					if(player.getEquipment().getChestplate() != null && player.getEquipment().getChestplate().getEnchantments().containsKey(TestPlugin.COLLECT_ENCH)){
						searchItemsNearby(player);
					}
				}
			}
		
			private void searchItemsNearby(Player player){
					int enchLvl = player.getEquipment().getChestplate().getEnchantmentLevel(TestPlugin.COLLECT_ENCH);
					Location playerLocation = player.getLocation();
					Collection<Entity> entitys = player.getWorld().getNearbyEntities(playerLocation, enchLvl * (3.0), 2.0, enchLvl * (3.0));
			
					for (Entity item : entitys) {
						if(item instanceof Item){
							collectNearbyItems(player, (Item)item);
						}
					}
			}

		}, 200*3L, 100);
	}
	
	@Override
	public void onDisable() {
		getLogger().info("onDisable has been invoked!");
	}

	public static Plugin getInstance(){
		return instance;
	}
}
