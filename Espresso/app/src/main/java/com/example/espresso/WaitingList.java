package com.example.espresso;

import android.content.Context;

import java.util.ArrayList;

public class WaitingList extends Entrant {
    private ArrayList<Entrant> entrants;
    private Context context;

    /**
     * Create a new entrant.
     *
     * @param context App context.
     */
    public WaitingList(Context context, ArrayList<Entrant> entrants) {
        super(context);
        this.context = context;
        this.entrants = entrants;
    }
}
