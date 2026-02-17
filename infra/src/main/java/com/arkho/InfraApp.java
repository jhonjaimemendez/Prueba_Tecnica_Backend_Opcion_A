package com.arkho;

import software.amazon.awscdk.App;

public class InfraApp {

    public static void main(final String[] args) {

        App app = new App();

        new InfraStack(app, "GestionFlotasStack");

        app.synth();
    }
}