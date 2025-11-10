package edu.itba.cryptotracker.adapter.input.rest.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.itba.cryptotracker.adapter.input.rest.platform.dto.PlatformRequestDTO;
import edu.itba.cryptotracker.adapter.input.rest.platform.mapper.PlatformRestMapper;
import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.exception.DuplicatedPlatformException;
import edu.itba.cryptotracker.domain.exception.PlatformNotFoundException;
import edu.itba.cryptotracker.domain.usecases.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlatformController.class)
class PlatformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetAllPlatformsUseCasePort getAllPlatformsUseCase;

    @MockBean
    private FindPlatformByIdUseCasePort findPlatformByIdUseCase;

    @MockBean
    private SavePlatformUseCasePort savePlatformUseCase;

    @MockBean
    private UpdatePlatformUseCasePort updatePlatformUseCase;

    @MockBean
    private DeletePlatformUseCasePort deletePlatformUseCase;

    @MockBean
    private PlatformRestMapper mapper;

    @Test
    void shouldReturnAllPlatforms() throws Exception {
        // Given
        List<Platform> platforms = List.of(
            Platform.reconstitute("1", "BINANCE"),
            Platform.reconstitute("2", "COINBASE")
        );
        
        when(getAllPlatformsUseCase.getAllPlatforms()).thenReturn(platforms);
        when(mapper.toResponseDTOs(platforms)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/platforms"))
                .andExpect(status().isOk());

        verify(getAllPlatformsUseCase).getAllPlatforms();
        verify(mapper).toResponseDTOs(platforms);
    }

    @Test
    void shouldReturnNoContentWhenNoPlatforms() throws Exception {
        // Given
        when(getAllPlatformsUseCase.getAllPlatforms()).thenReturn(List.of());
        when(mapper.toResponseDTOs(List.of())).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/platforms"))
                .andExpect(status().isNoContent());

        verify(getAllPlatformsUseCase).getAllPlatforms();
    }

    @Test
    void shouldReturnPlatformById() throws Exception {
        // Given
        String platformId = "123e4567-e89b-12d3-a456-426614174000";
        Platform platform = Platform.reconstitute(platformId, "BINANCE");
        
        when(findPlatformByIdUseCase.findById(platformId)).thenReturn(platform);

        // When & Then
        mockMvc.perform(get("/api/v1/platforms/{platformId}", platformId))
                .andExpect(status().isOk());

        verify(findPlatformByIdUseCase).findById(platformId);
        verify(mapper).toResponseDTO(platform);
    }

    @Test
    void shouldReturnNotFoundWhenPlatformDoesNotExist() throws Exception {
        // Given
        String platformId = "123e4567-e89b-12d3-a456-426614174000";
        
        when(findPlatformByIdUseCase.findById(platformId))
                .thenThrow(new PlatformNotFoundException("Platform not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/platforms/{platformId}", platformId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Platform Not Found"));

        verify(findPlatformByIdUseCase).findById(platformId);
    }

    @Test
    void shouldCreatePlatformSuccessfully() throws Exception {
        // Given
        PlatformRequestDTO request = new PlatformRequestDTO("BINANCE");
        Platform platform = Platform.create("BINANCE");
        
        when(savePlatformUseCase.savePlatform("BINANCE")).thenReturn(platform);

        // When & Then
        mockMvc.perform(post("/api/v1/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(savePlatformUseCase).savePlatform("BINANCE");
        verify(mapper).toResponseDTO(platform);
    }

    @Test
    void shouldReturnBadRequestForInvalidPlatformName() throws Exception {
        // Given
        PlatformRequestDTO request = new PlatformRequestDTO("");

        // When & Then
        mockMvc.perform(post("/api/v1/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verify(savePlatformUseCase, never()).savePlatform(anyString());
    }

    @Test
    void shouldReturnConflictWhenPlatformAlreadyExists() throws Exception {
        // Given
        PlatformRequestDTO request = new PlatformRequestDTO("BINANCE");
        
        when(savePlatformUseCase.savePlatform("BINANCE"))
                .thenThrow(new DuplicatedPlatformException("Platform already exists"));

        // When & Then
        mockMvc.perform(post("/api/v1/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Platform Already Exists"));

        verify(savePlatformUseCase).savePlatform("BINANCE");
    }

    @Test
    void shouldUpdatePlatformSuccessfully() throws Exception {
        // Given
        String platformId = "123e4567-e89b-12d3-a456-426614174000";
        PlatformRequestDTO request = new PlatformRequestDTO("UPDATED_BINANCE");
        Platform updatedPlatform = Platform.reconstitute(platformId, "UPDATED_BINANCE");
        
        when(updatePlatformUseCase.updatePlatform(platformId, "UPDATED_BINANCE"))
                .thenReturn(updatedPlatform);

        // When & Then
        mockMvc.perform(put("/api/v1/platforms/{platformId}", platformId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(updatePlatformUseCase).updatePlatform(platformId, "UPDATED_BINANCE");
        verify(mapper).toResponseDTO(updatedPlatform);
    }

    @Test
    void shouldDeletePlatformSuccessfully() throws Exception {
        // Given
        String platformId = "123e4567-e89b-12d3-a456-426614174000";

        // When & Then
        mockMvc.perform(delete("/api/v1/platforms/{platformId}", platformId))
                .andExpect(status().isNoContent());

        verify(deletePlatformUseCase).deletePlatform(platformId);
    }

    @Test
    void shouldReturnBadRequestForInvalidUUID() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/platforms/invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(findPlatformByIdUseCase, never()).findById(anyString());
    }
}