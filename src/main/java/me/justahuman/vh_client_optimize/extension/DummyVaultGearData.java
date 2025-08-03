package me.justahuman.vh_client_optimize.extension;

import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;

public class DummyVaultGearData extends VaultGearData {
    public DummyVaultGearData(VaultGearState state) {
        setState(state);
    }
}
