package fr.birdia.tile19.file.hash;

import fr.birdia.tile19.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
