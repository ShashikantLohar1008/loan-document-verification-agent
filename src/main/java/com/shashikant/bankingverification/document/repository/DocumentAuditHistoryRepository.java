package com.shashikant.bankingverification.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shashikant.bankingverification.document.entity.DocumentAuditHistoryEntity;

@Repository
public interface DocumentAuditHistoryRepository extends JpaRepository<DocumentAuditHistoryEntity, Long> {

    List<DocumentAuditHistoryEntity> findByDocumentKeyOrderByCreatedAtDesc(Long documentKey);
}
