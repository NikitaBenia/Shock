package net.gribok.shock.blocks;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.gribok.shock.Shock;
import net.gribok.shock.blocks.custom.ElectricalFurnaceBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block ELECTRICAL_FURNACE = registerBlock("electrical_furnace",
            new ElectricalFurnaceBlock(AbstractBlock.Settings.create().strength(4f).nonOpaque()));

    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Shock.MOD_ID, name), block);
    }

    public static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Shock.MOD_ID, name), new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ELECTRICAL_FURNACE);
        });
    }

}
