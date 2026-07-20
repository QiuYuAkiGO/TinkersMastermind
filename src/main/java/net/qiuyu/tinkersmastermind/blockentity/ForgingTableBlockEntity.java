package net.qiuyu.tinkersmastermind.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.qiuyu.tinkersmastermind.recipe.ForgingRecipe;
import net.qiuyu.tinkersmastermind.recipe.ModRecipes;
import net.qiuyu.tinkersmastermind.register.ModBlockEntities;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;

import java.util.Optional;

public class ForgingTableBlockEntity extends BlockEntity {
    public static final int SLOT_COUNT = 16;
    public static final double AMBIENT_TEMPERATURE = 25.0D;

    private static final int HAMMER_TEMPERATURE_MISS_DAMAGE = 100;
    private static final double WATER_BUCKET_COOLING = 250.0D;
    private static final double COOLING_RATE = 0.35D;
    private static final String TAG_ITEMS = "Items";
    private static final String TAG_TEMPERATURE = "Temperature";
    private static final String TAG_HEAT_START = "HeatStart";
    private static final String TAG_FUEL_TICKS = "FuelTicks";
    private static final String TAG_FUEL_TEMPERATURE = "FuelTemperature";
    private static final String TAG_FUEL_RATE = "FuelRate";
    private static final String TAG_POWERED = "Powered";
    private static final String TAG_REDSTONE_SIGNAL = "RedstoneSignal";

    private final ItemStackHandler items = new ItemStackHandler(SLOT_COUNT) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            sync();
        }
    };
    private LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> items);

    private double temperature = AMBIENT_TEMPERATURE;
    private double heatStartTemperature = AMBIENT_TEMPERATURE;
    private int fuelTicks;
    private int fuelTemperature = (int) AMBIENT_TEMPERATURE;
    private int fuelRate;
    private int redstoneSignal;

    public ForgingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FORGING_TABLE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ForgingTableBlockEntity forgingTable) {
        int signalNow = level.getBestNeighborSignal(pos);
        boolean poweredBefore = forgingTable.redstoneSignal > 0;
        boolean poweredNow = signalNow > 0;
        boolean powerChanged = poweredBefore != poweredNow;
        boolean signalChanged = forgingTable.redstoneSignal != signalNow;
        forgingTable.redstoneSignal = signalNow;
        if (powerChanged) {
            forgingTable.heatStartTemperature = forgingTable.temperature;
        }

        double oldTemperature = forgingTable.temperature;
        int oldFuelTicks = forgingTable.fuelTicks;
        if (poweredNow) {
            forgingTable.tickHeating(level, pos);
        } else {
            forgingTable.tickCooling();
        }

        boolean changed = powerChanged
                || signalChanged
                || oldFuelTicks != forgingTable.fuelTicks
                || Math.abs(oldTemperature - forgingTable.temperature) >= 0.01D;
        if (changed) {
            forgingTable.setChanged();
            if (powerChanged || level.getGameTime() % 10L == 0L || forgingTable.temperature == AMBIENT_TEMPERATURE) {
                forgingTable.sync();
            }
        }
    }

    private void tickHeating(Level level, BlockPos pos) {
        if (fuelTicks <= 0) {
            FuelUse fuel = consumeFuel(level, pos.below());
            if (fuel.isEmpty()) {
                tickCooling();
                return;
            }
            fuelTicks = Math.max(1, fuel.duration);
            fuelTemperature = Math.max((int) AMBIENT_TEMPERATURE, fuel.temperature);
            fuelRate = Math.max(1, fuel.rate);
        }

        fuelTicks--;
        double target = Math.max(AMBIENT_TEMPERATURE, fuelTemperature);
        if (temperature < target) {
            double signalMultiplier = 0.5D + redstoneSignal / 15.0D;
            temperature = Math.min(target, temperature + Math.max(0.1D, fuelRate * signalMultiplier / 5.0D));
        } else if (temperature > target) {
            temperature = Math.max(target, temperature - COOLING_RATE * 0.5D);
        }
    }

    private void tickCooling() {
        if (temperature > AMBIENT_TEMPERATURE) {
            temperature = Math.max(AMBIENT_TEMPERATURE, temperature - COOLING_RATE);
        } else if (temperature < AMBIENT_TEMPERATURE) {
            temperature = Math.min(AMBIENT_TEMPERATURE, temperature + COOLING_RATE);
        }
    }

    public boolean placeOneItem(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return false;
        }

        ItemStack placed = held.copy();
        placed.setCount(1);
        ItemStack remainder = ItemHandlerHelper.insertItem(items, placed, false);
        if (!remainder.isEmpty()) {
            return false;
        }

        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        playSound(SoundEvents.ITEM_FRAME_ADD_ITEM);
        sync();
        return true;
    }

    public boolean takeLastItem(Player player) {
        for (int slot = SLOT_COUNT - 1; slot >= 0; slot--) {
            ItemStack stack = items.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                ItemStack extracted = items.extractItem(slot, stack.getCount(), false);
                giveOrDrop(player, extracted);
                playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM);
                sync();
                return true;
            }
        }
        return false;
    }

    public boolean coolWithWater(Player player, InteractionHand hand) {
        if (level == null || level.isClientSide || temperature <= AMBIENT_TEMPERATURE) {
            return false;
        }

        temperature = Math.max(AMBIENT_TEMPERATURE, temperature - WATER_BUCKET_COOLING);
        if (redstoneSignal > 0 && temperature < heatStartTemperature) {
            heatStartTemperature = temperature;
        }

        consumeWaterBucket(player, hand);
        player.awardStat(Stats.ITEM_USED.get(Items.WATER_BUCKET));
        playSound(SoundEvents.FIRE_EXTINGUISH);
        setChanged();
        sync();
        return true;
    }

    private void consumeWaterBucket(Player player, InteractionHand hand) {
        if (player.getAbilities().instabuild) {
            return;
        }

        ItemStack held = player.getItemInHand(hand);
        ItemStack bucket = new ItemStack(Items.BUCKET);
        if (held.getCount() == 1) {
            player.setItemInHand(hand, bucket);
        } else {
            held.shrink(1);
            giveOrDrop(player, bucket);
        }
    }

    public boolean tryForge(Player player, InteractionHand hand) {
        if (level == null || level.isClientSide) {
            return false;
        }
        if (!canHammerAffordForge(player, hand)) {
            playSound(SoundEvents.ANVIL_LAND);
            return false;
        }

        SimpleContainer container = createRecipeContainer();
        Optional<ForgingRecipe> recipe = findMatchingRecipe(container);

        if (recipe.isEmpty()) {
            returnAllItems(player);
            playSound(SoundEvents.ANVIL_LAND);
            sync();
            return false;
        }

        ForgingRecipe forgingRecipe = recipe.get();
        if (forgingRecipe.isTemperatureClose(temperature)) {
            clearItems();
            placeOrGive(player, forgingRecipe.getResultItem(level.registryAccess()).copy());
            playSound(SoundEvents.ANVIL_USE);
            sync();
            return true;
        }

        damageHammerForTemperatureMiss(player, hand);
        if (forgingRecipe.getByproduct().isEmpty()) {
            returnAllItems(player);
        } else {
            clearItems();
            placeOrGive(player, forgingRecipe.getByproduct().copy());
        }
        playSound(SoundEvents.FIRE_EXTINGUISH);
        sync();
        return false;
    }

    private boolean canHammerAffordForge(Player player, InteractionHand hand) {
        if (player.getAbilities().instabuild) {
            return true;
        }

        ItemStack hammer = player.getItemInHand(hand);
        if (hammer.getItem() instanceof ModifiableItem) {
            ToolStack tool = ToolStack.from(hammer);
            return tool.isUnbreakable() || (!tool.isBroken() && tool.getCurrentDurability() >= HAMMER_TEMPERATURE_MISS_DAMAGE);
        }
        return !hammer.isDamageableItem() || hammer.getMaxDamage() - hammer.getDamageValue() >= HAMMER_TEMPERATURE_MISS_DAMAGE;
    }

    private void damageHammerForTemperatureMiss(Player player, InteractionHand hand) {
        if (player.getAbilities().instabuild) {
            return;
        }

        ItemStack hammer = player.getItemInHand(hand);
        if (hammer.getItem() instanceof ModifiableItem) {
            ToolDamageUtil.damageAnimated(ToolStack.from(hammer), HAMMER_TEMPERATURE_MISS_DAMAGE, player, hand);
        } else if (hammer.isDamageableItem()) {
            hammer.hurtAndBreak(HAMMER_TEMPERATURE_MISS_DAMAGE, player, damagedPlayer -> damagedPlayer.broadcastBreakEvent(hand));
        }
    }

    public void dropContents() {
        if (level == null) {
            return;
        }
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                Block.popResource(level, worldPosition, stack.copy());
                items.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    private void returnAllItems(Player player) {
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            ItemStack stack = items.extractItem(slot, items.getStackInSlot(slot).getCount(), false);
            if (!stack.isEmpty()) {
                giveOrDrop(player, stack);
            }
        }
    }

    private void clearItems() {
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            items.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    private void placeOrGive(Player player, ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int slot = 0; slot < SLOT_COUNT && !remaining.isEmpty(); slot++) {
            remaining = items.insertItem(slot, remaining, false);
        }
        if (!remaining.isEmpty()) {
            giveOrDrop(player, remaining);
        }
    }

    private SimpleContainer createRecipeContainer() {
        SimpleContainer container = new SimpleContainer(SLOT_COUNT);
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            container.setItem(slot, items.getStackInSlot(slot).copy());
        }
        return container;
    }

    public Optional<ForgingRecipe> findMatchingRecipe() {
        return findMatchingRecipe(createRecipeContainer());
    }

    private Optional<ForgingRecipe> findMatchingRecipe(SimpleContainer container) {
        if (level == null) {
            return Optional.empty();
        }
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.FORGING_TYPE.get())
                .stream()
                .filter(candidate -> candidate.matches(container, level))
                .findFirst();
    }

    private void giveOrDrop(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private void playSound(net.minecraft.sounds.SoundEvent sound) {
        if (level != null) {
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 0.8F, 1.0F);
        }
    }

    private static FuelUse consumeFuel(Level level, BlockPos sourcePos) {
        BlockState sourceState = level.getBlockState(sourcePos);
        BlockEntity source = level.getBlockEntity(sourcePos);
        if (source == null) {
            return FuelUse.EMPTY;
        }

        if (source instanceof HeatingStructureBlockEntity heatingStructure) {
            FuelUse heatingStructureFuel = consumeHeatingStructureFuel(heatingStructure);
            if (!heatingStructureFuel.isEmpty()) {
                return heatingStructureFuel;
            }
        }

        boolean fuelTank = sourceState.is(TinkerTags.Blocks.FUEL_TANKS);
        boolean heater = sourceState.is(TinkerTags.Blocks.HEATER_CONTROLLERS);
        if (!fuelTank && !heater) {
            return FuelUse.EMPTY;
        }

        FuelUse liquidFuel = source.getCapability(ForgeCapabilities.FLUID_HANDLER)
                .map(ForgingTableBlockEntity::consumeFluidFuel)
                .orElse(FuelUse.EMPTY);
        if (!liquidFuel.isEmpty()) {
            return liquidFuel;
        }

        if (heater) {
            return source.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .map(handler -> consumeSolidFuel(level, sourcePos, handler))
                    .orElse(FuelUse.EMPTY);
        }
        return FuelUse.EMPTY;
    }

    private static FuelUse consumeHeatingStructureFuel(HeatingStructureBlockEntity heatingStructure) {
        FuelModule fuelModule = heatingStructure.getFuelModule();
        if (!fuelModule.hasFuel() && fuelModule.findFuel(true) <= 0) {
            return FuelUse.EMPTY;
        }

        fuelModule.decreaseFuel(1);
        return new FuelUse(1, fuelModule.getTemperature(), fuelModule.getRate());
    }

    private static FuelUse consumeFluidFuel(IFluidHandler handler) {
        for (int tank = 0; tank < handler.getTanks(); tank++) {
            FluidStack stack = handler.getFluidInTank(tank);
            if (stack.isEmpty()) {
                continue;
            }

            Fluid fluid = stack.getFluid();
            MeltingFuel fuel = MeltingFuelLookup.findFuel(fluid);
            if (fuel == null) {
                continue;
            }

            int amount = fuel.getAmount(fluid);
            if (amount <= 0 || stack.getAmount() < amount) {
                continue;
            }

            FluidStack toDrain = new FluidStack(stack, amount);
            FluidStack drained = handler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
            if (drained.getAmount() >= amount) {
                return new FuelUse(fuel.getDuration(), fuel.getTemperature(), fuel.getRate());
            }
        }
        return FuelUse.EMPTY;
    }

    private static FuelUse consumeSolidFuel(Level level, BlockPos sourcePos, IItemHandler handler) {
        MeltingFuel solidFuel = MeltingFuelLookup.getSolid();
        if (solidFuel == null) {
            return FuelUse.EMPTY;
        }

        RecipeType<?> fuelRecipeType = TinkerRecipeTypes.FUEL.get();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            int burnTime = ForgeHooks.getBurnTime(stack, fuelRecipeType) / 4;
            if (burnTime <= 0) {
                continue;
            }

            ItemStack extracted = handler.extractItem(slot, 1, false);
            if (extracted.isEmpty()) {
                continue;
            }

            ItemStack remainder = extracted.getCraftingRemainingItem();
            if (!remainder.isEmpty()) {
                ItemStack rejected = ItemHandlerHelper.insertItem(handler, remainder, false);
                if (!rejected.isEmpty()) {
                    Block.popResource(level, sourcePos.above(), rejected);
                }
            }

            return new FuelUse(burnTime, solidFuel.getTemperature(), solidFuel.getRate());
        }
        return FuelUse.EMPTY;
    }

    public ItemStack getDisplayStack(int slot) {
        return items.getStackInSlot(slot);
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHeatStartTemperature() {
        return heatStartTemperature;
    }

    public int getFuelTemperature() {
        return fuelTemperature;
    }

    public boolean shouldShowTemperatureBar() {
        return redstoneSignal > 0;
    }

    public double getTemperatureProgress() {
        double min = Math.min(heatStartTemperature, fuelTemperature);
        double max = Math.max(heatStartTemperature, fuelTemperature);
        if (Math.abs(max - min) < 0.001D) {
            return 0.0D;
        }
        return Math.max(0.0D, Math.min(1.0D, (temperature - min) / (max - min)));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(TAG_ITEMS, items.serializeNBT());
        tag.putDouble(TAG_TEMPERATURE, temperature);
        tag.putDouble(TAG_HEAT_START, heatStartTemperature);
        tag.putInt(TAG_FUEL_TICKS, fuelTicks);
        tag.putInt(TAG_FUEL_TEMPERATURE, fuelTemperature);
        tag.putInt(TAG_FUEL_RATE, fuelRate);
        tag.putInt(TAG_REDSTONE_SIGNAL, redstoneSignal);
        tag.putBoolean(TAG_POWERED, redstoneSignal > 0);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        CompoundTag itemTag = tag.getCompound(TAG_ITEMS).copy();
        itemTag.putInt("Size", SLOT_COUNT);
        items.deserializeNBT(itemTag);
        temperature = tag.contains(TAG_TEMPERATURE) ? tag.getDouble(TAG_TEMPERATURE) : AMBIENT_TEMPERATURE;
        heatStartTemperature = tag.contains(TAG_HEAT_START) ? tag.getDouble(TAG_HEAT_START) : AMBIENT_TEMPERATURE;
        fuelTicks = tag.getInt(TAG_FUEL_TICKS);
        fuelTemperature = tag.contains(TAG_FUEL_TEMPERATURE) ? tag.getInt(TAG_FUEL_TEMPERATURE) : (int) AMBIENT_TEMPERATURE;
        fuelRate = tag.getInt(TAG_FUEL_RATE);
        redstoneSignal = tag.contains(TAG_REDSTONE_SIGNAL) ? tag.getInt(TAG_REDSTONE_SIGNAL) : (tag.getBoolean(TAG_POWERED) ? 15 : 0);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            load(tag);
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemCapability = LazyOptional.of(() -> items);
    }

    private void sync() {
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    private static class FuelUse {
        private static final FuelUse EMPTY = new FuelUse(0, 0, 0);

        private final int duration;
        private final int temperature;
        private final int rate;

        private FuelUse(int duration, int temperature, int rate) {
            this.duration = duration;
            this.temperature = temperature;
            this.rate = rate;
        }

        private boolean isEmpty() {
            return duration <= 0 || temperature <= 0;
        }
    }
}
