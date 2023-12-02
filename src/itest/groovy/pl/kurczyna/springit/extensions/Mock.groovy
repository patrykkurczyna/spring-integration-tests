package pl.kurczyna.springit.extensions

trait Mock {
    abstract void start()

    abstract void stop()

    void cleanup() {

    }

    /**
     * Inlined properties that should be registered in the application context
     * format: ["propertyA=valueA", "propertyB=valueB"]
     */
    String[] propertiesToRegister() {
        return []
    }
}
