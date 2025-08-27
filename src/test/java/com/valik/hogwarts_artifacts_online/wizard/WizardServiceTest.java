package com.valik.hogwarts_artifacts_online.wizard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.valik.hogwarts_artifacts_online.artifact.Artifact;
import com.valik.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.valik.hogwarts_artifacts_online.system.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;

    @Mock
    ArtifactRepository artifactRepository;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards;

    @BeforeEach
    void setUp() {
        this.wizards = new ArrayList<>();

        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus Dumbledore");
        this.wizards.add(w1);

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        this.wizards.add(w2);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");
        this.wizards.add(w3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        Wizard wizard = new Wizard();
        wizard.setId(2);
        wizard.setName("Harry Potter");

        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImageUrl("imageUrl");

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a2.setImageUrl("imageUrl");

        List<Artifact> ownedArtifacts = new ArrayList<>();
        ownedArtifacts.add(a1);
        ownedArtifacts.add(a2);

        wizard.setArtifacts(ownedArtifacts);

        given(wizardRepository.findById(2)).willReturn(Optional.of(wizard));

        Wizard returnedWizard = wizardService.findById(2);
        
        assertThat(returnedWizard.getId()).isEqualTo(wizard.getId());
        assertThat(returnedWizard.getName()).isEqualTo(wizard.getName());
        assertThat(returnedWizard.getArtifacts().size()).isEqualTo(wizard.getArtifacts().size());

        verify(wizardRepository, times(1)).findById(2);
    }

    @Test
    void testFindByIdNotFound() {
        given(wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            wizardService.findById(2);
        });

        assertThat(thrown)
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Could not find wizard with Id 2");
        verify(wizardRepository, times(1)).findById(2);

    }

    @Test
    void testFindAllSuccess() {
        given(this.wizardRepository.findAll()).willReturn(this.wizards);

        List<Wizard> actualWizards = this.wizardService.findAll();

        assertThat(actualWizards.size()).isEqualTo(this.wizards.size());
        verify(this.wizardRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        Wizard newWizard = new Wizard();
        newWizard.setId(4);
        newWizard.setName("Hermione Granger");

        given(this.wizardRepository.save(newWizard)).willReturn(newWizard);

        Wizard savedWizard = this.wizardRepository.save(newWizard);

        assertThat(savedWizard.getId()).isEqualTo(4);
        assertThat(savedWizard.getName()).isEqualTo("Hermione Granger");
        verify(this.wizardRepository, times(1)).save(newWizard);
    }

    @Test
    void testUpdateSuccess() {
        Wizard oldWizard = new Wizard();
        oldWizard.setId(2);
        oldWizard.setName("Harry Potter");

        Wizard update = new Wizard();
        update.setId(2);
        update.setName("Harry Potter-update");

        given(this.wizardRepository.findById(2)).willReturn(Optional.of(oldWizard));
        given(this.wizardRepository.save(oldWizard)).willReturn(oldWizard);

        Wizard updatedWizard = this.wizardService.update(2, update);

        assertThat(updatedWizard.getId()).isEqualTo(2);
        assertThat(updatedWizard.getName()).isEqualTo("Harry Potter-update");
        verify(this.wizardRepository, times(1)).findById(2);
        verify(this.wizardRepository, times(1)).save(oldWizard);
    }

    @Test
    void testUpdateNotFound() {
        Wizard update = new Wizard();
        update.setId(2);
        update.setName("Harry Potter-update");
        
        given(this.wizardRepository.findById(2)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            this.wizardService.update(2, update);
        });

        verify(this.wizardRepository, times(1)).findById(2);
    }

    @Test
    void testDeleteSuccess() {
        Wizard wizard = new Wizard();
        wizard.setId(1);
        wizard.setName("Albus Dumbledore");

        given(this.wizardRepository.findById(1)).willReturn(Optional.of(wizard));
        doNothing().when(this.wizardRepository).deleteById(1);

        this.wizardService.delete(1);

        verify(this.wizardRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteNotFoud() {
        given(this.wizardRepository.findById(1)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            this.wizardService.delete(1);
        });

        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testAssingArtifactSuccess() {
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("ImageUrl");

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        w2.addArtifact(a);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
        given(this.wizardRepository.findById(3)).willReturn(Optional.of(w3));

        this.wizardService.assingArtifact(3, "1250808601744904192");

        assertThat(a.getOwner().getId()).isEqualTo(3);
        assertThat(w3.getArtifacts()).contains(a);
    }

    @Test
    void testAssingArtifactErrorWithNonExistentWizardId() {
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("ImageUrl");

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        w2.addArtifact(a);

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
        given(this.wizardRepository.findById(3)).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ResourceNotFoundException.class, () -> {
            this.wizardService.assingArtifact(3, "1250808601744904192");
        });

        assertThat(thrown)
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Could not find wizard with Id 3");
        assertThat(a.getOwner().getId()).isEqualTo(2);
    }

    @Test
    void testAssingArtifactErrorWithNonExistentArtifactId() {
        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ResourceNotFoundException.class, () -> {
            this.wizardService.assingArtifact(3, "1250808601744904192");
        });

        assertThat(thrown)
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Could not find artifact with Id 1250808601744904192");
    }
}