package net.gribok.shock.blocks.entity.custom;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.gribok.shock.blocks.custom.ElectricalFurnaceBlock;
import net.gribok.shock.blocks.entity.ImplementedInventory;
import net.gribok.shock.blocks.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElectricalFurnaceBlockEntity extends BlockEntity implements ImplementedInventory {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int energy = 0;

    public ElectricalFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELECTRICAL_FURNACE_BE, pos, state);
    }

    public void startBurning(int burnTicks) {
        if (burnTicks > 0) {
            this.burnTime = burnTicks;
            this.maxBurnTime = burnTicks;
            markDirty();
            updateLitProperty(true);
        }
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    private void updateLitProperty(boolean lit) {
        if (world != null && !world.isClient) {
            BlockState state = world.getBlockState(pos);
            if (state.get(ElectricalFurnaceBlock.LIT) != lit) {
                world.setBlockState(pos, state.with(ElectricalFurnaceBlock.LIT, lit), 3);
            }
        }
    }

    // Статический метод, который будет вызываться из тикера
    public static void tick(World world, BlockPos pos, BlockState state, ElectricalFurnaceBlockEntity blockEntity) {
        blockEntity.tickInstance(world, pos, state);
    }

    // Нестатический метод с твоей текущей логикой
    public void tickInstance(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;

        if (burnTime > 0) {
            burnTime--;
            if (burnTime == 0) {
                int energyAdded = 1000;
                energy += energyAdded;
                System.out.println("Electrical Furnace energy: " + energy);
                updateLitProperty(false);
                markDirty();
            }
        } else {
            ItemStack fuel = getStack(0);
            Integer fuelTime = FuelRegistry.INSTANCE.get(fuel.getItem());
            if (fuelTime != null && !fuel.isEmpty()) {
                startBurning(fuelTime);
                fuel.decrement(1);
            }
        }

        if (!state.get(ElectricalFurnaceBlock.LIT)) return;

        Box box = new Box(pos).expand(2, 3, 2);
        List<PlayerEntity> players = world.getNonSpectatingEntities(PlayerEntity.class, box);

        for (PlayerEntity player : players) {
            if (!player.hasStatusEffect(StatusEffects.POISON)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 3));
            }
            if (!player.hasStatusEffect(StatusEffects.NAUSEA)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 180, 5));
            }
        }
    }


    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    // Чтение/запись энергии

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, items, registryLookup);
        nbt.putInt("energy", energy);
        nbt.putInt("burnTime", burnTime);
        nbt.putInt("maxBurnTime", maxBurnTime);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, items, registryLookup);
        energy = nbt.getInt("energy");
        burnTime = nbt.getInt("burnTime");
        maxBurnTime = nbt.getInt("maxBurnTime");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
