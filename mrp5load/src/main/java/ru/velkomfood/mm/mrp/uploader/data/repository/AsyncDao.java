package ru.velkomfood.mm.mrp.uploader.data.repository;

import java.util.concurrent.Future;

// Asynchronous data access object
public interface AsyncDao<Entity, Key> {
    Future<Boolean> exists(Key key);
    Future<Void> create(Entity entity);
    Future<Void> update(Entity entity);
}
