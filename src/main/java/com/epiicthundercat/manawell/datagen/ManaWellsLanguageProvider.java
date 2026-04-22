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
        } else {
            add(Registration.MANA_WELL_BEDROCK.get(), "Mana Well");
        }
    }
}
