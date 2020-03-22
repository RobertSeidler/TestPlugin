package live.lih_programmer.minecraft.TestPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantment extends Enchantment {

    private String name;
    private String description;
    private int maxLvl;
    private int startLvl;
    private EnchantmentTarget target;
    private boolean treasure;
    private boolean cursed;
    private ArrayList<Enchantment> conflicts;
    private ArrayList<ItemStack> enchantables;

    public CustomEnchantment(
        int id, 
        String name, 
        String description,
        int maxLvl, 
        int startLvl, 
        EnchantmentTarget target, 
        boolean treasure, 
        boolean cursed, 
        ArrayList<Enchantment> conflicts, 
        ArrayList<ItemStack> enchantables
    ){
        super(id);
        this.name = name;
        this.description = description;
        this.maxLvl = maxLvl;
        this.startLvl = startLvl;
        this.target = target;
        this.treasure = treasure;
        this.cursed = cursed;
        this.conflicts = conflicts;
        this.enchantables = enchantables;

        this.defineCustomEnchantment();
    }

    public CustomEnchantment(int id, String name, String description, int maxLvl, int startLvl){
        this(id, name, description, maxLvl, startLvl, null, false, false, new ArrayList<Enchantment>(), new ArrayList<ItemStack>());
    }

    public CustomEnchantment(int id, String name, String description, int maxLvl, int startLvl, ArrayList<ItemStack> enchantables){
        this(id, name, description, maxLvl, startLvl, null, false, false, new ArrayList<Enchantment>(), enchantables);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getMaxLevel() {
        return this.maxLvl;
    }

    @Override
    public int getStartLevel() {
        return this.startLvl;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return this.target;
    }

    @Override
    public boolean isTreasure() {
        return this.treasure;
    }

    @Override
    public boolean isCursed() {
        return this.cursed;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return conflicts.contains(other);
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        if(enchantables.size() == 0){
            return true;
        }
        return enchantables.contains(item);
    }

    public ItemStack createScroll(){
        return (new EnchantedItem(this.name + " 1", this.description, this, 1, Material.ENCHANTED_BOOK)).getItemStack();
    }

    /**
     * Needs to be updated using Enchantment.getKey for newer Version
     */
    @SuppressWarnings("deprecated")
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
				Enchantment.registerEnchantment(this);
			} catch (IllegalArgumentException e){
                if(!this.equals(Enchantment.getById(this.getId()))){
                    Bukkit.getLogger().info("Enchantment ID " + this.getId() + " is already in use. " + this.name);
                }
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}