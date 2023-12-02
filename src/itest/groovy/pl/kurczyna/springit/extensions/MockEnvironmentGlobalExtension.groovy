package pl.kurczyna.springit.extensions

import org.spockframework.runtime.extension.IGlobalExtension

class MockEnvironmentGlobalExtension implements IGlobalExtension {
    @Override
    void stop() {
        MockEnvironment.stop()
    }
}
