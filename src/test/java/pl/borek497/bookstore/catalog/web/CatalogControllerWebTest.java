package pl.borek497.bookstore.catalog.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.borek497.bookstore.catalog.application.port.CatalogUseCase;
import pl.borek497.bookstore.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CatalogController.class})
//@ActiveProfiles("test")
@WithMockUser
class CatalogControllerWebTest {

    @MockBean
    CatalogUseCase catalogUseCase;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void shouldGetAllBooks() throws Exception{
        // given
        Book effective = new Book("Effective Java", 2005, new BigDecimal("99.90"), 50L);
        Book concurrency = new Book("Java Concurency", 2006, new BigDecimal("99.90"), 50L);

        //Jeśli catalogUseCase zostanie zawołany na metodzie findAll() to zwróć mi listę tych dwóch obiektów
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effective, concurrency));

        // expect - trochę inny blok
        //perfom - służy do wykonania jakiegoś żądania HTTP
        mockMvc.perform(get("/catalog"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}