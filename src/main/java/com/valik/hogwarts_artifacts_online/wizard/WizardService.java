package com.valik.hogwarts_artifacts_online.wizard;

import java.util.List;

import org.springframework.stereotype.Service;

import com.valik.hogwarts_artifacts_online.artifact.Artifact;
import com.valik.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.valik.hogwarts_artifacts_online.system.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;

    private final ArtifactRepository artifactRepository;

        public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public Wizard findById(Integer wizardId) {
        return this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ResourceNotFoundException("wizard", wizardId));
    }

    public List<Wizard> findAll() {
        return this.wizardRepository.findAll();
    }

    public Wizard save(Wizard newWizard) {
        return this.wizardRepository.save(newWizard);
    }

    public Wizard update(Integer wizardId, Wizard update) {
        return this.wizardRepository.findById(wizardId)
                .map(oldWizard -> {
                    oldWizard.setName(update.getName());
                    return this.wizardRepository.save(oldWizard);
                })
                .orElseThrow(() -> new ResourceNotFoundException("wizard", wizardId));
    }

    public void delete(Integer wizardId) {
        Wizard wizardToBeDeleted = this.wizardRepository.findById(wizardId)
            .orElseThrow(() -> new ResourceNotFoundException("wizard", wizardId));
        wizardToBeDeleted.removeAllArtifact();
        this.wizardRepository.deleteById(wizardId);
    }

    public void assingArtifact(Integer wizardId, String artifactId) {
        // Busco el Artifact en la DB por Id.
        Artifact artifactToBeAssigned = this.artifactRepository.findById(artifactId)
        .orElseThrow(() -> new ResourceNotFoundException("artifact", artifactId));

        // Busco el Wizard en la DB por Id.
        Wizard wizard = this.wizardRepository.findById(wizardId)
            .orElseThrow(() -> new ResourceNotFoundException("wizard", wizardId));

        // Asignación de Artifact
        // 1. Me fijo si algún Wizard ya tiene el Artifact
        if (artifactToBeAssigned.getOwner() != null) {
            artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
        }

        // 2. Asigno el Artifact al Wizard deseado.
        wizard.addArtifact(artifactToBeAssigned);
    }

}
