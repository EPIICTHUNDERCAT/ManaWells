package com.epiicthundercat.manawell.datagen;

import com.epiicthundercat.manawell.Reference;
import com.epiicthundercat.manawell.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import static com.epiicthundercat.manawell.setup.ModSetup.TAB_NAME;
public class ManaWellsLanguageProvider extends LanguageProvider {

    public ManaWellsLanguageProvider(DataGenerator gen, String locale) {
        super(gen, Reference.MODID, locale);
    }

    @Override
    protected void addTranslations() {

        add("itemGroup." + TAB_NAME, "Mana Wells");
        add(Registration.MANA_WELL_BEDROCK.get(), "Bedrock ManaWell");


    }
}
