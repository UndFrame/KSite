package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.istu.b1978201.KSite.mode.Role;

public interface RoleDao extends JpaRepository<Role, Long> {
}
