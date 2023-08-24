package dev.mikita.rolt.rest;

import dev.mikita.rolt.entity.Landlord;
import dev.mikita.rolt.entity.Tenant;
import dev.mikita.rolt.environment.Generator;
import dev.mikita.rolt.rest.handler.ErrorInfo;
import dev.mikita.rolt.service.LandlordService;
import dev.mikita.rolt.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TenantControllerGetTest extends BaseControllerTestRunner {
    @Mock
    private TenantService tenantServiceMock;

    @InjectMocks
    private TenantController sut;

    @BeforeEach
    public void setUp() {
        super.setUp(sut);
    }

    @Test
    public void getByIdReturnsMatchingTenant() throws Exception {
        final Tenant tenant = new Tenant();
        tenant.setId(Generator.randomInt());
        when(tenantServiceMock.find(tenant.getId())).thenReturn(tenant);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/v1/tenants/" + tenant.getId())).andReturn();

        final Tenant result = readValue(mvcResult, Tenant.class);
        assertNotNull(result);
        assertEquals(tenant.getId(), result.getId());
    }

    @Test
    public void getByIdThrowsNotFoundForUnknownTenantId() throws Exception {
        final int id = 123;
        final MvcResult mvcResult = mockMvc.perform(get("/rest/v1/tenants/" + id)).andExpect(status().isNotFound())
                .andReturn();
        final ErrorInfo result = readValue(mvcResult, ErrorInfo.class);
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Tenant identified by "));
        assertThat(result.getMessage(), containsString(Integer.toString(id)));
    }

}