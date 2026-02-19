package com.example.LegiTrack.repository;

import com.example.LegiTrack.model.BillSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillSummaryRepository extends JpaRepository<BillSummaryEntity, Long> {
    Optional<BillSummaryEntity> findByBillId(Long billId);

    void deleteByBillId(Long billId);

    boolean existsByBillId(Long billId);
}