package com.example.accounts;

import java.io.Serializable;

public class LogApp implements Serializable {
    private boolean initial;
    private boolean flagApp;

    public LogApp(boolean flagApp, boolean initial) {
        this.initial = initial;
        this.flagApp = flagApp;
    }

    public LogApp() {
        this.initial = false;
        this.flagApp = false;
    }

    public Boolean getFlagApp() {
        return this.flagApp;
    }

    public Boolean getInitial() {
        return this.initial;
    }

    @Override
    public boolean equals(Object object) {
        boolean bool = false;
        if (object != null && object instanceof LogApp) {
            bool = (this.getFlagApp().equals(((LogApp) object).getFlagApp()));
        }
        return bool;
    }
}
