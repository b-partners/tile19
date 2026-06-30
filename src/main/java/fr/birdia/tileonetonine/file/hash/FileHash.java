package fr.birdia.tileonetonine.file.hash;

import fr.birdia.tileonetonine.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
