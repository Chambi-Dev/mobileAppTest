package com.strange.gestion501;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase auxiliar que gestiona el almacenamiento de usuarios
 * usando SharedPreferences (sin base de datos).
 */
public class UsuarioPrefs {

    private static final String PREFS_NAME = "usuarios_prefs";
    private static final String KEY_PREFIX_PASS = "pass_";

    private final SharedPreferences prefs;

    public UsuarioPrefs(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Registra un nuevo usuario guardando su contraseña.
     * Devuelve false si el usuario ya existe.
     */
    public boolean registrar(String usuario, String password) {
        if (existeUsuario(usuario)) {
            return false;
        }
        prefs.edit()
                .putString(KEY_PREFIX_PASS + usuario.toLowerCase().trim(), password)
                .apply();
        return true;
    }

    /**
     * Valida si el usuario y contraseña son correctos.
     */
    public boolean validar(String usuario, String password) {
        String key = KEY_PREFIX_PASS + usuario.toLowerCase().trim();
        if (!prefs.contains(key)) {
            return false;
        }
        return prefs.getString(key, "").equals(password);
    }

    /**
     * Comprueba si un usuario ya está registrado.
     */
    public boolean existeUsuario(String usuario) {
        return prefs.contains(KEY_PREFIX_PASS + usuario.toLowerCase().trim());
    }
}

