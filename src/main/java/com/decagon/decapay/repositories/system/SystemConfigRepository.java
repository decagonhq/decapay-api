package com.decagon.decapay.repositories.system;

import com.decagon.decapay.model.systemConfig.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    @Query("select s.value from SystemConfig s where s.configKey = ?1")
    Optional<String> findByConfigKey(String key);

    Collection<SystemConfig> findByConfigGroupOrderBySortOrderAsc(String group);
}
