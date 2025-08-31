package net.qiuyu.tinkersmastermind.datagen;


import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.qiuyu.tinkersmastermind.TinkersMastermind;
import net.qiuyu.tinkersmastermind.register.ModBlocks;
import net.qiuyu.tinkersmastermind.register.ModEffects;
import net.qiuyu.tinkersmastermind.register.ModItems;


public class ModLangGenCN extends LanguageProvider {
    public ModLangGenCN(PackOutput output, String locale) {
        super(output, TinkersMastermind.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModItems.ZOMBIE_IRON.get(), "僵尸铁");
        add(ModBlocks.ZOMBIE_IRON_BLOCK.get(), "僵尸铁块");
        add("material.tinkersmastermind.zombie_iron","僵尸铁");
        add(ModBlocks.JIMSONWEED_BLOCK.get(), "曼陀罗合金块");
        add(ModItems.JIMSONWEED.get(), "曼陀罗合金");
        add("material.tinkersmastermind.jimsonweed","曼陀罗");
        add(ModItems.VOMIT.get(), "呕吐物");
        add(ModItems.INSOLE.get(), "鞋垫");
        add(ModEffects.POISON_RESIST.get(),"猛毒耐性");
        add(ModEffects.FOOT_CLEAN.get(),"干净的脚");
        add(ModEffects.FOOT_ODOR.get(),"脚臭");
        add("modifier.tinkersmastermind.insole.description","记得洗脚.");
        add("itemGroup.tinkersmastermind.tab","匠魂长阶");
        add("item.tinkersmastermind.molten_zombie_iron_bucket", "熔融僵尸铁桶");
        add("fluid_type.tinkersmastermind.molten_zombie_iron", "熔融僵尸铁");
        add("fluid_type.tinkersmastermind.molten_jimsonweed", "熔融曼陀罗合金");
        add("item.tinkersmastermind.molten_jimsonweed_bucket", "熔融曼陀罗合金桶");
        add("modifier.tinkersmastermind.rotten","霉味");
        add("modifier.tinkersmastermind.hungry","我饿了");
        add("modifier.tinkersmastermind.bluster","爆裂");
        add("modifier.tinkersmastermind.feeding","投喂");
        add("modifier.tinkersmastermind.foot_odor","脚臭");
        add("modifier.tinkersmastermind.poison_sparkle","毒性荆棘");
        add("modifier.tinkersmastermind.poison_erosion","毒性腐蚀");
        add("modifier.tinkersmastermind.heavy_poison","猛毒");
        add("modifier.tinkersmastermind.double_shot","双头锥刺");
        add("modifier.tinkersmastermind.rotten.flavor","就像在炫腐肉.");
        add("modifier.tinkersmastermind.rotten.description","做出来的工具可以吃.");
        add("modifier.tinkersmastermind.double_shot.flavor","AN94,但是在我的世界");
<<<<<<< HEAD
        add("modifier.tinkersmastermind.double_shot.description","对目标在0.5s后再次施加一次相同的物理伤害.");
=======
        add("modifier.tinkersmastermind.double_shot.description","对目标施加一次相同的物理伤害.");
>>>>>>> baf182fceb5437ddc46569ad22ad3bb38208f9d5
        add("modifier.tinkersmastermind.hungry.flavor","我说饿了吗?如饿.");
        add("modifier.tinkersmastermind.hungry.description","穿上护甲会获得饥饿效果.被攻击有概率回复饱食度.");
        add("modifier.tinkersmastermind.bluster.flavor","苦力怕发射器.");
        add("modifier.tinkersmastermind.bluster.description","在箭矢落点产生一次爆炸.");
        add("modifier.tinkersmastermind.feeding.flavor","有一种饿是我觉得你饿.");
        add("modifier.tinkersmastermind.feeding.description","给命中的实体回复饱食度,当然会喂撑着.");
        add("modifier.tinkersmastermind.foot_odor.flavor","方块世界偶遇玉足,脱下鞋子强如怪物,拼劲全力无法战胜.");
        add("modifier.tinkersmastermind.foot_odor.description","并非饭盒.");
        add("modifier.tinkersmastermind.poison_sparkle.flavor","毒雷系.");
        add("modifier.tinkersmastermind.poison_sparkle.description","减免魔法伤害.被攻击时会给来源施加中毒效果.");
        add("modifier.tinkersmastermind.poison_erosion.flavor","毒蚀一体.");
        add("modifier.tinkersmastermind.poison_erosion.description","尽量不要碰这个东西,会中毒.");
        add("modifier.tinkersmastermind.heavy_poison.flavor","不能舔.");
<<<<<<< HEAD
        add("modifier.tinkersmastermind.heavy_poison.description","攻击会施加中毒效果.自身剩余中毒时间越长,伤害越高");
=======
        add("modifier.tinkersmastermind.heavy_poison.description","攻击会施加中毒效果.目标剩余中毒时间越长,伤害越高");
>>>>>>> baf182fceb5437ddc46569ad22ad3bb38208f9d5

    }
}
