package de.fhb.campusapp.eval.interfaces;

public interface PagerAdapterPageEvent {
    public void onGettingPrimary(int oldPosition);
    public void onLeavingPrimary(int newPosition);
}