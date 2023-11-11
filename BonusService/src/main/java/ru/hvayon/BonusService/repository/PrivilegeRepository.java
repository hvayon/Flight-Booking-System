package ru.hvayon.BonusService.repository;

import ru.hvayon.BonusService.domain.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {
    Optional<Privilege> findByUsername(String username);
}
