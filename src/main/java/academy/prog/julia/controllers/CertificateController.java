package academy.prog.julia.controllers;

import academy.prog.julia.json_responses.CertificateResponse;
import academy.prog.julia.services.CertificateService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;

/**
 * REST controller for handling certificate-related requests.
 * This controller provides endpoints to retrieve certificate files by their ID.
 */
@RestController
@RequestMapping("/api/certificate")
public class CertificateController {
    private final CertificateService certificateService;

    /**
     * Constructor that injects the CertificateService dependency.
     *
     * @param certificateService the service to handle certificate-related operations
     */
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    /**
     * Endpoint to retrieve a certificate by its ID.
     * This method fetches the certificate file data as a byte array using the provided certificate ID.
     *
     * @param certificateId the ID of the certificate to be retrieved
     * @return ResponseEntity containing the certificate file data as a IMAGE_PNG file
     */
    @GetMapping("/{id}")
    public ResponseEntity<String> getCertificate(@PathVariable("id") String certificateId) {
        byte[] image = certificateService.getCertificateById(certificateId);

        String base64Image = Base64.getEncoder().encodeToString(image);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(base64Image);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CertificateResponse>> getAllUserCertificates(@PathVariable("userId") String userId) {
        List<CertificateResponse> certificates = certificateService.getAllCertificatesByUserId(userId);

        return ResponseEntity.ok().body(certificates);
    }

}