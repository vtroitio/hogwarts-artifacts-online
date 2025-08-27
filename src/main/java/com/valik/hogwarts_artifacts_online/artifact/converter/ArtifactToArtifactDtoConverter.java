package com.valik.hogwarts_artifacts_online.artifact.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.valik.hogwarts_artifacts_online.artifact.Artifact;
import com.valik.hogwarts_artifacts_online.artifact.dto.ArtifactDto;
import com.valik.hogwarts_artifacts_online.wizard.converter.WizardToWizardDtoConverter;

@Component
public class ArtifactToArtifactDtoConverter implements Converter<Artifact, ArtifactDto> {

    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

    public ArtifactToArtifactDtoConverter(WizardToWizardDtoConverter wizardToWizardDtoConverter) {
        this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
    }

    @Override
    public ArtifactDto convert(@NonNull Artifact source) {
        ArtifactDto artifactDto = new ArtifactDto(source.getId(),
                                                  source.getName(),
                                                  source.getDescription(),
                                                  source.getImageUrl(),
                                                  source.getOwner() != null 
                                                    ? this.wizardToWizardDtoConverter.convert(source.getOwner())
                                                    : null);
        return artifactDto;
    }

}
