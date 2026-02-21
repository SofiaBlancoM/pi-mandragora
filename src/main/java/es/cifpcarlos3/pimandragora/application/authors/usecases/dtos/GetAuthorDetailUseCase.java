package es.cifpcarlos3.pimandragora.application.authors.usecases.dtos;

import es.cifpcarlos3.pimandragora.application.authors.repositories.AuthorQueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAuthorDetailUseCase {
    private static final Logger log = LoggerFactory.getLogger(GetAuthorDetailUseCase.class);
    private final AuthorQueryRepository queryRepository;

    public GetAuthorDetailUseCase(AuthorQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }


    public AuthorDetailResponse execute(String id) {
        log.debug("Executing GetAuthorDetailUseCase for id: {}", id);

        AuthorDetailResponse response = queryRepository.findById(id);

        if (response == null) {
            log.warn("Author not found with id: {}", id);
        }

        return response;
    }
}