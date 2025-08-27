package com.valik.hogwarts_artifacts_online.artifact;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.valik.hogwarts_artifacts_online.artifact.utils.IdWorker;
import com.valik.hogwarts_artifacts_online.system.exception.ResourceNotFoundException;
import com.valik.hogwarts_artifacts_online.wizard.Wizard;

@ExtendWith(MockitoExtension.class)
public class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts;

    @BeforeEach
    void setUp() {
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

        this.artifacts = new ArrayList<>();
        this.artifacts.add(a1);
        this.artifacts.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        Artifact a = new Artifact();
        a.setId("1250808601744904191");
        a.setName("Deluminator");
        a.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a.setImageUrl("ImageUrl");

        Wizard w = new Wizard();
        w.setId(2);
        w.setName("Harry Potter");

        a.setOwner(w);

        given(this.artifactRepository.findById("1250808601744904191")).willReturn(Optional.of(a));

        Artifact returnedArtifact = this.artifactService.findById("1250808601744904191");

        assertThat(returnedArtifact.getId()).isEqualTo("1250808601744904191");
        assertThat(returnedArtifact.getName()).isEqualTo(a.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(a.getDescription());
        assertThat(returnedArtifact.getImageUrl()).isEqualTo(a.getImageUrl());

        verify(this.artifactRepository, times(1)).findById(a.getId());
    }

    @Test
    void testFindByIdNotFound() {
        BDDMockito.given(this.artifactRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            artifactService.findById("1250808601744904191");
        });

        assertThat(thrown)
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Could not find artifact with Id 1250808601744904191");
        verify(this.artifactRepository, times(1)).findById("1250808601744904191");
    }

    @Test
    void testFindAllSuccess() {
        given(this.artifactRepository.findAll()).willReturn(this.artifacts);

        List<Artifact> actualArtifacts = artifactService.findAll();

        assertThat(actualArtifacts.size()).isEqualTo(this.artifacts.size());
        verify(this.artifactRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Artifact 3");
        newArtifact.setDescription("Description");
        newArtifact.setImageUrl("ImageUrl");

        given(idWorker.nextId()).willReturn(123456L);
        given(this.artifactRepository.save(newArtifact)).willReturn(newArtifact);
    
        Artifact savedArtifact = artifactService.save(newArtifact);

        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());
        verify(this.artifactRepository, times(1)).save(newArtifact);
    }

    @Test
    void testUpdateSuccess() {
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("1250808601744904192");
        oldArtifact.setName("Invisibility Cloak");
        oldArtifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
        oldArtifact.setImageUrl("imageUrl");
 
        Artifact update = new Artifact();
        // update.setId("1250808601744904192");
        update.setName("Invisibility Cloak");
        update.setDescription("A new description.");
        update.setImageUrl("imageUrl");

        // 1. Find by id
        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(oldArtifact));
        // 2. Save the modified version
        given(this.artifactRepository.save(oldArtifact)).willReturn(oldArtifact);

        Artifact updatedAritfact = artifactService.update("1250808601744904192", update);

        assertThat(updatedAritfact.getId()).isEqualTo("1250808601744904192");
        assertThat(updatedAritfact.getDescription()).isEqualTo(update.getDescription());
        verify(this.artifactRepository, times(1)).findById("1250808601744904192");
        verify(this.artifactRepository, times(1)).save(oldArtifact);
    }

    @Test
    void testUpdateNotFound() {
        Artifact update = new Artifact();
        update.setName("Invisibility Cloak");
        update.setDescription("A new description.");
        update.setImageUrl("imageUrl");
        
        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            this.artifactService.update("1250808601744904192", update);
        });
        
        verify(this.artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testDeleteSuccess() {
        Artifact artifact = new Artifact();
        // artifact.setId("1250808601744904192");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
        artifact.setImageUrl("imageUrl");

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));
        doNothing().when(this.artifactRepository).deleteById("1250808601744904192");

        artifactService.delete("1250808601744904192");

        verify(this.artifactRepository, times(1)).deleteById("1250808601744904192");
    }

    @Test
    void testDeleteNotFound() {
        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            this.artifactService.delete("1250808601744904192");
        });

        verify(this.artifactRepository, times(1)).findById("1250808601744904192");
    }
}
