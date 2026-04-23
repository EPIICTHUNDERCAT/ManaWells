package com.epiicthundercat.manawell.datagen;

import com.epiicthundercat.manawell.Reference;
import com.epiicthundercat.manawell.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import static com.epiicthundercat.manawell.setup.ModSetup.TAB_NAME;

// Constructor takes PackOutput instead of DataGenerator in 1.20.1.
public class ManaWellsLanguageProvider extends LanguageProvider {

    private final String locale;

    public ManaWellsLanguageProvider(PackOutput output, String locale) {
        super(output, Reference.MODID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + TAB_NAME, "Mana Wells");
        if (locale.equals("pt_br")) {
            add(Registration.MANA_WELL_BEDROCK.get(), "Pedra de Mana");
            add("advancement.manawell.root.title", "Mana Wells");
            add("advancement.manawell.root.description", "The Mana Well Discovery.");
            add("advancement.manawell.first_mana_source.title", "My first source of Mana...");
            add("advancement.manawell.first_mana_source.description", "Você localizou os sons estranhos na caverna, talvez eu deva manter um registro de onde esses estão.");
            add("advancement.manawell.oom.title", "OOM!");
            add("advancement.manawell.oom.description", "A bruxa ficou sem mana! E agora você também.");
        } else {
            add(Registration.MANA_WELL_BEDROCK.get(), "Mana Well");
            add("advancement.manawell.root.title", "Mana Wells");
            add("advancement.manawell.root.description", "The Mana Well Discovery.");
            add("advancement.manawell.first_mana_source.title", "My first source of Mana...");
            add("advancement.manawell.first_mana_source.description", "You located the odd sounds in the cave, maybe I should keep a log of where these are.");
            add("advancement.manawell.oom.title", "OOM!");
            add("advancement.manawell.oom.description", "The witch was out of mana! and now so are you.");
        }
    }
}
