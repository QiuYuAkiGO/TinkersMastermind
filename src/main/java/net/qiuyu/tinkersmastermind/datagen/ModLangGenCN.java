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

        add(ModItems.CORAL.get(), "珊瑚");
        add("material.tinkersmastermind.coral","珊瑚");

        add(ModItems.VOMIT.get(), "呕吐物");
        add(ModItems.INSOLE.get(), "鞋垫");
        add(ModEffects.POISON_RESIST.get(),"猛毒耐性");
        add(ModEffects.FOOT_CLEAN.get(),"干净的脚");
        add(ModEffects.FOOT_ODOR.get(),"脚臭");

        add("modifier.tinkersmastermind.insole.description","记得洗脚.");
        add("itemGroup.tinkersmastermind.tab","匠魂长阶");

        add("item.tinkersmastermind.molten_zombie_iron_bucket", "熔融僵尸铁桶");
        add("fluid_type.tinkersmastermind.molten_zombie_iron", "熔融僵尸铁");
        add("block.tinkersmastermind.molten_zombie_iron_fluid", "熔融僵尸铁");

        add("fluid_type.tinkersmastermind.molten_jimsonweed", "熔融曼陀罗合金");
        add("block.tinkersmastermind.molten_jimsonweed_fluid", "熔融曼陀罗合金");
        add("item.tinkersmastermind.molten_jimsonweed_bucket", "熔融曼陀罗合金桶");

        add("fluid_type.tinkersmastermind.molten_coral", "熔融珊瑚");
        add("item.tinkersmastermind.molten_coral_bucket", "熔融珊瑚桶");
        add("block.tinkersmastermind.molten_coral_fluid", "熔融珊瑚");

        add("modifier.tinkersmastermind.rotten","霉味");
        add("modifier.tinkersmastermind.hungry","我饿了");
        add("modifier.tinkersmastermind.bluster","爆裂");
        add("modifier.tinkersmastermind.feeding","投喂");
        add("modifier.tinkersmastermind.foot_odor","脚臭");
        add("modifier.tinkersmastermind.shield_breaking","碎盾者");
        add("modifier.tinkersmastermind.smash","锤击");
        add("modifier.tinkersmastermind.stickin","见缝插针");
        add("modifier.tinkersmastermind.charge","储能");
        add("modifier.tinkersmastermind.poison_sparkle","毒性荆棘");
        add("modifier.tinkersmastermind.poison_erosion","毒性腐蚀");
        add("modifier.tinkersmastermind.heavy_poison","猛毒");
        add("modifier.tinkersmastermind.double_shot","双头锥刺");
        add("modifier.tinkersmastermind.leap_slash","跳斩");
        add("modifier.tinkersmastermind.rotten.flavor","就像在炫腐肉.");
        add("modifier.tinkersmastermind.rotten.description","做出来的工具可以吃.");
        add("modifier.tinkersmastermind.double_shot.flavor","AN94,但是在我的世界");
        add("modifier.tinkersmastermind.double_shot.description","对目标施加一次相同的物理伤害.");
        add("modifier.tinkersmastermind.hungry.flavor","我说饿了吗?如饿.");
        add("modifier.tinkersmastermind.hungry.description","穿上护甲会获得饥饿效果.被攻击有概率回复饱食度.");
        add("modifier.tinkersmastermind.bluster.flavor","苦力怕发射器.");
        add("modifier.tinkersmastermind.bluster.description","在箭矢落点产生一次爆炸.");
        add("modifier.tinkersmastermind.feeding.flavor","有一种饿是我觉得你饿.");
        add("modifier.tinkersmastermind.feeding.description","给命中的实体回复饱食度,当然会喂撑着.");
        add("modifier.tinkersmastermind.foot_odor.flavor","方块世界偶遇玉足,脱下鞋子强如怪物,拼劲全力无法战胜.");
        add("modifier.tinkersmastermind.foot_odor.description","并非饭盒.");
        add("modifier.tinkersmastermind.shield_breaking.flavor","把你的脸从那个破木板后伸出来!");
        add("modifier.tinkersmastermind.shield_breaking.description","你的劈刀可以破盾，根据伤害额外损耗目标盾牌耐久并增加破盾时间");
        add("modifier.tinkersmastermind.smash.flavor","送你的敌人去见解压软件去吧");
        add("modifier.tinkersmastermind.smash.description","右键可以蓄力攻击，最高造成2倍的伤害。");
        add("modifier.tinkersmastermind.leap_slash.flavor","我在等CD，你在等什么？");
        add("modifier.tinkersmastermind.leap_slash.description","位于空中时额外造成1.5点伤害，右键使使用者腾空。");
        add("modifier.tinkersmastermind.charge.flavor","刑啊，很刑啊");
        add("modifier.tinkersmastermind.charge.description","使你的弓可以蓄力更久，发射的初速度更高");
        add("modifier.tinkersmastermind.stickin.flavor","那可要对准了");
        add("modifier.tinkersmastermind.stickin.description","基于基础伤害额外造成一次穿甲伤害，等级越高比例越高");
        add("modifier.tinkersmastermind.poison_sparkle.flavor","毒雷系.");
        add("modifier.tinkersmastermind.poison_sparkle.description","减免魔法伤害.被攻击时会给来源施加中毒效果.");
        add("modifier.tinkersmastermind.poison_erosion.flavor","毒蚀一体.");
        add("modifier.tinkersmastermind.poison_erosion.description","尽量不要碰这个东西,会中毒.");
        add("modifier.tinkersmastermind.heavy_poison.flavor","不能舔.");
        add("modifier.tinkersmastermind.heavy_poison.description","攻击会施加中毒效果.目标剩余中毒时间越长,伤害越高");
        add("modifier.tinkersmastermind.coral_healing","珊瑚愈合");
        add("modifier.tinkersmastermind.coral_healing.flavor","海洋的祝福.");
        add("modifier.tinkersmastermind.coral_healing.description","攻击或挖掘后有概率产生生命恢复效果.");
        add("modifier.tinkersmastermind.coral_hardening","珊瑚硬化");
        add("modifier.tinkersmastermind.coral_hardening.flavor","海洋的守护.");
        add("modifier.tinkersmastermind.coral_hardening.description","溺水时,减少受到的窒息伤害,回复氧气值与速度提升效果.");

    }
}
