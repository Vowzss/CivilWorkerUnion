package com.oneliferp.cwu.misc;

public enum StockType {
    CARDBOARD("Cartons", 2),
    CAN("Canettes", 2),
    PLASTIC_BAG("Sachets plastiques", 2),
    TOOLBOX("Valises à outils", 40),
    SESSION_CHIP("Jetons de session", 50),
    RATION_CHIP("Jetons de ration", 100),
    ARMBAND("Brassards", 100),
    TOOLS("Outils", null),
    BARREL("Barils", 100);

    private final String label;
    private final Integer price;

    StockType(final String label, final Integer price) {
        this.label = label;
        this.price = price;
    }

    public String getLabel() {
        return this.label;
    }

    public int getPrice() {
        return this.price;
    }
}
