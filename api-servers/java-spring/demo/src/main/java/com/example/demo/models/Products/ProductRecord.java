package com.example.demo.models.Products;

import java.util.Date;

public record ProductRecord(
        Integer id,
        String name,
        Date createdAt
) { // canonical-constructor auto-gen'd with its own definition above
    // auto-gen'd accessors - id(), name(), createdAt(), ..
    // auto-gen'd method - equals, hashCode, toString

    private static Date updatedAt; // only static props allowed

    public Date updatedAt() { // (non-)static methods allowed
        return updatedAt;
    }
    public void updatedAt(Date datetime) { // custom setter
        updatedAt = datetime; // prop not set on instantiation
    }

}
