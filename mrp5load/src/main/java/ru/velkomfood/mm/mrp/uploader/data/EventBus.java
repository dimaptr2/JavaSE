package ru.velkomfood.mm.mrp.uploader.data;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;
import ru.velkomfood.mm.mrp.uploader.data.model.md.MaterialUomPair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventBus implements MrpUploaderComponent {

    private final Map<String, Queue<InfoRecord>> dataQueueMap;
    private final Map<String, MaterialUomPair> materialCache;

    public EventBus() {
        dataQueueMap = new ConcurrentHashMap<>();
        dataQueueMap.put("uom.queue", new ConcurrentLinkedQueue<>());
        dataQueueMap.put("purchase.group.queue", new ConcurrentLinkedQueue<>());
        dataQueueMap.put("store.place.queue", new ConcurrentLinkedQueue<>());
        dataQueueMap.put("material.queue", new ConcurrentLinkedQueue<>());
        dataQueueMap.put("material.unit.queue", new ConcurrentLinkedQueue<>());
        dataQueueMap.put("stock.queue", new ConcurrentLinkedQueue<>());
        dataQueueMap.put("requirement.queue", new ConcurrentLinkedQueue<>());
        materialCache = new ConcurrentHashMap<>();
    }

    public int queueSize(String address) {
        return dataQueueMap.get(address).size();
    }

    public void push(String address, InfoRecord record) {
        dataQueueMap.get(address).add(record);
    }

    public InfoRecord pull(String address) {
        return dataQueueMap.get(address).poll();
    }

    public void addMaterial(String materialNumber, String uom) {
        materialCache.put(materialNumber, new MaterialUomPair(Long.parseLong(materialNumber), uom));
    }

    public Map<String, MaterialUomPair> getMaterialCache() {
        return materialCache;
    }

}
