package com.dentalclinic.dental.main;

import org.mindrot.jbcrypt.BCrypt;
public class HashPassword {
    public static void main(String[] args) {
        String password = "admin123";

        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(10));

        System.out.println("Original password: " + password );
        System.out.println("Hashed password: " + hashed );

    }
}
