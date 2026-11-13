package io.github.ajayanuragi.job_tracker.validation;

import io.github.ajayanuragi.job_tracker.exception.InvalidSortFieldException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SortFieldValidatorTest {
    private final SortFieldValidator validator = new SortFieldValidator();

    @Test
    void validate_acceptMultipleSupportedFields(){
        var pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending().and(Sort.by("title").ascending()));
        assertDoesNotThrow(() -> validator.validate(pageable, Set.of("createdAt", "title", "companyName")));
    }

    @Test
    void validate_rejectsUnsupportedField() {
        var pageable = PageRequest.of(
                0, 10,
                Sort.by("password").descending()
        );

        assertThrows(
                InvalidSortFieldException.class,
                () -> validator.validate(
                        pageable,
                        Set.of("createdAt", "title", "companyName")
                )
        );
    }

    @Test
    void validate_rejectsWhenOneOfMultipleFieldsIsUnsupported() {
        var pageable = PageRequest.of(
                0, 10,
                Sort.by("createdAt").descending()
                        .and(Sort.by("password").ascending())
        );

        assertThrows(
                InvalidSortFieldException.class,
                () -> validator.validate(
                        pageable,
                        Set.of("createdAt", "title", "companyName")
                )
        );
    }

}