package dev.mikita.rolt.rest;

import dev.mikita.rolt.entity.Property;
import dev.mikita.rolt.environment.Generator;
import dev.mikita.rolt.rest.handler.ErrorInfo;
import dev.mikita.rolt.service.PropertyService;
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
class PropertyControllerGetTest extends BaseControllerTestRunner {
    @Mock
    private PropertyService propertyServiceMock;

    @InjectMocks
    private PropertyController sut;

    @BeforeEach
    public void setUp() {
        super.setUp(sut);
    }

    @Test
    public void getByIdReturnsMatchingProperty() throws Exception {
        final Property property = new Property();
        property.setId(Generator.randomInt());
        when(propertyServiceMock.find(property.getId())).thenReturn(property);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/v1/properties/" + property.getId())).andReturn();

        final Property result = readValue(mvcResult, Property.class);
        assertNotNull(result);
        assertEquals(property.getId(), result.getId());
    }

    @Test
    public void getByIdThrowsNotFoundForUnknownPropertyId() throws Exception {
        final int id = 123;
        final MvcResult mvcResult = mockMvc.perform(get("/rest/v1/properties/" + id)).andExpect(status().isNotFound())
                .andReturn();
        final ErrorInfo result = readValue(mvcResult, ErrorInfo.class);
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Property identified by "));
        assertThat(result.getMessage(), containsString(Integer.toString(id)));
    }
}