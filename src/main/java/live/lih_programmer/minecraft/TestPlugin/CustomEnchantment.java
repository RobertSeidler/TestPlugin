package live.lih_programmer.minecraft.TestPlugin;

import java.util.ArrayList;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantment extends Enchantment {

    private String name;
    private int maxLvl;
    private int startLvl;
    private EnchantmentTarget target;
    private boolean treasure;
    private boolean cursed;
    private ArrayList<Enchantment> conflicts;
    private ArrayList<ItemStack> enchantables;

    public CustomEnchantment(
        int id, String name, int maxLvl, int startLvl, EnchantmentTarget target, 
        boolean treasure, boolean cursed, ArrayList<Enchantment> conflicts, ArrayList<ItemStack> enchantables
    ){
        super(id);
        this.name = name;
        this.maxLvl = maxLvl;
        this.startLvl = startLvl;
        this.target = target;
        this.treasure = treasure;
        this.cursed = cursed;
        this.conflicts = conflicts;
        this.enchantables = enchantables;
    }

    public CustomEnchantment(int id, String name, int maxLvl, int startLvl){
        this(id, name, maxLvl, startLvl, null, false, false, null, null);
    }

    public CustomEnchantment(int id, String name, int maxLvl, int startLvl, ArrayList<ItemStack> enchantables){
        this(id, name, maxLvl, startLvl, null, false, false, null, enchantables);
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
        return enchantables.contains(item);
    }

}