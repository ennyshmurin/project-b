package com.example.checkme;

import java.io.Serializable;
public class Item implements Serializable {

    private String name;
    private String userid;

    public Item() { }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
            return name;
        }

        public Item(String name) {
            this.name = name;
        }
    }