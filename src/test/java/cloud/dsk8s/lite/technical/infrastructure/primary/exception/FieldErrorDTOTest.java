package cloud.dsk8s.lite.technical.infrastructure.primary.exception;

import static org.assertj.core.api.Assertions.*;

import cloud.dsk8s.lite.UnitTest;
import org.junit.jupiter.api.Test;

@UnitTest
class FieldErrorDTOTest {

  @Test
  void shouldBuild() {
    FieldErrorDTO fieldErrorDTO = new FieldErrorDTO("dto", "field", "message");

    assertThat(fieldErrorDTO.getObjectName()).isEqualTo("dto");
    assertThat(fieldErrorDTO.getField()).isEqualTo("field");
    assertThat(fieldErrorDTO.getMessage()).isEqualTo("message");
  }
}
