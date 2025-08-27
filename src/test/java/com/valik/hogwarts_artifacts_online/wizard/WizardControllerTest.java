package com.valik.hogwarts_artifacts_online.wizard;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valik.hogwarts_artifacts_online.system.StatusCode;
import com.valik.hogwarts_artifacts_online.system.exception.ResourceNotFoundException;
import com.valik.hogwarts_artifacts_online.wizard.dto.WizardDto;

@SpringBootTest
@AutoConfigureMockMvc
public class WizardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    WizardService wizardService;

    List<Wizard> wizards;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

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
    void testFindWizardByIdSuccess() throws Exception {
        given(this.wizardService.findById(1)).willReturn(this.wizards.get(0));

        this.mockMvc.perform(get(this.baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find One Success"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Albus Dumbledore"));
    }

    @Test
    void testFindWizardByIdNotFound() throws Exception {
        given(this.wizardService.findById(1)).willThrow(new ResourceNotFoundException("wizard", 1));

        this.mockMvc.perform(get(this.baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find wizard with Id 1"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllWizardsSuccess() throws Exception {
        given(this.wizardService.findAll()).willReturn(this.wizards);

        this.mockMvc.perform(get(this.baseUrl + "/wizards").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find All Success"))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].name").value("Albus Dumbledore"))
            .andExpect(jsonPath("$.data[1].id").value(2))
            .andExpect(jsonPath("$.data[1].name").value("Harry Potter"))
            .andExpect(jsonPath("$.data[2].id").value(3))
            .andExpect(jsonPath("$.data[2].name").value("Neville Longbottom"));
    }

    @Test
    void testAddWizardSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(
            null,
            "Hermione Granger",
            null
        );
        String json = this.objectMapper.writeValueAsString(wizardDto);

        Wizard savedWizard = new Wizard();
        savedWizard.setId(4);
        savedWizard.setName("Hermione Granger");

        given(this.wizardService.save(any(Wizard.class))).willReturn(savedWizard);

        this.mockMvc.perform(post(this.baseUrl + "/wizards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Add Success"))
            .andExpect(jsonPath("$.data.id").value(4))
            .andExpect(jsonPath("$.data.name").value("Hermione Granger"));
    }

    @Test
    void testUpdateWizardSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(
            2,
            "Harry Potter",
            null
        );
        String json = this.objectMapper.writeValueAsString(wizardDto);

        Wizard updatedWizard = new Wizard();
        updatedWizard.setId(2);
        updatedWizard.setName("Harry Potter-update");

        given(this.wizardService.update(eq(2), Mockito.any(Wizard.class))).willReturn(updatedWizard);

        this.mockMvc.perform(put(this.baseUrl + "/wizards/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Update Success"))
            .andExpect(jsonPath("$.data.id").value(2))
            .andExpect(jsonPath("$.data.name").value(updatedWizard.getName()));
    }

    @Test
    void testUpdateWizardErrorWithNonExistentId() throws Exception {
        WizardDto wizardDto = new WizardDto(
            2,
            "Harry Potter",
            null
        );
        String json = this.objectMapper.writeValueAsString(wizardDto);

        given(this.wizardService.update(eq(2), Mockito.any(Wizard.class))).willThrow(new ResourceNotFoundException("wizard", 2));

        this.mockMvc.perform(put(this.baseUrl + "/wizards/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find wizard with Id 2"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteWizardSuccess() throws Exception {
        doNothing().when(this.wizardService).delete(1);

        this.mockMvc.perform(delete(this.baseUrl + "/wizards/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Delete Success"))
            .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testDeleteWizardErrorWithNonExistentId() throws Exception {
        doThrow(new ResourceNotFoundException("wizard", 1))
            .when(this.wizardService).delete(1);

        this.mockMvc.perform(delete(this.baseUrl + "/wizards/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find wizard with Id 1"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssingArtifactSuccess() throws Exception {
        doNothing().when(this.wizardService).assingArtifact(2, "1250808601744904192");

        this.mockMvc.perform(put(this.baseUrl + "/wizards/2/artifacts/1250808601744904192")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Artifact Assignment Success"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssingArtifactErrorWithNonExistentWizardtId() throws Exception {
        doThrow(new ResourceNotFoundException("wizard", 2))
            .when(this.wizardService).assingArtifact(2, "1250808601744904192");

        this.mockMvc.perform(put(this.baseUrl + "/wizards/2/artifacts/1250808601744904192")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find wizard with Id 2"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssingArtifactErrorWithNonExistentArtifactId() throws Exception {
        doThrow(new ResourceNotFoundException("artifact", "1250808601744904192"))
            .when(this.wizardService).assingArtifact(2, "1250808601744904192");

        this.mockMvc.perform(put(this.baseUrl + "/wizards/2/artifacts/1250808601744904192")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904192"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

}
