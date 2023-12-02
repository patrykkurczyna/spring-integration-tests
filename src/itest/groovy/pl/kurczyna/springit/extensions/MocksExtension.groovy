package pl.kurczyna.springit.extensions

import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.SpecInfo

class MocksExtension implements IAnnotationDrivenExtension<Mocks> {
    @Override
    void visitSpecAnnotation(Mocks annotation, SpecInfo spec) {
        MockEnvironment.init(annotation.services().collect { it.service })
        spec.addSharedInitializerInterceptor(new IMethodInterceptor() {
            @Override
            void intercept(IMethodInvocation invocation) throws Throwable {
                MockEnvironment.start()
            }
        })
        spec.addCleanupInterceptor(new IMethodInterceptor() {
            @Override
            void intercept(IMethodInvocation invocation) throws Throwable {
                MockEnvironment.cleanup()
            }
        })
    }
}
