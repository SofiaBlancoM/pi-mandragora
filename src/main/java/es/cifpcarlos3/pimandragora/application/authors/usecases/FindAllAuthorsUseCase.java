package es.cifpcarlos3.pimandragora.application.authors.usecases;

import es.cifpcarlos3.pimandragora.application.authors.repositories.AuthorQueryRepository;
import es.cifpcarlos3.pimandragora.application.authors.repositories.AuthorRepository;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.FindAllAuthorsResponse;
import es.cifpcarlos3.pimandragora.domain.entities.Author;
import es.cifpcarlos3.pimandragora.infrastructure.external.WikipediaClient;
import es.cifpcarlos3.pimandragora.shared.paging.Page;
import es.cifpcarlos3.pimandragora.shared.paging.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FindAllAuthorsUseCase {
    private static final Logger log = LoggerFactory.getLogger(FindAllAuthorsUseCase.class);

    private final AuthorQueryRepository queryRepository;
    private final AuthorRepository commandRepository;
    private final WikipediaClient wikipediaClient;

    public FindAllAuthorsUseCase(
            AuthorQueryRepository queryRepository,
            AuthorRepository commandRepository,
            WikipediaClient wikipediaClient
    ) {
        this.queryRepository = queryRepository;
        this.commandRepository = commandRepository;
        this.wikipediaClient = wikipediaClient;
    }


    public List<FindAllAuthorsResponse> execute() {
        log.debug("Fetching all authors for reference data (non-paginated)");
        return queryRepository.findAll();
    }


    public Page<FindAllAuthorsResponse> execute(PageRequest pageRequest) {

        Page<FindAllAuthorsResponse> page = queryRepository.findPage(pageRequest);


        List<FindAllAuthorsResponse> enrichedItems = page.items().stream().map(dto -> {

            if (dto.bio() == null || dto.bio().isBlank()) {
                log.info("Bio no encontrada en DB para: {}. Consultando Wikipedia...", dto.fullName());

                Author authorEntity = Author.builder()
                        .id(dto.id())
                        .fullName(dto.fullName())
                        .build();

                wikipediaClient.enrichAuthor(authorEntity);

                if (authorEntity.getBio() != null) {

                    commandRepository.update(
                            authorEntity.getId().toString(),
                            Map.of("bio", authorEntity.getBio())
                    );
                }
                return new FindAllAuthorsResponse(
                        dto.id(),
                        dto.fullName(),
                        authorEntity.getBio()
                );
            }

            return dto;
        }).collect(Collectors.toList());


        return new Page<>(
                enrichedItems,
                page.page(),
                page.size(),
                page.totalItems()
        );
    }
}