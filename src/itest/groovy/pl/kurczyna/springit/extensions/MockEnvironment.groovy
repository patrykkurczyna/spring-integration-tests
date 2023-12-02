package pl.kurczyna.springit.extensions

class MockEnvironment {

    private static boolean started

    private static List<Mock> mocks

    static init(List<Mock> mocks) {
        this.mocks = mocks
    }

    static start() {
        if (!started) {
            started = true
            mocks.each { it.start() }
        }
    }

    static stop() {
        if (started) {
            mocks.each { it.stop() }
        }
    }

    static cleanup() {
        mocks.each { it.cleanup() }
    }

    /**
     * Could be added to application context like this:
     * @Override
     * void initialize(ConfigurableApplicationContext applicationContext) {
     *      TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
     *          applicationContext,
     *          MockEnvironment.propertiesToRegister()
     *      )
     * }
     */
    static String[] propertiesToRegister() {
        mocks.collect {it.propertiesToRegister() }.flatten()
    }
}
