package io.github.ajayanuragi.job_tracker.validation;

import io.github.ajayanuragi.job_tracker.exception.InvalidSortFieldException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class SortFieldValidator {
    public void validate(Pageable pageable, Set<String> allowedFields) {
        for (Sort.Order order : pageable.getSort()) {
            String field = order.getProperty();

            if (!allowedFields.contains(field)) {
                throw new InvalidSortFieldException("Sorting by " + field + " is not supported");
            }
        }
    }

}
