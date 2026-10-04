package com.phoenix.nothingwidget.widgets.quick_actions

object QuickActionsConfig {
    const val WIDGET_ID = "quick_actions"

    /** Run one configured action (extra = [EXTRA_ACTION]). */
    const val ACTION_RUN = "com.phoenix.nothingwidget.widgets.quick_actions.ACTION_RUN"
    const val EXTRA_ACTION = "quick_actions_action"

    /**
     * DataStore element ids → `quick_actions.slotN.icon_style`.
     * First three are always on; 4-8 are optional extras for wider / scrollable strips.
     */
    const val ELEMENT_SLOT1 = "slot1"
    const val ELEMENT_SLOT2 = "slot2"
    const val ELEMENT_SLOT3 = "slot3"
    const val ELEMENT_SLOT4 = "slot4"
    const val ELEMENT_SLOT5 = "slot5"
    const val ELEMENT_SLOT6 = "slot6"
    const val ELEMENT_SLOT7 = "slot7"
    const val ELEMENT_SLOT8 = "slot8"

    val SLOT_ELEMENTS = listOf(
        ELEMENT_SLOT1,
        ELEMENT_SLOT2,
        ELEMENT_SLOT3,
        ELEMENT_SLOT4,
        ELEMENT_SLOT5,
        ELEMENT_SLOT6,
        ELEMENT_SLOT7,
        ELEMENT_SLOT8,
    )

    /** Slots that ship enabled on first install. */
    const val REQUIRED_SLOT_COUNT = 3

    const val SLOT_COUNT = 8

    const val INFO_NOTE_ID = "_resize_note"
}
