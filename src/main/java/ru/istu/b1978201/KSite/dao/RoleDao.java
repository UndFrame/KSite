package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.istu.b1978201.KSite.mode.Role;
/**
 * Интерфейс обеспечивает основные операции по поиску, сохранения, удалению данных из таблицы roles.
 */
public interface RoleDao extends JpaRepository<Role, Long> {
}
