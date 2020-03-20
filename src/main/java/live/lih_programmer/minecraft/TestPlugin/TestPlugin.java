package live.lih_programmer.minecraft.TestPlugin;

import java.lang.reflect.Field;
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
import org.bukkit.plugin.java.JavaPlugin;


public class TestPlugin extends JavaPlugin implements Listener{

	public static Enchantment COLLECT_ENCH = new CustomEnchantment(69, "Collect", 2, 1);

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("givecollectchest")){
			try{
				int lvl = Integer.parseInt(args[0]);
				ItemStack chest = (new EnchantedItem("Collect " + lvl, "", TestPlugin.COLLECT_ENCH, lvl).getItemStack());
				((Player) sender).getInventory().addItem(chest);
				return true;
			} catch(Exception e){
				sender.sendMessage(e.getMessage());
			}
		}
		return false;
	}

	@Override
	public void onEnable() {
		getLogger().info("onEnable has been invoked!");

		defineCustomEnchantment();

		defineScheduler();
	}

	private void defineCustomEnchantment(){
		try{
			try{
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
			} catch(Exception e){
				e.printStackTrace();
			}
			try{
				Enchantment.registerEnchantment(TestPlugin.COLLECT_ENCH);
			} catch (IllegalArgumentException e){
				getLogger().info("Enchantment ID is already in use.");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private void defineScheduler(){
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		
			@Override
			public void run() {
				// long start = System.currentTimeMillis();
				
				applyCollectAbillity();

				// long timePassed = System.currentTimeMillis() - start;

				// getLogger().info("Time passed during sync Task: " + timePassed + " ms");
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
}
