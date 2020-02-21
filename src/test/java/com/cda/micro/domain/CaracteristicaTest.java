package com.cda.micro.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.cda.micro.web.rest.TestUtil;

public class CaracteristicaTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Caracteristica.class);
        Caracteristica caracteristica1 = new Caracteristica();
        caracteristica1.setId(1L);
        Caracteristica caracteristica2 = new Caracteristica();
        caracteristica2.setId(caracteristica1.getId());
        assertThat(caracteristica1).isEqualTo(caracteristica2);
        caracteristica2.setId(2L);
        assertThat(caracteristica1).isNotEqualTo(caracteristica2);
        caracteristica1.setId(null);
        assertThat(caracteristica1).isNotEqualTo(caracteristica2);
    }
}
