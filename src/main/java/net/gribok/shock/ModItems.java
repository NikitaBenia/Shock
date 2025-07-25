package net.gribok.shock;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item COPPER_NUGGET = register("copper_nugget", new Item(new Item.Settings()));

    public static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Shock.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(ModItems.COPPER_NUGGET);
        });
    }
}
