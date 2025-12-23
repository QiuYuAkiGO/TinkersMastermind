package net.qiuyu.tinkersmastermind.tinkering.modifier.melee;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.BiConsumer;

public class CoralBoostModifier extends Modifier implements AttributesModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        int level = Math.min(modifier.getLevel(), 3);
        if (level <= 0) {
            return;
        }

        UUID uuid = UUID.nameUUIDFromBytes(
                ("tinkersmastermind:coral_boost:" + slot.getName()).getBytes(StandardCharsets.UTF_8)
        );
        consumer.accept(
                Attributes.MAX_HEALTH,
                new AttributeModifier(uuid, "tinkersmastermind.coral_boost", level * 3.0D, AttributeModifier.Operation.ADDITION)
        );
    }

}
