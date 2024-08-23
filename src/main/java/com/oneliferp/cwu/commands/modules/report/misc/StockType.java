package com.oneliferp.cwu.commands.modules.report.misc;

public enum StockType {
    UNKNOWN(null, null),
    CARDBOARD("Carton", 2),
    CAN("Canette", 2),
    DETERGENT("Sachet lessive", 2),
    PLASTIC_BAG("Sachet plastique", 2),
    TOOLBOX("Valise à outils", 40),
    SESSION_CHIP("Jetons de session", 50),
    RATION_CHIP("Jetons de ration", 100),
    ARMBAND("Brassards", 100),
    TOOLS("Outils", null),
    BARREL("Barils", 100),
    EGGS("Oeuf d'antlion", 100);

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
