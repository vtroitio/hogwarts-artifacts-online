package com.valik.hogwarts_artifacts_online.wizard.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.valik.hogwarts_artifacts_online.wizard.Wizard;
import com.valik.hogwarts_artifacts_online.wizard.dto.WizardDto;

@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDto> {

    @Override
    public WizardDto convert(@NonNull Wizard source) {
        WizardDto wizardDto = new WizardDto(source.getId(),
                                            source.getName(),
                                            source.getNumberOfArtifacts());
        return wizardDto;
    }

}
