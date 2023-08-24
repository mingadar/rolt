package dev.mikita.rolt.rest;

import dev.mikita.rolt.entity.Landlord;
import dev.mikita.rolt.environment.Generator;
import dev.mikita.rolt.rest.handler.ErrorInfo;
import dev.mikita.rolt.service.LandlordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MvcResult;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LandlordControllerGetTest extends BaseControllerTestRunner {
    @Mock
    private LandlordService landlordServiceMock;

    @InjectMocks
    private LandlordController sut;

    @BeforeEach
    public void setUp() {
        super.setUp(sut);
    }

    @Test
    public void getByIdReturnsMatchingLandlord() throws Exception {
        final Landlord landlord = new Landlord();
        landlord.setId(Generator.randomInt());
        when(landlordServiceMock.find(landlord.getId())).thenReturn(landlord);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/v1/landlords/" + landlord.getId())).andReturn();

        final Landlord result = readValue(mvcResult, Landlord.class);
        assertNotNull(result);
        assertEquals(landlord.getId(), result.getId());
    }

    @Test
    public void getByIdThrowsNotFoundForUnknownLandlordId() throws Exception {
        final int id = 123;
        final MvcResult mvcResult = mockMvc.perform(get("/rest/v1/landlords/" + id)).andExpect(status().isNotFound())
                .andReturn();
        final ErrorInfo result = readValue(mvcResult, ErrorInfo.class);
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Landlord identified by "));
        assertThat(result.getMessage(), containsString(Integer.toString(id)));
    }
}