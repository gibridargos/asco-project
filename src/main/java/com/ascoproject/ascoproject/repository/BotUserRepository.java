package com.ascoproject.ascoproject.repository;

import com.ascoproject.ascoproject.entity.TaxInfoEntity;
import com.ascoproject.ascoproject.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BotUserRepository extends JpaRepository<UserEntity,Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_tax_info WHERE tax_info_id = :id", nativeQuery = true)
    void deleteTaxInfoLinks(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_tax_info", nativeQuery = true)
    void deleteAllUserTaxInfo();

    UserEntity findByChatId(Long chatId);

    UserEntity getUserEntityById(Long id);

    Page<UserEntity> findAllByChatIdStartingWith(String s, Pageable pageable);

    Page<UserEntity> findAllByChatIdLessThan(Long value, Pageable pageable);
}
