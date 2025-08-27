package com.valik.hogwarts_artifacts_online.wizard;

import java.util.List;

import org.springframework.stereotype.Service;

import com.valik.hogwarts_artifacts_online.system.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;

    public WizardService(WizardRepository wizardRepository) {
        this.wizardRepository = wizardRepository;
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

}
