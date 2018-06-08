package com.example.medell.locator;

/**
 * Created by me dell on 11-03-2018.
 */

class database {
    String id;
    String name;
    String email;
    String password;

    database(){

    }

    public database(String id,String name, String email, String password) {
        this.id=id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
