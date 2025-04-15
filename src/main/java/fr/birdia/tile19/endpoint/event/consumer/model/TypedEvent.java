package fr.birdia.tile19.endpoint.event.consumer.model;

import fr.birdia.tile19.PojaGenerated;
import fr.birdia.tile19.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
