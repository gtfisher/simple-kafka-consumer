package com.russmiles.antifragile.api;


import com.russmiles.antifragile.api.Command;

public interface CommandProcessor {

    public void handleCommand(Command command);
}
