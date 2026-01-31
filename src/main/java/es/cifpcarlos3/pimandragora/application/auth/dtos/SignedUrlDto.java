package es.cifpcarlos3.pimandragora.application.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignedUrlDto(@JsonProperty("signedURL") String signedURL) {
}
