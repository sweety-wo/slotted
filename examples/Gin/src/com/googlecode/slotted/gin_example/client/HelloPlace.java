package com.googlecode.slotted.gin_example.client;

import com.google.gwt.activity.shared.Activity;
import com.googlecode.slotted.client.Slot;
import com.googlecode.slotted.client.SlottedPlace;

public class HelloPlace extends SlottedPlace {
    private HelloPlace() {}

    public HelloPlace(String token) {
        setParameter("helloName", token);
    }

    public String getHelloName() {
        return getParameter("helloName");
    }

    @Override public Slot getParentSlot() {
        return BasePlace.SLOT;
    }

    @Override public Slot[] getChildSlots() {
        return null;
    }

    @Override public Activity getActivity() {
        return AppGinjector.instance.getHelloActivity();
    }
}