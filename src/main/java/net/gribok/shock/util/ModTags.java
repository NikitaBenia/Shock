package net.gribok.shock.util;

import net.gribok.shock.Shock;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {

    public static class Items {

        public static final TagKey<Item> COPPER_NUGGET = createTag( "copper_nugget");

        public static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Shock.MOD_ID, name));
        }
    }
}
