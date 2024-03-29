package org.myshelfie.model;

/**
 * Enumerates the possible states of a Game.
 */
public enum ModelState {

    WAITING_SELECTION_TILE,
    WAITING_3_SELECTION_TILE_FROM_HAND,
    WAITING_2_SELECTION_TILE_FROM_HAND,
    WAITING_1_SELECTION_TILE_FROM_HAND,
    WAITING_SELECTION_BOOKSHELF_COLUMN,
    PAUSE, END_GAME
}
