package academy.prog.julia.integration.controllers;

import academy.prog.julia.json_responses.CertificateResponse;
import academy.prog.julia.model.Certificate;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.CertificateRepository;
import academy.prog.julia.services.CertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificateRepository certificateRepository;

    @MockBean
    private CertificateService certificateService;

    @BeforeEach
    void setUp() {

        Certificate certificate = new Certificate();
        certificate.setId(1L);
        certificate.setUniqueId("cert123");
        certificate.setFile(new byte[]{1, 2, 3});
        certificate.setGroupName("Group A");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        certificate.setUser(user);


        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        when(certificateRepository.findById(999L)).thenReturn(Optional.empty());
        when(certificateRepository.findByUserId(1L)).thenReturn(List.of(certificate));
        when(certificateRepository.findByUserId(999L)).thenReturn(List.of());
        when(certificateService.getCertificateById("1")).thenReturn(new byte[]{1, 2, 3});
        when(certificateService.getCertificateById("5")).thenThrow(new AccessDeniedException("Forbidden"));
    }


    @Test
    @WithMockUser(username = "Test", roles = "ADMIN_ROLE", password = "111111")
    void testGetCertificateById_Success() throws Exception {
        mockMvc.perform(get("/api/certificate/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string(notNullValue()))
        ;
    }


    @Test
    void testGetCertificateById_ThrowsNullPointerException() throws Exception {
        mockMvc.perform(get("/api/certificate/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NullPointerException))
        ;
    }


    @Test
    void testGetCertificateById_Forbidden() throws Exception {
        mockMvc.perform(get("/api/certificate/5"))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
        ;
    }


    @Test
    void testGetAllUserCertificates_Success() throws Exception {
        CertificateResponse certificateResponse = new CertificateResponse(1L, "cert123", "Group A");
        when(certificateService.getAllCertificatesByUserId("1")).thenReturn(List.of(certificateResponse));

        mockMvc.perform(get("/api/certificate/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1))) // Очікуємо 1 сертифікат
                .andExpect(jsonPath("$[0].uniqueId", is("cert123")))
                .andExpect(jsonPath("$[0].groupName", is("Group A")))
        ;
    }


    @Test
    @WithMockUser(username = "Test", roles = "ADMIN_ROLE", password = "111111")
    void testGetAllUserCertificates_NoCertificates() throws Exception {
        mockMvc.perform(get("/api/certificate/user/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    void testGetCertificateById_EmptyFile() throws Exception {
        when(certificateService.getCertificateById("1")).thenReturn(new byte[0]);

        mockMvc.perform(get("/api/certificate/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string(""))
        ;
    }

}

