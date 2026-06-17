package com.shashikant.bankingverification.infrastructure.persistence.repositories.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shashikant.bankingverification.infrastructure.persistence.entities.document.DocumentEntity;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
}
