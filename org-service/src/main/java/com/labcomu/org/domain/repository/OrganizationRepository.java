package com.labcomu.org.domain.repository;

import com.labcomu.org.domain.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends CrudRepository<Organization, Long> {
    Optional<Organization> findByUrl(@NotNull final String url);
}
