package com.test;

import java.util.Objects;

public class Demo {
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Demo demo = (Demo) o;
        return Objects.equals(name, demo.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
