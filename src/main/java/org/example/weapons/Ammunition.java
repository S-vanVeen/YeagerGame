package org.example.weapons;

import org.example.ui.AmmoText;

public class Ammunition {
    private int currentAmmo;
    private final int maxAmmo;
    private AmmoText ammoText;

    public Ammunition(int startingAmmo, int maxAmmo) {
        this.currentAmmo = startingAmmo;
        this.maxAmmo = maxAmmo;
    }

    public void setAmmoText(AmmoText ammoText) {
        this.ammoText = ammoText;
        updateAmmoText();
    }

    public boolean useAmmo() {
        if (currentAmmo > 0) {
            currentAmmo--;
            updateAmmoText();
            return true;
        }
        return false;
    }

    public void reload(int amount) {
        currentAmmo = Math.min(currentAmmo + amount, maxAmmo);
        updateAmmoText();
    }

    public void reload() {
        currentAmmo = maxAmmo;
        updateAmmoText();
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    private void updateAmmoText() {
        if (ammoText != null) {
            ammoText.update(currentAmmo, maxAmmo);
        }
    }
}