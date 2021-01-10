package ru.istu.b1978201.KSite.utils;

import ru.istu.b1978201.KSite.mode.Role;

/**
 * Объект для преставления наличия роли у пользователя
 * role - роль которую проверям
 * check - наличие роли
 */
public class CheckedRole {
    private Role role;
    private boolean check;

    public CheckedRole(Role role, boolean check) {
        this.role = role;
        this.check = check;
    }

    public Role getRole() {
        return role;
    }

    public boolean isCheck() {
        return check;
    }
}
