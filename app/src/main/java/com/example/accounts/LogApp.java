package com.example.accounts;

import java.io.Serializable;

public class LogApp implements Serializable {
    private boolean flagApp;

    public LogApp(boolean flagApp) {
        this.flagApp = flagApp;
    }

    public LogApp() {
        this.flagApp = false;
    }

    public Boolean getFlagApp() {
        return this.flagApp;
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
