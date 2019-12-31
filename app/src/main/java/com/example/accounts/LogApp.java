package com.example.accounts;

import java.io.Serializable;

public class LogApp implements Serializable {
    private boolean flagApp;
    private String user;

    LogApp(boolean flagApp, String user) {
        this.flagApp = flagApp;
        this.user = user;
    }

    LogApp() {
        this.flagApp = false;
        this.user = "";
    }

    boolean getFlagApp() {
        return this.flagApp;
    }

    public String getUser() {
        return this.user;
    }

    @Override
    public boolean equals(Object object) {
        boolean bool = false;
        if (object instanceof LogApp) {
            bool = (this.getUser().equals(((LogApp) object).getUser()));
        }
        return bool;
    }
}
