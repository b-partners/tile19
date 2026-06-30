package fr.birdia.tileonetonine.endpoint.event.consumer.model;

import fr.birdia.tileonetonine.PojaGenerated;
import fr.birdia.tileonetonine.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
