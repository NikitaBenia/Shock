package net.gribok.shock.blocks.entity;

import net.gribok.shock.Shock;
import net.gribok.shock.blocks.ModBlocks;
import net.gribok.shock.blocks.entity.custom.ElectricalFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<ElectricalFurnaceBlockEntity> ELECTRICAL_FURNACE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Shock.MOD_ID, "electrical_furnace_be"),
                    BlockEntityType.Builder.create(ElectricalFurnaceBlockEntity::new, ModBlocks.ELECTRICAL_FURNACE).build(null));

    public static void registerBlockEntities() {
    }
}
