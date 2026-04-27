package com.epiicthundercat.manawell.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

// Trigger for "My first source of Mana..." — fires on first player contact with a Mana Well.
// Registered as "manawell:player_stepped_on_well". See ManaWells_1.20.1_to_1.21.1_Changes.txt for full system explanation.
public class PlayerSteppedOnManaWellTrigger extends SimpleCriterionTrigger<PlayerSteppedOnManaWellTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        super.trigger(player, instance -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
            ).apply(inst, TriggerInstance::new)
        );
    }
}
