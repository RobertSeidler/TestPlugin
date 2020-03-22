package live.lih_programmer.minecraft.TestPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class EnchantedItem{
    
    private String name;
    private ArrayList<String> lore = new ArrayList<String>();
    private Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
    private ItemStack item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
    
    public EnchantedItem(String name, ArrayList<String> lore, Map<Enchantment, Integer> enchants){
        
        this.name = name;
        this.lore = lore;
        this.enchants = enchants;

        this.item.addEnchantments(this.enchants);
        this.setItemMeta();
    }

    public EnchantedItem(String name, ArrayList<String> lore, Map<Enchantment, Integer> enchants, ItemStack item){
        this(name, lore, enchants);
        this.item = item;
    }

    public EnchantedItem(String name, String description, Enchantment enchant, int enchantLvl){

        this.name = name;        
        this.lore.add(description);
        this.enchants.put(enchant, enchantLvl);
        
        this.item.addEnchantments(this.enchants);
        this.setItemMeta();
    }

    public EnchantedItem(String name, String description, Enchantment enchant, int enchantLvl, Material material){
        this(name, description, enchant, enchantLvl);
        this.item = new ItemStack(material);
        this.setItemMeta();
    }

    private void setItemMeta(){
        ItemMeta scrollMeta = this.item.getItemMeta();
        scrollMeta.setDisplayName(this.name);
        scrollMeta.setLore(this.lore);
        for(Enchantment ench : this.enchants.keySet()){
            scrollMeta.addEnchant(ench, this.enchants.get(ench), false);
        } 
        this.item.setItemMeta(scrollMeta);
    }

    public ItemStack getItemStack(){
        return this.item;
    }
}