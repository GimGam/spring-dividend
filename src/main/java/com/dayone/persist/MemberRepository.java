package com.dayone.persist;

import com.dayone.persist.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository
    extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByUserName(String userName);
    boolean existsByUserName(String userName);
}
