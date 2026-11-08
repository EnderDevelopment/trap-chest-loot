package com.enderdevelopment.plugin;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class TrapChestLootListener implements Listener {
    private final NamespacedKey lootedKey;

    public TrapChestLootListener(JavaPlugin plugin) {
        this.lootedKey = new NamespacedKey(plugin, "loot_generated");
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        if (!(event.getInventory().getHolder(false) instanceof Chest chest)) {
            return;
        }
        if (chest.getBlock().getType() != Material.TRAPPED_CHEST) {
            return;
        }

        PersistentDataContainer data = chest.getPersistentDataContainer();
        if (data.has(lootedKey, PersistentDataType.BYTE)) {
            return;
        }

        addLoot(event.getInventory());
        data.set(lootedKey, PersistentDataType.BYTE, (byte) 1);
        chest.update(true, false);
        player.sendMessage(Component.text("Trap chest loot discovered!", NamedTextColor.GOLD));
    }

    private void addLoot(Inventory inventory) {
        inventory.addItem(createGun());
        inventory.addItem(createAmmo(12));
        inventory.addItem(createModdedOre());
        if (ThreadLocalRandom.current().nextBoolean()) {
            inventory.addItem(createAmmo(8));
        }
    }

    private ItemStack createGun() {
        ItemStack gun = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = gun.getItemMeta();
        if (meta == null) {
            return gun;
        }
        meta.displayName(Component.text("Combat Rifle", NamedTextColor.RED));
        meta.lore(List.of(
                Component.text("A salvaged automatic weapon.", NamedTextColor.GRAY),
                Component.text("Uses standard arrow ammunition.", NamedTextColor.DARK_GRAY)));
        Enchantment quickCharge = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("quick_charge"));
        Enchantment piercing = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("piercing"));
        if (quickCharge != null) {
            meta.addEnchant(quickCharge, 3, true);
        }
        if (piercing != null) {
            meta.addEnchant(piercing, 2, true);
        }
        gun.setItemMeta(meta);
        return gun;
    }

    private ItemStack createAmmo(int amount) {
        ItemStack ammo = new ItemStack(Material.ARROW, amount);
        ItemMeta meta = ammo.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Rifle Ammunition", NamedTextColor.YELLOW));
            ammo.setItemMeta(meta);
        }
        return ammo;
    }

    private ItemStack createModdedOre() {
        Material material = ThreadLocalRandom.current().nextBoolean()
                ? Material.AMETHYST_SHARD
                : Material.RAW_GOLD;
        ItemStack ore = new ItemStack(material, ThreadLocalRandom.current().nextInt(2, 5));
        ItemMeta meta = ore.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Modded Mineral", NamedTextColor.AQUA));
            meta.lore(List.of(Component.text("A strange ore from another world.", NamedTextColor.GRAY)));
            ore.setItemMeta(meta);
        }
        return ore;
    }
}
