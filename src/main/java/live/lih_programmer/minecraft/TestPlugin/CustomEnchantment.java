package live.lih_programmer.minecraft.TestPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class CustomEnchantment extends Enchantment implements Listener {

    private String name;
    private String description;
    private int maxLvl;
    private int startLvl;
    private EnchantmentTarget target;
    private boolean treasure;
    private boolean cursed;
    private ArrayList<Enchantment> conflicts;
    private int anvilCostModifier;

    private final Plugin testPlugin;

    public CustomEnchantment(int id, String name, String description, int maxLvl, int startLvl,
            EnchantmentTarget target, boolean treasure, boolean cursed, ArrayList<Enchantment> conflicts, int anvilCostModifier) {
        super(id);
        this.name = name;
        this.description = description;
        this.maxLvl = maxLvl;
        this.startLvl = startLvl;
        this.target = target;
        this.treasure = treasure;
        this.cursed = cursed;
        this.conflicts = conflicts;
        this.anvilCostModifier = anvilCostModifier;

        this.defineCustomEnchantment();

        testPlugin = TestPlugin.getInstance();
        testPlugin.getServer().getPluginManager().registerEvents(this,
                testPlugin.getServer().getPluginManager().getPlugin("TestPlugin"));
    }

    public CustomEnchantment(int id, String name, String description, int maxLvl, int startLvl) {
        this(id, name, description, maxLvl, startLvl, EnchantmentTarget.ARMOR_TORSO, false, false, new ArrayList<Enchantment>(), 1);
    }

    public int getAnvilCostModifier() {
        return anvilCostModifier;
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
        if (target.includes(item) || item.getType().equals(Material.ENCHANTED_BOOK) || item.getType().equals(Material.BOOK)) {
            return true;
        }
        return false;
    }

    public ItemStack createScroll(int lvl) {
        return (new EnchantedItem("Enchanted Book", this.name + " " + lvl, this, lvl, Material.ENCHANTED_BOOK))
                .getItemStack();
    }

    // private ArrayList<ItemStack> createScrollForEachLvl() {
    //     ArrayList<ItemStack> scrolls = new ArrayList<ItemStack>();
    //     for (int i = 1; i <= this.maxLvl; i++) {
    //         scrolls.add(this.createScroll(i));
    //     }
    //     return scrolls;
    // }

    /**
     * Needs to be updated using Enchantment.getKey for newer Version
     */
    @SuppressWarnings("deprecated")
    private void defineCustomEnchantment() {
        try {
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Enchantment.registerEnchantment(this);
            } catch (IllegalArgumentException e) {
                if (!this.equals(Enchantment.getById(this.getId()))) {
                    Bukkit.getLogger().info("Enchantment ID " + this.getId() + " is already in use. " + this.name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int calculateCombinedEnchantmentLevel(ItemStack item1, ItemStack item2, Enchantment enchantment) {
        if (item1.getEnchantmentLevel(enchantment) == item2.getEnchantmentLevel(enchantment)) {
            return Math.min(enchantment.getMaxLevel(), item1.getEnchantmentLevel(enchantment) + 1);
        } else {
            return Math.min(enchantment.getMaxLevel(), Math.max(item1.getEnchantmentLevel(enchantment), item2.getEnchantmentLevel(enchantment)));
        }
    }

    public static int setCombinedDurabilityGetAddedCost(ItemStack resultItem, ItemStack item1, ItemStack item2) {
        try {
            short maxDurability = resultItem.getType().getMaxDurability();
            if (item1.getType().equals(item2.getType()) && !(item1.getType().equals(Material.ENCHANTED_BOOK)
                    && item2.getType().equals(Material.ENCHANTED_BOOK))) {
                resultItem.setDurability((short) Math.min(maxDurability,
                        item1.getDurability() + item2.getDurability() + (0.12 * maxDurability)));
            }
            return 2;
        } catch (Exception e) {
            // Item might not have durability, in that case added cost.
            return 0;
        }
    }

    public static int combineEnchantmentsGetCost(ItemStack resultItem, ItemStack item1, ItemStack item2,
            boolean bookSacrifice) {
        int cost = 0; 

        for (Enchantment ench : item2.getEnchantments().keySet()) {
            boolean conflict = false;
            for(Enchantment targetEnch : item1.getEnchantments().keySet()){
                if((targetEnch instanceof CustomEnchantment && targetEnch.conflictsWith(ench)) || (ench instanceof CustomEnchantment && ench.conflictsWith(targetEnch))) conflict = true;
            }
            if (ench.canEnchantItem(resultItem) && !conflict) {
                System.out.println("Ench: " + ench.getName());
                int targetLevel = calculateCombinedEnchantmentLevel(item1, item2, ench);
                resultItem.addEnchantment(ench, targetLevel);
                cost += targetLevel * getCostMultiplierForEnchantment(ench, !bookSacrifice);
            } else if(conflict){
                cost += 1;
            }
        }
        return cost;
    }

    private void setItemMeta(ItemStack targetItem, ItemStack sacrifice){
        ItemMeta itemMeta = targetItem.getItemMeta();
        
        ArrayList<String> lore = new ArrayList<String>();

        for(Enchantment ench : targetItem.getEnchantments().keySet()){
            lore.add(ench.getName() + " " + targetItem.getEnchantmentLevel(ench));
        }
        itemMeta.setLore(lore);
        
        targetItem.setItemMeta(itemMeta);
    }

    public void setAnvilCostDelayed(final AnvilInventory anvilInv, final int cost) {

        testPlugin.getServer().getScheduler().runTask(testPlugin, new Runnable() {
            @Override
            public void run() {
                anvilInv.setRepairCost(cost);
            }
        });
    }

    private void createAnvilResult(PrepareAnvilEvent event, Inventory anvilInv, ItemStack item1, ItemStack item2, boolean bookSacrifice){
            ItemStack resultItem = item1.clone();
            int cost = 0;
            cost += combineEnchantmentsGetCost(resultItem, item1, item2, bookSacrifice);
            cost += setCombinedDurabilityGetAddedCost(resultItem, item1, item2);
            setItemMeta(resultItem, item2);
            event.setResult(resultItem);
            setAnvilCostDelayed((AnvilInventory)anvilInv, cost);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePrepareAnvilEvent(PrepareAnvilEvent event) {
        try {
            System.out.println("event handler prepareAnvil was called.");
            Inventory anvilInv = event.getInventory();
            ItemStack item1 = anvilInv.getItem(0);
            ItemStack item2 = anvilInv.getItem(1);
            boolean sameType = item1.getType().equals(item2.getType());
            boolean bookSacrifice = item2.getType() == (Material.ENCHANTED_BOOK);
            System.out.println("type: " + item1.getType() + " | " + item2.getType());
            if ((item1.containsEnchantment(this) || item2.containsEnchantment(this)) && (sameType || bookSacrifice)) {
                createAnvilResult(event, anvilInv, item1, item2, bookSacrifice);
            }
            
        } catch (NullPointerException e) {
            // one of the items is null, so i dont care about setting the result.
        }
    }

    // public enum EnchantmentEnum{
    //     PROTECTION();

    //     private Enchantment enchantment;
    //     private int itemModifier;
    //     private int bookModifier;

    //     private EnchantmentEnum(Enchantment enchantment, int itemModifier, int bookModifier){
    //         this.enchantment = enchantment;
            
    //     }
    // }

    public static class AdditionalEnchantmentInfo{
        
        private Enchantment enchantment;
        private String displayName;
        private int itemModifier;
        private int bookModifier;
        
        public AdditionalEnchantmentInfo(Enchantment enchantment, String displayName, int itemModifier, int bookModifier){
            this.enchantment = enchantment;
            this.displayName = displayName;
            this.itemModifier = itemModifier;
            this.bookModifier = bookModifier;
        }

        public Enchantment getEnchantment(){
            return enchantment;
        }

        public String getDisplayName(){
            return displayName;
        }

        public int getItemModifier(){
            return itemModifier;
        }

        public int getBookModifier(){
            return bookModifier;
        }
    }

    public static int getCostMultiplierForEnchantment(Enchantment ench, boolean isItemModifier){
        int modifier;
        //Protection / 1 / 1
        if(ench.equals(Enchantment.PROTECTION_ENVIRONMENTAL)){
            modifier = isItemModifier ? 1 : 1 ;
        }
        //Fire Protection / 2 / 1
        else if(ench.equals(Enchantment.PROTECTION_FIRE)){
            modifier = isItemModifier ? 2 : 1;
        }
        //Feather Falling / 2 / 1
        else if(ench.equals(Enchantment.PROTECTION_FALL)){
            modifier = isItemModifier ? 2 : 1;
        }
        //Blast Protection / 4 / 2
        else if(ench.equals(Enchantment.PROTECTION_EXPLOSIONS)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Projectile Protection / 2 / 1
        else if(ench.equals(Enchantment.PROTECTION_PROJECTILE)){
            modifier = isItemModifier ? 2 : 1;
        }
        //Thorns / 8 / 4
        else if(ench.equals(Enchantment.THORNS)){
            modifier = isItemModifier ? 8 : 4;
        }
        //Respiration / 4 / 2
        else if(ench.equals(Enchantment.OXYGEN)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Depth Strider / 4 / 2
        else if(ench.equals(Enchantment.DEPTH_STRIDER)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Aqua Affinity / 4 / 2
        // if(ench.equals(Enchantment.)){
        //     modifier = 1;
        // }
        //Sharpness / 1 / 1
        else if(ench.equals(Enchantment.DAMAGE_ALL)){
            modifier = isItemModifier ? 1 : 1;
        }
        //Smite / 2 / 1
        else if(ench.equals(Enchantment.DAMAGE_UNDEAD)){
            modifier = isItemModifier ? 2 : 1;
        }
        //Bane of Arthropods / 2 / 1
        else if(ench.equals(Enchantment.DAMAGE_ARTHROPODS)){
            modifier = isItemModifier ? 2 : 1;
        }
        //Knockback / 2 / 1
        else if(ench.equals(Enchantment.KNOCKBACK)){
            modifier = isItemModifier ? 2 : 1;
        }
        //Fire Aspect / 4 / 2
        else if(ench.equals(Enchantment.FIRE_ASPECT)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Looting / 4 / 2
        else if(ench.equals(Enchantment.LOOT_BONUS_MOBS)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Efficiency / 1 / 1
        else if(ench.equals(Enchantment.DIG_SPEED)){
            modifier = isItemModifier ? 1 : 1;
        }
        //Silk Touch / 8 / 4
        else if(ench.equals(Enchantment.SILK_TOUCH)){
            modifier = isItemModifier ? 8 : 4;
        }
        //Unbreaking / 2 / 1
        else if(ench.equals(Enchantment.DURABILITY)){
            modifier = isItemModifier ? 2 : 1;
        }
        //Fortune / 4 / 2
        else if(ench.equals(Enchantment.LOOT_BONUS_BLOCKS)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Power / 1 / 1
        else if(ench.equals(Enchantment.ARROW_DAMAGE)){
            modifier = isItemModifier ? 1 : 1;
        }
        //Punch / 4 / 2
        else if(ench.equals(Enchantment.ARROW_KNOCKBACK)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Flame / 4 / 2
        else if(ench.equals(Enchantment.ARROW_FIRE)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Infinity / 8 / 4
        else if(ench.equals(Enchantment.ARROW_INFINITE)){
            modifier = isItemModifier ? 8 : 4;
        }
        //Luck of the Sea / 4 / 2
        else if(ench.equals(Enchantment.LUCK)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Lure / 4 / 2
        else if(ench.equals(Enchantment.LURE)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Frost Walker / 4 / 2
        else if(ench.equals(Enchantment.FROST_WALKER)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Mending / 4 / 2
        else if(ench.equals(Enchantment.MENDING)){
            modifier = isItemModifier ? 4 : 2;
        }
        //Curse of Binding / 8 / 4
        else if(ench.equals(Enchantment.BINDING_CURSE)){
            modifier = isItemModifier ? 8 : 4;
        }
        //Curse of Vanishing / 8 / 4
        else if(ench.equals(Enchantment.VANISHING_CURSE)){
            modifier = isItemModifier ? 8 : 4;
        }
        //Impaling / 4 / 2
        // if(ench.equals(Enchantment.)){
        //     modifier = 1;
        // }
        //Riptide / 4 / 2
        // if(ench.equals(Enchantment.)){
        //     modifier = 1;
        // }
        //Loyalty / 1 / 1
        // if(ench.equals(Enchantment.)){
        //     modifier = 1;
        // }
        //Channeling / 8 / 4
        // if(ench.equals(Enchantment.)){
        //     modifier = 1;
        // }
        //Multishot / 4 / 2
        // if(ench.equals(Enchantment.)){
        //     modifier = 1;
        // }
        //Piercing / 1 / 1
        // if(ench.equals(Enchantment.)){
        //     modifier = 1;
        // }
        //Quick Charge / 2 / 1
        // if(ench.equals(Enchantment.)){
        //     modifier = 1;
        // }
        //Sweeping Edge / 4 / 2
        else if(ench.equals(Enchantment.SWEEPING_EDGE)){
            modifier = isItemModifier ? 4 : 2;
        } else{
            try{
                CustomEnchantment customEnch = ((CustomEnchantment)ench);
                modifier = customEnch.getAnvilCostModifier();
            } catch(Exception e){
                modifier = 1;
            }
        }
        return modifier;
    }
}