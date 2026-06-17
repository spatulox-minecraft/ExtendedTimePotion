package com.spatulox;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedTimePotion implements ModInitializer {
    public static final String MOD_ID = "extended-time-potion";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final int ELEVEN_MINUTES = 13200;  // 11 minutes en ticks
    public static final int FIFTEEN_MINUTES = 18000; // 15 minutes en ticks

    // NIGHT_VISION
    public static Holder<Potion> LONG_LONG_NIGHT_VISION = registerPotion("long_long_night_vision",
            new Potion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_NIGHT_VISION = registerPotion("ultra_long_night_vision",
            new Potion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, FIFTEEN_MINUTES)));

    // INVISIBILITY
    public static Holder<Potion> LONG_LONG_INVISIBILITY = registerPotion("long_long_invisibility",
            new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_INVISIBILITY = registerPotion("ultra_long_invisibility",
            new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, FIFTEEN_MINUTES)));

    // LEAPING
    public static Holder<Potion> LONG_LONG_LEAPING = registerPotion("long_long_leaping",
            new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_LEAPING = registerPotion("ultra_long_leaping",
            new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, FIFTEEN_MINUTES)));

    // STRONG_LEAPING
    public static Holder<Potion> LONG_LONG_STRONG_LEAPING = registerPotion("long_long_strong_leaping",
            new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_STRONG_LEAPING = registerPotion("ultra_long_strong_leaping",
            new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, FIFTEEN_MINUTES)));

    // FIRE RESISTANCE
    public static Holder<Potion> LONG_LONG_FIRE_RESISTANCE = registerPotion("long_long_fire_resistance",
            new Potion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_FIRE_RESISTANCE = registerPotion("ultra_long_fire_resistance",
            new Potion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FIFTEEN_MINUTES)));

    // SWIFTNESS
    public static Holder<Potion> LONG_LONG_SWIFTNESS = registerPotion("long_long_swiftness",
            new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_SWIFTNESS = registerPotion("ultra_long_swiftness",
            new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, FIFTEEN_MINUTES)));

    // STRONG SWIFTNESS
    public static Holder<Potion> LONG_LONG_STRONG_SWIFTNESS = registerPotion("long_long_strong_swiftness",
            new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, ELEVEN_MINUTES, 1)));
    public static Holder<Potion> ULTRA_LONG_STRONG_SWIFTNESS = registerPotion("ultra_long_strong_swiftness",
            new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, FIFTEEN_MINUTES, 1)));

    // SLOWNESS
    public static Holder<Potion> LONG_LONG_SLOWNESS = registerPotion("long_long_slowness",
            new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_SLOWNESS = registerPotion("ultra_long_slowness",
            new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, FIFTEEN_MINUTES)));

    // STRONG SLOWNESS
    public static Holder<Potion> LONG_LONG_STRONG_SLOWNESS = registerPotion("long_long_strong_slowness",
            new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, ELEVEN_MINUTES, 3)));
    public static Holder<Potion> ULTRA_LONG_STRONG_SLOWNESS = registerPotion("ultra_long_strong_slowness",
            new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, FIFTEEN_MINUTES, 3)));

    // TURTLE MASTER
    public static Holder<Potion> LONG_LONG_TURTLE_MASTER = registerPotion("long_long_turtle_master",
            new Potion("turtle_master", new MobEffectInstance[]{
                    new MobEffectInstance(MobEffects.SLOWNESS, ELEVEN_MINUTES, 3),
                    new MobEffectInstance(MobEffects.RESISTANCE, ELEVEN_MINUTES, 2)
            }));
    public static Holder<Potion> ULTRA_LONG_TURTLE_MASTER = registerPotion("ultra_long_turtle_master",
            new Potion("turtle_master", new MobEffectInstance[]{
                    new MobEffectInstance(MobEffects.SLOWNESS, FIFTEEN_MINUTES, 3),
                    new MobEffectInstance(MobEffects.RESISTANCE, FIFTEEN_MINUTES, 2)
            }));

    // STRONG TURTLE MASTER
    public static Holder<Potion> LONG_LONG_STRONG_TURTLE_MASTER = registerPotion("long_long_strong_turtle_master",
            new Potion("turtle_master", new MobEffectInstance[]{
                    new MobEffectInstance(MobEffects.SLOWNESS, ELEVEN_MINUTES, 5),
                    new MobEffectInstance(MobEffects.RESISTANCE, ELEVEN_MINUTES, 3)
            }));
    public static Holder<Potion> ULTRA_LONG_STRONG_TURTLE_MASTER = registerPotion("ultra_long_strong_turtle_master",
            new Potion("turtle_master", new MobEffectInstance[]{
                    new MobEffectInstance(MobEffects.SLOWNESS, FIFTEEN_MINUTES, 5),
                    new MobEffectInstance(MobEffects.RESISTANCE, FIFTEEN_MINUTES, 3)
            }));

    // WATER BREATHING
    public static Holder<Potion> LONG_LONG_WATER_BREATHING = registerPotion("long_long_water_breathing",
            new Potion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_WATER_BREATHING = registerPotion("ultra_long_water_breathing",
            new Potion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, FIFTEEN_MINUTES)));

    // POISON
    public static Holder<Potion> LONG_LONG_POISON = registerPotion("long_long_poison",
            new Potion("poison", new MobEffectInstance(MobEffects.POISON, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_POISON = registerPotion("ultra_long_poison",
            new Potion("poison", new MobEffectInstance(MobEffects.POISON, FIFTEEN_MINUTES)));

    // STRONG POISON
    public static Holder<Potion> LONG_LONG_STRONG_POISON = registerPotion("long_long_strong_poison",
            new Potion("poison", new MobEffectInstance(MobEffects.POISON, ELEVEN_MINUTES, 1)));
    public static Holder<Potion> ULTRA_LONG_STRONG_POISON = registerPotion("ultra_long_strong_poison",
            new Potion("poison", new MobEffectInstance(MobEffects.POISON, FIFTEEN_MINUTES, 1)));

    // REGENERATION
    public static Holder<Potion> LONG_LONG_REGENERATION = registerPotion("long_long_regeneration",
            new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_REGENERATION = registerPotion("ultra_long_regeneration",
            new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, FIFTEEN_MINUTES)));

    // STRONG REGENERATION
    public static Holder<Potion> LONG_LONG_STRONG_REGENERATION = registerPotion("long_long_strong_regeneration",
            new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, ELEVEN_MINUTES, 1)));
    public static Holder<Potion> ULTRA_LONG_STRONG_REGENERATION = registerPotion("ultra_long_strong_regeneration",
            new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, FIFTEEN_MINUTES, 1)));

    // STRENGTH
    public static Holder<Potion> LONG_LONG_STRENGTH = registerPotion("long_long_strength",
            new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_STRENGTH = registerPotion("ultra_long_strength",
            new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, FIFTEEN_MINUTES)));

    // STRONG STRENGTH
    public static Holder<Potion> LONG_LONG_STRONG_STRENGTH = registerPotion("long_long_strong_strength",
            new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, ELEVEN_MINUTES, 1)));
    public static Holder<Potion> ULTRA_LONG_STRONG_STRENGTH = registerPotion("ultra_long_strong_strength",
            new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, FIFTEEN_MINUTES, 1)));

    // WEAKNESS
    public static Holder<Potion> LONG_LONG_WEAKNESS = registerPotion("long_long_weakness",
            new Potion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_WEAKNESS = registerPotion("ultra_long_weakness",
            new Potion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, FIFTEEN_MINUTES)));

    // LUCK
    public static Holder<Potion> LONG_LONG_LUCK = registerPotion("long_long_luck",
            new Potion("luck", new MobEffectInstance(MobEffects.LUCK, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_LUCK = registerPotion("ultra_long_luck",
            new Potion("luck", new MobEffectInstance(MobEffects.LUCK, FIFTEEN_MINUTES)));

    // SLOW FALLING
    public static Holder<Potion> LONG_LONG_SLOW_FALLING = registerPotion("long_long_slow_falling",
            new Potion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_SLOW_FALLING = registerPotion("ultra_long_slow_falling",
            new Potion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, FIFTEEN_MINUTES)));

    // WIND CHARGED
    public static Holder<Potion> LONG_LONG_WIND_CHARGED = registerPotion("long_long_wind_charged",
            new Potion("wind_charged", new MobEffectInstance(MobEffects.WIND_CHARGED, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_WIND_CHARGED = registerPotion("ultra_long_wind_charged",
            new Potion("wind_charged", new MobEffectInstance(MobEffects.WIND_CHARGED, FIFTEEN_MINUTES)));

    // WEAVING
    public static Holder<Potion> LONG_LONG_WEAVING = registerPotion("long_long_weaving",
            new Potion("weaving", new MobEffectInstance(MobEffects.WEAVING, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_WEAVING = registerPotion("ultra_long_weaving",
            new Potion("weaving", new MobEffectInstance(MobEffects.WEAVING, FIFTEEN_MINUTES)));

    // OOZING
    public static Holder<Potion> LONG_LONG_OOZING = registerPotion("long_long_oozing",
            new Potion("oozing", new MobEffectInstance(MobEffects.OOZING, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_OOZING = registerPotion("ultra_long_oozing",
            new Potion("oozing", new MobEffectInstance(MobEffects.OOZING, FIFTEEN_MINUTES)));

    // INFESTED
    public static Holder<Potion> LONG_LONG_INFESTED = registerPotion("long_long_infested",
            new Potion("infested", new MobEffectInstance(MobEffects.INFESTED, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_INFESTED = registerPotion("ultra_long_infested",
            new Potion("infested", new MobEffectInstance(MobEffects.INFESTED, FIFTEEN_MINUTES)));

    @Override
    public void onInitialize() {

        // HEALING and HARMING cannot be time extended
        FabricPotionBrewingBuilder.BUILD.register(builder -> {
            // NIGHT_VISION
            builder.addMix(Potions.LONG_NIGHT_VISION, Items.GOLD_NUGGET, LONG_LONG_NIGHT_VISION);
            builder.addMix(LONG_LONG_NIGHT_VISION, Items.GOLDEN_CARROT, ULTRA_LONG_NIGHT_VISION);

            // INVISIBILITY
            builder.addMix(Potions.LONG_INVISIBILITY, Items.GOLD_NUGGET, LONG_LONG_INVISIBILITY);
            builder.addMix(LONG_LONG_INVISIBILITY, Items.GOLDEN_CARROT, ULTRA_LONG_INVISIBILITY);

            // LEAPING
            builder.addMix(Potions.LONG_LEAPING, Items.GOLD_NUGGET, LONG_LONG_LEAPING);
            builder.addMix(LONG_LONG_LEAPING, Items.GOLDEN_CARROT, ULTRA_LONG_LEAPING);

            // STRONG_LEAPING
            builder.addMix(Potions.STRONG_LEAPING, Items.GOLD_NUGGET, LONG_LONG_STRONG_LEAPING);
            builder.addMix(LONG_LONG_STRONG_LEAPING, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_LEAPING);

            // FIRE_RESISTANCE
            builder.addMix(Potions.LONG_FIRE_RESISTANCE, Items.GOLD_NUGGET, LONG_LONG_FIRE_RESISTANCE);
            builder.addMix(LONG_LONG_FIRE_RESISTANCE, Items.GOLDEN_CARROT, ULTRA_LONG_FIRE_RESISTANCE);

            // SWIFTNESS
            builder.addMix(Potions.LONG_SWIFTNESS, Items.GOLD_NUGGET, LONG_LONG_SWIFTNESS);
            builder.addMix(LONG_LONG_SWIFTNESS, Items.GOLDEN_CARROT, ULTRA_LONG_SWIFTNESS);

            // STRONG_SWIFTNESS
            builder.addMix(Potions.STRONG_SWIFTNESS, Items.GOLD_NUGGET, LONG_LONG_STRONG_SWIFTNESS);
            builder.addMix(LONG_LONG_STRONG_SWIFTNESS, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_SWIFTNESS);

            // SLOWNESS
            builder.addMix(Potions.LONG_SLOWNESS, Items.GOLD_NUGGET, LONG_LONG_SLOWNESS);
            builder.addMix(LONG_LONG_SLOWNESS, Items.GOLDEN_CARROT, ULTRA_LONG_SLOWNESS);

            // STRONG_SLOWNESS
            builder.addMix(Potions.STRONG_SLOWNESS, Items.GOLD_NUGGET, LONG_LONG_STRONG_SLOWNESS);
            builder.addMix(LONG_LONG_STRONG_SLOWNESS, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_SLOWNESS);

            // TURTLE_MASTER
            builder.addMix(Potions.LONG_TURTLE_MASTER, Items.GOLD_NUGGET, LONG_LONG_TURTLE_MASTER);
            builder.addMix(LONG_LONG_TURTLE_MASTER, Items.GOLDEN_CARROT, ULTRA_LONG_TURTLE_MASTER);

            // STRONG_TURTLE_MASTER
            builder.addMix(Potions.STRONG_TURTLE_MASTER, Items.GOLD_NUGGET, LONG_LONG_STRONG_TURTLE_MASTER);
            builder.addMix(LONG_LONG_STRONG_TURTLE_MASTER, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_TURTLE_MASTER);

            // WATER_BREATHING
            builder.addMix(Potions.LONG_WATER_BREATHING, Items.GOLD_NUGGET, LONG_LONG_WATER_BREATHING);
            builder.addMix(LONG_LONG_WATER_BREATHING, Items.GOLDEN_CARROT, ULTRA_LONG_WATER_BREATHING);

            // POISON
            builder.addMix(Potions.LONG_POISON, Items.GOLD_NUGGET, LONG_LONG_POISON);
            builder.addMix(LONG_LONG_POISON, Items.GOLDEN_CARROT, ULTRA_LONG_POISON);

            // STRONG_POISON
            builder.addMix(Potions.STRONG_POISON, Items.GOLD_NUGGET, LONG_LONG_STRONG_POISON);
            builder.addMix(LONG_LONG_STRONG_POISON, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_POISON);

            // REGENERATION
            builder.addMix(Potions.LONG_REGENERATION, Items.GOLD_NUGGET, LONG_LONG_REGENERATION);
            builder.addMix(LONG_LONG_REGENERATION, Items.GOLDEN_CARROT, ULTRA_LONG_REGENERATION);

            // STRONG_REGENERATION
            builder.addMix(Potions.STRONG_REGENERATION, Items.GOLD_NUGGET, LONG_LONG_STRONG_REGENERATION);
            builder.addMix(LONG_LONG_STRONG_REGENERATION, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_REGENERATION);

            // STRENGTH
            builder.addMix(Potions.LONG_STRENGTH, Items.GOLD_NUGGET, LONG_LONG_STRENGTH);
            builder.addMix(LONG_LONG_STRENGTH, Items.GOLDEN_CARROT, ULTRA_LONG_STRENGTH);

            // STRONG_STRENGTH
            builder.addMix(Potions.STRONG_STRENGTH, Items.GOLD_NUGGET, LONG_LONG_STRONG_STRENGTH);
            builder.addMix(LONG_LONG_STRONG_STRENGTH, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_STRENGTH);

            // WEAKNESS
            builder.addMix(Potions.LONG_WEAKNESS, Items.GOLD_NUGGET, LONG_LONG_WEAKNESS);
            builder.addMix(LONG_LONG_WEAKNESS, Items.GOLDEN_CARROT, ULTRA_LONG_WEAKNESS);

            // LUCK
            builder.addMix(Potions.LUCK, Items.GOLD_NUGGET, LONG_LONG_LUCK);
            builder.addMix(LONG_LONG_LUCK, Items.GOLDEN_CARROT, ULTRA_LONG_LUCK);

            // SLOW_FALLING
            builder.addMix(Potions.LONG_SLOW_FALLING, Items.GOLD_NUGGET, LONG_LONG_SLOW_FALLING);
            builder.addMix(LONG_LONG_SLOW_FALLING, Items.GOLDEN_CARROT, ULTRA_LONG_SLOW_FALLING);

            // WIND_CHARGED
            builder.addMix(Potions.WIND_CHARGED, Items.GOLD_NUGGET, LONG_LONG_WIND_CHARGED);
            builder.addMix(LONG_LONG_WIND_CHARGED, Items.GOLDEN_CARROT, ULTRA_LONG_WIND_CHARGED);

            // WEAVING
            builder.addMix(Potions.WEAVING, Items.GOLD_NUGGET, LONG_LONG_WEAVING);
            builder.addMix(LONG_LONG_WEAVING, Items.GOLDEN_CARROT, ULTRA_LONG_WEAVING);

            // OOZING
            builder.addMix(Potions.OOZING, Items.GOLD_NUGGET, LONG_LONG_OOZING);
            builder.addMix(LONG_LONG_OOZING, Items.GOLDEN_CARROT, ULTRA_LONG_OOZING);

            // INFESTED
            builder.addMix(Potions.INFESTED, Items.GOLD_NUGGET, LONG_LONG_INFESTED);
            builder.addMix(LONG_LONG_INFESTED, Items.GOLDEN_CARROT, ULTRA_LONG_INFESTED);
        });
    }

    private static Holder<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, ExtendedTimePotion.id(name), potion);
    }

    public static Identifier id(String path){
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
