package net.qiuyu.tinkersmastermind.tinkering.modifier.defense;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Map;

public class CoralDefenseModifier extends Modifier implements EnchantmentModifierHook {
    private final ResourceLocation KEY = new ResourceLocation("tinkersmastermind", "coral_defense");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ENCHANTMENTS);
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView iToolStackView, ModifierEntry modifierEntry, Enchantment enchantment, int i) {
        return 0;
    }


    @Override
    public void updateEnchantments(IToolStackView iToolStackView, ModifierEntry modifierEntry, Map<Enchantment, Integer> map) {
        if (map.containsKey(Enchantments.UNBREAKING)) {
            map.put(Enchantments.UNBREAKING, map.get(Enchantments.UNBREAKING) + modifierEntry.getLevel());
        } else {
            map.put(Enchantments.UNBREAKING, modifierEntry.getLevel());
        }
    }

}
