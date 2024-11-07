package code.frontend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import code.configuration.WebAbstract;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HomePage.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
class HomePageTest extends WebAbstract {

  private MockMvc mockMvc;

  @Test
  void should_return_view() throws Exception {
    mockMvc.perform(get("/"))
      .andExpect(view().name("home/home"));
  }

  @Test
  void should_return_fragment() throws Exception {
    mockMvc.perform(get("/").header("HX-Request", true))
      .andExpect(view().name("home/home :: content"));
  }
}