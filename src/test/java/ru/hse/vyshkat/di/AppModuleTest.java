package ru.hse.vyshkat.di;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.junit.jupiter.api.Test;
import ru.hse.vyshkat.domain.Bicycle;
import ru.hse.vyshkat.domain.Scooter;
import ru.hse.vyshkat.service.Fleet;
import ru.hse.vyshkat.service.InspectionResult;
import ru.hse.vyshkat.service.NoviceRule;
import ru.hse.vyshkat.service.ReportService;
import ru.hse.vyshkat.service.ServiceCenter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppModuleTest {

    @Test
    void realServiceCenterIsWiredWithConfiguredThreshold() {
        Fleet fleet = Guice.createInjector(new AppModule()).getInstance(Fleet.class);

        assertTrue(fleet.acceptTransport(new Bicycle("ok", 5, AppModule.MAX_WEAR_FOR_INSPECTION)).accepted());
        assertFalse(fleet.acceptTransport(new Bicycle("worn", 5, AppModule.MAX_WEAR_FOR_INSPECTION + 1)).accepted());
    }

    @Test
    void noviceRuleIsWiredWithConfiguredSettings() {
        NoviceRule rule = Guice.createInjector(new AppModule()).getInstance(NoviceRule.class);

        assertTrue(rule.isSuitable(new Bicycle("B", AppModule.NOVICE_MIN_EASE, AppModule.NOVICE_MAX_WEAR)));
        assertFalse(rule.isSuitable(new Bicycle("B", AppModule.NOVICE_MIN_EASE - 1, 0)));
    }

    @Test
    void fleetIsSharedWithReportService() {
        Injector injector = Guice.createInjector(new AppModule());

        injector.getInstance(Fleet.class).acceptTransport(new Scooter("S", 8, 10, 0.5));

        assertEquals(1, injector.getInstance(ReportService.class).totalUnits());
    }

    @Test
    void serviceCenterIsReplacedByMockThroughContainer() {
        ServiceCenter mockCenter = mock(ServiceCenter.class);
        when(mockCenter.inspect(any())).thenReturn(InspectionResult.reject("mock: отклонить"));
        Injector injector = Guice.createInjector(Modules.override(new AppModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ServiceCenter.class).toInstance(mockCenter);
            }
        }));
        Scooter perfect = new Scooter("Идеальный", 10, 0, 0.5);

        InspectionResult result = injector.getInstance(Fleet.class).acceptTransport(perfect);

        assertFalse(result.accepted());
        assertEquals("mock: отклонить", result.reason());
        assertEquals(0, injector.getInstance(ReportService.class).totalUnits());
        verify(mockCenter).inspect(perfect);
    }
}