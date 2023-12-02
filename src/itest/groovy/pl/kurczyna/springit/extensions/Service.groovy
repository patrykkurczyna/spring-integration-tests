package pl.kurczyna.springit.extensions

import pl.kurczyna.springit.extensions.mocks.GcsMock
import pl.kurczyna.springit.extensions.mocks.KafkaMock
import pl.kurczyna.springit.extensions.mocks.MailMock
import pl.kurczyna.springit.extensions.mocks.SqsMock
import pl.kurczyna.springit.extensions.mocks.StripeMock

enum Service {
    GCS(new GcsMock()),
    KAFKA(new KafkaMock()),
    MAIL(new MailMock()),
    SQS(new SqsMock()),
    STRIPE(new StripeMock())

    public final Mock service

    private Service(Mock service) {
        this.service = service
    }
}
