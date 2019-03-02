/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.api.item.inventory.menu;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.property.ContainerType;
import org.spongepowered.api.item.inventory.slot.SlotIndex;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Helper for Menus based on Inventories.
 * <p>This helper provides simple callbacks that can be used instead of listening to inventory events.</p>
 * <p>InventoryMenus are by default readonly and automatically prevent any changes made by players in the menu.</p>
 */
public interface InventoryMenu {

    /**
     * Creates a new InventoryMenu based on given inventory.
     *
     * @param inventory the inventory
     *
     * @return the new menu.
     */
    static InventoryMenu of(ViewableInventory inventory) {
        return inventory.asMenu();
    }

    /**
     * Returns the current inventory used in this menu.
     *
     * @return the current inventory
     */
    ViewableInventory getCurrentInventory();

    /**
     * Returns the container type of the current inventory.
     *
     * @return current container type.
     */
    ContainerType getType();

    /**
     * Sets a new inventory. If the ContainerType does not change the inventory will be swapped out silently.
     * <p>If the ContainerType is different all existing callbacks are cleared and open menus are closed and reopened with the new inventory.</p>
     *
     * @param inventory the new inventory
     */
    void setCurrentInventory(ViewableInventory inventory);

    /**
     * Sets the title of this menu.
     * <p>Any open menus are closed and reopened with the new title.</p>
     *
     * @param title the new title.
     */
    void setTitle(Text title);

    /**
     * Registers a callback for given slotIndices. If none are specified the callback fires for all slots.
     *
     * @param handler the callback handler
     * @param slotIndices the slot indices the handler should be active for.
     */
    // Mixin at begin of Container#slotClick and do nothing. (maybe need to send rollback packets to client)
    void registerClick(SlotClickHandler handler, SlotIndex... slotIndices);

    /**
     * Registers a callback for given slotIndices. If none are specified the callback fires for all slots.
     * <p>You can override the behaviour of {@link #setReadOnly(boolean)} with this.</p>
     *
     * @param handler the callback handler
     * @param slotIndices the slot indices the handler should be active for
     */
    // TODO impl: in detectAndSendChanges
    void registerChange(SlotChangeHandler handler, SlotIndex... slotIndices);

    /**
     * Registers a callback when this menu is closed.
     *
     * @param handler the callback handler
     */
    void registerClose(BiConsumer<Container, Player> handler);

    /**
     * Registers a callback handler that prevents any change to given slotIndeces.
     * <p>You can override the behaviour of {@link #setReadOnly(boolean)} with this.</p>
     *
     * @param slotIndices the slot indices the handler should be active for
     */
    default void registerReadOnly(SlotIndex... slotIndices) {
        registerChange((container, slot, slotIndex) -> false, slotIndices);
    }

    /**
     * Sets the readonly mode for this menu.
     * <p>By default this is true and cancels any change in menu.</p>
     *
     * @param readOnly whether to make the menu readonly or not.
     *
     * @return this menu
     */
    InventoryMenu setReadOnly(boolean readOnly);

    /**
     * Unregisters all callback handlers at given slotIndices.
     *
     * @param slotIndices the slot indices the handlers should be removed for
     */
    void unregisterAt(SlotIndex... slotIndices);

    /**
     * Unregisters all callback handlers.
     */
    void unregisterAll();

    /**
     * Opens this menu for given player.
     *
     * @param player the player.
     *
     * @return the opened Container.
     */
    Optional<Container> open(Player player);

}
