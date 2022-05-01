package cloud.dsk8s.lite.technical.infrastructure.primary.redirection;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class RedirectionResource {

  @GetMapping(value = "/{path:[^\\.]*}")
  public String redirectApi() {
    return "forward:/";
  }
}
